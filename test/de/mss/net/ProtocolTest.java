package de.mss.net;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import de.mss.utils.exception.MssException;

public class ProtocolTest {

   private void checkProtocol(Protocol expected, Protocol is) {
      assertNotNull(is);
      assertEquals(expected.getProtocol(), is.getProtocol());
      assertEquals(expected.useSsl(), is.useSsl());

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
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_PROTOCOL_NOT_SUPPORTED, e.getError());
      }
   }
}
