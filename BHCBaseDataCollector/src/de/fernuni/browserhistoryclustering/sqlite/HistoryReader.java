package de.fernuni.browserhistoryclustering.sqlite;

import java.util.Map;
import java.util.Set;

import de.fernuni.browserhistoryclustering.common.types.HistoryEntry;

/**
 * @author ah
 *
 * Interface for reading out browser histories.
 */
public interface HistoryReader {

   /**
    * @param p_HistoryFileLoc
    * @return All entries of the browser history.
    * @throws Exception
    */
   public abstract Set<HistoryEntry> getHistoryEntries(String p_HistoryFileLoc)
	   throws Exception;

   /**
    * @param p_HistoryFileLoc
    * @return All history entries not pointing to searches.
    * @throws Exception
    */
   public abstract Set<HistoryEntry> getHistoryEntriesWithoutSearches(
	   String p_HistoryFileLoc) throws Exception;

   /**
    * @param p_HistoryFileLoc
    * @return All seaches with google within the history.
    * @throws Exception
    */
   public abstract Set<HistoryEntry> getGoogleSearches(String p_HistoryFileLoc)
	   throws Exception;

   /**
    * @param p_Searches
    * @param p_HistoryFileLoc
    * @return Map search -> visited paged based on that search, 
    * map keys contain all searches within the history.
    * @throws Exception
    */
   public abstract Map<HistoryEntry, Set<HistoryEntry>> getPagesPerSearch(
	   Set<HistoryEntry> p_Searches, String p_HistoryFileLoc)
	   throws Exception;

}