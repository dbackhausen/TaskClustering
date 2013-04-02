package de.fernuni.browserhistoryclustering.common.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author ah
 * 
 * Holds helper methods for collection handling
 *
 */
public class CollectionUtils {

   /**
    * @param map
    * @return Input map sorted by value
    */
   public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
         Map<K, V> map) {
     List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
             map.entrySet());
     Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
         public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
             return (o2.getValue()).compareTo(o1.getValue());
         }
     });

     Map<K, V> result = new LinkedHashMap<K, V>();
     for (Map.Entry<K, V> entry : list) {
         result.put(entry.getKey(), entry.getValue());
     }
     return result;
 }
   
   /**
    * @param p_List
    * @return Input list sorted numerically
    */
   public static List<String> sortNumerically (List<String> p_List)
   {
      Comparator<String> v_Comparator = new Comparator<String>() {

         public int compare(String s1, String s2) {
           return  Integer.valueOf(s1) - Integer.valueOf(s2);
         }
       };      
      Collections.sort(p_List,v_Comparator);
      return p_List;
   }
   
	
}
