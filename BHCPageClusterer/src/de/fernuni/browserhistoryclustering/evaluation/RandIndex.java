package de.fernuni.browserhistoryclustering.evaluation;

import java.util.List;

import com.discoversites.util.collections.tree.TreeNode;

import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;

/**
 * @author ah
 *
 */
public class RandIndex extends AbstractCooccurrenceIndex {

   /**
    * @param p_SameClusterDecider
    * @param p_Nodes
    */
   public RandIndex(SameClassDecider p_SameClusterDecider, List<TreeNode<ClusterDescriptor>> p_Nodes) {
      super (p_SameClusterDecider, p_Nodes);
   }

   /**
    * @return Value of Rand index
    */
   public double calculate() {
      double v_RandIndex = ((double) (m_TruePositives + m_TrueNegatives)) / ((double) (m_TruePositives + m_FalseNegatives + m_FalsePositives + m_TrueNegatives));
      return v_RandIndex;
   }

}
