package de.mss.net.webservice;

import org.eclipse.jetty.server.Request;
import org.junit.Test;

import de.mss.net.rest.RestMethod;
import junit.framework.TestCase;

public class AlwaysOkWebServiceTest extends TestCase {

   private AlwaysOkWebService<WebServiceTestRequest, WebServiceTestResponse> classUnderTest;


   @Override
   public void setUp() throws Exception {
      super.setUp();

      this.classUnderTest = new AlwaysOkWebService<>(WebServiceTestRequest::new, WebServiceTestResponse::new);
   }


   @Test
   public void testError() {
      assertTrue(
            "handleRequest",
            new AlwaysOkWebServiceForTest<>(WebServiceTestResponse::new)
                  .handleRequest("loggingId", "", null, new Request(null, null), new HttpServletTestRequest(), new HttpServletTestResponse()));
   }


   @Test
   public void testOk() {
      assertEquals("Path", "", this.classUnderTest.getPath());
      assertEquals("Method", RestMethod.UNKNOWN, this.classUnderTest.getMethod());
      assertEquals("Method", RestMethod.UNKNOWN.getMethod(), this.classUnderTest.getMethodAsString());

      assertTrue(
            "handleRequest",
            this.classUnderTest
                  .handleRequest("loggingId", "", null, new Request(null, null), new HttpServletTestRequest(), new HttpServletTestResponse()));
   }

}
