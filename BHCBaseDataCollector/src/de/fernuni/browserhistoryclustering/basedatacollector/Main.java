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
import de.fernuni.browserhistoryclustering.sqlite.HistoryReader;
import de.fernuni.browserhistoryclustering.sqlite.HistoryReaderFirefox;
import de.l3s.boilerpipe.BoilerpipeProcessingException;

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
      HistoryReader v_HistoryReader = new HistoryReaderFirefox();

      Set<HistoryEntry> v_HistoryEntries = v_HistoryReader
            .getHistoryEntriesWithoutSearches(s_Conf.getBaseDir()
                  + s_Conf.getBrowserHistoryFile());
      
      Set<HistoryEntry> v_Searches = v_HistoryReader
            .getGoogleSearches(s_Conf.getBaseDir()
                  + s_Conf.getBrowserHistoryFile());
      
      Map<HistoryEntry, Set<HistoryEntry>> v_PagesPerSearch = v_HistoryReader
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
                  v_Entry.getId(), s_Conf.getMinBoilerpipeResultSize(), s_Conf.getIncludeTitle());

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
            logger.log(Level.SEVERE, v_Entry.getId() + ": " + e.getMessage());
         } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE, v_Entry.getId() + ": " +  e.getMessage());
         } catch (BoilerpipeProcessingException e) {
      	// TODO Auto-generated catch block
            logger.log(Level.SEVERE, v_Entry.getId() + ": " +  e.getMessage());
         }
         
         
         finally {
            v_UrlCtr++;
         }
      }      
   }

   private static void initialize() throws InvalidFileFormatException,
         IOException {
      s_Conf = Config.getInstance();
   }

}
