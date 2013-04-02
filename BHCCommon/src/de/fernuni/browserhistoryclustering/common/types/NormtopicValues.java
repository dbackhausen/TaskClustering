package de.fernuni.browserhistoryclustering.common.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;




/**
 * @author ah
 *
 * Container for mapping filename -> (normalized topic -> topic analysis value)
 */
public class NormtopicValues extends FilePersistanceObject {

	private static final long serialVersionUID = 7644777231341845368L;
	
	private Map<String, Map<String, Double>> m_Filename2Topic2Value = new HashMap<String, Map<String,Double>>();

	/**
	 * @param p_Filename2Topic2Value
	 */
	public NormtopicValues( Map<String, Map<String, Double>> p_Filename2Topic2Value)
	{
		m_Filename2Topic2Value = p_Filename2Topic2Value;
	}

	/**
	 * @return Mapping filename -> (normalized topic -> topic analysis value)
	 */
	public Map<String, Map<String, Double>> get() {
		return m_Filename2Topic2Value;
	}

	public static NormtopicValues load(String p_Filename)
			throws ClassNotFoundException, IOException {
		return (NormtopicValues) FilePersistanceObject.load(p_Filename);
	}

}
