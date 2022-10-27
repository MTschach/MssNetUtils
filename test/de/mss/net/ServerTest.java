package de.mss.net;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.mss.utils.exception.MssException;

public class ServerTest {


   @SuppressWarnings("unused")
   @Test
   public void testOk() throws MssException {
      Server s = new Server("localhost");

      assertEquals(Protocol.HTTP, s.getProtocol());
      assertEquals("localhost", s.getHost());
      assertEquals(Integer.valueOf(80), s.getPort());
      assertNull(s.getUrl());
      assertEquals("http://localhost:80", s.getCompleteUrl());

      assertEquals("http://localhost:83", new Server("localhost:83").getCompleteUrl());
      assertEquals("http://localhost:84/v1/test", new Server("localhost:84/v1/test").getCompleteUrl());
      assertEquals("https://localhost:443/v1", new Server("https://localhost:443/v1").getCompleteUrl());

      assertEquals("http://localhost:80", new Server(Protocol.HTTP, "localhost").getCompleteUrl());
      assertEquals("http://localhost:81", new Server(Protocol.HTTP, "localhost", 81).getCompleteUrl());
      assertEquals("http://localhost:82/v2/test", new Server(Protocol.HTTP, "localhost", 82, "/v2/test").getCompleteUrl());
      assertEquals("http://localhost:83", new Server(Protocol.HTTP, "localhost", Integer.valueOf(83)).getCompleteUrl());
      assertEquals(
            "http://localhost:84/v2/test",
            new Server(Protocol.HTTP, "localhost", Integer.valueOf(84), "/v2/test").getCompleteUrl());

      assertEquals("https://localhost:80", new Server("https", "localhost").getCompleteUrl());
      assertEquals("https://localhost:444", new Server("https", "localhost", 444).getCompleteUrl());
      assertEquals("https://localhost:445/v2/test", new Server("https", "localhost", 445, "/v2/test").getCompleteUrl());
      assertEquals("https://localhost:446", new Server("https", "localhost", Integer.valueOf(446)).getCompleteUrl());
      assertEquals(
            "https://localhost:447/v2/test",
            new Server("https", "localhost", Integer.valueOf(447), "/v2/test").getCompleteUrl());

      s = new Server(null);
      s.setHost("localhost");
      s.setPort(80);
      s.setProtocol(Protocol.HTTP);
      s.setUrl("/v3/test");

      assertEquals("http://localhost:80/v3/test", s.getCompleteUrl());

      s.setPort(Integer.valueOf(443));
      s.setProtocol("https");
      assertEquals("https://localhost:443/v3/test", s.getCompleteUrl());


      try {
         new Server("https://localhost:blah");
      }
      catch (final MssException e) {
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_NOT_PARSABLE, e.getError());
      }

      s = new Server("localhost");
      s.setPort(Integer.valueOf(0));
      assertEquals("http://localhost:80", s.getCompleteUrl());

      s.setPort(Integer.valueOf(235436));
      assertEquals("http://localhost:80", s.getCompleteUrl());

      s.setProtocol((Protocol)null);
      s.setHost(null);
      s.setPort(null);
      s.setUrl(null);

      assertEquals(":80", s.getCompleteUrl());
   }

}
