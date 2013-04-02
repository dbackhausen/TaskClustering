package de.fernuni.browserhistoryclustering.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.fernuni.browserhistoryclustering.common.types.HistoryEntry;

/**
 * 
 * History Reader Implementation for Mozilla Browsers
 * (sqlite history format).
 * 
 * @author ah
 *
 */
public class HistoryReaderFirefox implements HistoryReader {

   /* (non-Javadoc)
    * @see de.fernuni.browserhistoryclustering.sqlite.HistoryReader#getHistoryEntries(java.lang.String)
    */
   @Override
   public Set<HistoryEntry> getHistoryEntries(String p_HistoryFileLoc)
         throws Exception {
      Set<HistoryEntry> v_HistoryEntries = new HashSet<HistoryEntry>();

      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:"
            + p_HistoryFileLoc);
      Statement stat = conn.createStatement();

      ResultSet rs = stat
            .executeQuery("select id, url, title  from moz_places where id > 0 and url not like '%www.google%'");

      while (rs.next()) {
         String v_Url = rs.getString("url");
         String v_Title = rs.getString("title");
         Integer v_Id = rs.getInt("id");

         HistoryEntry v_Entry = new HistoryEntry();
         v_Entry.setId(Long.valueOf(v_Id));
         v_Entry.setUrl(v_Url);
         v_Entry.setTitle(v_Title);

         v_HistoryEntries.add(v_Entry);
      }

      return v_HistoryEntries;
   }

   /* (non-Javadoc)
    * @see de.fernuni.browserhistoryclustering.sqlite.HistoryReader#getHistoryEntriesWithoutSearches(java.lang.String)
    */
   @Override
   public Set<HistoryEntry> getHistoryEntriesWithoutSearches(String p_HistoryFileLoc)
         throws Exception {
      Set<HistoryEntry> v_HistoryEntries = new HashSet<HistoryEntry>();

      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:"
            + p_HistoryFileLoc);
      Statement stat = conn.createStatement();

      ResultSet rs = stat
            .executeQuery("select id, url, title  from moz_places "
                  + "where id > 0 and url not like '%www.google%' and url like 'http%'");

      while (rs.next()) {
         String v_Url = rs.getString("url");
         String v_Title = rs.getString("title");
         Integer v_Id = rs.getInt("id");

         HistoryEntry v_Entry = new HistoryEntry();
         v_Entry.setId(Long.valueOf(v_Id));
         v_Entry.setUrl(v_Url);
         v_Entry.setTitle(v_Title);

         v_HistoryEntries.add(v_Entry);
      }

      return v_HistoryEntries;
   }

   private static Set<HistoryEntry> getHistoryEntries(Set<Long> p_Ids,
         String p_HistoryFileLoc) throws Exception {
      StringBuilder v_IdList = new StringBuilder();

      int i = 0;
      for (Long v_Id : p_Ids) {
         if (i != 0) {
            v_IdList.append(",");
         }
         v_IdList.append(v_Id);
         i++;
      }

      Set<HistoryEntry> v_HistoryEntries = new HashSet<HistoryEntry>();

      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:"
            + p_HistoryFileLoc);
      Statement stat = conn.createStatement();

      ResultSet rs = stat
            .executeQuery("select id, url, title from moz_places where id > 0 and id in ("
                  + v_IdList.toString() + ")");

      while (rs.next()) {
         String v_Url = rs.getString("url");
         String v_Title = rs.getString("title");
         Integer v_Id = rs.getInt("id");

         HistoryEntry v_Entry = new HistoryEntry();
         v_Entry.setId(Long.valueOf(v_Id));
         v_Entry.setUrl(v_Url);
         v_Entry.setTitle(v_Title);

         v_HistoryEntries.add(v_Entry);
      }

      return v_HistoryEntries;
   }

   /* (non-Javadoc)
    * @see de.fernuni.browserhistoryclustering.sqlite.HistoryReader#getGoogleSearches(java.lang.String)
    */
   @Override
   public Set<HistoryEntry> getGoogleSearches(String p_HistoryFileLoc)
         throws Exception {
      Set<HistoryEntry> v_HistoryEntries = new HashSet<HistoryEntry>();

      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:"
            + p_HistoryFileLoc);
      Statement stat = conn.createStatement();

      ResultSet rs = stat
            .executeQuery("select id, url, title from moz_places where id > 0 and (title like '%Google-Suche' or title like '%Google Search')");

      while (rs.next()) {
         String v_Url = rs.getString("url");
         String v_Title = rs.getString("title");
         Integer v_Id = rs.getInt("id");

         HistoryEntry v_Entry = new HistoryEntry();
         v_Entry.setId(Long.valueOf(v_Id));
         v_Entry.setUrl(v_Url);
         v_Entry.setTitle(v_Title);

         v_HistoryEntries.add(v_Entry);
      }

      return v_HistoryEntries;
   }

   /* (non-Javadoc)
    * @see de.fernuni.browserhistoryclustering.sqlite.HistoryReader#getPagesPerSearch(java.util.Set, java.lang.String)
    */
   @Override
   public Map<HistoryEntry, Set<HistoryEntry>> getPagesPerSearch(
         Set<HistoryEntry> p_Searches, String p_HistoryFileLoc)
         throws Exception {      
      Map<HistoryEntry, Set<HistoryEntry>> v_Result = new HashMap<HistoryEntry, Set<HistoryEntry>>();
     
      for (HistoryEntry v_Search : p_Searches) {
         Set<Long> v_ConsecutivePagesIds = getConsecutivePages(
               Collections.singleton(v_Search.getId()),
               Collections.singleton(v_Search.getId()), p_HistoryFileLoc);
         Set<HistoryEntry> v_ConsecutivePages = getHistoryEntries(
               v_ConsecutivePagesIds, p_HistoryFileLoc);

         v_Result.put(v_Search, v_ConsecutivePages);
      }
      return v_Result;
   }

   private static Set<Long> getConsecutivePages(Set<Long> p_ParentIds,
         Set<Long> p_KnowAncestorIds, String p_HistoryFileLoc) throws Exception {
      StringBuilder v_IdList = new StringBuilder();

      int i = 0;
      for (Long v_UrlId : p_ParentIds) {
         if (i != 0) {
            v_IdList.append(",");
         }
         v_IdList.append(v_UrlId);
         i++;
      }

      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:"
            + p_HistoryFileLoc);

      Statement stat = conn.createStatement();

      ResultSet rs = stat
            .executeQuery("select mhv.place_id from moz_historyvisits mhv where mhv.from_visit in (select id from moz_historyvisits where place_id in ("
                  + v_IdList.toString() + "))");

      Set<Long> v_ResultIds = new HashSet<Long>();

      while (rs.next()) {
         if (!p_KnowAncestorIds.contains(rs.getLong("place_id"))) {
            v_ResultIds.add(rs.getLong("place_id"));
         }

      }

      Set<Long> v_KnownAncestorIds = new HashSet<Long>();
      v_KnownAncestorIds.addAll(v_ResultIds);
      v_KnownAncestorIds.addAll(p_KnowAncestorIds);

      if (v_ResultIds.size() > 0) {
         v_ResultIds.addAll(getConsecutivePages(v_ResultIds,
               v_KnownAncestorIds, p_HistoryFileLoc));
      }
      return v_ResultIds;
   }

}
