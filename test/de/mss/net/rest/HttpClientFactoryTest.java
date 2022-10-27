package de.mss.net.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.mss.net.AuthenticatedServer;
import de.mss.utils.exception.MssException;

public class HttpClientFactoryTest {

   @BeforeEach
   public void setUp() throws Exception {
      HttpClientFactory.initializeHttpClientFactory(null);
   }


   @SuppressWarnings("resource")
   @Test
   public void test() throws MssException {
      assertNotNull(HttpClientFactory.getHttpClient(new RestServer("http://localhost:8080/v1/info")));
   }


   @SuppressWarnings("resource")
   @Test
   public void testAuthenticatedProxy() throws MssException {
      final AuthenticatedServer a = new AuthenticatedServer("http://localhost:8080/info", "user", "pass");
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      s.setProxy(a);
      assertNotNull(HttpClientFactory.getHttpClient(s));
   }


   @SuppressWarnings("resource")
   @Test
   public void testClient() throws MssException {
      final CloseableHttpClient c = HttpClientFactory.getHttpClient(new RestServer("http://localhost:8080/v1/info"));
      HttpClientFactory.initializeHttpClientFactory(c);

      assertEquals(c, HttpClientFactory.getHttpClient(new RestServer("http://localhost:8080/v2/info")));
   }


   @SuppressWarnings("resource")
   @Test
   public void testPassword() throws MssException {
      final AuthenticatedServer a = new AuthenticatedServer("http://localhost:8080/info", "user", "pass");
      a.setUserName(null);
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      s.setProxy(a);
      assertNotNull(HttpClientFactory.getHttpClient(s));
   }


   @SuppressWarnings("resource")
   @Test
   public void testProxy() throws MssException {
      assertNotNull(HttpClientFactory.getHttpClient(new RestServer("http://localhost:8080/v1/info", "http://localhost:8080/proxy")));
   }


   @SuppressWarnings("resource")
   @Test
   public void testUser() throws MssException {
      final AuthenticatedServer a = new AuthenticatedServer("http://localhost:8080/info", "user", "pass");
      a.setPassword(null);
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      s.setProxy(a);
      assertNotNull(HttpClientFactory.getHttpClient(s));
   }
}
