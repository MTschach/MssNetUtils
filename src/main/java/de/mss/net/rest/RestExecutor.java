package de.mss.net.rest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mss.net.AuthenticatedServer;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;

public class RestExecutor {

   private static Logger defaultLogger = LogManager.getLogger();


   private Logger           logger     = defaultLogger;

   private int     connectionTimeout = 10000;
   private int     requestTimeout    = 180000;

   private boolean binaryResponse    = false;


   private List<RestServer> serverList = null;

   public RestExecutor(RestServer server) {
      addServer(server);
   }


   public RestExecutor(RestServer server, Logger l) {
      addServer(server);
      setLogger(l);
   }


   public RestExecutor(List<RestServer> servers) {
      for (RestServer server : servers)
         addServer(server);
   }


   public RestExecutor(List<RestServer> servers, Logger l) {
      for (RestServer server : servers)
         addServer(server);

      setLogger(l);
   }


   public RestExecutor(RestServer[] servers) {
      for (RestServer server : servers)
         addServer(server);
   }


   public RestExecutor(RestServer[] servers, Logger l) {
      for (RestServer server : servers)
         addServer(server);

      setLogger(l);
   }


   public RestResponse executeRequest(String loggingId, RestRequest request, String bindAddress) throws MssException {
      for (RestServer server : this.serverList) {
         try {
            return executeRequest(loggingId, request, server, bindAddress);
         }
         catch (MssException e) {
            getLogger()
                  .debug(
                        de.mss.utils.Tools.formatLoggingId(loggingId)
                              + "could not execute request for server "
                              + (server.getServer() != null ? server.getServer().getUrl() : "null"),
                        e);
         }

      }
      throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_UNABLE_TO_EXECUTE_REQUEST);
   }


   public RestResponse executeRequest(String loggingId, RestRequest request, RestServer server, String bindAddress) throws MssException {
      if (request == null || server == null || server.getServer() == null)
         throw new MssException(de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM, "some required parameter are null");

      getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "executing request to " + server.getServer().getCompleteUrl());
      de.mss.utils.StopWatch stopWatch = new de.mss.utils.StopWatch();
      
      try (CloseableHttpClient httpClient = HttpClientFactory.getHttpClient(server)) {
         RestResponse response = executeWithRetry(loggingId, httpClient, request, server, 3);
         stopWatch.stop();
         getLogger()
               .debug(
                     de.mss.utils.Tools.formatLoggingId(loggingId)
                           + "executing request to "
                           + server.getServer().getCompleteUrl()
                           + " done ["
                           + stopWatch.getDuration()
                           + " ms]");

         return response;
      }
      catch (IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_UNABLE_TO_EXECUTE_REQUEST, e, "error while working on httpclient");
      }
   }


   private RestResponse executeWithRetry(String loggingId, CloseableHttpClient httpClient, RestRequest request, RestServer server, int retryCount)
         throws MssException {
      int retries = retryCount;
      HttpUriRequest req = getRequestBuilder(request, server).build();

      HttpHost target = new HttpHost(server.getServer().getHost(), server.getServer().getPort().intValue());
      while (retries > 0) {
         retries-- ;
         try (CloseableHttpResponse resp = httpClient.execute(target, req)) {
            RestResponse response = new RestResponse(resp.getStatusLine().getStatusCode());

            response.setContent(readContent(resp));
            response.setBinaryContent(readBinaryContent(resp));

            if (resp.getAllHeaders() != null) {
               Map<String, String> headers = new HashMap<>();
               for (Header header : resp.getAllHeaders()) {
                  headers.put(header.getName(), header.getValue());
               }
               response.setHeaderParams(headers);
            }

            response.setRedirectUrl(getRedirectUrl(resp));

            return response;
         }
         catch (IOException e) {
            getLogger().error(de.mss.utils.Tools.formatLoggingId(loggingId) + "executing request failed. " + retries + " retries left", e);
         }
      }
      return null;
   }


   private String getRedirectUrl(CloseableHttpResponse resp) {
      if (isRedirect(resp.getStatusLine().getStatusCode())) {
         Header redirectHeader = resp.getFirstHeader("location");
         if (redirectHeader != null)
            return redirectHeader.getValue();
      }

      return null;
   }


   private byte[] readBinaryContent(CloseableHttpResponse resp) throws MssException {
      if (resp.getEntity() == null || !this.binaryResponse)
         return null;

      try (InputStream s = resp.getEntity().getContent()) {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         int nRead;
         byte[] data = new byte[16384];

         while ((nRead = s.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
         }

         return buffer.toByteArray();
      }
      catch (UnsupportedOperationException | IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED, e);
      }

   }


   private String readContent(CloseableHttpResponse resp) throws MssException {

      if (resp.getEntity() == null || this.binaryResponse)
         return null;
      
      String encoding = null;
      if (resp.getEntity().getContentEncoding() != null)
         encoding = resp.getEntity().getContentEncoding().getValue();
      
      if (encoding == null)
         encoding = "UTF-8";
      
      try (BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), encoding))) {
         StringBuilder sb = new StringBuilder();
         
         String line = null;
         while ((line = br.readLine()) != null) {
            if (sb.length() > 0)
               sb.append(System.getProperty("line.separator"));
            sb.append(line);
         }

         return sb.toString();
      }
      catch (UnsupportedOperationException | IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED, e);
      }
   }


   public static boolean isRedirect(int statusCode) {
      switch (statusCode) {
         case org.apache.http.HttpStatus.SC_MOVED_TEMPORARILY:
         case org.apache.http.HttpStatus.SC_MOVED_PERMANENTLY:
         case org.apache.http.HttpStatus.SC_SEE_OTHER:
         case org.apache.http.HttpStatus.SC_TEMPORARY_REDIRECT:
            return true;
         default:
            return false;
      }
   }


   private RequestBuilder getRequestBuilder(RestRequest request, RestServer server) throws MssException {
      RequestBuilder requestBuilder;

      switch (request.getMethod()) {
         case DELETE:
            requestBuilder = RequestBuilder.delete();
            break;

         case GET:
            requestBuilder = RequestBuilder.get();
            break;

         case PATCH:
            requestBuilder = RequestBuilder.patch();
            break;

         case POST:
            requestBuilder = RequestBuilder.post();
            break;

         default:
            throw new MssException(
                  de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED,
                  "the method '" + request.getMethod().getMethod() + "' is not supported");
      }

      requestBuilder = applyProxy(requestBuilder, server.getProxy());
      requestBuilder = applyHeaderParams(requestBuilder, request.getHeaderParams());
      requestBuilder = applyParams(requestBuilder, request.getUrlParams());
      requestBuilder = applyParams(requestBuilder, request.getPostParams());
      requestBuilder = applyUrlAndParams(requestBuilder, request.getUrl(), request.getUrlParams());
      
      RequestConfig conf = RequestConfig
            .custom()
            .setConnectionRequestTimeout(this.requestTimeout)
            .setConnectTimeout(this.connectionTimeout)
            .setSocketTimeout(this.connectionTimeout)
            .build();
      
      requestBuilder.setConfig(conf);

      return requestBuilder;
   }


   private RequestBuilder applyUrlAndParams(RequestBuilder requestBuilder, String url, Map<String, String> urlParams) {
      RequestBuilder retBuilder = requestBuilder;

      String u = url;

      if (urlParams != null)
         for (String key : urlParams.keySet()) {
            u = u.replaceAll("\\{" + key + "\\}", urlParams.get(key));
         }

      retBuilder.setUri(u);

      return retBuilder;
   }


   private RequestBuilder applyHeaderParams(RequestBuilder requestBuilder, Map<String, String> headerParams) {
      RequestBuilder retBuilder = requestBuilder;

      if (headerParams != null)
         headerParams.forEach((key, value) -> retBuilder.addHeader(key, value));

      return retBuilder;
   }


   private RequestBuilder applyParams(RequestBuilder requestBuilder, Map<String, String> urlParams) {
      RequestBuilder retBuilder = requestBuilder;

      if (urlParams != null)
         urlParams.forEach((key, value) -> retBuilder.addParameter(key, value));

      return retBuilder;
   }


   private RequestBuilder applyProxy(RequestBuilder requestBuilder, AuthenticatedServer proxy) {
      RequestBuilder retBuilder = requestBuilder;

      if (proxy != null && Tools.isSet(proxy.getUser()) && Tools.isSet(proxy.getPassword())) {
         HttpHost p = new HttpHost(proxy.getHost(), proxy.getPort().intValue(), proxy.getProtocol().getProtocol());
         RequestConfig conf = RequestConfig.custom().setProxy(p).build();
         retBuilder.setConfig(conf);
      }

      return retBuilder;
   }


   public void addServer(RestServer server) {
      if (server == null)
         return;

      if (this.serverList == null)
         this.serverList = new ArrayList<>();

      this.serverList.add(server);
   }


   public void setLogger(Logger l) {
      if (l != null)
         this.logger = l;
   }


   public Logger getLogger() {
      return this.logger;
   }


   public void setConnectionTimeout(int sec) {
      this.connectionTimeout = sec * 1000;
   }


   public void setRequestTimeout(int sec) {
      this.requestTimeout = sec * 1000;
   }


   public long getConnectionTimeout() {
      return this.connectionTimeout / 1000;
   }


   public long getRequestTimeout() {
      return this.requestTimeout / 1000;
   }
}
