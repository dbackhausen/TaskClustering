package de.fernuni.browserhistoryclustering.evaluation;

import java.util.List;

import com.discoversites.util.collections.tree.TreeNode;

import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;

/**
 * @author ah
 *
 */
public class Precision extends AbstractCooccurrenceIndex {

   /**
    * @param p_SameClusterDecider
    * @param p_Nodes
    */
   public Precision(SameClassDecider p_SameClusterDecider, List<TreeNode<ClusterDescriptor>> p_Nodes) {
      super (p_SameClusterDecider, p_Nodes);
   }

   /**
    * @return Value of Precision
    */
   public double calculate() {
      double v_Precision = (double) (m_TruePositives) / (double) (m_TruePositives + m_FalsePositives);
      return v_Precision;
   }

}
