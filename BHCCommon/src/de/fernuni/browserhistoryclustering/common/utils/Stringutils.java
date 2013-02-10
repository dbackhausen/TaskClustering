package de.fernuni.browserhistoryclustering.common.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

public class Stringutils {

	public static String getQueryFromTitle(String p_Title) {
		if (p_Title.endsWith(" - Google-Suche")) {
			return p_Title.substring(0,
					p_Title.length() - " - Google-Suche".length());
		} 
		else if (p_Title.endsWith(" - Google Search")) {
			return p_Title.substring(0,
					p_Title.length() - " - Google Search".length());
		}
		else
			return p_Title;
	}

	public static String formatFilename(String p_Filename) {
		return StringUtils.leftPad(p_Filename, 8, '0') + ".txt";
	}
	
	public static String formatFilename(String p_Prefix, String p_Filename) {
		if (p_Prefix == null)
		{
			p_Prefix = "";
		}
		return p_Prefix + StringUtils.leftPad(p_Filename, 8, '0') + ".txt";
	}
	
	public static String concatStrings(Collection<String> p_Strings,
			String p_Separator) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String v_String : p_Strings) {
			sb.append(v_String);
			if (i < p_Strings.size() - 1)
				sb.append(p_Separator);
			i++;
		}
		return sb.toString();
	}
	
	public static boolean testInclusion(String p_Proband, String p_Base)   
   {
      return (p_Base.matches(".*\\b"+p_Proband+"\\b.*"));
   }
	
	
	public static boolean isNullOrEmpty(String p_String)
	{
	   if (p_String == null || p_String.length() < 1)
	   {
	      return true;	      
	   }
	   else
	   {
	      return false;
	   }
	}

	public static ArrayList<Double> parseToDoubles(String p_String, String p_SplitRegex) {
      ArrayList<Double> v_Result = new ArrayList<>();
      String[] v_Split = p_String.split(p_SplitRegex);
      for (int i = 0; i < v_Split.length; i++) {
         v_Result.add(Double.parseDouble(v_Split[i]));         
      }
      return v_Result;
   }

}
