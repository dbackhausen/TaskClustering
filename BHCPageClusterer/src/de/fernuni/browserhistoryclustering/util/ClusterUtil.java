package de.fernuni.browserhistoryclustering.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.math.NamedVector;

/**
 * @author ah
 *
 * Utility class for mahout clustering related helper methods.
 */
public class ClusterUtil {
   

   /**
    * @param p_ParentDirPath
    * @return Path of final cluster (last iteration)
    */
   public static Path getFinalClusterPath(Path p_ParentDirPath) {
      File v_Dir = new File(p_ParentDirPath.toString());
      FileFilter v_FileFilter = new WildcardFileFilter("clusters-*-final");
      File[] v_Files = v_Dir.listFiles(v_FileFilter);
      if (v_Files != null && v_Files.length > 0) {
         return new Path(v_Files[0].toString());
      } else {
         return null;
      }
   }
   
   /**
    * @param p_Configuration
    * @param p_ClusterPath
    * @return Map ClusterNo -> Documents of Cluster
    * @throws IOException
    */
   public static Map<Integer, List<String>> getFilesPerCluster(Configuration p_Configuration,
         Path p_ClusterPath) throws IOException {

      FileSystem v_FileSystem = null;
      Map<Integer, List<String>> v_ClusterId2FileList = new HashMap<Integer, List<String>>();

      v_FileSystem = FileSystem.get(p_Configuration);
      
      SequenceFile.Reader reader = null;

      reader = new SequenceFile.Reader(v_FileSystem, new Path(p_ClusterPath,
            Cluster.CLUSTERED_POINTS_DIR + "/part-m-0"), p_Configuration);

      IntWritable v_Key = new IntWritable();
      WeightedVectorWritable v_Value = new WeightedVectorWritable();

      while (reader.next(v_Key, v_Value)) {
         NamedVector v_Vector = (NamedVector) v_Value.getVector();
         String v_FilenameSansSlash = v_Vector.getName().substring(1);

         if (v_ClusterId2FileList.containsKey(v_Key.get())) {
            v_ClusterId2FileList.get(v_Key.get()).add(v_FilenameSansSlash);
         } else {
            v_ClusterId2FileList.put(v_Key.get(),
                  new ArrayList<String>(Collections.singletonList(v_FilenameSansSlash)));
         }

      }

      return v_ClusterId2FileList;
   } 

}
