package de.fernuni.browserhistoryclustering.evaluation;

public interface SameClusterDecider {

   boolean belongToSameCluster(String p_IdOne, String p_IdTwo);
   
}
