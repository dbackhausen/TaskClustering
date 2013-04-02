/**
 * 
 */
package de.fernuni.browserhistoryclustering.basedatacollector.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.cybozu.labs.langdetect.LangDetectException;

import de.fernuni.browserhistoryclustering.langdetect.LangDetect;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

/**
 * @author ah
 * 
 * Retrieves web resources
 */
public class Crawler {

   private static Crawler m_Instance = new Crawler();
   static Logger logger = Logger.getLogger("");

   private Crawler() {
   }

   /**
    * @param p_Url URL of the resource
    * @param p_Id History id of resource
    * @param p_MinBoilerpipeResultSize Lower threshold for boilerpipe result, switching to Tika parser if lower 
    * @param p_ExtractTitle Whether to prepend the resource title to the extracted resource content
    * @return WebResource
    * @throws ClientProtocolException
    * @throws IOException
    * @throws IllegalStateException
    * @throws SAXException
    * @throws TikaException
    * @throws BoilerpipeProcessingException
    */
   public WebResource getWebResource(String p_Url, Long p_Id, Long p_MinBoilerpipeResultSize, Boolean p_ExtractTitle)
         throws ClientProtocolException, IOException, IllegalStateException,
         SAXException, TikaException, BoilerpipeProcessingException {
      WebResource v_WebResource = new WebResource();
      v_WebResource.setHistoryId(p_Id);
      HttpResponse v_HttpResponse = getResponse(p_Url);
      Header[] v_ContentTypeHeader = v_HttpResponse.getHeaders("Content-Type");
      byte[] v_Content = EntityUtils.toByteArray(v_HttpResponse.getEntity());
      
      String v_ParsedBodyText = null;

      if (v_ContentTypeHeader != null
            && v_ContentTypeHeader.length > 0
            && v_ContentTypeHeader[0].toString().toLowerCase()
                  .contains("text/html")) {
         v_ParsedBodyText = removeBoilerplate(getString(v_Content));
         
         if (v_ParsedBodyText.length() < p_MinBoilerpipeResultSize) {
            v_ParsedBodyText = extractHtml(new ByteArrayInputStream(v_Content));
         }
         
         if (p_ExtractTitle)
         {
      	String v_Title = extractTitle(new ByteArrayInputStream(v_Content));
      	if (!(v_Title == null) && !(v_Title.isEmpty()))
      	{
      	   v_ParsedBodyText = v_Title + System.getProperty("line.separator") + v_ParsedBodyText;
      	}
         }
         
      } else if (v_ContentTypeHeader != null
            && v_ContentTypeHeader.length > 0
            && v_ContentTypeHeader[0].toString().toLowerCase()
                  .contains("text/pdf")) {
         v_ParsedBodyText = extractPdf(new ByteArrayInputStream(v_Content));
         v_WebResource.setBody("");
      } else if (v_ContentTypeHeader != null && v_ContentTypeHeader.length > 0) {
         logger.log(Level.SEVERE,
               "Content type is neither text/html nor text/pdf.");
      } else {
         logger.log(Level.SEVERE, "No content type detected.");
      }

      if (v_ParsedBodyText != null) {

         v_WebResource.setUrl(p_Url);
         v_WebResource.setParsedBody(v_ParsedBodyText);
         v_WebResource.setBody(getString(v_Content));

         try {
            v_WebResource.setLanguage(LangDetect.getInstance().detect(
                  v_ParsedBodyText));
         } catch (LangDetectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      return v_WebResource;
   }

   private  HttpResponse getResponse(String p_UrlString)
         throws ClientProtocolException, IOException {
      DefaultHttpClient v_Client = new DefaultHttpClient();
      DefaultHttpRequestRetryHandler v_RetryHandler = new DefaultHttpRequestRetryHandler(
            1, false);
      v_Client.setHttpRequestRetryHandler(v_RetryHandler);
      HttpParams v_Params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(v_Params, 5000);
      HttpConnectionParams.setSoTimeout(v_Params, 5000);
      v_Client.setParams(v_Params);

      HttpGet v_Request = new HttpGet(p_UrlString);
      v_Request
            .setHeader("User-Agent",
                  "Mozilla/5.0 (Windows NT 5.1; rv:15.0) Gecko/20100101 Firefox/13.0.1");
      HttpResponse v_Response = v_Client.execute(v_Request);

      return v_Response;
   }

   private String removeBoilerplate(String p_BoilerplatedText) throws BoilerpipeProcessingException {
	return ArticleExtractor.getInstance().getText(p_BoilerplatedText);            
   }

   private String extractPdf(InputStream p_InputStream) throws IOException,
         SAXException, TikaException {
      Parser v_Parser = new PDFParser();
      Metadata v_Metadata = new Metadata();
      ParseContext v_Context = new ParseContext();
      ContentHandler v_Handler = new BodyContentHandler(-1);
      v_Parser.parse(p_InputStream, v_Handler, v_Metadata, v_Context);
      return v_Handler.toString();
   }

   private String extractHtml(InputStream p_InputStream) throws IOException,
         SAXException, TikaException {
      Parser v_Parser = new HtmlParser();
      Metadata v_Metadata = new Metadata();
      ParseContext v_Context = new ParseContext();
      ContentHandler v_Handler = new BodyContentHandler(-1);
      v_Parser.parse(p_InputStream, v_Handler, v_Metadata, v_Context);
      return v_Handler.toString();
   }
   
   private String extractTitle(InputStream p_InputStream) throws IOException,
  	   SAXException, TikaException {
      Parser v_Parser = new AutoDetectParser();
      Metadata v_Metadata = new Metadata();
      ParseContext v_Context = new ParseContext();
      ContentHandler v_Handler = new BodyContentHandler(-1);
      v_Parser.parse(p_InputStream, v_Handler, v_Metadata, v_Context);
      return v_Metadata.get("title");
   }

   private String getString(byte[] p_ByteArray) throws IllegalStateException,
         IOException {
      ByteArrayInputStream p_InputStream = new ByteArrayInputStream(p_ByteArray);
      BufferedReader rd = new BufferedReader(new InputStreamReader(
            p_InputStream));
      String line = "";
      StringBuilder v_StringBuilder = new StringBuilder();
      while ((line = rd.readLine()) != null) {
         v_StringBuilder.append("\r\n" + line);
      }

      return v_StringBuilder.toString();
   }

   /**
    * @return Crawler singleton
    */
   public static Crawler getInstance() {
      return m_Instance;
   }

}
