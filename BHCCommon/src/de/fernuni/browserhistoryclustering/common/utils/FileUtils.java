package de.fernuni.browserhistoryclustering.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ah
 *
 */
public class FileUtils {

	final static Charset ENCODING = StandardCharsets.ISO_8859_1;
	
	/**
	 * @param p_DirName Directory
	 * @param p_Extension Only consider files with this extensions
	 * @return Array of Files
	 */
	public static File[] getFiles(String p_DirName, final String p_Extension){
    	File v_Dir = new File(p_DirName);

    	return v_Dir.listFiles(new FilenameFilter() {			
			@Override
			public boolean accept(File v_Dir, String v_Filename) {
				 { return v_Filename.endsWith("." + p_Extension); }
			}
		});
    }
	
	/**
    * @param p_DirName Directory  
    * @return List of subdirectories
    */
   public static List<String> getDirectories(String p_DirName) {
      ArrayList<String> v_Result = new ArrayList<String>();
      File p_File = new File(p_DirName);
      String[] v_Directories = p_File.list(new FilenameFilter() {
         @Override
         public boolean accept(File p_Current, String p_Name) {
            return new File(p_Current, p_Name).isDirectory();
         }
      });
      Collections.addAll(v_Result, v_Directories);
      return v_Result;
   }
	
	
	/**
    * @param p_DirName Directory
    * @param p_Extension Only consider files with this extensions
    * @return List of Filenames
    */
   public static List<String> getFilenames(String p_DirName, final String p_Extension){
      File v_Dir = new File(p_DirName);

      List<String> v_Filenames = new ArrayList<String>();
      File[] v_Files = v_Dir.listFiles(new FilenameFilter() {         
         @Override
         public boolean accept(File v_Dir, String v_Filename) {
             { return v_Filename.endsWith("." + p_Extension); }
         }
      });
      
      for (File v_File : v_Files) {
         v_Filenames.add(v_File.getName());         
      }
      
      return v_Filenames;
    }
	
	/**
	 * @param p_Filename File to be read
	 * @return File Content as String
	 * @throws IOException
	 */
	public static String readTextFile (String p_Filename) throws IOException {
	    Path v_Path = Paths.get(p_Filename);
	    StringBuilder v_StringBuilder = new StringBuilder();
	    try (BufferedReader v_Reader = Files.newBufferedReader(v_Path, ENCODING)){
	      String v_Line = null;
	      while ((v_Line = v_Reader.readLine()) != null) {
	    	  v_StringBuilder.append(v_Line);
	    	  v_StringBuilder.append(System.getProperty("line.separator"));
	      }      
	    }
	    return v_StringBuilder.toString();
	  }
	
   /**
    * @param p_Text
    * @param p_Filename
    * @throws IOException
    */
   public static void saveText(String p_Text, String p_Filename) throws IOException {
      FileWriter v_FileWriter = new FileWriter(p_Filename);
      v_FileWriter.write(p_Text);
      v_FileWriter.close();
   }

   /**
    * @param p_Dir
    */
   public static void createDirIfNotExists(String p_Dir) {
      File v_Dir = new File(p_Dir);

      // if the directory does not exist, create it
      if (!v_Dir.exists())
      { 
        v_Dir.mkdir();
      }      
   }
	
}
