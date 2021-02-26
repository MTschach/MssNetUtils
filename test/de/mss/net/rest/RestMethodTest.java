package de.mss.net.rest;

import org.junit.Test;

import de.mss.utils.exception.MssException;
import junit.framework.TestCase;

public class RestMethodTest extends TestCase {


   private void checkRestMethod(RestMethod expected, RestMethod is) {
      assertNotNull("is not null", is);
      assertEquals("method", expected.getMethod(), is.getMethod());
   }


   @Test
   public void testException() {
      try {
         RestMethod.getByMethod("blah", () -> {
            return new MssException(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED, "Method 'blah' is not supported");
         });
         fail("no exception was thrown");
      }
      catch (final MssException e) {
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED, e.getError());
         assertEquals("Method 'blah' is not supported", e.getAltErrorText());
      }
   }


   @Test
   public void testOk() throws MssException {
      checkRestMethod(RestMethod.DELETE, RestMethod.getByMethod("DELETE", null));
      checkRestMethod(RestMethod.GET, RestMethod.getByMethod("GET", null));
      checkRestMethod(RestMethod.PATCH, RestMethod.getByMethod("PATCH", null));
      checkRestMethod(RestMethod.POST, RestMethod.getByMethod("POST", null));

      checkRestMethod(RestMethod.UNKNOWN, RestMethod.getByMethod("", null));
      assertNull(RestMethod.getByMethod("blah", null));
   }
}
