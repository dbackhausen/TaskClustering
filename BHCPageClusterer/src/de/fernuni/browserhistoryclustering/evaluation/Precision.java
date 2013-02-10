package de.fernuni.browserhistoryclustering.evaluation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.fernuni.browserhistoryclustering.common.utils.CollectionUtils;
import de.fernuni.browserhistoryclustering.exception.EvaluationException;
import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;

public class Precision {

   ClassMapper m_ClassMapper;

   @SuppressWarnings("unused")
   private Precision() {
   }

   public Precision(ClassMapper p_ClassMapper) {
      m_ClassMapper = p_ClassMapper;
   }

   public double calculate(List<String> p_Files, ClusterDescriptor p_ClusterDescriptor) throws EvaluationException {

      Map<String, Integer> v_Class2OccCollection = getClassOccurrences(p_Files);

      List<String> v_FilesOfCluster = p_ClusterDescriptor.getDocuments();
      Map<String, Integer> v_Class2OccCluster = getClassOccurrences(v_FilesOfCluster);

      String v_DominantClass = v_Class2OccCluster.keySet().iterator().next();

      Integer v_NumDocsOfClassInCluster = v_Class2OccCluster.get(v_DominantClass);
      Integer v_NumDocsOfClassInCollection = v_Class2OccCollection.get(v_DominantClass);

      return (double) v_NumDocsOfClassInCollection / (double) v_NumDocsOfClassInCluster;
   }

   private Map<String, Integer> getClassOccurrences(List<String> p_Filenames) throws EvaluationException {
      
      Map<String, Integer> v_Class2Occ = new HashMap<String, Integer>();
      List<String> v_MappedClusterFiles = m_ClassMapper.mapToClass(p_Filenames);
      Set<String> v_Classes = getClasses(p_Filenames);
      
      for (String v_Class : v_Classes) {
         int v_Occurrences = Collections.frequency(v_MappedClusterFiles, v_Class);
         v_Class2Occ.put(v_Class, v_Occurrences);
      }

      return CollectionUtils.sortByValue(v_Class2Occ);
   }
   
   private Set<String> getClasses (List<String> p_Filenames) throws EvaluationException
   {
      return new HashSet<String>(m_ClassMapper.mapToClass(p_Filenames));
   }
   
   
}
