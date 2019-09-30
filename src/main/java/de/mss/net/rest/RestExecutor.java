package de.mss.net.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mss.net.AuthenticatedServer;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;

public class RestExecutor {

   private static Logger defaultLogger = LogManager.getLogger();


   private Logger           logger     = defaultLogger;

   private long             connectionTimeout = 10 * 1000;
   private long             requestTimeout    = 180 * 1000;


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
      
      try (CloseableHttpClient httpClient = getClientBuilder(server).build()) {

      }
      catch (IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_UNABLE_TO_EXECUTE_REQUEST, e, "error while working on httpclient");
      }


      stopWatch.stop();
      getLogger()
            .debug(
                  de.mss.utils.Tools.formatLoggingId(loggingId)
                        + "executing request to "
                        + server.getServer().getCompleteUrl()
                        + " done ["
                        + stopWatch.getDuration()
                        + " ms]");

      throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_UNABLE_TO_EXECUTE_REQUEST);
   }


   private HttpClientBuilder getClientBuilder(RestServer server) {
      HttpClientBuilder clientBuilder = HttpClients.custom();

      clientBuilder = applyProxy(clientBuilder, server.getProxy());

      return clientBuilder;
   }


   private HttpClientBuilder applyProxy(HttpClientBuilder clientBuilder, AuthenticatedServer proxy) {
      HttpClientBuilder retBuilder = clientBuilder;

      if (proxy != null && Tools.isSet(proxy.getUser()) && Tools.isSet(proxy.getPassword())) {
         CredentialsProvider credsProvider = new BasicCredentialsProvider();
         credsProvider
               .setCredentials(
                     new AuthScope(proxy.getHost(), proxy.getPort().intValue()),
                     new UsernamePasswordCredentials(proxy.getUser(), proxy.getPassword()));

         retBuilder = retBuilder.setDefaultCredentialsProvider(credsProvider);
      }

      return retBuilder;
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


   public void setConnectionTimeout(long sec) {
      this.connectionTimeout = sec * 1000;
   }


   public void setRequestTimeout(long sec) {
      this.requestTimeout = sec * 1000;
   }


   public long getConnectionTimeout() {
      return this.connectionTimeout / 1000;
   }


   public long getRequestTimeout() {
      return this.requestTimeout / 1000;
   }
}
