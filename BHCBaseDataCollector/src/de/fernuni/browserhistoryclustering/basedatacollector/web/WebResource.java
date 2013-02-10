package de.fernuni.browserhistoryclustering.basedatacollector.web;

public class WebResource {

   private String m_Body;
   private String m_ParsedBody;
   private String m_Title;
   private String m_Url;
   private String m_Language;
   private Long m_HistoryId;

   public WebResource() {
   }

   public String getBody() {
      return m_Body;
   }

   public void setBody(String resourceText) {
      m_Body = resourceText;
   }

   public String getTitle() {
      return m_Title;
   }

   public void setTitle(String p_Title) {
      m_Title = p_Title;
   }

   public String getUrl() {
      return m_Url;
   }

   public void setUrl(String p_ResourceUrl) {
      m_Url = p_ResourceUrl;
   }

   public String getLanguage() {
      return m_Language;
   }

   public void setLanguage(String p_Language) {
      m_Language = p_Language;
   }

   public String getParsedBody() {
      return m_ParsedBody;
   }

   public void setParsedBody(String p_ParsedBody) {
      m_ParsedBody = p_ParsedBody;
   }

   public Long getHistoryId() {
      return m_HistoryId;
   }

   public void setHistoryId(Long p_HistoryId) {
      m_HistoryId = p_HistoryId;
   }

}
