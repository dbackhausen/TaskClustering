package de.fernuni.browserhistoryclustering.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.discoversites.util.collections.tree.TreeNode;

import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;

public class RandIndex {

   private SameClusterDecider m_SharedClusterDecider;
   private int a = 0;
   private int b = 0;
   private int c = 0;
   private int d = 0;

   private RandIndex() {
   }

   public RandIndex(SameClusterDecider p_SharedClusterDecider) {
      m_SharedClusterDecider = p_SharedClusterDecider;
   }

   public double calculate(List<TreeNode<ClusterDescriptor>> p_Nodes) {

      List<String> v_AllFiles = new ArrayList<String>();
      Map<String, String> v_File2ClusterId = new HashMap<>();

      for (TreeNode<ClusterDescriptor> v_Node : p_Nodes) {
         List<String> v_FilesOfNode = v_Node.getElement().getDocuments();
         v_AllFiles.addAll(v_FilesOfNode);
         for (String v_File : v_FilesOfNode) {
            v_File2ClusterId.put(v_File, v_Node.getElement().getClusterId());
         }
      }

      for (int i = 0; i < v_AllFiles.size(); i++) {
         for (int j = i + 1; j < v_AllFiles.size(); j++) {
            if (m_SharedClusterDecider.belongToSameCluster(v_AllFiles.get(i), v_AllFiles.get(j))) {
               if (v_File2ClusterId.get(v_AllFiles.get(i)).equals(
                     v_File2ClusterId.get(v_AllFiles.get(j)))) {
                  a++;
               } else {
                  b++;
               }
            } else {
               if (v_File2ClusterId.get(v_AllFiles.get(i)).equals(
                     v_File2ClusterId.get(v_AllFiles.get(j)))) {
                  c++;
               } else {
                  d++;
               }
            }
         }
      }

      double v_RandIndex = ((double) (a + d)) / ((double) (a + b + c + d));
      return v_RandIndex;

   }

}
