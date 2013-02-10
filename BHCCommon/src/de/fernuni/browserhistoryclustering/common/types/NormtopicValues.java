package de.fernuni.browserhistoryclustering.common.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;




public class NormtopicValues extends FilePersistanceObject {

	private static final long serialVersionUID = 7644777231341845368L;
	
	private Map<String, Map<String, Double>> m_Filename2Topic2Value = new HashMap<String, Map<String,Double>>();

	public NormtopicValues( Map<String, Map<String, Double>> p_Filename2Topic2Value)
	{
		m_Filename2Topic2Value = p_Filename2Topic2Value;
	}

	public Map<String, Map<String, Double>> get() {
		return m_Filename2Topic2Value;
	}

	public static NormtopicValues load(String p_Filename)
			throws ClassNotFoundException, IOException {
		return (NormtopicValues) FilePersistanceObject.load(p_Filename);
	}

}
