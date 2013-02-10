package de.fernuni.browserhistoryclustering.langdetect;

import java.util.ArrayList;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

import de.fernuni.browserhistoryclustering.common.config.Config;

public class LangDetect {

   private static LangDetect s_Instance = new LangDetect();
   private static String s_LangprofileDir;
   private static String s_DataDir;
   private static String s_BaseDir;

   private LangDetect() {

      try {
         Config v_Config = Config.getInstance();
         s_DataDir = v_Config.getDataDir();
         s_BaseDir = v_Config.getBaseDir();
         s_LangprofileDir = v_Config.getLangprofilesDir();

         String path = (s_BaseDir + s_DataDir + s_LangprofileDir);
         init(path.toString());
      } catch (LangDetectException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private void init(String profileDirectory) throws LangDetectException {
      DetectorFactory.loadProfile(profileDirectory);
   }

   public String detect(String text) throws LangDetectException {
      Detector detector = DetectorFactory.create();
      detector.append(text);
      return detector.detect();
   }

   public ArrayList<Language> detectLangs(String text)
         throws LangDetectException {
      Detector detector = DetectorFactory.create();
      detector.append(text);
      return detector.getProbabilities();
   }

   public static LangDetect getInstance() {
      return s_Instance;
   }

}
