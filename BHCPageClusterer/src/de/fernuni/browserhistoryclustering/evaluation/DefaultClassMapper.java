package de.fernuni.browserhistoryclustering.evaluation;

import java.util.ArrayList;
import java.util.List;

import de.fernuni.browserhistoryclustering.common.utils.Stringutils;
import de.fernuni.browserhistoryclustering.exception.EvaluationException;

public class DefaultClassMapper implements ClassMapper {

   @Override
   public String mapToClass(String p_Filename) throws EvaluationException {
      if (!Stringutils.isNullOrEmpty(p_Filename) && p_Filename.length() > 2) {
         return p_Filename.substring(0, 3);
      } else
         throw new EvaluationException("Could not map Filename to class.");

   }

   @Override
   public List<String> mapToClass(List<String> p_Filenames) throws EvaluationException {
      List<String> v_MappedFilenames = new ArrayList<String>();
      for (String v_Filename : p_Filenames) {
         v_MappedFilenames.add(mapToClass(v_Filename));
      }
      return v_MappedFilenames;
   }

}
