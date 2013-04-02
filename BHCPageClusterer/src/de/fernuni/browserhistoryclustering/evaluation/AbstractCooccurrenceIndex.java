package de.fernuni.browserhistoryclustering.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.discoversites.util.collections.tree.TreeNode;

import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;

/**
 * @author ah
 *
 * Abstract base class for evaluating
 * clusters by comparing document co-occurences
 * in the clustering and a classification.
 */
public abstract class AbstractCooccurrenceIndex {

   protected SameClassDecider m_SameClusterDecider;  
   
   protected int m_TruePositives = 0;
   protected int m_FalseNegatives = 0;   
   protected int m_FalsePositives = 0;   
   protected int m_TrueNegatives = 0;
   
   /**
    * @param p_SameClusterDecider 
    * @param p_Nodes
    */
   public AbstractCooccurrenceIndex(SameClassDecider p_SameClusterDecider,
	   List<TreeNode<ClusterDescriptor>> p_Nodes) {
	m_SameClusterDecider = p_SameClusterDecider;

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
		if (m_SameClusterDecider.belongToSameClass(v_AllFiles.get(i),
		      v_AllFiles.get(j))) {
		   if (v_File2ClusterId.get(v_AllFiles.get(i)).equals(
			   v_File2ClusterId.get(v_AllFiles.get(j)))) {
			m_TruePositives++;
		   } else {
			m_FalseNegatives++;
		   }
		} else {
		   if (v_File2ClusterId.get(v_AllFiles.get(i)).equals(
			   v_File2ClusterId.get(v_AllFiles.get(j)))) {
			m_FalsePositives++;
		   } else {
			m_TrueNegatives++;
		   }
		}
	   }
	}
   }
   
}
