package de.fernuni.browserhistoryclustering.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ToolRunner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.PathFilters;
import org.apache.mahout.common.iterator.sequencefile.PathType;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileDirIterable;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.mahout.utils.SequenceFileDumper;
import org.apache.mahout.utils.clustering.ClusterDumper;
import org.apache.mahout.vectorizer.DocumentProcessor;

import de.fernuni.browserhistoryclustering.pageclusterer.MyAnalyzer;

public class ClusterUtil {
   
   public static void dumpSequence(Configuration p_Conf, Path p_Path) {
      SequenceFileDumper v_SeqDumper = new SequenceFileDumper();
      v_SeqDumper.setConf(p_Conf);
      String[] v_args = new String[] { "-i", p_Path.toString() };
      try {
         v_SeqDumper.run(v_args);
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void dumpCluster(Configuration p_Conf, Path p_SeqFileDir, Path p_PointsDir,
         Path p_OutputFile) {
      ClusterDumper v_ClusterDumper = new ClusterDumper(p_SeqFileDir, p_PointsDir);
      String[] v_Args = { "-of", "CSV", "-i", p_SeqFileDir.toString(), "-p",
            p_PointsDir.toString(), "-o", p_OutputFile.toString() };
      v_ClusterDumper.setConf(p_Conf);
      try {
         v_ClusterDumper.run(v_Args);
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      Map<Integer, List<WeightedVectorWritable>> clusterIdToPoints = v_ClusterDumper
            .getClusterIdToPoints();
   }

   public static Path getFinalClusterPath(Path p_ParentDirPath) {
      File v_Dir = new File(p_ParentDirPath.toString());
      FileFilter v_FileFilter = new WildcardFileFilter("clusters-*-final");
      File[] v_Files = v_Dir.listFiles(v_FileFilter);
      if (v_Files != null && v_Files.length > 0) {
         return new Path(v_Files[0].toString());
      } else {
         return null;
      }
   }

   public static int getNumberOfSubdirs(Path p_ParentDirPath) {
      int v_NumDirs = 0;
      File v_Dir = new File(p_ParentDirPath.toString());
      File[] v_Files = v_Dir.listFiles();
      for (File v_File : v_Files) {
         if (v_File.isDirectory()) {
            v_NumDirs++;
         }
      }
      return v_NumDirs;
   }

   public static Map<Integer, List<String>> getFilesPerCluster(Configuration p_Configuration,
         Path p_ClusterPath) throws IOException {

      FileSystem v_FileSystem = null;
      Map<Integer, List<String>> v_ClusterId2FileList = new HashMap<Integer, List<String>>();

      v_FileSystem = FileSystem.get(p_Configuration);

      List<String> v_FileNames = new ArrayList<String>();
      SequenceFile.Reader reader = null;

      reader = new SequenceFile.Reader(v_FileSystem, new Path(p_ClusterPath,
            Cluster.CLUSTERED_POINTS_DIR + "/part-m-0"), p_Configuration);

      IntWritable v_Key = new IntWritable();
      WeightedVectorWritable v_Value = new WeightedVectorWritable();

      while (reader.next(v_Key, v_Value)) {
         NamedVector v_Vector = (NamedVector) v_Value.getVector();
         String v_FilenameSansSlash = v_Vector.getName().substring(1);

         if (v_ClusterId2FileList.containsKey(v_Key.get())) {
            v_ClusterId2FileList.get(v_Key.get()).add(v_FilenameSansSlash);
         } else {
            v_ClusterId2FileList.put(v_Key.get(),
                  new ArrayList<String>(Collections.singletonList(v_FilenameSansSlash)));
         }

         // System.out.println(v_Key.toString() + " belongs to cluster " +
         // v_Value.toString());

      }

      return v_ClusterId2FileList;
   }

   public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
      List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
      Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
         public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
            return (o2.getValue()).compareTo(o1.getValue());
         }
      });

      Map<K, V> result = new LinkedHashMap<K, V>();
      for (Map.Entry<K, V> entry : list) {
         result.put(entry.getKey(), entry.getValue());
      }
      return result;
   }

   public static void createSequence(String p_InputPath, String p_OutputPath) throws Exception {
      // String v_args[] = new String[] { "-c", "UTF-8", "-i", p_InputPath,
      String v_args[] = new String[] { "-i", p_InputPath, "-o", p_OutputPath };
      ToolRunner.run(new SequenceFilesFromDirectory(), v_args);

   }

   public static void createTokens(Path p_InputDir, Path p_OutputDir, Configuration p_Conf)
         throws ClassNotFoundException, IOException, InterruptedException {
      MyAnalyzer analyzer = new MyAnalyzer();
      DocumentProcessor.tokenizeDocuments(p_InputDir, analyzer.getClass()
            .asSubclass(Analyzer.class), p_OutputDir, p_Conf);
   }

   /*
   private static void createTFVectors(Path p_Input, Path p_Output, String p_TFVectorsFolderName)
         throws ClassNotFoundException, IOException, InterruptedException {
      DictionaryVectorizer.createTermFrequencyVectors(p_Input, p_Output, p_TFVectorsFolderName,
            s_HadoopConf, s_MinSupport, s_MaxNGramSize, s_MinLLRValue, s_TFNorm, s_LogNormalize,
            s_ReduceTasks, s_ChunkSize, s_SequentialAccessOutput, s_NamedVectors);
   }

   private static void createTFIDFVectors(Path p_InputDir, Path p_OutputDir, Configuration p_Conf)
         throws ClassNotFoundException, IOException, InterruptedException {
      Pair<Long[], List<Path>> dfData = TFIDFConverter.calculateDF(p_InputDir, p_OutputDir, p_Conf,
            s_ChunkSize);
      TFIDFConverter.processTfIdf(p_InputDir, p_OutputDir, p_Conf, dfData, s_MinDf, s_MaxDFPercent,
            -1, true, s_SequentialAccessOutput, true, s_ReduceTasks);

   }
   */
   
   public static void process(Configuration p_Conf, Path clusteredPoints) throws IOException {
      
      for (Pair<?,WeightedVectorWritable> record : 
           new SequenceFileDirIterable<Writable,WeightedVectorWritable>(clusteredPoints,
                                                                        PathType.GLOB,
                                                                        PathFilters.partFilter(),
                                                                        null,
                                                                        false,
                                                                        p_Conf)) {
        String clusterId = record.getFirst().toString().trim();
       WeightedVectorWritable a = record.getSecond();
      }      
    }

}
