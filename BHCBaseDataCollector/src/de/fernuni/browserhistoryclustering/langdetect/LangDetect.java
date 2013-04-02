package de.fernuni.browserhistoryclustering.langdetect;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import de.fernuni.browserhistoryclustering.common.config.Config;

/**
 * @author ah
 *
 * Language detection using language-detection
 * library by Nakatani Shuyo 
 * (http://code.google.com/p/language-detection/) 
 */
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

   /**
    * @param text
    * @return Locale string of detected language
    * @throws LangDetectException
    */
   public String detect(String text) throws LangDetectException {
      Detector detector = DetectorFactory.create();
      detector.append(text);
      return detector.detect();
   }

   /**
    * @return LangDetect singleton
    */
   public static LangDetect getInstance() {
      return s_Instance;
   }

}
