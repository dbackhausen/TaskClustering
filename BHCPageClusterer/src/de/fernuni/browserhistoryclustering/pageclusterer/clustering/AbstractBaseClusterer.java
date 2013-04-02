package de.fernuni.browserhistoryclustering.pageclusterer.clustering;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import de.fernuni.browserhistoryclustering.common.config.Config;
import de.fernuni.browserhistoryclustering.common.types.Filenames2Queries;
import de.fernuni.browserhistoryclustering.common.types.Normtopic2Topic;
import de.fernuni.browserhistoryclustering.common.types.NormtopicValues;

/**
 * @author ah
 *
 * Abstract Base Class for clusterer implementations
 */
public class AbstractBaseClusterer{
   
   protected static Config s_Config;   
   protected static String s_PathPrefix;
   protected static Path s_Tf_Idf;
   protected static Path s_TextPath;
   protected static Configuration s_HadoopConf;
   protected static Map<String, Map<String, Double>> s_Filename2Normtopic2Value;
   protected static Map<String, Map<String, String>> s_Filename2Normtopic2Topic;
   protected static Map<String, Set<String>> s_Filenames2Queries;
  
   
   static void loadConfiguration()
   {
      s_Config = Config.getInstance();
      s_PathPrefix = s_Config.getBaseDir() + s_Config.getDataDir() + "mahout/";
      s_Tf_Idf = new Path(s_PathPrefix, "tfidf/");
      s_TextPath = new Path(s_Config.getTextPath()); 
   }
   
   static void loadMaps() throws ClassNotFoundException, IOException
   {
      NormtopicValues v_NormtopicValues = NormtopicValues.load(s_Config.getBaseDir()
            + s_Config.getDataDir() + s_Config.getNormtopic2ValueFile());
      Normtopic2Topic v_Normtopic2Topic = Normtopic2Topic.load(s_Config.getBaseDir()
            + s_Config.getDataDir() + s_Config.getNormtopic2TopicFile());
      Filenames2Queries v_Filenames2Queries = Filenames2Queries.load(s_Config.getBaseDir()
            + s_Config.getDataDir() + s_Config.getFilename2QueriesFile());
      
      s_Filename2Normtopic2Value = v_NormtopicValues.get();
      s_Filename2Normtopic2Topic = v_Normtopic2Topic.get();
      s_Filenames2Queries = v_Filenames2Queries.get();
   }

}
