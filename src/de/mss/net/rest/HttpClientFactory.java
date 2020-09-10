package de.mss.net.rest;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import de.mss.net.AuthenticatedServer;
import de.mss.utils.Tools;

public class HttpClientFactory {

   static CloseableHttpClient client = null;


   private HttpClientFactory() {}


   public static void initializeHttpClientFactory(CloseableHttpClient c) {
      HttpClientFactory.client = c;
   }


   public static CloseableHttpClient getHttpClient(RestServer server) {
      if (HttpClientFactory.client != null)
         return HttpClientFactory.client;

      HttpClientBuilder clientBuilder = HttpClients.custom();

      clientBuilder = applyProxy(clientBuilder, server.getProxy());

      return clientBuilder.build();
   }


   private static HttpClientBuilder applyProxy(HttpClientBuilder clientBuilder, AuthenticatedServer proxy) {
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


}
