/**
 * 
 */

package de.fernuni.browserhistoryclustering.topicextractor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import de.fernuni.browserhistoryclustering.common.config.Config;
import de.fernuni.browserhistoryclustering.common.types.Normtopic2Topic;
import de.fernuni.browserhistoryclustering.common.types.NormtopicValues;
import de.fernuni.browserhistoryclustering.common.utils.FileUtils;

/**
 * @author ah  
 * 
 * Creates mapping data for cluster labels:
 * filename -> normalized topic -> full topic & 
 * filename -> normalized topic -> topic analysis value
 * 
 */
public class Main {

   static Logger s_Logger = Logger.getLogger("");

   /**
    * @param args
    * @throws Exception
    */
   public static void main(String[] args) throws Exception {

	s_Logger.info("Topic Extraction started.");
	Config v_Config = Config.getInstance();
	Extractor v_TopicExtractor = new Extractor();

	File[] v_TextFiles = FileUtils.getFiles(v_Config.getTextPath(), "txt");

	Map<String, Map<String, Double>> v_Filename2Normtopic2Value = new HashMap<String, Map<String, Double>>();
	Map<String, Map<String, String>> v_Filename2Normtopic2Topic = new HashMap<String, Map<String, String>>();

	for (File v_File : v_TextFiles) {
	   String v_Text = FileUtils.readTextFile(v_File.getCanonicalPath());

	   Map<String, String> v_Stem2Full = new HashMap<String, String>();

	   Map<String, Double> v_Normtopic2Value = v_TopicExtractor
		   .extractNormalizedKeyphrase(v_Text, v_File.getName(),
		         v_Stem2Full);
	   
	   v_Filename2Normtopic2Value.put(v_File.getName(), v_Normtopic2Value);
	   v_Filename2Normtopic2Topic.put(v_File.getName(), v_Stem2Full);
	}

	Normtopic2Topic v_Normtopic2Topic = new Normtopic2Topic(
	      v_Filename2Normtopic2Topic);
	v_Normtopic2Topic.save(v_Config.getBaseDir() + v_Config.getDataDir()
	      + v_Config.getNormtopic2TopicFile());

	NormtopicValues v_NormtopicValues = new NormtopicValues(
	      v_Filename2Normtopic2Value);
	v_NormtopicValues.save(v_Config.getBaseDir() + v_Config.getDataDir()
	      + v_Config.getNormtopic2ValueFile());

	s_Logger.info("Topic Extraction finished.");
   }
}
