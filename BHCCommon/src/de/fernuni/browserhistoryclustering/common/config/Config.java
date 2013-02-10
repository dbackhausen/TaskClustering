package de.fernuni.browserhistoryclustering.common.config;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.ini4j.InvalidFileFormatException;

import de.fernuni.browserhistoryclustering.common.utils.FileUtils;


public class Config {
   
   private static Config s_Config = new Config();
   static String s_BrowserHistoryFile;
   static String s_BaseDir;
   static String s_Filename2QueriesFile;
   static String s_DataDir;
   static String s_HtmlDir;
   static String s_TextDir;
   static String s_HtmlPath;
   static String s_TextPath; 
   static String s_LangprofilesDir;   
   static String s_Normtopic2TopicFile;   
   static String s_Normtopic2ValueFile;
   static String s_MauiModelFile;
   static String s_MauiStopwordsFile;
   static String s_CanopyRanges;
   static Integer s_SubdivisionThreshold;
   static Boolean s_ReuseTFIDF;
   static String s_OutputTreeFilename;
   static String s_OutputXmlFilename;
   
   
   private Config()
   {
      Preferences v_Prefs;
      try {
         v_Prefs = new IniPreferences(new Ini(new File("../config.ini")));
         Preferences v_FilePrefs = v_Prefs.node("filesystem");
         s_BrowserHistoryFile = v_FilePrefs.get("history", "history.sqlite");
         s_Filename2QueriesFile = v_FilePrefs.get("filename2qeries", "file2queries.dat");
         s_Normtopic2TopicFile = v_FilePrefs.get("normtopic2topic", "norm2top.dat");
         s_Normtopic2ValueFile = v_FilePrefs.get("normtopic2value", "norm2val.dat");         
         s_MauiModelFile = v_FilePrefs.get("maui_model", "keyphrextr");
         s_MauiStopwordsFile = v_FilePrefs.get("maui_stopwords", "stopwords_en.txt");         
         s_DataDir = v_FilePrefs.get("datadir", "Data/");
         s_BaseDir = v_FilePrefs.get("basedir", "../");
         s_HtmlDir = v_FilePrefs.get("htmldir", "html/");
         s_TextDir = v_FilePrefs.get("textdir", "text/");
         s_LangprofilesDir = v_FilePrefs.get("langprofilesdir", "langprofiles");
         s_HtmlPath = s_BaseDir + s_DataDir + s_HtmlDir;
         s_TextPath = s_BaseDir + s_DataDir + s_TextDir;
         
         Preferences v_ClusteringPrefs = v_Prefs.node("clustering");         
         s_CanopyRanges = v_ClusteringPrefs.get("canopy_ranges", "0.98 0.94 0.92 0.9 0.8");         
         s_ReuseTFIDF = v_ClusteringPrefs.getBoolean("reuseTFIDF", Boolean.FALSE);
         s_SubdivisionThreshold = v_ClusteringPrefs.getInt("subdivision_threshold", 5);
         
         Preferences v_OutputPrefs = v_Prefs.node("output");         
         s_OutputTreeFilename = v_OutputPrefs.get("txt", "ClusterTree.txt");         
         s_OutputXmlFilename = v_OutputPrefs.get("xml", "Clusters.xml");         
         
         FileUtils.createDirIfNotExists(s_BaseDir + s_DataDir);
         FileUtils.createDirIfNotExists(s_HtmlPath);
         FileUtils.createDirIfNotExists(s_TextPath);
      } catch (InvalidFileFormatException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public static Config getInstance() {
      return s_Config;
   }

   public String getBrowserHistoryFile() {
      return s_BrowserHistoryFile;
   }

   public String getBaseDir() {
      return s_BaseDir;
   }

   public String getFilename2QueriesFile() {
      return s_Filename2QueriesFile;
   }

   public String getDataDir() {
      return s_DataDir;
   }

   public String getHtmlDir() {
      return s_HtmlDir;
   }

   public String getTextDir() {
      return s_TextDir;
   }

   public String getHtmlPath() {
      return s_HtmlPath;
   }

   public String getTextPath() {
      return s_TextPath;
   }

   public String getLangprofilesDir() {
      return s_LangprofilesDir;
   }

   public String getNormtopic2TopicFile() {
      return s_Normtopic2TopicFile;
   }

   public String getNormtopic2ValueFile() {
      return s_Normtopic2ValueFile;
   }

   public String getMauiModelFile() {
      return s_MauiModelFile;
   }

   public String getMauiStopwordsFile() {
      return s_MauiStopwordsFile;
   }  

   public Boolean get_ReuseTFIDF() {
      return s_ReuseTFIDF;
   }

   public String getCanopyRanges() {
      return s_CanopyRanges;
   }
   
   public Integer getSubdivisionThreshold() {
      return s_SubdivisionThreshold;
   }

   public String getOutputTreeFilename() {
      return s_OutputTreeFilename;
   }

   public String getOutputXmlFilename() {
      return s_OutputXmlFilename;
   }
}
