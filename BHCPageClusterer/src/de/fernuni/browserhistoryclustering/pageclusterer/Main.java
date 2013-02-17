package de.fernuni.browserhistoryclustering.pageclusterer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.Pair;
import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.mahout.vectorizer.DictionaryVectorizer;
import org.apache.mahout.vectorizer.DocumentProcessor;
import org.apache.mahout.vectorizer.tfidf.TFIDFConverter;

import de.fernuni.browserhistoryclustering.common.config.Config;
import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;
import de.fernuni.browserhistoryclustering.graph.SetTree;
import de.fernuni.browserhistoryclustering.graph.Util;
import de.fernuni.browserhistoryclustering.pageclusterer.clustering.HierarchicalKMeansClusterer;
import de.fernuni.browserhistoryclustering.pageclusterer.clustering.QueryClusterer;

/**
 * 
 * @author ah
 * 
 */
public class Main {

   static Logger log = Logger.getLogger("");
   private static Configuration s_HadoopConf;
   private static Config s_Config;
   private static int s_MinSupport = 5;
   private static int s_MinDf = 5;
   private static int s_MaxDFPercent = 95;
   private static int s_MaxNGramSize = 2;
   private static int s_MinLLRValue = 50;
   private static int s_ReduceTasks = 1;
   private static int s_ChunkSize = 200;
   private static int s_TFNorm = -1;
   private static int s_TFIDFNorm = 2;
   private static boolean s_SequentialAccessOutput = true;
   private static boolean s_LogNormalize = false;
   private static boolean s_NamedVectors = true;

   /**
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {

	log.setLevel(Level.INFO);

	Configuration v_HadoopConf = new Configuration();
	s_HadoopConf = v_HadoopConf;
	s_Config = Config.getInstance();
	String v_PathPrefix = s_Config.getBaseDir() + s_Config.getDataDir()
	      + "mahout/";
	String v_TextDir = s_Config.getTextPath();

	Path m_DocumentDir = new Path(v_TextDir);
	Path m_SequenceDir = new Path(v_PathPrefix, "sequence/");
	Path m_TokensDir = new Path(v_PathPrefix, "tokens");
	Path m_TF = new Path(v_PathPrefix, "termfreq/");
	String m_VecFolder = "Vectors";
	Path m_tf_idf = new Path(v_PathPrefix, "tfidf/");

	boolean m_Sequential = true;

	HadoopUtil.delete(v_HadoopConf, new Path(v_PathPrefix, "clusters/"));

	if (!s_Config.get_ReuseTFIDF()) {
	   createTFIDF(v_HadoopConf, m_DocumentDir, m_SequenceDir, m_TokensDir,
		   m_TF, m_VecFolder, m_tf_idf);
	}

	HierarchicalKMeansClusterer v_Hkmc = new HierarchicalKMeansClusterer();
	SetTree<ClusterDescriptor> v_Tree = v_Hkmc
	      .run(s_HadoopConf, m_Sequential);

	saveAsTree(v_Tree);

	saveAsXml(v_Tree);
   }

   private static void createTFIDF(Configuration p_HadoopConf,
	   Path p_DocumentDir, Path p_SequenceDir, Path p_TokensDir, Path p_TF,
	   String p_VecFolder, Path p_tf_idf) throws IOException, Exception,
	   ClassNotFoundException, InterruptedException {

	log.info(new Date() + " - Creating TF-IDF vectors started.");

	HadoopUtil.delete(p_HadoopConf, p_SequenceDir);
	HadoopUtil.delete(p_HadoopConf, p_TokensDir);
	HadoopUtil.delete(p_HadoopConf, p_TF);

	createSequence(p_DocumentDir.toString(), p_SequenceDir.toString());
	createTokens(p_SequenceDir, p_TokensDir, s_HadoopConf);
	createTFVectors(p_TokensDir, p_TF, p_VecFolder);
	createTFIDFVectors(new Path(p_TF, p_VecFolder), p_tf_idf, s_HadoopConf);

	log.info("Creating TF-IDF vectors finished.");
   }

   private static void saveAsXml(SetTree<ClusterDescriptor> v_Tree)
	   throws FileNotFoundException {
	try {

	   PrintStream v_PS = new PrintStream(s_Config.getOutputXmlFilename());

	   JAXBContext jaxbContext = JAXBContext.newInstance(SetTree.class);
	   Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

	   // output pretty printed
	   jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	   jaxbMarshaller.marshal(v_Tree, v_PS);

	} catch (JAXBException e) {
	   e.printStackTrace();
	}
   }

   private static void saveAsTree(SetTree<ClusterDescriptor> v_Tree)
	   throws FileNotFoundException {
	PrintStream v_PS = new PrintStream(s_Config.getOutputTreeFilename());
	v_PS.println("Canopy T2 values: " + s_Config.getCanopyRanges());
	v_PS.println();
	Util.prettyPrintFiltered(v_Tree, v_PS);
   }

   private static void createSequence(String p_InputPath, String p_OutputPath)
	   throws Exception {
	// String v_args[] = new String[] { "-c", "UTF-8", "-i", p_InputPath,
	String v_args[] = new String[] { "-i", p_InputPath, "-o", p_OutputPath };
	ToolRunner.run(new SequenceFilesFromDirectory(), v_args);

   }

   private static void createTokens(Path p_InputDir, Path p_OutputDir,
	   Configuration p_Conf) throws ClassNotFoundException, IOException,
	   InterruptedException {
	MyAnalyzer analyzer = new MyAnalyzer();
	DocumentProcessor.tokenizeDocuments(p_InputDir, analyzer.getClass()
	      .asSubclass(Analyzer.class), p_OutputDir, p_Conf);
   }

   private static void createTFVectors(Path p_Input, Path p_Output,
	   String p_TFVectorsFolderName) throws ClassNotFoundException,
	   IOException, InterruptedException {
	DictionaryVectorizer.createTermFrequencyVectors(p_Input, p_Output,
	      p_TFVectorsFolderName, s_HadoopConf, s_MinSupport, s_MaxNGramSize,
	      s_MinLLRValue, s_TFNorm, s_LogNormalize, s_ReduceTasks,
	      s_ChunkSize, s_SequentialAccessOutput, s_NamedVectors);
   }

   private static void createTFIDFVectors(Path p_InputDir, Path p_OutputDir,
	   Configuration p_Conf) throws ClassNotFoundException, IOException,
	   InterruptedException {
	Pair<Long[], List<Path>> dfData = TFIDFConverter.calculateDF(p_InputDir,
	      p_OutputDir, p_Conf, s_ChunkSize);
	TFIDFConverter.processTfIdf(p_InputDir, p_OutputDir, p_Conf, dfData,
	      s_MinDf, s_MaxDFPercent, s_TFIDFNorm, true,
	      s_SequentialAccessOutput, true, s_ReduceTasks);

   }

}
