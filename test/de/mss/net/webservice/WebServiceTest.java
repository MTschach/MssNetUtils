package de.mss.net.webservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.easymock.EasyMock;
import org.eclipse.jetty.server.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.mss.net.rest.RestMethod;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;
import jakarta.servlet.http.HttpServletResponse;

public class WebServiceTest {

   private WebServiceForTest classUnderTest;


   private Map<String, String> getParams(String loggingId) {
      final Map<String, String> map = new HashMap<>();

      map.put("loggingId", loggingId == null ? "loggingId" : loggingId);
      map.put("sessionId", "sessionId");
      map.put("customerNumber", "1234567");
      map.put("username", "username");
      map.put("name", "name");
      map.put("checkInterval", "30");
      map.put("birthDate", "2000-01-01");
      map.put("bigDval", "1.23");
      map.put("bigVal", "123");
      map.put("doubleVal", "2.34");
      map.put("floatVal", "3.45");
      map.put("enumVal", "simple");


      return map;
   }


   @BeforeEach
   public void setUp() throws Exception {
      this.classUnderTest = new WebServiceForTest(WebServiceTestRequest::new, WebServiceTestResponse::new);
   }


   @Test
   public void testCheckRequest() throws MssException {
      this.classUnderTest.checkRequest("", new WebServiceTestRequest());
      try {
         this.classUnderTest.checkRequest("", null);
         fail("no exception was thrown");
      }
      catch (final MssException e) {
         assertEquals(de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM, e.getError());
      }
   }


   @Test
   public void testGetDefaultOkResponse() {
      final WebServiceResponse resp = WebService.getDefaultOkResponse();

      assertNotNull(resp);
      assertNull(resp.getErrorCode());
      assertEquals(Integer.valueOf(200), resp.getStatusCode());
      assertNull(resp.getErrorText());
      assertNull(resp.getBinaryContent());
   }


   @Test
   public void testGetMethod() {
      assertEquals(RestMethod.GET, this.classUnderTest.getMethod());
      this.classUnderTest.setMethod(RestMethod.POST);
      assertEquals(RestMethod.POST, this.classUnderTest.getMethod());
   }


   @Test
   public void testGetParsedRequestOk() throws MssException {
      final Map<String, String> params = getParams(null);

      final WebServiceTestRequest req = this.classUnderTest.getParsedRequestForTest(null, params, (Request)null);

      assertNotNull(req);
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
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_NOT_PARSABLE, e.getError());
      }
   }


   @Test
   public void testGetPath() {
      assertEquals("v1/info", this.classUnderTest.getPath());
   }


   @Test
   public void testGetUrlParams() {
      Map<String, String> params = WebServiceForTest.getUrlParamsForTest(null);
      assertNotNull(params);
      assertTrue(params.isEmpty());

      params = WebServiceForTest.getUrlParamsForTest(new RequestForTest(null));
      assertNotNull(params);
      assertTrue(params.isEmpty());

      params = WebServiceForTest.getUrlParamsForTest(new RequestForTest("http://localhost:8080/v1/info"));
      assertNotNull(params);
      assertTrue(params.isEmpty());

      params = WebServiceForTest.getUrlParamsForTest(new RequestForTest("http://localhost:8080/v1/info?param1=value1&param2=value2"));
      assertNotNull(params);
      assertEquals(Integer.valueOf(2), Integer.valueOf(params.size()));
      assertEquals("value1", params.get("param1"));
      assertEquals("value2", params.get("param2"));
   }


   @Test
   public void testHandleException() throws IOException {
      final String loggingId = Tools.getId(new Throwable());

      final WebServiceTestResponse resp = new WebServiceTestResponse();

      final HttpServletResponse respMock = EasyMock.createNiceMock(HttpServletResponse.class);

      respMock.setStatus(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
      EasyMock.expectLastCall();
      respMock.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyString());
      EasyMock.expectLastCall();

      respMock.setStatus(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
      EasyMock.expectLastCall();
      respMock.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyString());
      EasyMock.expectLastCall();

      respMock.setStatus(EasyMock.eq(123));
      EasyMock.expectLastCall();
      respMock.sendError(EasyMock.eq(123), EasyMock.anyString());
      EasyMock.expectLastCall();

      respMock.setStatus(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
      EasyMock.expectLastCall();
      respMock.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyString());
      EasyMock.expectLastCall();

      respMock.setStatus(EasyMock.eq(405));
      EasyMock.expectLastCall();
      respMock.sendError(EasyMock.eq(405), EasyMock.anyString());
      EasyMock.expectLastCall();

      respMock.setStatus(EasyMock.eq(405));
      EasyMock.expectLastCall();
      respMock.sendError(EasyMock.eq(405), EasyMock.anyString());
      EasyMock.expectLastCall().andThrow(new IOException());

      EasyMock.replay(respMock);

      assertEquals(
            Integer.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
            Integer
                  .valueOf(
                        this.classUnderTest
                              .handleExceptionForTest(
                                    loggingId,
                                    new MssException(de.mss.net.exception.ErrorCodes.ERROR_NO_RESPONSE),
                                    null,
                                    respMock)));


      assertEquals(
            Integer.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
            Integer
                  .valueOf(
                        this.classUnderTest
                              .handleExceptionForTest(
                                    loggingId,
                                    new MssException(de.mss.net.exception.ErrorCodes.ERROR_NO_RESPONSE),
                                    resp,
                                    respMock)));

      resp.setStatusCode(123);
      assertEquals(
            Integer.valueOf(123),
            Integer
                  .valueOf(
                        this.classUnderTest
                              .handleExceptionForTest(
                                    loggingId,
                                    new MssException(de.mss.net.exception.ErrorCodes.ERROR_NO_RESPONSE),
                                    resp,
                                    respMock)));

      resp.setStatusCode(null);
      assertEquals(
            Integer.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
            Integer
                  .valueOf(
                        this.classUnderTest
                              .handleExceptionForTest(
                                    loggingId,
                                    new MssException(123),
                                    resp,
                                    respMock)));

      resp.setStatusCode(123);
      assertEquals(
            Integer.valueOf(405),
            Integer
                  .valueOf(
                        this.classUnderTest
                              .handleExceptionForTest(
                                    loggingId,
                                    new MssException(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED),
                                    resp,
                                    respMock)));

      assertEquals(
            Integer.valueOf(405),
            Integer
                  .valueOf(
                        this.classUnderTest
                              .handleExceptionForTest(
                                    loggingId,
                                    new MssException(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED),
                                    resp,
                                    respMock)));

      EasyMock.verify(respMock);
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
      assertNotNull(this.classUnderTest.handleRequest(null, new WebServiceTestRequest()));
   }
}
