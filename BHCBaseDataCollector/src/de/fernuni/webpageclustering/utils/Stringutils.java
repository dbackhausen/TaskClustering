package de.fernuni.webpageclustering.utils;

import org.apache.commons.lang3.StringUtils;

public class Stringutils {

   public static String getQueryFromTitle(String p_Title) {
      if (p_Title.endsWith(" - Google-Suche")) {
         return p_Title.substring(0,
               p_Title.length() - " - Google-Suche".length());
      } else if (p_Title.endsWith(" - Google Search")) {
         return p_Title.substring(0,
               p_Title.length() - " - Google Search".length());
      } else
         return p_Title;
   }

   public static String formatFilename(String p_Filename) {
      return StringUtils.leftPad(p_Filename, 8, '0') + ".txt";
   }

   public static String formatFilename(String p_Prefix, String p_Filename) {
      if (p_Prefix == null) {
         p_Prefix = "";
      }
      return p_Prefix + StringUtils.leftPad(p_Filename, 8, '0') + ".txt";
   }

}
