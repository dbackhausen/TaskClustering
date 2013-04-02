package de.fernuni.browserhistoryclustering.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.discoversites.util.collections.tree.TreeAware;
import com.discoversites.util.collections.tree.TreeNode;

import de.fernuni.browserhistoryclustering.common.utils.CollectionUtils;
import de.fernuni.browserhistoryclustering.common.utils.Stringutils;
import de.fernuni.browserhistoryclustering.exception.ClusterDescriptorException;
import de.fernuni.browserhistoryclustering.pageclusterer.clustering.QueryClusterer;

/**
 * @author ah
 * 
 */
@XmlType(propOrder = { "clusterId", "label", "documentCount", "tasks",
      "queries", "childElements" })
public class ClusterDescriptor implements TreeAware {

   TreeNode<?> m_TreeNode;
   private String m_Label;
   private static Map<String, Map<String, Double>> s_Filename2Normtopic2Value;
   private static Map<String, Map<String, String>> s_Filename2Normtopic2Topic;
   private static Map<String, Set<String>> s_Filename2Queries;
   private Map<String, Double> m_Normtopic2Value;
   private List<String> m_Documents;
   private Set<String> m_HierarchicalCumulatedNormtopics;
   private List<String> m_FilteredNormtopics;
   private int s_NumTopicsInLabel = 5;
   private String m_ClusterId;

   /**
    * @return Number of Documents in cluster
    */
   @XmlElement(name = "DocumentCount")
   public int getDocumentCount() {
	return getDocuments().size();
   }

   /**
    * @return Suffix tree clustered queries in cluster
    */
   @XmlElement(name = "Task")
   @XmlElementWrapper(name = "Tasks")
   public Collection<String> getTasks() {
	return QueryClusterer.clusterQueries(getQueries());
   }

   /**
    * @return Direct
    */
   @XmlElement(name = "ChildElement")
   public Collection<ClusterDescriptor> getChildElements() {
	Collection<ClusterDescriptor> v_ChildElements = new HashSet<ClusterDescriptor>();
	for (TreeNode<?> v_Node : getNode().getChildren()) {
	   v_ChildElements.add((ClusterDescriptor) v_Node.getElement());
	}
	return v_ChildElements;
   }

   @XmlTransient
   private List<String> getFilteredNormtopics() {
	if (m_FilteredNormtopics == null) {
	   filterNormtopics();
	}
	return m_FilteredNormtopics;
   }

   @XmlTransient
   private Set<String> getHierarchicalCumulatedNormtopics() {
	if (getNode().isRoot()) {
	   return new HashSet<String>();
	}

	if (m_HierarchicalCumulatedNormtopics == null) {
	   m_HierarchicalCumulatedNormtopics = new HashSet<String>();
	   Set<String> v_ParentTopics = ((ClusterDescriptor) getNode()
		   .getParent().getElement()).getHierarchicalCumulatedNormtopics();

	   int i = 0;
	   Iterator<String> v_FilteredNormtopicsIterator = getFilteredNormtopics()
		   .iterator();
	   while (v_FilteredNormtopicsIterator.hasNext()
		   && i < s_NumTopicsInLabel) {
		m_HierarchicalCumulatedNormtopics.add(v_FilteredNormtopicsIterator
		      .next());
		i++;
	   }

	   m_HierarchicalCumulatedNormtopics.addAll(v_ParentTopics);
	}
	return m_HierarchicalCumulatedNormtopics;
   }

   @XmlTransient
   private Map<String, Double> getRankedNormtopics() {
	if (m_Normtopic2Value == null) {
	   m_Normtopic2Value = calculateNormtopicsRank();
	}
	return m_Normtopic2Value;
   }

   private Map<String, Double> calculateNormtopicsRank() {
	Map<String, Double> v_ClusterTopic2Value = new HashMap<String, Double>();
	for (String v_Filename : m_Documents) {
	   Map<String, Double> v_Topic2Value = s_Filename2Normtopic2Value
		   .get(v_Filename);

	   if (v_Topic2Value == null)
		v_Topic2Value = new HashMap<>();

	   for (String v_Topic : v_Topic2Value.keySet()) {
		Double v_Value = v_Topic2Value.get(v_Topic);
		if (v_ClusterTopic2Value.containsKey(v_Topic)) {
		   v_ClusterTopic2Value.put(v_Topic,
			   v_ClusterTopic2Value.get(v_Topic) + v_Value);
		} else {
		   v_ClusterTopic2Value.put(v_Topic, v_Value);
		}
	   }
	   v_ClusterTopic2Value = CollectionUtils
		   .sortByValue(v_ClusterTopic2Value);
	}
	return v_ClusterTopic2Value;
   }

   private boolean testInclusion(String v_Probe, Set<String> v_BaseSet) {
	boolean v_IsIncluded = false;
	for (String v_Base : v_BaseSet) {
	   if (Stringutils.testInclusion(v_Probe, v_Base)) {
		v_IsIncluded = true;
		break;
	   }
	}
	return v_IsIncluded;
   }

