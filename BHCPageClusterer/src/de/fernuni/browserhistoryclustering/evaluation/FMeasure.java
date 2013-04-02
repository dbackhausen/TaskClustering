package de.fernuni.browserhistoryclustering.evaluation;

import java.util.List;

import com.discoversites.util.collections.tree.TreeNode;

import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;

/**
 * @author ah
 *
 */
public class FMeasure {

   /**
    * @param p_SameClusterDecider
    * @param p_Nodes
    * @param p_Beta
    * @return Value of F-Measure
    */
   public static Double calculate(SameClassDecider p_SameClusterDecider, List<TreeNode<ClusterDescriptor>> p_Nodes, Double p_Beta) {
      Double v_Precision = (new Precision(p_SameClusterDecider, p_Nodes)).calculate();
      Double v_Recall= (new Recall(p_SameClusterDecider, p_Nodes)).calculate();
      return (Math.pow(p_Beta,2d) + 1) *( v_Precision * v_Recall / ((Math.pow(p_Beta, 2d) * v_Precision + v_Recall)));
   }
   
}
