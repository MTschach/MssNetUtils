package de.mss.net.webservice;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.server.Request;
import org.junit.Test;

import de.mss.net.rest.RestMethod;
import de.mss.utils.exception.MssException;
import junit.framework.TestCase;

public class WebServiceTest extends TestCase {

   private WebServiceForTest classUnderTest;


   private Map<String, String> getParams(String loggingId) {
      final Map<String, String> map = new HashMap<>();

      map.put("loggingId", loggingId == null ? "loggingId" : loggingId);
      map.put("sessionId", "sessionId");
      map.put("customerNumber", "1234567");
      map.put("username", "username");
      map.put("name", "name");
      map.put("checkIntervale", "30");
      map.put("birthDate", "2000-01-01");
      map.put("bigDval", "1.23");
      map.put("bigVal", "123");
      map.put("doubleVal", "2.34");
      map.put("floatVal", "3.45");

      return map;
   }


   @Override
   public void setUp() throws Exception {
      super.setUp();

      this.classUnderTest = new WebServiceForTest(WebServiceTestRequest::new, WebServiceTestResponse::new);
   }


   @Override
   public void tearDown() throws Exception {
      super.tearDown();
   }


   @Test
   public void testGetDefaultOkResponse() {
      final WebServiceResponse resp = WebService.getDefaultOkResponse();

      assertNotNull("Response is not null", resp);
      assertNull("ErrorCode", resp.getErrorCode());
      assertEquals("StatusCode", Integer.valueOf(200), resp.getStatusCode());
      assertNull("ErrorText", resp.getErrorText());
      assertNull("Binary content", resp.getBinaryContent());
   }


   @Test
   public void testGetMethod() {
      assertEquals("Method GET", RestMethod.GET.getMethod(), this.classUnderTest.getMethod());
      this.classUnderTest.setMethod(RestMethod.POST);
      assertEquals("Method GET", RestMethod.POST.getMethod(), this.classUnderTest.getMethod());
   }


   @Test
   public void testGetParsedRequestOk() throws MssException {
      final Map<String, String> params = getParams(null);

      final WebServiceTestRequest req = this.classUnderTest.getParsedRequestForTest(null, params, (Request)null);

      assertNotNull("Request is not null", req);
   }


   @Test
   public void testGetParsedRequestParseError() {
      final Map<String, String> params = getParams(null);

      params.put("name", "exception");
      try {
         this.classUnderTest.getParsedRequestForTest(null, params, (Request)null);
         fail("no exception was thrown");
      }
      catch (final MssException e) {
         assertNotNull(e);
         assertEquals("ErrorCode", de.mss.net.exception.ErrorCodes.ERROR_NOT_PARSABLE, e.getError());
      }
   }


   @Test
   public void testGetPath() {
      assertEquals("Path", "v1/info", this.classUnderTest.getPath());
   }


   @Test
   public void testGetUrlParams() throws UnsupportedEncodingException {
      Map<String, String> params = WebServiceForTest.getUrlParamsForTest(null);
      assertNotNull("params are not null", params);
      assertTrue("params are empty", params.isEmpty());

      params = WebServiceForTest.getUrlParamsForTest(new RequestForTest(null));
      assertNotNull("params are not null", params);
      assertTrue("params are empty", params.isEmpty());

      params = WebServiceForTest.getUrlParamsForTest(new RequestForTest("http://localhost:8080/v1/info"));
      assertNotNull("params are not null", params);
      assertTrue("params are empty", params.isEmpty());

      params = WebServiceForTest.getUrlParamsForTest(new RequestForTest("http://localhost:8080/v1/info?param1=value1&param2=value2"));
      assertNotNull("params are not null", params);
      assertEquals("params size", Integer.valueOf(2), Integer.valueOf(params.size()));
      assertEquals("param1", "value1", params.get("param1"));
      assertEquals("param2", "value2", params.get("param2"));
   }


   @Test
   public void testHandleRequest() {
      //      this.classUnderTest.handleRequest(null, "v1/info", null, null, null, null);
   }


   @Test
   public void testLogger() {
      this.classUnderTest.setLogger(null);
      assertNotNull(this.classUnderTest.getLogger());
      this.classUnderTest.setLogger(LogManager.getRootLogger());
      assertNotNull(this.classUnderTest.getLogger());
   }


   @Test
   public void testSimpleHandleRequest() throws MssException {
      assertNotNull("Response", this.classUnderTest.handleRequest(null, new WebServiceTestRequest()));
   }
}
