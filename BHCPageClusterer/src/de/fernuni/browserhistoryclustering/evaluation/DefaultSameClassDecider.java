package de.fernuni.browserhistoryclustering.evaluation;

import de.fernuni.browserhistoryclustering.common.utils.Stringutils;

/**
 * @author ah
 * 
 * Implementation of SameClassDecieders.
 * 
 * Assumes two documents belong to the same class
 * if the first three characters of their
 * filenames are identical.
 *
 */
public class DefaultSameClassDecider implements SameClassDecider {

   @Override
   public boolean belongToSameClass(String p_IdOne, String p_IdTwo) {
      if (Stringutils.isNullOrEmpty(p_IdTwo) || Stringutils.isNullOrEmpty(p_IdOne)
            || p_IdOne.length() < 3 || p_IdTwo.length() < 3) {
         return false;
      } else if (p_IdOne.substring(0, 3).equals(p_IdTwo.substring(0, 3))) {
         return true;
      } else {
         return false;
      }

   }

}
