package de.fernuni.browserhistoryclustering.common.types;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class QueryClusters extends FilePersistanceObject {

	private static final long serialVersionUID = 8388358167747816412L;
	private Set<Set<String>> m_QuerySets;

	public QueryClusters(Set<Set<String>> p_QuerySets) {
		m_QuerySets = new HashSet<Set<String>>();
		m_QuerySets = p_QuerySets;
	}
	
	public Set<Set<String>> get()
	{
		return m_QuerySets;
	}

	public static QueryClusters load(String p_Filename)
			throws ClassNotFoundException, IOException {
		return (QueryClusters) FilePersistanceObject.load(p_Filename);
	}

}
