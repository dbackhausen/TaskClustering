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

import org.apache.hadoop.fs.Path;

import com.discoversites.util.collections.tree.TreeAware;
import com.discoversites.util.collections.tree.TreeNode;

import de.fernuni.browserhistoryclustering.common.utils.CollectionUtils;
import de.fernuni.browserhistoryclustering.common.utils.Stringutils;
import de.fernuni.browserhistoryclustering.exception.ClusterDescriptorException;

public class ClusterDescriptor implements TreeAware {

	TreeNode<?> m_TreeNode;
	private String m_Label;
	private static Map<String, Map<String, Double>> s_Filename2Normtopic2Value;
	private static Map<String, Map<String, String>> s_Filename2Normtopic2Topic;
	private static Map<String, Set<String>> s_Filename2Queries;
	private Map<String, Double> m_Topic2Value;
	private Map<String, Double> m_Normtopic2Value;
	private List<String> m_Documents;
	private List<String> m_FilteredTopics;
	private Set<String> m_HierarchicalCumulatedTopics;
	private Set<String> m_HierarchicalCumulatedNormtopics;
	private Double m_Value = 0d;
	private Path m_VectorsPath;
	private Path m_ClustersPath;
	private Path m_CanopyCentroidsPath;
	private List<String> m_FilteredNormtopics;
	private int s_NumTopicsInLabel = 5;
	private String m_ClusterId;
		
	@XmlElement
	public Collection<ClusterDescriptor> getChildElements()
	{
		Collection<ClusterDescriptor> v_ChildElements = new HashSet<ClusterDescriptor>();
		for(TreeNode<?> v_Node : getNode().getChildren())
		{
			v_ChildElements.add((ClusterDescriptor)v_Node.getElement());
		}
		return v_ChildElements;		
	}

	@XmlTransient
	public List<String> getFilteredTopics() {
		if (m_FilteredTopics == null)
		{
			filterTopicsIntrinsic();
			filterTopicsExtrinsic();						
		}		
		return m_FilteredTopics;
	}
	
	@XmlTransient
	public List<String> getFilteredNormtopics() {
		if (m_FilteredNormtopics == null)
		{
			filterNormtopics_Internal();
			filterNormtopics_Hierarchical();						
		}		
		return m_FilteredNormtopics;
	}
	
	@XmlTransient
	public Set<String> getHierarchicalCumulatedTopics() {
		if (getNode().isRoot())
		{
			return new HashSet<String>();
		}
		
		if (m_HierarchicalCumulatedTopics == null)
		{
			m_HierarchicalCumulatedTopics = new HashSet<String>();
			Set<String> v_ParentTopics = ((ClusterDescriptor)getNode().getParent().getElement()).getHierarchicalCumulatedTopics();
			m_HierarchicalCumulatedTopics.addAll(getFilteredTopics());
			m_HierarchicalCumulatedTopics.addAll(v_ParentTopics);
		}
		return m_HierarchicalCumulatedTopics;				
	}
	
	@XmlTransient
	public Set<String> getHierarchicalCumulatedNormtopics() {
		if (getNode().isRoot())
		{
			return new HashSet<String>();
		}
		
		if (m_HierarchicalCumulatedNormtopics== null)
		{
			m_HierarchicalCumulatedNormtopics = new HashSet<String>();
			Set<String> v_ParentTopics = ((ClusterDescriptor)getNode().getParent().getElement()).getHierarchicalCumulatedNormtopics();
			
			int i = 0;
			Iterator<String> v_FilteredNormtopicsIterator = getFilteredNormtopics().iterator();			
			while (v_FilteredNormtopicsIterator.hasNext() && i < s_NumTopicsInLabel )
			{
				m_HierarchicalCumulatedNormtopics.add(v_FilteredNormtopicsIterator.next());
				i++;
			}
			
			m_HierarchicalCumulatedNormtopics.addAll(v_ParentTopics);
		}
		return m_HierarchicalCumulatedNormtopics;				
	}
	
	@XmlTransient
	public Map<String, Double> getRankedTopics() {
		if (m_Topic2Value == null) {
			m_Topic2Value = calculateTopicsRank();
		}

		return m_Topic2Value;
	}
	
	@XmlTransient
	public Map<String, Double> getRankedNormtopics() {
		if (m_Normtopic2Value == null) {
			m_Normtopic2Value = calculateNormtopicsRank();
		}
		return m_Normtopic2Value;
	}

