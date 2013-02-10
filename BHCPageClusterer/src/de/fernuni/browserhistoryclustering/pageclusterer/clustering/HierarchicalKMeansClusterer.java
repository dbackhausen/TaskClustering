package de.fernuni.browserhistoryclustering.pageclusterer.clustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.canopy.CanopyDriver;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.topdown.postprocessor.ClusterOutputPostProcessorDriver;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.distance.CosineDistanceMeasure;

import com.discoversites.util.collections.tree.TreeNode;

import de.fernuni.browserhistoryclustering.common.utils.Stringutils;
import de.fernuni.browserhistoryclustering.exception.ClusterDescriptorException;
import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;
import de.fernuni.browserhistoryclustering.graph.SetTree;
import de.fernuni.browserhistoryclustering.util.ClusterUtil;

public class HierarchicalKMeansClusterer extends AbstractBaseClusterer implements ClustererIF {
   
   protected static Path s_CanopyCentroids_Lv0;
   protected static Path s_CanopyCentroids_Lv1;
   protected static Path s_Clusters_Lv0; 
   protected static Path s_Clusters_Lv1; 
   protected static Path s_Vectorsets_Lv1;
   protected static boolean s_DoSequential;
   protected static Map<Integer, Set<Integer>> s_Lever2ClusterIds;
   protected static Map<Integer, List<String>> s_ClusterId2FileList;
   protected static ArrayList<Double> s_CanopyRanges;
   protected static Logger log = Logger.getLogger("");

   @Override
   public SetTree<ClusterDescriptor> run(Configuration p_HadoopConf, Boolean p_DoSequential) throws Exception {
      
      log.info("Hierarchical k-means clustering started.");
            
      s_HadoopConf = p_HadoopConf;
      s_DoSequential = p_DoSequential;
      
      loadConfiguration();
      
      loadMaps();
      
      HadoopUtil.delete(s_HadoopConf, new Path(s_PathPrefix, "clusters/"));
      
      HadoopUtil.delete(s_HadoopConf, new Path(s_PathPrefix, "0/"));
      
      ClusterDescriptor.initialize(s_Filename2Normtopic2Value, s_Filename2Normtopic2Topic,
            s_Filenames2Queries);

      ClusterDescriptor v_RootElem = new ClusterDescriptor("Root", s_Tf_Idf, s_Clusters_Lv0,
            s_CanopyCentroids_Lv0, null);
      v_RootElem.setDocuments(new ArrayList<String>());
      SetTree<ClusterDescriptor> v_Tree = new SetTree<ClusterDescriptor>(v_RootElem);
      
      // Build top cluster level       
      List<ClusterDescriptor> v_FirstLevelClusters = buildFirstLevelClusters(s_CanopyRanges.get(0));
      
      for (ClusterDescriptor v_Descriptor : v_FirstLevelClusters) {
         v_Tree.getRoot().addElement(v_Descriptor);
      }    
      
      // Build lower levels
      for (int v_Level = 1; v_Level < s_CanopyRanges.size(); v_Level++)
      {         
         clusterTreeLevel(v_Level, v_Tree);
      }
      
      log.info("Hierarchical k-means clustering finished.");
      
      return v_Tree;
       
   }

