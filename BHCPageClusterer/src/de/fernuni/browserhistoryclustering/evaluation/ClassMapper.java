package de.fernuni.browserhistoryclustering.evaluation;

import java.util.List;

import de.fernuni.browserhistoryclustering.exception.EvaluationException;

/**
 * @author ah
 *
 */
public interface ClassMapper {

   /**
    * @param p_Filename
    * @return Class the Filename maps to
    * @throws EvaluationException
    */
   String mapToClass(String p_Filename) throws EvaluationException;
   
   /**
    * @param p_Filenames
    * @return List of CLasses the filenames map to
    * @throws EvaluationException
    */
   List<String> mapToClass(List<String> p_Filenames) throws EvaluationException;
   
}