	public Map<String, Double> calculateNormtopicsRank() {
		Map<String, Double> v_ClusterTopic2Value = new HashMap<String, Double>();
		for (String v_Filename : m_Documents) {
			Map<String, Double> v_Topic2Value = s_Filename2Normtopic2Value
					.get(v_Filename);
			
			if (v_Topic2Value == null) v_Topic2Value = new HashMap<>();

			for (String v_Topic : v_Topic2Value.keySet()) {
				Double v_Value = v_Topic2Value.get(v_Topic);
				if (v_ClusterTopic2Value.containsKey(v_Topic)) {
					v_ClusterTopic2Value.put(v_Topic,
							v_ClusterTopic2Value.get(v_Topic) + v_Value);
				} else {
					v_ClusterTopic2Value.put(v_Topic, v_Value);
				}
			}
			v_ClusterTopic2Value = CollectionUtils.sortByValue(v_ClusterTopic2Value);
		}
		return v_ClusterTopic2Value;
	}
	
	private boolean testInclusion (String v_Probe, Set<String> v_BaseSet)
	{
		boolean v_IsIncluded = false;
		for (String v_Base : v_BaseSet)
		{
			if (Stringutils.testInclusion(v_Probe, v_Base))
			{
				v_IsIncluded = true;
				break;
			}
		}
		return v_IsIncluded;
	}
	
	public void filterNormtopics_Internal()
	{
		m_FilteredNormtopics = new ArrayList<String>();
		Set<String> v_ParentCumulatedNormtopics = ((ClusterDescriptor)getNode().getParent().getElement()).getHierarchicalCumulatedNormtopics();
		if (getNode().isRoot())
		{
			return;
		}		
		// TODO: eff. Implementierung
				
		Iterator<String> v_RankedTopicsIterator =  getRankedNormtopics().keySet().iterator();
		Iterator<String> v_LabelTopicsIterator =  m_FilteredNormtopics.iterator();
		Boolean v_DismissRankedTopic;
		Boolean v_DismissLabelTopic; 
		
		while (m_FilteredNormtopics.size() < s_NumTopicsInLabel && v_RankedTopicsIterator.hasNext())
		{
			v_DismissLabelTopic = false;
			v_DismissRankedTopic = false;
			String v_RankedNormTopic = v_RankedTopicsIterator.next();
			
			for (int i = 0; i < m_FilteredNormtopics.size(); i++)
			{
				String v_LabelTopic = m_FilteredNormtopics.get(i);
				v_DismissRankedTopic = Stringutils.testInclusion(v_RankedNormTopic, v_LabelTopic);
				v_DismissLabelTopic = (Stringutils.testInclusion(v_LabelTopic, v_RankedNormTopic) || testInclusion(v_LabelTopic, v_ParentCumulatedNormtopics));
				
				if(v_DismissLabelTopic)
				{
					m_FilteredNormtopics.remove(v_LabelTopic);
					i--;
				}
				
				if (v_DismissRankedTopic)
				{
					break;
				}				
			}	
			
			if (!v_DismissRankedTopic)
			{	
				m_FilteredNormtopics.add(v_RankedNormTopic);
			}
			
		}
		getHierarchicalCumulatedNormtopics().addAll(m_FilteredNormtopics);
	}
		
	
	public void filterTopicsIntrinsic()
	{
		m_FilteredTopics = new ArrayList<String>();
		if (getNode().isRoot())
		{
			return;
		}		
		// TODO: eff. Implementierung
		for (String v_Probe : getRankedTopics().keySet()) 
		{
			boolean v_IsValid = true;
			for (String v_Base : getRankedTopics().keySet()) 
			{
				if (!v_Probe.equals(v_Base) && Stringutils.testInclusion(v_Probe, v_Base))
				{					
					v_IsValid = false;
				}
			}
			if(v_IsValid)
			{
				m_FilteredTopics.add(v_Probe);
			}
		}
	}
	
	
	public void filterNormtopics_Hierarchical()
	{	
		
		if (getNode().isRoot())
		{
			return;
		}
		
		Set<String> v_ParentCumulatedNormtopics = ((ClusterDescriptor)getNode().getParent().getElement()).getHierarchicalCumulatedNormtopics();
		Set<String> v_RemoveTopics = new HashSet<String>();
		
		for( String v_Topic : m_FilteredNormtopics)
		{
			if(v_ParentCumulatedNormtopics.contains(v_Topic))
			{
				v_RemoveTopics.add(v_Topic);
			}			
			if (testInclusion(v_Topic, v_ParentCumulatedNormtopics))
			{
				v_RemoveTopics.add(v_Topic);
			}						
		}
		
		m_FilteredNormtopics.removeAll(v_RemoveTopics);
		if (v_ParentCumulatedNormtopics != null)
		{
			//getHierarchicalCumulatedNormtopics().addAll(v_ParentCumulatedNormtopics);
		}
		//getHierarchicalCumulatedNormtopics().addAll(m_FilteredNormtopics);
		
	}
	
	
	public void filterTopicsExtrinsic()
	{	
		
		if (getNode().isRoot())
		{
			return;
		}
		
		Set<String> v_ParentCumulatedTopics = ((ClusterDescriptor)getNode().getParent().getElement()).getHierarchicalCumulatedTopics();
		Set<String> v_RemoveTopics = new HashSet<String>();
		
		for( String v_Topic : m_FilteredTopics)
		{
			if(v_ParentCumulatedTopics.contains(v_Topic))
			{
				v_RemoveTopics.add(v_Topic);
			}
			for (String v_Base : v_ParentCumulatedTopics) 
			{
				if (Stringutils.testInclusion(v_Topic, v_Base))
				{
					v_RemoveTopics.add(v_Topic);
				}
			}			
		}
		
		m_FilteredTopics.removeAll(v_RemoveTopics);
		if (v_ParentCumulatedTopics != null)
		{
			m_FilteredTopics.addAll(v_ParentCumulatedTopics);
		}
		getHierarchicalCumulatedTopics().addAll(m_FilteredTopics);			
	}

