package de.fernuni.browserhistoryclustering.topicextractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import maui.main.MauiModelBuilder;

import org.wikipedia.miner.model.Wikipedia;

import de.fernuni.browserhistoryclustering.common.config.Config;
import de.fernuni.browserhistoryclustering.common.utils.CollectionUtils;

import weka.core.Instance;

/**
 * TODO: insert javadoc
 * 
 * @author ah
 * 
 */
public class Extractor {

   private MauiTopicExtractor topicExtractor;
   private MauiModelBuilder modelBuilder;

   private final int TOPICSPERDOCUMENT = 10;

   public Extractor() throws Exception {
      Config v_Config = Config.getInstance();
      topicExtractor = new MauiTopicExtractor();
      modelBuilder = new MauiModelBuilder();
      setOptions();
      setFeatures();

      // name of the file to save the model
      String modelName = v_Config.getBaseDir() + v_Config.getDataDir()
            + v_Config.getMauiModelFile();

      // Settings for topic extractor
      topicExtractor.modelName = modelName;

      // Run topic extractor
      topicExtractor.loadModel();
      topicExtractor.setOptions(new String[] { "-l", ".", "-m", modelName, "-v", "none" });
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
    * Set which features to use
    */
   private void setFeatures() {
      modelBuilder.setBasicFeatures(true);
      modelBuilder.setKeyphrasenessFeature(true);
      modelBuilder.setFrequencyFeatures(true);
      modelBuilder.setPositionsFeatures(true);
      modelBuilder.setLengthFeature(true);
      modelBuilder.setNodeDegreeFeature(true);
      modelBuilder.setBasicWikipediaFeatures(false);
      modelBuilder.setAllWikipediaFeatures(false);
   }
  
   public Map<String, Double> extractNormalizedKeyphrase(String p_Text, String p_TextId,
         Map<String, String> p_Norm2RealMap) throws Exception {
      Map<String, Double> v_Topic2ValueMap = new HashMap<String, Double>();
     
      ArrayList<Instance> v_Keyphrases = topicExtractor.extractKeyphrases(p_TextId, p_Text);

      int loopCtr = 0;
      for (Instance v_Keyphrase : v_Keyphrases) {
         Double v_Value = 1d * (v_Keyphrases.size() - loopCtr);
         v_Value = Math.pow(v_Value, 1.3);

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
      }

      v_Topic2ValueMap = CollectionUtils.sortByValue(v_Topic2ValueMap);

      return v_Topic2ValueMap;
   }

}
