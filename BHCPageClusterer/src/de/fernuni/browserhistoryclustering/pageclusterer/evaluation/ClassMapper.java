package de.fernuni.browserhistoryclustering.pageclusterer.evaluation;

import java.util.List;

import de.fernuni.browserhistoryclustering.exception.EvaluationException;

public interface ClassMapper {

   String mapToClass(String p_Filename) throws EvaluationException;
   
   List<String> mapToClass(List<String> p_Filenames) throws EvaluationException;
   
}
