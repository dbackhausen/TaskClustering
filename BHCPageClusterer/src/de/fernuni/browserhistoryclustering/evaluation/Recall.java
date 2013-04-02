package de.fernuni.browserhistoryclustering.evaluation;

import java.util.List;

import com.discoversites.util.collections.tree.TreeNode;

import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;

/**
 * @author ah
 *
 */
public class Recall extends AbstractCooccurrenceIndex {

   /**
    * @param p_SameClusterDecider
    * @param p_Nodes
    */
   public Recall(SameClassDecider p_SameClusterDecider, List<TreeNode<ClusterDescriptor>> p_Nodes) {
      super (p_SameClusterDecider, p_Nodes);
   }

   /**
    * @return Value of Recall
    */
   public double calculate() {
      double v_Recall = (double) (m_TruePositives) / (double) (m_TruePositives + m_FalseNegatives);
      return v_Recall;
   }

}