   private void clusterTreeLevel(Integer p_Level, SetTree<ClusterDescriptor> v_Tree)
         throws IOException, InterruptedException, ClassNotFoundException,
         ClusterDescriptorException {
      
      Set<TreeNode<ClusterDescriptor>> v_NodesOfLevel = v_Tree.getNodes(p_Level);
      
      for (TreeNode<ClusterDescriptor> v_Node : v_NodesOfLevel) {
         
         // Test if cluster cardinality allows further subdivision
         if (v_Node.getElement().getDocuments().size() <= s_Config.getSubdivisionThreshold())
         {
            continue;
         }
         
         ClusterDescriptor v_Element = v_Node.getElement();         
         CanopyDriver.run(
               new Path(s_PathPrefix, "0/sv/"+v_Element.getClusterId()),
               new Path(s_PathPrefix, "0/cc/"+v_Element.getClusterId()),           
               new CosineDistanceMeasure(), s_CanopyRanges.get(p_Level), s_CanopyRanges.get(p_Level),
               true, 0.0, s_DoSequential);
         KMeansDriver.run(s_HadoopConf,  new Path(s_PathPrefix, "0/sv/"+v_Element.getClusterId()),
               ClusterUtil.getFinalClusterPath(new Path(s_PathPrefix, "0/cc/"+v_Element.getClusterId())), new Path(s_PathPrefix, "0/cl/"+v_Element.getClusterId()),
               new CosineDistanceMeasure(), 0.01, 20, true, 0.0, s_DoSequential);
         
         ClusterOutputPostProcessorDriver.run(new Path(s_PathPrefix, "0/cl/"+v_Element.getClusterId()), new Path(s_PathPrefix, "0/sv/"+v_Element.getClusterId()), s_DoSequential);         
         
         s_ClusterId2FileList = ClusterUtil.getFilesPerCluster(s_HadoopConf, new Path(s_PathPrefix, "0/cl/"+v_Element.getClusterId()));
         
         for (int v_Key : s_ClusterId2FileList.keySet())
         {
            ClusterDescriptor v_Elem = new ClusterDescriptor(v_Element.getClusterId()+"/"+v_Key);
            v_Elem.setDocuments(s_ClusterId2FileList.get(v_Key));
            v_Node.addElement(v_Elem);
         }
         
      }
   }

   private List<ClusterDescriptor> buildFirstLevelClusters(Double p_CanopyRange) throws IOException,
         InterruptedException, ClassNotFoundException, ClusterDescriptorException{
      
      ArrayList<ClusterDescriptor> v_Result = new ArrayList<ClusterDescriptor>();
      
      CanopyDriver.run(
            new Path(s_Tf_Idf, "tfidf-vectors"),
            new Path(s_PathPrefix, "0/cc/0"),           
            new CosineDistanceMeasure(), p_CanopyRange, p_CanopyRange,
            true, 0.0, s_DoSequential);     
      
      KMeansDriver.run(s_HadoopConf, new Path(s_Tf_Idf, "tfidf-vectors"),
            ClusterUtil.getFinalClusterPath(new Path(s_PathPrefix, "0/cc/0")), new Path(s_PathPrefix, "0/cl/0"),
            new CosineDistanceMeasure(), 0.01, 20, true, 0.0, s_DoSequential);
      
      ClusterOutputPostProcessorDriver.run(new Path(s_PathPrefix, "0/cl/0"), new Path(s_PathPrefix, "0/sv/0"), s_DoSequential);
      
      s_ClusterId2FileList = ClusterUtil.getFilesPerCluster(s_HadoopConf, new Path(s_PathPrefix, "0/cl/0"));
      
      for (int v_Key : s_ClusterId2FileList.keySet())
      {
         ClusterDescriptor v_Elem = new ClusterDescriptor("0/"+v_Key);
         v_Elem.setDocuments(s_ClusterId2FileList.get(v_Key));
         v_Result.add(v_Elem);
      }
      return v_Result;
   }   
   
   static void loadConfiguration()
   {  
      AbstractBaseClusterer.loadConfiguration();
      
      s_CanopyCentroids_Lv0 = new Path(s_PathPrefix, "clusters/lvl-0/canopy-centroids");
      s_CanopyCentroids_Lv1 = new Path(s_PathPrefix, "clusters/lvl-1/canopy-centroids");
      s_Clusters_Lv0 = new Path(s_PathPrefix, "clusters/lvl-0/clusters");
      s_Clusters_Lv1 = new Path(s_PathPrefix, "clusters/lvl-1/clusters");
      s_Vectorsets_Lv1 = new Path(s_PathPrefix, "clusters/lvl-1/vectorsets");
      s_CanopyRanges = Stringutils.parseToDoubles(s_Config.getCanopyRanges(), "(\\s)+");
   }

}
