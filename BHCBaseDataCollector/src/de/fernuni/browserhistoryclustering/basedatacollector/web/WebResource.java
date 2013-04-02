package de.fernuni.browserhistoryclustering.basedatacollector.web;

/**
 * @author ah
 *
 * Stores information of visited resources 
 */
public class WebResource {

   private String m_BodyText;
   private String m_ParsedBody;
   private String m_Title;
   private String m_Url;
   private String m_Language;
   private Long m_HistoryId;

   /**
    * @return Body text
    */
   public String getBody() {
      return m_BodyText;
   }

   /**
    * @param p_BodyText
    */
   public void setBody(String p_BodyText) {
      m_BodyText = p_BodyText;
   }

   /**
    * @return Resource title
    */
   public String getTitle() {
      return m_Title;
   }

   /**
    * @param p_Title
    */
   public void setTitle(String p_Title) {
      m_Title = p_Title;
   }

   /**
    * @return Resource URL
    */
   public String getUrl() {
      return m_Url;
   }

   /**
    * @param p_ResourceUrl
    */
   public void setUrl(String p_ResourceUrl) {
      m_Url = p_ResourceUrl;
   }

   /**
    * @return Resource Language
    */
   public String getLanguage() {
      return m_Language;
   }

   /**
    * @param p_Language
    */
   public void setLanguage(String p_Language) {
      m_Language = p_Language;
   }

   /**
    * @return Extracted content of body text
    */
   public String getParsedBody() {
      return m_ParsedBody;
   }

   /**
    * @param p_ParsedBody
    */
   public void setParsedBody(String p_ParsedBody) {
      m_ParsedBody = p_ParsedBody;
   }

   /**
    * @return Id of resource in browser history
    */
   public Long getHistoryId() {
      return m_HistoryId;
   }/**
    * @param p_HistoryId
    */
   

   public void setHistoryId(Long p_HistoryId) {
      m_HistoryId = p_HistoryId;
   }

}
