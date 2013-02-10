package de.fernuni.browserhistoryclustering.evaluation;

import de.fernuni.browserhistoryclustering.common.utils.Stringutils;

public class DefaultSameClusterDecider implements SameClusterDecider {

   @Override
   public boolean belongToSameCluster(String p_IdOne, String p_IdTwo) {
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
