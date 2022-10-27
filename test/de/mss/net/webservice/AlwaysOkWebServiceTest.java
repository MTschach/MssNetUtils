package de.mss.net.webservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jetty.server.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.mss.net.rest.RestMethod;

public class AlwaysOkWebServiceTest {

   private AlwaysOkWebService<WebServiceTestRequest, WebServiceTestResponse> classUnderTest;


   @BeforeEach
   public void setUp() throws Exception {
      this.classUnderTest = new AlwaysOkWebService<>(WebServiceTestRequest::new, WebServiceTestResponse::new);
   }


   @Test
   public void testError() {
      assertTrue(
            new AlwaysOkWebServiceForTest<>(WebServiceTestResponse::new)
                  .handleRequest("loggingId", "", null, new Request(null, null), new HttpServletTestRequest(), new HttpServletTestResponse()));
   }


   @Test
   public void testOk() {
      assertEquals("", this.classUnderTest.getPath());
      assertEquals(RestMethod.UNKNOWN, this.classUnderTest.getMethod());
      assertEquals(RestMethod.UNKNOWN.getMethod(), this.classUnderTest.getMethodAsString());

      assertTrue(
            this.classUnderTest
                  .handleRequest("loggingId", "", null, new Request(null, null), new HttpServletTestRequest(), new HttpServletTestResponse()));
   }

}
