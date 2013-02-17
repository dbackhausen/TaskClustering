package de.fernuni.browserhistoryclustering.pageclusterer.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithmDescriptor;
import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;

public class QueryClusterer {
   
   public static void test(String[] args) {
	/*
	 * [[[start:clustering-document-list-intro]]]
	 * 
	 * <div> <p> The easiest way to get started with Carrot2 is to cluster a
	 * collection of {@link org.carrot2.core.Document}s. Each document can
	 * consist of: </p>
	 * 
	 * <ul> <li>document content: a query-in-context snippet, document
	 * abstract or full text,</li> <li>document title: optional, some
	 * clustering algorithms give more weight to document titles,</li>
	 * <li>document URL: optional, used by the {@link
	 * org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm}, ignored
	 * by other algorithms.</li> </ul>
	 * 
	 * <p> To make the example short, the code shown below clusters only 5
	 * documents. Use at least 20 to get reasonable clusters. If you have
	 * access to the query that generated the documents being clustered, you
	 * should also provide it to Carrot2 to get better clusters. </p> </div>
	 * 
	 * [[[end:clustering-document-list-intro]]]
	 */
	{
		// [[[start:clustering-document-list]]]
		/*
		 * A few example documents, normally you would need at least 20 for
		 * reasonable clusters.
		 */
		final String[][] data = new String[][] {
				{
						"http://en.wikipedia.org/wiki/Data_mining",
						"Data mining - Wikipedia, the free encyclopedia",
						"Article about knowledge-discovery in databases (KDD), the practice of automatically searching large stores of data for patterns." },

				{
						"http://www.ccsu.edu/datamining/resources.html",
						"CCSU - Data Mining",
						"A collection of Data Mining links edited by the Central Connecticut State University ... Graduate Certificate Program. Data Mining Resources. Resources. Groups ..." },

				{
						"http://www.kdnuggets.com/",
						"KDnuggets: Data Mining, Web Mining, and Knowledge Discovery",
						"Newsletter on the data mining and knowledge industries, offering information on data mining, knowledge discovery, text mining, and web mining software, courses, jobs, publications, and meetings." },

				{
						"http://en.wikipedia.org/wiki/Data-mining",
						"Data mining - Wikipedia, the free encyclopedia",
						"Data mining is considered a subfield within the Computer Science field of knowledge discovery. ... claim to perform \"data mining\" by automating the creation ..." },

				{
						"http://www.anderson.ucla.edu/faculty/jason.frand/teacher/technologies/palace/datamining.htm",
						"Data Mining: What is Data Mining?",
						"Outlines what knowledge discovery, the process of analyzing data from different perspectives and summarizing it into useful information, can do and how it works." }, };

		/* Prepare Carrot2 documents */
		final ArrayList<Document> documents = new ArrayList<Document>();
		for (String[] row : data) {
			//documents.add(new Document(row[1], row[2], row[0]));
		   documents.add(new Document(row[2]));
		}

		/* A controller to manage the processing pipeline. */
		final Controller controller = ControllerFactory.createSimple();

		/*
		 * Perform clustering by topic using the Lingo algorithm. Lingo can
		 * take advantage of the original query, so we provide it along with
		 * the documents.
		 */
		final ProcessingResult byTopicClusters = controller.process(
				documents, "", STCClusteringAlgorithm.class);
		final List<Cluster> clustersByTopic = byTopicClusters.getClusters();

		/*
		 * Perform clustering by domain. In this case query is not useful,
		 * hence it is null.
		 */
		final ProcessingResult byDomainClusters = controller.process(
				documents, null, ByUrlClusteringAlgorithm.class);
		final List<Cluster> clustersByDomain = byDomainClusters
				.getClusters();
		// [[[end:clustering-document-list]]]
		System.out.print("aaa");
		//ConsoleFormatter.displayClusters(clustersByTopic);
		//ConsoleFormatter.displayClusters(clustersByDomain);
	}
}
   
   

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
	attributes.put(STCClusteringAlgorithmDescriptor.Keys.MIN_BASE_CLUSTER_SCORE, 2);
	attributes.put(STCClusteringAlgorithmDescriptor.Keys.IGNORE_WORD_IF_IN_HIGHER_DOCS_PERCENT, 1);

	final Controller controller = ControllerFactory.createSimple();

	final ProcessingResult byTopicClusters = controller.process(attributes,
	      STCClusteringAlgorithm.class);
	final List<Cluster> v_Clusters = byTopicClusters.getClusters();
	
	List<Document> v_Docs = byTopicClusters.getDocuments();

	for (Cluster v_Cluster : v_Clusters) {
	   if (!v_Cluster.isOtherTopics()) {
		v_Result.add(v_Cluster.getLabel());
	   }
	}
	return v_Result;
   }

}