   private void filterNormtopics() {
	m_FilteredNormtopics = new ArrayList<String>();
	Set<String> v_ParentCumulatedNormtopics = ((ClusterDescriptor) getNode()
	      .getParent().getElement()).getHierarchicalCumulatedNormtopics();
	if (getNode().isRoot()) {
	   return;
	}
	// TODO: eff. Implementierung
	Iterator<String> v_RankedTopicsIterator = getRankedNormtopics().keySet()
	      .iterator();
	Boolean v_DismissRankedTopic;
	Boolean v_DismissLabelTopic;

	while (m_FilteredNormtopics.size() < s_NumTopicsInLabel
	      && v_RankedTopicsIterator.hasNext()) {
	   v_DismissLabelTopic = false;
	   v_DismissRankedTopic = false;
	   String v_RankedNormTopic = v_RankedTopicsIterator.next();

	   for (int i = 0; i < m_FilteredNormtopics.size(); i++) {
		String v_LabelTopic = m_FilteredNormtopics.get(i);
		v_DismissRankedTopic = (Stringutils.testInclusion(
		      v_RankedNormTopic, v_LabelTopic) || testInclusion(
		      v_RankedNormTopic, v_ParentCumulatedNormtopics));
		v_DismissLabelTopic = (Stringutils.testInclusion(v_LabelTopic,
		      v_RankedNormTopic) || testInclusion(v_LabelTopic,
		      v_ParentCumulatedNormtopics));

		if (v_DismissLabelTopic) {
		   m_FilteredNormtopics.remove(v_LabelTopic);
		   i--;
		}

		if (v_DismissRankedTopic) {
		   break;
		}
	   }

	   if (!v_DismissRankedTopic) {
		m_FilteredNormtopics.add(v_RankedNormTopic);
	   }

	}
	getHierarchicalCumulatedNormtopics().addAll(m_FilteredNormtopics);
   }

   /**
    * @return File names of documents in cluster
    */
   @XmlTransient
   public List<String> getDocuments() {
	return m_Documents;
   }

   /**
    * @param p_Documents
    *           File names of documents in cluster
    */
   public void setDocuments(List<String> p_Documents) {
	this.m_Documents = p_Documents;
   }

   /**
    * No-arg default constructor, required for JAXB
    */
   @SuppressWarnings("unused")   
   private ClusterDescriptor() {
   }

   /**
    * @param p_Id
    *           Cluster ID
    * @param p_Filenames
    *           List with file names of documents in cluster
    * @throws ClusterDescriptorException
    */
   public ClusterDescriptor(String p_Id, List<String> p_Filenames)
	   throws ClusterDescriptorException {
	if (s_Filename2Normtopic2Value == null) {
	   throw new ClusterDescriptorException(
		   "Filename-Topic-Value Map not initialized!");
	}
	setClusterId(p_Id);
	setDocuments(p_Filenames);

   }

   @Override
   public void setNode(TreeNode<?> p_TreeNode) {
	m_TreeNode = p_TreeNode;
   }

   /**
    * @return Tree node containing the cluster descriptor
    */
   @XmlTransient
   public TreeNode<?> getNode() {
	return m_TreeNode;
   }

   /**
    * @return Cluster label, built from dominant topics
    */
   @XmlElement(name = "Label")
   public String getLabel() {
	if (m_Label == null) {
	   m_Label = "";
	   Map<String, Long> v_Topic2NumOcc = new HashMap<String, Long>();
	   List<String> v_LabelParts = new ArrayList<String>();

	   for (String v_Normtopic : getFilteredNormtopics()) {
		v_Topic2NumOcc.clear();
		for (String v_Filename : getDocuments()) {

		   Map<String, String> v_Normtopic2Topic = s_Filename2Normtopic2Topic
			   .get(v_Filename);

		   String v_Topic = v_Normtopic2Topic.get(v_Normtopic);
		   if (v_Topic != null) {
			if (v_Topic2NumOcc.containsKey(v_Topic)) {
			   Long v_NumOccurrences = v_Topic2NumOcc.get(v_Topic);
			   v_Topic2NumOcc.put(v_Topic, ++v_NumOccurrences);
			} else {
			   v_Topic2NumOcc.put(v_Topic, 1L);
			}
		   }
		}
		Map<String, Long> v_SortedTopics = CollectionUtils
		      .sortByValue(v_Topic2NumOcc);
		if (v_SortedTopics.keySet().size() > 0) {
		   v_LabelParts.add(v_SortedTopics.keySet().iterator().next());
		}
	   }
	   m_Label = Stringutils.concatStrings(v_LabelParts, " / ");
	}
	return m_Label;
   }

   /**
    * @return Cluster ID
    */
   public String getClusterId() {
	return m_ClusterId;
   }

   /**
    * @param p_Id
    *           Cluster ID
    */
   public void setClusterId(String p_Id) {
	m_ClusterId = p_Id;
   }

   /**
    * @return List of queries related to the documents inside the cluster
    */
   @XmlElement(name = "Query")
   @XmlElementWrapper(name = "Queries")
   public Set<String> getQueries() {
	Set<String> v_Result = new HashSet<String>();
	for (String v_Filename : getDocuments()) {
	   Set<String> v_Queries = s_Filename2Queries.get(v_Filename);
	   if (v_Queries != null) {
		v_Result.addAll(v_Queries);
	   }
	}
	return v_Result;
   }

   /**
    * @param p_Filename2Normtopic2Value
    * @param p_Filename2Normtopic2Topic
    * @param p_Filename2Queries
    * @throws ClusterDescriptorException
    */
   public static void initialize(
	   Map<String, Map<String, Double>> p_Filename2Normtopic2Value,
	   Map<String, Map<String, String>> p_Filename2Normtopic2Topic,
	   Map<String, Set<String>> p_Filename2Queries)
	   throws ClusterDescriptorException {
	if (p_Filename2Normtopic2Value == null
	      || p_Filename2Normtopic2Topic == null || p_Filename2Queries == null) {
	   throw new ClusterDescriptorException(
		   "Initialization parameters must not be null!");
	}	
	s_Filename2Normtopic2Value = p_Filename2Normtopic2Value;
	s_Filename2Normtopic2Topic = p_Filename2Normtopic2Topic;
	s_Filename2Queries = p_Filename2Queries;
   }

}
