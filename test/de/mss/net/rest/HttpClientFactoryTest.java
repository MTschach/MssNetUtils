package de.mss.net.rest;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import de.mss.net.AuthenticatedServer;
import de.mss.utils.exception.MssException;
import junit.framework.TestCase;

public class HttpClientFactoryTest extends TestCase {

   @Override
   public void setUp() throws Exception {
      super.setUp();
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


   @Test
   public void testUser() throws MssException {
      final AuthenticatedServer a = new AuthenticatedServer("http://localhost:8080/info", "user", "pass");
      a.setPassword(null);
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      s.setProxy(a);
      assertNotNull(HttpClientFactory.getHttpClient(s));
   }
}
