package de.fernuni.browserhistoryclustering.topicextractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weka.core.Instance;
import de.fernuni.browserhistoryclustering.common.config.Config;
import de.fernuni.browserhistoryclustering.common.utils.CollectionUtils;

/**
 * Keyphrase extraction using Maui library.
 * 
 * @author ah
 * 
 */
public class Extractor {

   private MauiTopicExtractor topicExtractor;
   //private MauiModelBuilder modelBuilder;

   private final int TOPICSPERDOCUMENT = 7;

   /**
    * @throws Exception
    */
   public Extractor() throws Exception {
      Config v_Config = Config.getInstance();
      topicExtractor = new MauiTopicExtractor();
      setOptions();      

      // name of the file to save the model
      String modelName = v_Config.getBaseDir() + v_Config.getDataDir()
            + v_Config.getMauiModelFile();

      // Settings for topic extractor
      topicExtractor.modelName = modelName;

      // Run topic extractor
      topicExtractor.loadModel();
      topicExtractor.setOptions(new String[] { "-l", ".", "-m", modelName, "-n", String.valueOf(TOPICSPERDOCUMENT), "-v", "none" });
      topicExtractor.debugMode = true;
   }

   /**
    * Sets general parameters: debugging printout, language specific options
    * like stemmer, stopwords.
    * 
    * @throws Exception
    */
   private void setOptions() {

      topicExtractor.debugMode = false;
      topicExtractor.topicsPerDocument = TOPICSPERDOCUMENT;
      topicExtractor.wikipedia = null;
   }
  
   
   /**
    * @param p_Text Text from which topics are to be extracted
    * @param p_TextId Text-ID
    * @param p_Norm2RealMap out param, stores mapping normalized topic -> full topic
    * @return Mapping normalized topic -> topic analysis value
    * @throws Exception
    */
   public Map<String, Double> extractNormalizedKeyphrase(String p_Text, String p_TextId,
         Map<String, String> p_Norm2RealMap) throws Exception {
      Map<String, Double> v_Topic2ValueMap = new HashMap<String, Double>();
     
      ArrayList<Instance> v_Keyphrases = topicExtractor.extractKeyphrases(p_TextId, p_Text);

      int loopCtr = 1;
      for (Instance v_Keyphrase : v_Keyphrases) {
         Double v_Value = 1d / loopCtr;
         
         String v_TopicNormalized = v_Keyphrase.stringValue(0);

         String v_Topic = v_Keyphrase.stringValue(topicExtractor.getMauiFilter()
               .getOutputFormIndex());

         p_Norm2RealMap.put(v_TopicNormalized, v_Topic);

         if (v_Topic2ValueMap.containsKey(v_TopicNormalized)) {
            v_Topic2ValueMap.put(v_TopicNormalized, v_Topic2ValueMap.get(v_TopicNormalized)
                  + v_Value);
         } else {
            v_Topic2ValueMap.put(v_TopicNormalized, v_Value);
         }
         loopCtr++;
         System.err.println(loopCtr);
      }

      v_Topic2ValueMap = CollectionUtils.sortByValue(v_Topic2ValueMap);

      return v_Topic2ValueMap;
   }

}
