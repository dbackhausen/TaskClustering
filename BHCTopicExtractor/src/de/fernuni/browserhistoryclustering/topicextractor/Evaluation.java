/**
 * 
 */

package de.fernuni.browserhistoryclustering.topicextractor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import de.fernuni.browserhistoryclustering.common.utils.FileUtils;

/**
 * @author ah  
 * 
 * Class for evaluating topic extraction,
 * outputs extracted topics for each file.
 */
public class Evaluation {

   static Logger s_Logger = Logger.getLogger("");

   /**
    * @param args
    * @throws Exception
    */
   public static void main(String[] args) throws Exception {

	s_Logger.info("Topic Extraction started.");
	Extractor v_TopicExtractor = new Extractor();

	File[] v_TextFiles = FileUtils.getFiles( "../Data/maui_validation_tika/", "txt");

	for (File v_File : v_TextFiles) {
	   String v_Text = FileUtils.readTextFile(v_File.getCanonicalPath());

	   Map<String, String> v_Stem2Full = new HashMap<String, String>();

	   Map<String, Double> v_Normtopic2Value = v_TopicExtractor
		   .extractNormalizedKeyphrase(v_Text, v_File.getName(),
		         v_Stem2Full);
	  
	   String v_Line = v_File.getName() + ",";
	   for (String v_NormTopic : v_Normtopic2Value.keySet()) {		
		String v_Topic = v_Stem2Full.get(v_NormTopic);
		v_Line = v_Line + v_Topic + ",";		
         }
	   System.err.println(v_Line);
	 
	}

	s_Logger.info("Topic Extraction finished.");
   }
}
