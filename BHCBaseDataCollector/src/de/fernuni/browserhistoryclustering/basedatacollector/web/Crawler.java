/**
 * 
 */
package de.fernuni.browserhistoryclustering.basedatacollector.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.cybozu.labs.langdetect.LangDetectException;

import de.fernuni.browserhistoryclustering.langdetect.LangDetect;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;

/**
 * @author ah
 * 
 */
public class Crawler {

   private static Crawler m_Instance = new Crawler();
   static Logger logger = Logger.getLogger("");

   private Crawler() {
   }

   private String fetchPage(String p_UrlString) throws IOException {
      URL v_Url = new URL(p_UrlString);
      URLConnection v_UrlConnection = v_Url.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(
            v_UrlConnection.getInputStream()));
      StringBuilder v_StringBuilder = new StringBuilder();
      String v_InputLine;
      while ((v_InputLine = in.readLine()) != null) {
         v_StringBuilder.append(v_InputLine);
      }

      return v_StringBuilder.toString();
   }

   // private String detectLanguage(String p_Message) {
   // return null;// UberLanguageDetector.getInstance().detectLang(p_Message);
   // }

   public WebResource getWebResource(String p_UrlString) throws IOException {
      String v_PageContent = null;
      try {
         v_PageContent = fetchPage(p_UrlString);
      } catch (IOException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

      ContentHandler v_ContentHandler = new BodyContentHandler();
      Metadata v_Metadata = new Metadata();
      try {
         new HtmlParser().parse(
               new ByteArrayInputStream(v_PageContent.getBytes()),
               v_ContentHandler, v_Metadata, new ParseContext());
      } catch (SAXException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (TikaException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      WebResource v_WebResource = new WebResource();

      if (v_PageContent != null) {

         v_WebResource.setUrl(p_UrlString);
         v_WebResource.setBody(v_PageContent);
         v_WebResource.setParsedBody(v_ContentHandler.toString());
         v_WebResource.setTitle(v_Metadata.get("title"));
         try {
            v_WebResource.setLanguage(LangDetect.getInstance().detect(
                  v_PageContent));
         } catch (LangDetectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      return v_WebResource;

   }

   public WebResource getWebResource(String p_Url, Long p_Id)
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
         if (v_ParsedBodyText.length() < 512) {
            v_ParsedBodyText = extractHtml(new ByteArrayInputStream(v_Content));
         }
         v_WebResource.setBody(getString(v_Content));
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

   public HttpResponse getResponse(String p_UrlString)
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

   public String removeBoilerplate(String p_BoilerplatedText) {
      try {
         return ArticleExtractor.getInstance().getText(p_BoilerplatedText);
      } catch (BoilerpipeProcessingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return "";
      }      
   }

   public String tika(String p_Input) throws IOException, SAXException,
         TikaException {
      String v_Utf8String = new String(p_Input.getBytes());
      ByteArrayInputStream v_Stream = new ByteArrayInputStream(
            v_Utf8String.getBytes());
      byte[] v_ByteArray = v_Utf8String.getBytes();

      ParseContext context = new ParseContext();

      Parser parser = new AutoDetectParser();
      Metadata metadata = new Metadata();
      ParseContext ctx = new ParseContext();
      ContentHandler handler = new BodyContentHandler();

      parser.parse(v_Stream, handler, metadata, ctx);

      return handler.toString();
   }

   public String extractPdf(InputStream p_InputStream) throws IOException,
         SAXException, TikaException {
      Parser v_Parser = new PDFParser();
      Metadata v_Metadata = new Metadata();
      ParseContext v_Context = new ParseContext();
      ContentHandler v_Handler = new BodyContentHandler(-1);
      v_Parser.parse(p_InputStream, v_Handler, v_Metadata, v_Context);
      return v_Handler.toString();
   }

   public String extractHtml(InputStream p_InputStream) throws IOException,
         SAXException, TikaException {
      Parser v_Parser = new HtmlParser();
      Metadata v_Metadata = new Metadata();
      ParseContext v_Context = new ParseContext();
      ContentHandler v_Handler = new BodyContentHandler(-1);
      v_Parser.parse(p_InputStream, v_Handler, v_Metadata, v_Context);
      return v_Handler.toString();
   }

   public static String convertStreamToString(java.io.InputStream is) {
      java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
      return s.hasNext() ? s.next() : "";
   }

   public String getString(HttpResponse p_Response)
         throws IllegalStateException, IOException {
      BufferedReader rd = new BufferedReader(new InputStreamReader(p_Response
            .getEntity().getContent()));

      String line = "";
      StringBuilder v_StringBuilder = new StringBuilder();
      while ((line = rd.readLine()) != null) {
         v_StringBuilder.append("\r\n" + line);
      }

      return v_StringBuilder.toString();
   }

   public String getString(byte[] p_ByteArray) throws IllegalStateException,
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

   public WebResource getWebResourceByHttpClient(String p_UrlString, Long p_Id)
         throws ClientProtocolException, IOException,
         BoilerpipeProcessingException, SAXException, TikaException {
      WebResource v_WebResource = new WebResource();
      DefaultHttpClient client = new DefaultHttpClient();
      DefaultHttpRequestRetryHandler v_RetryHandler = new DefaultHttpRequestRetryHandler(
            1, false);
      client.setHttpRequestRetryHandler(v_RetryHandler);
      String userAgent = "Mozilla/5.0 (Windows NT 5.1; rv:15.0) Gecko/20100101 Firefox/13.0.1";
      client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 5000);
      HttpConnectionParams.setSoTimeout(params, 5000);
      client.setParams(params);

      HttpGet request = new HttpGet(p_UrlString);
      HttpResponse response = client.execute(request);

      // Get the response
      BufferedReader rd = new BufferedReader(new InputStreamReader(response
            .getEntity().getContent()));

      String line = "";
      StringBuilder v_StringBuilder = new StringBuilder();
      while ((line = rd.readLine()) != null) {
         v_StringBuilder.append("\r\n" + line);
      }

      String v_PageContent = v_StringBuilder.toString();

      ContentHandler v_ContentHandler = new BodyContentHandler();
      Metadata v_Metadata = new Metadata();

      Header[] v_ContentTypeHeader = response.getHeaders("Content-Type");

      /*
       * if (v_ContentTypeHeader != null && v_ContentTypeHeader.length > 0 &&
       * v_ContentTypeHeader[0].toString().toLowerCase().contains("text/html"))
       * {
       * 
       * String v_ExtracedText = ArticleExtractor.getInstance().getText( new
       * URL(p_UrlString));
       * 
       * 
       * 
       * if (v_PageContent != null) {
       * 
       * v_WebResource.setUrl(p_UrlString);
       * v_WebResource.setBody(v_PageContent);
       * v_WebResource.setParsedBody(v_ExtracedText);
       * v_WebResource.setTitle(v_Metadata.get("title")); //
       * v_WebResource.setLanguage(detectLanguage(v_ContentHandler.toString()));
       * try { v_WebResource.setLanguage(LangDetect.getInstance().detect(
       * v_ExtracedText)); } catch (LangDetectException e) { // TODO
       * Auto-generated catch block e.printStackTrace(); } } }
       */
      if (v_ContentTypeHeader != null
            && v_ContentTypeHeader.length > 0
            && v_ContentTypeHeader[0].toString().toLowerCase()
                  .contains("text/html")) {

         OutputStream outputstream = new ByteArrayOutputStream();
         Parser parser = new HtmlParser();
         ParseContext context = new ParseContext();

         Metadata metadata = new Metadata();

         InputStream input = TikaInputStream
               .get(new URL(p_UrlString), metadata);

         ContentHandler handler = new BoilerpipeContentHandler(
               new BodyContentHandler(outputstream), new DefaultExtractor());

         String v_ExtracedText = "";

         try {
            parser.parse(input, handler, metadata, context);
            System.out.println("content: " + outputstream.toString());
            v_ExtracedText = ArticleExtractor.getInstance().getText(
                  new URL(p_UrlString));
         } catch (Exception e) {
            // TODO: handle exception
         }
         input.close();

         if (v_PageContent != null) {

            v_WebResource.setUrl(p_UrlString);
            v_WebResource.setBody(v_PageContent);
            v_WebResource.setParsedBody(v_ExtracedText);
            v_WebResource.setTitle(v_Metadata.get("title"));
            // v_WebResource.setLanguage(detectLanguage(v_ContentHandler.toString()));
            try {
               v_WebResource.setLanguage(LangDetect.getInstance().detect(
                     v_ExtracedText));
            } catch (LangDetectException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      }

      if (v_ContentTypeHeader != null && v_ContentTypeHeader.length > 0
            && v_ContentTypeHeader[0].toString().toLowerCase().contains("/pdf")) {

         OutputStream outputstream = new ByteArrayOutputStream();
         Parser parser = new AutoDetectParser();
         ParseContext context = new ParseContext();

         Metadata metadata = new Metadata();

         InputStream input = TikaInputStream
               .get(new URL(p_UrlString), metadata);

         ContentHandler handler = new BodyContentHandler(outputstream);

         parser.parse(input, handler, metadata, context);
         System.out.println("content: " + outputstream.toString());

         input.close();

         /*
          * BufferedInputStream bis = new BufferedInputStream(new
          * ByteArrayInputStream(v_PageContent.getBytes()));
          * 
          * PDFParser parser = new PDFParser(); //parser.parse(new
          * ByteArrayInputStream(v_PageContent.getBytes()), v_ContentHandler,
          * v_Metadata);
          * 
          * parser.parse(bis, v_ContentHandler, v_Metadata, new ParseContext());
          * System.out.println("Title: " + v_Metadata.get("title"));
          * System.out.println("Author: " + v_Metadata.get("Author"));
          * System.out.println("content: " + v_ContentHandler.toString());
          */
      }

      v_WebResource.setHistoryId(p_Id);

      return v_WebResource;

   }

   public static Crawler getInstance() {
      return m_Instance;
   }

}
