package de.fernuni.browserhistoryclustering.pageclusterer;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
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
import de.fernuni.browserhistoryclustering.evaluation.DefaultSameClassDecider;
import de.fernuni.browserhistoryclustering.evaluation.FMeasure;
import de.fernuni.browserhistoryclustering.evaluation.JaccardIndex;
import de.fernuni.browserhistoryclustering.evaluation.Precision;
import de.fernuni.browserhistoryclustering.evaluation.RandIndex;
import de.fernuni.browserhistoryclustering.evaluation.Recall;
import de.fernuni.browserhistoryclustering.graph.ClusterDescriptor;
import de.fernuni.browserhistoryclustering.graph.SetTree;
import de.fernuni.browserhistoryclustering.pageclusterer.clustering.HierarchicalKMeansClusterer;

/**
 * 
 * @author ah
 * 
 */
public class Evaluation {

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

	HadoopUtil.delete(v_HadoopConf, new Path(v_PathPrefix, "0/"));
	
	createTFIDF(v_HadoopConf, m_DocumentDir, m_SequenceDir, m_TokensDir,
		m_TF, m_VecFolder, m_tf_idf);
	s_Config.setReuseTfidf(true);
		
	HierarchicalKMeansClusterer v_Hkmc;
	ArrayList<EvalStruct> v_EvalStructs = new ArrayList<EvalStruct>();
	
	for (Double v_Range = 1.0001d; v_Range >= 0.899d; v_Range = v_Range - 0.005d)
	{
	   v_HadoopConf = new Configuration();
	   v_Hkmc = new HierarchicalKMeansClusterer();
	   s_Config.setCanopyRanges(v_Range.toString());
	   SetTree<ClusterDescriptor> v_Tree = v_Hkmc
		      .run(s_HadoopConf, m_Sequential);
	   
	   RandIndex v_RandIndex = new RandIndex(new DefaultSameClassDecider(), new ArrayList<>( v_Tree.getNodes(1)));
	   Double v_RandIndexValue = v_RandIndex.calculate();
		
	   JaccardIndex v_JaccardIndex = new JaccardIndex(new DefaultSameClassDecider(), new ArrayList<>( v_Tree.getNodes(1)));
	   Double v_JaccardIndexValue = v_JaccardIndex.calculate();
	   
	   Precision v_Precision = new Precision(new DefaultSameClassDecider(), new ArrayList<>( v_Tree.getNodes(1)));
	   Double v_PrecisionValue = v_Precision.calculate();
	   
	   Recall v_Recall = new Recall (new DefaultSameClassDecider(), new ArrayList<>( v_Tree.getNodes(1)));
	   Double v_RecallValue = v_Recall.calculate();
	   
	   Double v_FMeasureValue = FMeasure.calculate(new DefaultSameClassDecider(), new ArrayList<>( v_Tree.getNodes(1)), 1d);
	   
	   EvalStruct v_EvalStruct = new EvalStruct();
	   v_EvalStruct.m_CanopyRange = v_Range;
	   v_EvalStruct.m_numClusters = v_Tree.getNodes(1).size();
	   v_EvalStruct.m_JaccardIndex = v_JaccardIndexValue;
	   v_EvalStruct.m_RandIndex = v_RandIndexValue;
	   v_EvalStruct.m_Precision = v_PrecisionValue;
	   v_EvalStruct.m_Recall = v_RecallValue;
	   v_EvalStruct.m_FMeasure = v_FMeasureValue;
	   
	   v_EvalStructs.add(v_EvalStruct);
	   
	   v_Tree = null;
	   v_Hkmc = null;
	   v_HadoopConf = null;
	   gc();
	   
	   //FileUtils.forceDelete(new File("D:/Projekte/GitHub/TaskClustering/Data/mahout/0/cl/0/1/clusters-1-final/_policy"));
	   
	   
	   HadoopUtil.delete(v_HadoopConf, new Path(v_PathPrefix, "0/"));	   
	   FileUtils.deleteDirectory(new File( "D:/Projekte/GitHub/TaskClustering/Data/mahout/0/"));
	   
	}
	
	for (EvalStruct v_EvalStruct : v_EvalStructs) {
	   v_EvalStruct.print();
      }
	
	
	int stop = 0;
	System.exit(stop);
	

	//saveAsTree(v_Tree);

	//saveAsXml(v_Tree);
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
   
   private static void gc() {
	     Object obj = new Object();
	     WeakReference<Object> ref = new WeakReference<Object>(obj);
	     obj = null;
	     while(ref.get() != null) {
	       System.gc();
	     }
	   }
   



}

class EvalStruct {   
   public double m_CanopyRange;
   public int m_numClusters;
   public double m_RandIndex;
   public double m_JaccardIndex;
   public double m_Precision;
   public double m_Recall;
   public double m_FMeasure;
   
   public void print(){
	System.out.println(formatDouble(m_CanopyRange) + ";"
                           + m_RandIndex + ";" 
                           + m_FMeasure + ";"
                           + m_JaccardIndex + ";" 
                           + m_Precision+ ";" 
                           + m_Recall + ";" 
                           + formatDouble(m_CanopyRange) + ";" 
                           + m_numClusters  
                          );
   }
   
   private String formatDouble(double p_Value){
	String shortString = "";
	DecimalFormat threeDec = new DecimalFormat("0.000", new
	DecimalFormatSymbols(Locale.US));
	shortString = (threeDec.format(p_Value));
	return shortString;
	}
   
}
