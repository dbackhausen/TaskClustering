package de.fernuni.browserhistoryclustering.common.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.fernuni.browserhistoryclustering.common.utils.Stringutils;





public class Queries2Filenames extends FilePersistanceObject {
	
	private static final long serialVersionUID = 4568684400141184981L;
	private Map<String, Set<String>> m_Queries2Filenames = new HashMap<String, Set<String>>();

	public Queries2Filenames(
         Map<HistoryEntry, Set<HistoryEntry>> p_QueryEntries2PageEntries) {
	   
	   new Queries2Filenames(p_QueryEntries2PageEntries,"");
      
   }
	
	public Queries2Filenames(
			Map<HistoryEntry, Set<HistoryEntry>> p_QueryEntries2PageEntries,
			String p_Prefix) {

		Set<String> v_Filenames;

		for (HistoryEntry v_QueryEntry : p_QueryEntries2PageEntries.keySet()) {
			v_Filenames = new HashSet<String>();
			for (HistoryEntry v_Page : p_QueryEntries2PageEntries
					.get(v_QueryEntry)) {
				v_Filenames.add(Stringutils.formatFilename(p_Prefix, v_Page
						.getId().toString()));
			}

			if (m_Queries2Filenames.get(v_QueryEntry.getQuery()) != null) {
				m_Queries2Filenames.get(v_QueryEntry.getQuery()).addAll(v_Filenames);
			}
			else {
				m_Queries2Filenames.put(v_QueryEntry.getQuery(), v_Filenames);
			}
		}
	}

	public Map<String, Set<String>> get() {
		return m_Queries2Filenames;
	}

	public static Queries2Filenames load(String p_Filename)
			throws ClassNotFoundException, IOException {
		return (Queries2Filenames) FilePersistanceObject.load(p_Filename);
	}

}
