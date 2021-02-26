package de.mss.net.rest;

import org.junit.Test;

import de.mss.net.AuthenticatedServer;
import de.mss.net.Server;
import de.mss.utils.exception.MssException;
import junit.framework.TestCase;

public class RestServerTest extends TestCase {

   @Test
   public void test() throws MssException {
      final RestServer r = new RestServer(null, null);
      final Server s = new Server("http://localhost:8080/v1/info");
      final AuthenticatedServer a = new AuthenticatedServer("http://localhost:8080/v2/info");
      r.setServer(s);
      r.setProxy(a);

      assertNotNull(r.getServer());
      assertNotNull(r.getProxy());
      assertEquals("http://localhost:8080/v1/info", r.getServer().getCompleteUrl());
      assertEquals("http://localhost:8080/v2/info", r.getProxy().getCompleteUrl());
   }


   @Test
   public void testProxy() throws MssException {
      final RestServer r = new RestServer(null, "http://localhost:8080/v2/info");

      assertNull(r.getServer());
      assertNotNull(r.getProxy());
      assertEquals("http://localhost:8080/v2/info", r.getProxy().getCompleteUrl());
   }


   @Test
   public void testUrl() throws MssException {
      final RestServer r = new RestServer("http://localhost:8080/v1/info");

      assertNotNull(r.getServer());
      assertNull(r.getProxy());
      assertEquals("http://localhost:8080/v1/info", r.getServer().getCompleteUrl());
   }

}
