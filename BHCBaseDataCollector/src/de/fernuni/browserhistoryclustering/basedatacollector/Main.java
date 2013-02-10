/**
 * 
 */

package de.fernuni.browserhistoryclustering.basedatacollector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.ini4j.InvalidFileFormatException;

import de.fernuni.browserhistoryclustering.basedatacollector.web.Crawler;
import de.fernuni.browserhistoryclustering.basedatacollector.web.WebResource;
import de.fernuni.browserhistoryclustering.common.config.Config;
import de.fernuni.browserhistoryclustering.common.types.Filenames2Queries;
import de.fernuni.browserhistoryclustering.common.types.HistoryEntry;
import de.fernuni.browserhistoryclustering.common.types.Queries2Filenames;
import de.fernuni.browserhistoryclustering.common.utils.FileUtils;
import de.fernuni.browserhistoryclustering.sqlite.HistoryReaderFirefox;

/**
 * @author ah
 * 
 */
public class Main {

   static Logger logger = Logger.getLogger("");
   static Config s_Conf;

   /**
    * @param args
    * @throws Exception
    * @throws IOException
    * @throws SQLException
    */
   public static void main(String[] args) throws Exception {

      initialize();
      logger.log(Level.INFO, "BaseDataCollector started");

      int v_UrlCtr = 1;

      Crawler v_Crawler = Crawler.getInstance();

      Set<HistoryEntry> v_HistoryEntries = HistoryReaderFirefox
            .getHistoryEntriesNet(s_Conf.getBaseDir()
                  + s_Conf.getBrowserHistoryFile());

      Set<HistoryEntry> v_Searches = HistoryReaderFirefox
            .getGoogleSearches(s_Conf.getBaseDir()
                  + s_Conf.getBrowserHistoryFile());

      Map<HistoryEntry, Set<HistoryEntry>> v_PagesPerSearch = HistoryReaderFirefox
            .getPagesPerSearch(v_Searches,
                  s_Conf.getBaseDir() + s_Conf.getBrowserHistoryFile());

      Queries2Filenames v_Queries2Filenames = new Queries2Filenames(
            v_PagesPerSearch, "");
      Filenames2Queries v_Filenames2Queries = new Filenames2Queries(
            v_Queries2Filenames);
      v_Filenames2Queries.save(s_Conf.getBaseDir() + s_Conf.getDataDir()
            + s_Conf.getFilename2QueriesFile());

      for (HistoryEntry v_Entry : v_HistoryEntries) {
         WebResource v_Resource;
         try {
            logger.log(Level.INFO, v_UrlCtr + " / " + v_HistoryEntries.size()
                  + " -- " + v_Entry.getUrl());
            v_Resource = v_Crawler.getWebResource(v_Entry.getUrl(),
                  v_Entry.getId());

            if (v_Resource.getBody() != null
                  && "en".equals(v_Resource.getLanguage())) {
               FileUtils.saveText(v_Resource.getBody(), s_Conf.getHtmlPath()
                     + v_Resource.getHistoryId().toString() + ".html");
            }
            if (v_Resource.getParsedBody() != null
                  && "en".equals(v_Resource.getLanguage())) {
               FileUtils.saveText(v_Resource.getParsedBody(),
                     s_Conf.getTextPath()
                           + v_Resource.getHistoryId().toString() + ".txt");
            }

         } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE, e.getMessage());
         } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE, e.getMessage());
         } finally {
            v_UrlCtr++;
         }
      }
   }

   private static void initialize() throws InvalidFileFormatException,
         IOException {
      s_Conf = Config.getInstance();
      /*
       * Preferences v_Prefs = new IniPreferences(new Ini(new
       * File("../config.ini"))); Preferences v_FilePrefs =
       * v_Prefs.node("filesystem"); s_BrowserHistoryFile =
       * v_FilePrefs.get("history", StringDefaults.BROWSERHISTORY_FILENAME);
       * s_Filename2QueriesFile = v_FilePrefs.get("filename2qeries",
       * StringDefaults.FILENAME_FILENAME2QUERIES); s_DataDir =
       * v_FilePrefs.get("datadir", StringDefaults.DATA_DIR); s_BaseDir =
       * v_FilePrefs.get("basedir", StringDefaults.BASE_DIR); s_HtmlDir =
       * v_FilePrefs.get("htmldir", StringDefaults.HTML_DIR); s_TextDir =
       * v_FilePrefs.get("textdir", StringDefaults.TEXT_DIR); s_HtmlPath =
       * s_BaseDir + s_DataDir + s_HtmlDir; s_TextPath = s_BaseDir + s_DataDir +
       * s_TextDir;
       * 
       * FileUtils.createDirIfNotExists(s_BaseDir + s_DataDir);
       * FileUtils.createDirIfNotExists(s_HtmlPath);
       * FileUtils.createDirIfNotExists(s_TextPath);
       */
   }

}
