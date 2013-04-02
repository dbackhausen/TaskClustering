package de.fernuni.browserhistoryclustering.evaluation;

/**
 * @author ah
 *
 * Interface for deciding whether two documents 
 * belong to the same class or not.
 * 
 */
public interface SameClassDecider {

   /**
    * @param p_IdOne
    * @param p_IdTwo
    * @return true, if p_IdOne and p_IdTwo denote two documents belonging to the same class. Else false. 
    * 
    */
   boolean belongToSameClass(String p_IdOne, String p_IdTwo);
   
}