	public Map<String, Double> calculateTopicsRank() {
		Map<String, Double> v_ClusterNormtopic2Value = new HashMap<String, Double>();
		Map<String, Map<String, Long>> v_Normtopic2Topic2NumOcc = new HashMap<String, Map<String, Long>>();
		for (String v_Filename : m_Documents) {
			Map<String, Double> v_Normtopic2Value = s_Filename2Normtopic2Value
					.get(v_Filename);
			Map<String, String> v_Normtopic2Topic = s_Filename2Normtopic2Topic
					.get(v_Filename);
			
			if (v_Normtopic2Value == null) v_Normtopic2Value = new HashMap<>();
			if (v_Normtopic2Topic == null) v_Normtopic2Topic = new HashMap<>();

			for (String v_Normtopic : v_Normtopic2Value.keySet()) {
				Double v_Value = v_Normtopic2Value.get(v_Normtopic);
				if (v_ClusterNormtopic2Value.containsKey(v_Normtopic)) {
					v_ClusterNormtopic2Value.put(v_Normtopic,
							v_ClusterNormtopic2Value.get(v_Normtopic) + v_Value);
				} else {
					v_ClusterNormtopic2Value.put(v_Normtopic, v_Value);
				}

				String v_Topic = v_Normtopic2Topic.get(v_Normtopic);
				if (v_Normtopic2Topic2NumOcc.containsKey(v_Normtopic)) {
					if (v_Normtopic2Topic2NumOcc.get(v_Normtopic).containsKey(v_Topic))
					{
						Long v_NumOccurrences = v_Normtopic2Topic2NumOcc.get(v_Normtopic).get(v_Topic);
						v_Normtopic2Topic2NumOcc.get(v_Normtopic).put(v_Topic, v_NumOccurrences++);
					}
					else
					{
						v_Normtopic2Topic2NumOcc.get(v_Normtopic).put(v_Topic, 1L);
					}
				}
				else
				{
					v_Normtopic2Topic2NumOcc.put(v_Normtopic, new HashMap<String, Long>());
					v_Normtopic2Topic2NumOcc.get(v_Normtopic).put(v_Topic, 1L);
				}
			}
		}
		
		Map<String, Double> v_ClusterTopic2Value = new HashMap<String, Double>();
		
		for (String v_Normtopic : v_ClusterNormtopic2Value.keySet())
		{
			Map<String, Long> v_Topic2NumOcc = v_Normtopic2Topic2NumOcc.get(v_Normtopic);
			Map<String, Long> v_Sorted_Topic2NumOcc = CollectionUtils.sortByValue(v_Topic2NumOcc);
			v_ClusterTopic2Value.put(v_Sorted_Topic2NumOcc.keySet().iterator().next(), v_ClusterNormtopic2Value.get(v_Normtopic));
		}
		
		v_ClusterTopic2Value = CollectionUtils.sortByValue(v_ClusterTopic2Value);
		
		return v_ClusterTopic2Value;
	}

	@XmlTransient
	public Path getCanopyCentroidsPath() {
		return m_CanopyCentroidsPath;
	}

	public void setCanopyCentroidsPath(Path p_CanopyCentroids) {
		m_CanopyCentroidsPath = p_CanopyCentroids;
	}

	@XmlTransient
	public Path getVectorsPath() {
		return m_VectorsPath;
	}
	
	public void setVectorsPath(Path p_VectorsPath) {
		m_VectorsPath = p_VectorsPath;
	}

	@XmlTransient
	public Path getClustersPath() {
		return m_ClustersPath;
	}

