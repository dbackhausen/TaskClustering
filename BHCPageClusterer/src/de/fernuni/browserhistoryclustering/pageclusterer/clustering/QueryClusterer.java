package de.fernuni.browserhistoryclustering.pageclusterer.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithmDescriptor;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;

/**
 * @author ah
 *
 */
public class QueryClusterer {

   /**
    * @param p_Queries
    * @return Suffix tree clustering result of input string set  
    */
   public static Set<String> clusterQueries(Set<String> p_Queries) {

	Set<String> v_Result = new HashSet<String>();

	final ArrayList<Document> documents = new ArrayList<Document>();

	for (String v_Query : p_Queries) {
	   documents.add(new Document(v_Query, v_Query));
	}

	/* Prepare attribute map */
	final Map<String, Object> attributes = new HashMap<String, Object>();

	/* Put attribute values using direct keys. */
	attributes.put(CommonAttributesDescriptor.Keys.DOCUMENTS, documents);
	attributes.put(STCClusteringAlgorithmDescriptor.Keys.OPTIMAL_PHRASE_LENGTH, 2);
	attributes.put(STCClusteringAlgorithmDescriptor.Keys.MIN_BASE_CLUSTER_SCORE, 1);
	attributes.put(STCClusteringAlgorithmDescriptor.Keys.IGNORE_WORD_IF_IN_FEWER_DOCS, 2);
	attributes.put(STCClusteringAlgorithmDescriptor.Keys.IGNORE_WORD_IF_IN_HIGHER_DOCS_PERCENT, 1);

	final Controller controller = ControllerFactory.createSimple();

	final ProcessingResult byTopicClusters = controller.process(attributes,
	      STCClusteringAlgorithm.class);
	final List<Cluster> v_Clusters = byTopicClusters.getClusters();
	
	for (Cluster v_Cluster : v_Clusters) {
	   if (!v_Cluster.isOtherTopics()) {
		v_Result.add(v_Cluster.getLabel());
	   }
	}
	return v_Result;
   }

}