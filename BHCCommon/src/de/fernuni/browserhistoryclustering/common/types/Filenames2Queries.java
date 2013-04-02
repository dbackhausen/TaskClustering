package de.fernuni.browserhistoryclustering.common.types;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author ah
 *
 * Mapping filenames -> search queries
 */
public class Filenames2Queries extends FilePersistanceObject {
	
	private static final long serialVersionUID = -7479090752038521417L;
	
	/**
	 * 
	 */
	private Map<String, Set<String>> m_Filenames2Queries = new HashMap<String, Set<String>>();
	
	/**
	 * @param p_Queries2Filenames
	 */
	public Filenames2Queries(Queries2Filenames p_Queries2Filenames) {
		
		Map<String, Set<String>> v_Queries2Filenames = p_Queries2Filenames.get();
		
		for (String v_Query :  v_Queries2Filenames.keySet())
		{
			for (String v_Filename : v_Queries2Filenames.get(v_Query))
			{
				if (m_Filenames2Queries.containsKey(v_Filename))
				{
					m_Filenames2Queries.get(v_Filename).add(v_Query);
				}
				else
				{
					m_Filenames2Queries.put(v_Filename, new HashSet<String>(Collections.singleton(v_Query)));
				}
			}
		}
	}
	
	/**
	 * @param p_Filename
	 * @return Instance of Filename2Queries mapping
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Filenames2Queries load (String p_Filename) throws ClassNotFoundException, IOException
	{		
		return (Filenames2Queries) FilePersistanceObject.load(p_Filename);
	}
	
	/**
	 * @return Mapping Filename -> Search queries
	 */
	public Map<String, Set<String>> get() {
		return m_Filenames2Queries;
	}
	

}
