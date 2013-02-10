package de.fernuni.browserhistoryclustering.common.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;




public class Normtopic2Topic extends FilePersistanceObject {

	private static final long serialVersionUID = 7644777231341845368L;
	
	private Map<String, Map<String, String>> m_Filename2Normtopic2Topic = new HashMap<String, Map<String,String>>();

	public Normtopic2Topic( Map<String, Map<String, String>> p_Filename2Normtopic2Topic)
	{
		m_Filename2Normtopic2Topic = p_Filename2Normtopic2Topic;
	}

	public Map<String, Map<String, String>> get() {
		return m_Filename2Normtopic2Topic;
	}

	public static Normtopic2Topic load(String p_Filename)
			throws ClassNotFoundException, IOException {
		return (Normtopic2Topic) FilePersistanceObject.load(p_Filename);
	}

}