	public void setClustersPath(Path p_ClustersPath) {
		m_ClustersPath = p_ClustersPath;
	}

	@XmlTransient
	public List<String> getDocuments() {
		return m_Documents;
	}

	public void setDocuments(List<String> p_Documents) {
		this.m_Documents = p_Documents;
	}

	@XmlTransient
	public Double getValue() {		
		Collection<?> v_Children = getNode().getChildren();
		if (!v_Children.isEmpty()) {
			for (Object v_Child : v_Children) {
				ClusterDescriptor v_ChildElement = ((TreeNode<ClusterDescriptor>) v_Child)
						.getElement();
				m_Value += v_ChildElement.getValue();
			}
		}
		return m_Value;
	}

	public void setValue(Double p_value) {
		this.m_Value = p_value;
	}

	@SuppressWarnings("unused")
	private ClusterDescriptor() {
	}

	public ClusterDescriptor(String p_Id) throws ClusterDescriptorException  {
		this(p_Id, null, null, null, null);
	}

	public ClusterDescriptor(String p_Id, Path p_VectorsPath, Path p_ClustersPath,
			Path p_CanopyCentroids, List<String> p_Filenames) throws ClusterDescriptorException {
		if (s_Filename2Normtopic2Value == null) {
			throw new ClusterDescriptorException("Filename-Topic-Value Map not initialized!");
		}
		setClusterId(p_Id);
		setVectorsPath(p_VectorsPath);
		setClustersPath(p_ClustersPath);
		setCanopyCentroidsPath(p_CanopyCentroids);
		setDocuments(p_Filenames);

	}

	@Override
	public void setNode(TreeNode<?> p_TreeNode) {

		m_TreeNode = p_TreeNode;
	}

	@XmlTransient
	public TreeNode<?> getNode() {
		return m_TreeNode;
	}

	@XmlElement
	public String getLabel() {
		if (m_Label == null) {
			m_Label = "";
			Map<String, Long> v_Topic2NumOcc = new HashMap<String, Long>();
			List<String> v_LabelParts = new ArrayList<String>();
			
			for (String v_Normtopic : getFilteredNormtopics()) {
				v_Topic2NumOcc.clear();
			for (String v_Filename : getDocuments()) {				
				
				Map<String, String> v_Normtopic2Topic = s_Filename2Normtopic2Topic.get(v_Filename);
								
					String v_Topic = v_Normtopic2Topic.get(v_Normtopic);
					if (v_Topic != null) 
					{
						if (v_Topic2NumOcc.containsKey(v_Topic)) {
							Long v_NumOccurrences = v_Topic2NumOcc.get(v_Topic);
							v_Topic2NumOcc.put(v_Topic, v_NumOccurrences++);
						} else {
							v_Topic2NumOcc.put(v_Topic, 1L);
						}
					}
				}
				Map<String, Long> v_SortedTopics = CollectionUtils.sortByValue(v_Topic2NumOcc);
				if (v_SortedTopics.keySet().size() > 0)
				{
					v_LabelParts.add(v_SortedTopics.keySet().iterator().next());
				}
			}
			m_Label = Stringutils.concatStrings(v_LabelParts, " / ");
		}
		return m_Label;
	}		
	
	public String getClusterId() {
		return m_ClusterId;
	}
	
	public void setClusterId(String p_Id) {
      m_ClusterId = p_Id;
   }
	
	@XmlElement(name="Query")
	@XmlElementWrapper(name="Queries")
	public Set<String> getQueries()
	{
		Set<String> v_Result = new HashSet<String>();
		for (String v_Filename : getDocuments())
		{
			Set<String> v_Queries = s_Filename2Queries.get(v_Filename);
			if (v_Queries != null)
			{
				v_Result.addAll(v_Queries);
			}
		}
		return v_Result;
	}
	
	public static void initialize(
			Map<String, Map<String, Double>> p_Filename2Normtopic2Value,
			Map<String, Map<String, String>> p_Filename2Normtopic2Topic, 
			Map<String, Set<String>> p_Filename2Queries)
			throws Exception {
		assert(p_Filename2Normtopic2Topic != null) : "Filename2Normtopic2Topic map must not be null!";
		assert(p_Filename2Normtopic2Value != null) : "Filename2Normtopic2Value map must not be null!";
		assert(p_Filename2Queries != null) : "Filename2Queries map must not be null!";
		
		if (s_Filename2Normtopic2Value != null) {
			throw new Exception("Filename2Normtopic2Topic map already initialized!");
		}
		s_Filename2Normtopic2Value = p_Filename2Normtopic2Value;
		s_Filename2Normtopic2Topic = p_Filename2Normtopic2Topic;
		s_Filename2Queries = p_Filename2Queries;
	}

}
