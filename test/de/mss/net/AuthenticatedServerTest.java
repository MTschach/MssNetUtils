package de.mss.net;

import org.junit.Test;

import de.mss.utils.exception.MssException;
import junit.framework.TestCase;

public class AuthenticatedServerTest extends TestCase {


   @SuppressWarnings("unused")
   @Test
   public void testOk() throws MssException {
      AuthenticatedServer s = new AuthenticatedServer("localhost");

      assertEquals("protocol", Protocol.HTTP, s.getProtocol());
      assertEquals("host", "localhost", s.getHost());
      assertEquals("port", Integer.valueOf(80), s.getPort());
      assertNull("url", s.getUrl());
      assertEquals("complete url", "http://localhost:80", s.getCompleteUrl());

      assertEquals("complete url 2", "http://localhost:83", new AuthenticatedServer("localhost:83").getCompleteUrl());
      assertEquals("complete url 3", "http://localhost:84/v1/test", new AuthenticatedServer("localhost:84/v1/test").getCompleteUrl());
      assertEquals("complete url 4", "https://localhost:443/v1", new AuthenticatedServer("https://localhost:443/v1").getCompleteUrl());

      assertEquals("complete url 5", "http://localhost:80", new AuthenticatedServer(Protocol.HTTP, "localhost").getCompleteUrl());
      assertEquals("complete url 6", "http://localhost:81", new AuthenticatedServer(Protocol.HTTP, "localhost", 81).getCompleteUrl());
      assertEquals(
            "complete url 7",
            "http://localhost:82/v2/test",
            new AuthenticatedServer(Protocol.HTTP, "localhost", 82, "/v2/test").getCompleteUrl());
      assertEquals(
            "complete url 8",
            "http://localhost:83",
            new AuthenticatedServer(Protocol.HTTP, "localhost", Integer.valueOf(83)).getCompleteUrl());
      assertEquals(
            "complete url 9",
            "http://localhost:84/v2/test",
            new AuthenticatedServer(Protocol.HTTP, "localhost", Integer.valueOf(84), "/v2/test").getCompleteUrl());


      assertEquals("complete url 10", "https://localhost:80", new AuthenticatedServer("https", "localhost").getCompleteUrl());
      assertEquals("complete url 11", "https://localhost:444", new AuthenticatedServer("https", "localhost", 444).getCompleteUrl());
      assertEquals(
            "complete url 12",
            "https://localhost:445/v2/test",
            new AuthenticatedServer("https", "localhost", 445, "/v2/test").getCompleteUrl());
      assertEquals("complete url 13", "https://localhost:446", new AuthenticatedServer("https", "localhost", Integer.valueOf(446)).getCompleteUrl());
      assertEquals(
            "complete url 14",
            "https://localhost:447/v2/test",
            new Server("https", "localhost", Integer.valueOf(447), "/v2/test").getCompleteUrl());

      s = new AuthenticatedServer(null);
      s.setHost("localhost");
      s.setPort(80);
      s.setProtocol(Protocol.HTTP);
      s.setUrl("/v3/test");

      assertEquals("complete url 15", "http://localhost:80/v3/test", s.getCompleteUrl());

      s.setPort(Integer.valueOf(443));
      s.setProtocol("https");
      assertEquals("complete url 16", "https://localhost:443/v3/test", s.getCompleteUrl());


      try {
         new Server("https://localhost:blah");
      }
      catch (final MssException e) {
         assertEquals("invalid port", de.mss.net.exception.ErrorCodes.ERROR_NOT_PARSABLE, e.getError());
      }

      s = new AuthenticatedServer("localhost");
      s.setPort(Integer.valueOf(0));
      assertEquals("complete url 17", "http://localhost:80", s.getCompleteUrl());

      s.setPort(Integer.valueOf(235436));
      assertEquals("complete url 18", "http://localhost:80", s.getCompleteUrl());

      s.setProtocol((Protocol)null);
      s.setHost(null);
      s.setPort(null);
      s.setUrl(null);

      assertEquals("complete url 19", ":80", s.getCompleteUrl());
   }


   @Test
   public void testWithCredentials() throws MssException {
      assertEquals("complete url 1", "http://localhost:80", new AuthenticatedServer("localhost", "user", "passwd").getCompleteUrl());
      assertEquals("complete url 1", "http://localhost:80", new AuthenticatedServer("http", "localhost", "user", "passwd").getCompleteUrl());
      assertEquals("complete url 1", "http://localhost:80", new AuthenticatedServer(Protocol.HTTP, "localhost", "user", "passwd").getCompleteUrl());
      assertEquals("complete url 1", "http://localhost:80", new AuthenticatedServer("http", "localhost", 80, "user", "passwd").getCompleteUrl());
      assertEquals(
            "complete url 1",
            "http://localhost:80",
            new AuthenticatedServer(Protocol.HTTP, "localhost", 80, "user", "passwd").getCompleteUrl());
      assertEquals(
            "complete url 1",
            "http://localhost:80",
            new AuthenticatedServer("http", "localhost", Integer.valueOf(80), "user", "passwd").getCompleteUrl());
      assertEquals(
            "complete url 1",
            "http://localhost:80",
            new AuthenticatedServer(Protocol.HTTP, "localhost", Integer.valueOf(80), "user", "passwd").getCompleteUrl());
      assertEquals(
            "complete url 1",
            "http://localhost:80/v1",
            new AuthenticatedServer("http", "localhost", 80, "/v1", "user", "passwd").getCompleteUrl());
      assertEquals(
            "complete url 1",
            "http://localhost:80/v1",
            new AuthenticatedServer(Protocol.HTTP, "localhost", 80, "/v1", "user", "passwd").getCompleteUrl());
      assertEquals(
            "complete url 1",
            "http://localhost:80/v1",
            new AuthenticatedServer("http", "localhost", Integer.valueOf(80), "/v1").getCompleteUrl());
      assertEquals(
            "complete url 1",
            "http://localhost:80/v1",
            new AuthenticatedServer("http", "localhost", Integer.valueOf(80), "/v1", "user", "passwd").getCompleteUrl());
      assertEquals(
            "complete url 1",
            "http://localhost:80/v1",
            new AuthenticatedServer(Protocol.HTTP, "localhost", Integer.valueOf(80), "/v1", "user", "passwd").getCompleteUrl());

      final AuthenticatedServer s = new AuthenticatedServer("localhost", "user", "passwd");
      assertEquals("username", "user", s.getUser());
      assertEquals("password", "passwd", s.getPassword());
   }
}
