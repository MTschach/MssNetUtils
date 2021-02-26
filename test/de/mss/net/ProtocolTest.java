package de.mss.net;

import org.junit.Test;

import de.mss.utils.exception.MssException;
import junit.framework.TestCase;

public class ProtocolTest extends TestCase {

   private void checkProtocol(Protocol expected, Protocol is) {
      assertNotNull("is not null", is);
      assertEquals("Protocol", expected.getProtocol(), is.getProtocol());
      assertEquals("use ssl", expected.useSsl(), is.useSsl());

   }


   @Test
   public void test() throws MssException {
      checkProtocol(Protocol.HTTP, Protocol.getByProtocol("http"));
      checkProtocol(Protocol.HTTPS, Protocol.getByProtocol("https"));

      try {
         Protocol.getByProtocol("");
         fail("no exception was thrown");
      }
      catch (final MssException e) {
         assertEquals("ErrorCode", de.mss.net.exception.ErrorCodes.ERROR_PROTOCOL_NOT_SUPPORTED, e.getError());
      }
   }
}
