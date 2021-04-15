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
import java.util.UUID;

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

   private static Logger defaultLogger = LogManager.getRootLogger();


   private static RequestBuilder applyHeaderParams(RequestBuilder requestBuilder, Map<String, String> headerParams) {
      final RequestBuilder retBuilder = requestBuilder;

      headerParams.forEach((key, value) -> retBuilder.addHeader(key.toLowerCase(), value));

      return retBuilder;
   }


   private static RequestBuilder applyParams(RequestBuilder requestBuilder, Map<String, String> urlParams) {
      final RequestBuilder retBuilder = requestBuilder;

      urlParams.forEach((key, value) -> retBuilder.addParameter(key, value));

      return retBuilder;
   }


   private static RequestBuilder applyProxy(RequestBuilder requestBuilder, AuthenticatedServer proxy) {
      final RequestBuilder retBuilder = requestBuilder;

      if (proxy != null && Tools.isSet(proxy.getUser()) && Tools.isSet(proxy.getPassword())) {
         final HttpHost p = new HttpHost(proxy.getHost(), proxy.getPort().intValue(), proxy.getProtocol().getProtocol());
         final RequestConfig conf = RequestConfig.custom().setProxy(p).build();
         retBuilder.setConfig(conf);
      }

      return retBuilder;
   }


   private static RequestBuilder applyUrlAndParams(RequestBuilder requestBuilder, String url, Map<String, String> urlParams) {
      final RequestBuilder retBuilder = requestBuilder;

      String u = url;

      for (final String key : urlParams.keySet()) {
         u = u.replaceAll("\\{" + key + "\\}", urlParams.get(key));
      }

      retBuilder.setUri(u);

      return retBuilder;
   }


   private static String getRedirectUrl(CloseableHttpResponse resp) {
      if (isRedirect(resp.getStatusLine().getStatusCode())) {
         final Header redirectHeader = resp.getFirstHeader("location");
         if (redirectHeader != null) {
            return redirectHeader.getValue();
         }
      }

      return null;
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


   private Logger logger = defaultLogger;


   private int connectionTimeout = 10000;


   private int requestTimeout = 180000;


   private boolean binaryResponse = false;


   private List<RestServer> serverList = null;


   public RestExecutor(List<RestServer> servers) {
      for (final RestServer server : servers) {
         addServer(server);
      }
   }


   public RestExecutor(List<RestServer> servers, boolean isBinary) {
      for (final RestServer server : servers) {
         addServer(server);
      }
      this.binaryResponse = isBinary;
   }


   public RestExecutor(List<RestServer> servers, Logger l) {
      for (final RestServer server : servers) {
         addServer(server);
      }

      setLogger(l);
   }


   public RestExecutor(List<RestServer> servers, Logger l, boolean isBinary) {
      for (final RestServer server : servers) {
         addServer(server);
      }

      this.binaryResponse = isBinary;
      setLogger(l);
   }


   public RestExecutor(RestServer server) {
      addServer(server);
   }


   public RestExecutor(RestServer server, boolean isBinary) {
      addServer(server);
      this.binaryResponse = isBinary;
   }


   public RestExecutor(RestServer server, Logger l) {
      addServer(server);
      setLogger(l);
   }


   public RestExecutor(RestServer server, Logger l, boolean isBinary) {
      addServer(server);
      setLogger(l);
      this.binaryResponse = isBinary;
   }


   public RestExecutor(RestServer[] servers) {
      for (final RestServer server : servers) {
         addServer(server);
      }
   }


   public RestExecutor(RestServer[] servers, boolean isBinary) {
      for (final RestServer server : servers) {
         addServer(server);
      }
      this.binaryResponse = isBinary;
   }


   public RestExecutor(RestServer[] servers, Logger l) {
      for (final RestServer server : servers) {
         addServer(server);
      }

      setLogger(l);
   }


   public RestExecutor(RestServer[] servers, Logger l, boolean isBinary) {
      for (final RestServer server : servers) {
         addServer(server);
      }

      setLogger(l);
      this.binaryResponse = isBinary;
   }


   public void addServer(RestServer server) {
      if (server == null) {
         return;
      }

      if (this.serverList == null) {
         this.serverList = new ArrayList<>();
      }

      this.serverList.add(server);
   }


   public RestResponse executeRequest(String loggingId, RestRequest request, RestServer server, String bindAddress) throws MssException {
      if (request == null || server == null || server.getServer() == null) {
         throw new MssException(de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM, "some required parameter are null");
      }

      String logId = loggingId;
      if (!Tools.isSet(logId)) {
         logId = UUID.randomUUID().toString();
      }

      final de.mss.utils.StopWatch stopWatch = new de.mss.utils.StopWatch();

      try (CloseableHttpClient httpClient = HttpClientFactory.getHttpClient(server)) {
         final RestResponse response = executeWithRetry(logId, httpClient, request, server, 3);
         stopWatch.stop();
         getLogger()
               .debug(
                     de.mss.utils.Tools.formatLoggingId(logId)
                           + "executing request to "
                           + server.getServer().getCompleteUrl()
                           + " done ["
                           + stopWatch.getDuration()
                           + " ms]");

         return response;
      }
      catch (final Exception e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_UNABLE_TO_EXECUTE_REQUEST, e, "error while working on httpclient");
      }
   }


   public RestResponse executeRequest(String loggingId, RestRequest request, String bindAddress) throws MssException {
      String logId = loggingId;
      if (!Tools.isSet(logId)) {
         logId = UUID.randomUUID().toString();
      }

      MssException thrownException = new MssException(de.mss.net.exception.ErrorCodes.ERROR_UNABLE_TO_EXECUTE_REQUEST);

      for (final RestServer server : this.serverList) {
         try {
            return executeRequest(logId, request, server, bindAddress);
         }
         catch (final MssException e) {
            thrownException = e;
            getLogger()
                  .debug(
                        de.mss.utils.Tools.formatLoggingId(logId)
                              + "could not execute request for server "
                              + (server.getServer() != null ? server.getServer().getUrl() : "null"),
                        e);
         }
      }
      throw thrownException;
   }


   private RestResponse executeWithRetry(String loggingId, CloseableHttpClient httpClient, RestRequest request, RestServer server, int retryCount)
         throws MssException {
      int retries = retryCount;
      final HttpUriRequest req = getRequestBuilder(request, server).build();

      getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "executing request to " + req.getURI().toString());

      //      HttpHost target = new HttpHost(server.getServer().getHost(), server.getServer().getPort().intValue());
      while (retries > 0) {
         retries-- ;
         try (CloseableHttpResponse resp = httpClient.execute(req)) {
            final RestResponse response = new RestResponse(resp.getStatusLine().getStatusCode());

            response.setContent(readContent(resp));
            response.setBinaryContent(readBinaryContent(resp));

            if (resp.getAllHeaders() != null) {
               final Map<String, String> headers = new HashMap<>();
               for (final Header header : resp.getAllHeaders()) {
                  headers.put(header.getName(), header.getValue());
               }
               response.setHeaderParams(headers);
            }

            response.setRedirectUrl(getRedirectUrl(resp));

            return response;
         }
         catch (final IOException e) {
            getLogger().error(de.mss.utils.Tools.formatLoggingId(loggingId) + "executing request failed. " + retries + " retries left", e);
         }
      }
      return null;
   }


   public long getConnectionTimeout() {
      return this.connectionTimeout / 1000;
   }


   public Logger getLogger() {
      return this.logger;
   }


   private RequestBuilder getRequestBuilder(RestRequest request, RestServer server) throws MssException {
      RequestBuilder requestBuilder;

      if (request.getMethod() == null) {
         throw new MssException(
               de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED,
               "the method is not supported");
      }

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
                  "the method '" + request.getMethod() + "' is not supported");
      }

      requestBuilder = applyProxy(requestBuilder, server.getProxy());
      requestBuilder = applyHeaderParams(requestBuilder, request.getHeaderParams());
      requestBuilder = applyParams(requestBuilder, request.getUrlParams());
      requestBuilder = applyParams(requestBuilder, request.getPostParams());
      requestBuilder = applyUrlAndParams(requestBuilder, server.getServer().getCompleteUrl() + "/" + request.getUrl(), request.getUrlParams());

      final RequestConfig conf = RequestConfig
            .custom()
            .setConnectionRequestTimeout(this.requestTimeout)
            .setConnectTimeout(this.connectionTimeout)
            .setSocketTimeout(this.connectionTimeout)
            .build();

      requestBuilder.setConfig(conf);

      return requestBuilder;
   }


   public long getRequestTimeout() {
      return this.requestTimeout / 1000;
   }


   private byte[] readBinaryContent(CloseableHttpResponse resp) throws MssException {
      if (resp.getEntity() == null || !this.binaryResponse) {
         return null;
      }

      try (InputStream s = resp.getEntity().getContent()) {
         final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         int nRead;
         final byte[] data = new byte[16384];

         while ((nRead = s.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
         }

         return buffer.toByteArray();
      }
      catch (UnsupportedOperationException | IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED, e);
      }

   }


   @SuppressWarnings("resource")
   private String readContent(CloseableHttpResponse resp) throws MssException {

      if (resp.getEntity() == null || this.binaryResponse) {
         return null;
      }

      try {
         if (resp.getEntity().getContent() == null) {
            return null;
         }
      }
      catch (final Exception e1) {
         Tools.doNullLog(e1);
         return null;
      }

      String encoding = null;
      if (resp.getEntity().getContentEncoding() != null) {
         encoding = resp.getEntity().getContentEncoding().getValue();
      }

      if (encoding == null) {
         encoding = "UTF-8";
      }

      try (BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), encoding))) {
         final StringBuilder sb = new StringBuilder();

         String line = null;
         while ((line = br.readLine()) != null) {
            if (sb.length() > 0) {
               sb.append(System.getProperty("line.separator"));
            }
            sb.append(line);
         }

         return sb.toString();
      }
      catch (UnsupportedOperationException | IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED, e);
      }
   }


   public void setConnectionTimeout(int sec) {
      this.connectionTimeout = sec * 1000;
   }


   public void setLogger(Logger l) {
      if (l != null) {
         this.logger = l;
      }
   }


   public void setRequestTimeout(int sec) {
      this.requestTimeout = sec * 1000;
   }
}
