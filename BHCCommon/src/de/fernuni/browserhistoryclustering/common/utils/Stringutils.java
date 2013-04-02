package de.fernuni.browserhistoryclustering.common.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ah
 *
 * Holds helper methods for string handling
 */
public class Stringutils {

	/**
	 * @param p_Title
	 * @return Google search query
	 */
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

	/**
	 * @param p_Filename
	 * @return Filename padded with zeros to 8 characters length
	 */
	public static String formatFilename(String p_Filename) {
		return StringUtils.leftPad(p_Filename, 8, '0') + ".txt";
	}
	
	/**
	 * @param p_Prefix
	 * @param p_Filename 
	 * @return Prefix + filename padded with zeros to 8 characters length
	 */
	public static String formatFilename(String p_Prefix, String p_Filename) {
		if (p_Prefix == null)
		{
			p_Prefix = "";
		}
		return p_Prefix + StringUtils.leftPad(p_Filename, 8, '0') + ".txt";
	}
	
	/**
	 * @param p_Strings
	 * @param p_Separator
	 * @return Concatenated string
	 */
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
	
	/**
	 * @param p_Proband
	 * @param p_Base
	 * @return true if p_Base contains p_Proband, else false. 
	 */
	public static boolean testInclusion(String p_Proband, String p_Base)   
   {
      return (p_Base.matches(".*\\b"+p_Proband+"\\b.*"));
   }
	
	
	/**
	 * @param p_String
	 * @return true if p_String is null or empty, else false
	 */
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

	/**
	 * @param p_String
	 * @param p_SplitRegex
	 * @return List of Double values contained in p_String 
	 */
	public static ArrayList<Double> parseToDoubles(String p_String, String p_SplitRegex) {
      ArrayList<Double> v_Result = new ArrayList<>();
      String[] v_Split = p_String.split(p_SplitRegex);
      for (int i = 0; i < v_Split.length; i++) {
         v_Result.add(Double.parseDouble(v_Split[i]));         
      }
      return v_Result;
   }

}
