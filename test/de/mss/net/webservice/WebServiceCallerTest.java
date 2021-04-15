package de.mss.net.webservice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.apache.logging.log4j.LogManager;
import org.easymock.EasyMock;
import org.junit.Test;

import de.mss.net.rest.HttpClientFactory;
import de.mss.net.rest.RestExecutor;
import de.mss.net.rest.RestMethod;
import de.mss.net.rest.RestServer;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;
import junit.framework.TestCase;

public class WebServiceCallerTest extends TestCase {

   private WebServiceCallerForTest<WebServiceTestRequest, WebServiceTestResponse> classUnderTest;
   private RestServer[]                                                           restServers;
   private CloseableHttpClient                                                    httpClientMock;
   private CloseableHttpResponse                                                  httpResponseMock;


   private void checkResponse(WebServiceTestResponse resp, String binaryContent, int errorCode) {
      assertNotNull("Response is not null", resp);
      assertEquals("ErrorCode", Integer.valueOf(errorCode), resp.getErrorCode());
      if (binaryContent == null) {
         assertNull("binaryContent", resp.getBinaryContent());
      } else {
         assertEquals("binaryContent", binaryContent, new String(resp.getBinaryContent()));
      }
   }


   private WebServiceTestRequest getRequest() throws IOException {
      final WebServiceTestRequest req = new WebServiceTestRequest();

      req.setBigDval(BigDecimal.ONE);
      req.setBigVal(BigInteger.TEN);
      req.setBirthday(new java.util.Date());
      req.setCheckInterval(30);
      req.setCustomerNumber(1234);
      req.setDoubleVal(2.3);
      req.setFloatVal(1.2f);
      req.setName("name");
      req.setSessionId("session");
      req.setUserName("username");

      return req;
   }


   private void replay() {
      EasyMock.replay(this.httpClientMock);
      EasyMock.replay(this.httpResponseMock);
   }


   @Override
   public void setUp() throws Exception {
      super.setUp();

      this.classUnderTest = new WebServiceCallerForTest<>();
      this.restServers = new RestServer[] {new RestServer("localhost:8080:/v1/info")};

      this.httpClientMock = EasyMock.createNiceMock(CloseableHttpClient.class);
      this.httpResponseMock = EasyMock.createNiceMock(CloseableHttpResponse.class);

      HttpClientFactory.initializeHttpClientFactory(this.httpClientMock);
   }


   @SuppressWarnings("resource")
   private void setupHttpClient(boolean ok) throws ClientProtocolException, IOException {
      if (ok) {
         EasyMock.expect(this.httpClientMock.execute(EasyMock.anyObject())).andReturn(this.httpResponseMock);
      } else {
         EasyMock.expect(this.httpClientMock.execute(EasyMock.anyObject())).andThrow(new IOException());
      }
   }


   private void setupHttpResponse(String content, String binaryContent, int statusCode) {
      final StatusLine statLine = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), statusCode, null);
      EasyMock.expect(this.httpResponseMock.getStatusLine()).andReturn(statLine).anyTimes();

      final BasicHttpEntity entity = new BasicHttpEntity();
      if (content != null) {
         entity.setContent(new ByteArrayInputStream(content.getBytes()));
      }

      if (binaryContent != null) {
         entity.setContent(new ByteArrayInputStream(binaryContent.getBytes()));
      }
      EasyMock.expect(this.httpResponseMock.getEntity()).andReturn(entity).anyTimes();

      if (RestExecutor.isRedirect(statusCode)) {
         final List<Header> hl = new ArrayList<>();
         hl.add(new BasicHeader("location", "https://localhost/v2/info"));
         EasyMock.expect(this.httpResponseMock.getAllHeaders()).andReturn(hl.toArray(new Header[hl.size()])).anyTimes();
         if (statusCode == 301) {
            EasyMock
                  .expect(this.httpResponseMock.getFirstHeader(EasyMock.eq("location")))
                  .andReturn(null);
         } else {
            EasyMock
                  .expect(this.httpResponseMock.getFirstHeader(EasyMock.eq("location")))
                  .andReturn(new BasicHeader("location", "https://localhost/v2/info"));
         }
      } else {
         EasyMock.expect(this.httpResponseMock.getAllHeaders()).andReturn(null).anyTimes();
      }
   }


   @Override
   public void tearDown() throws Exception {
      super.tearDown();

      verify();
   }


   @Test
   public void testCallLoggingInHeaderOk() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());

      setupHttpClient(true);
      setupHttpResponse("ok", null, 200);

      replay();

      final WebServiceTestRequest request = getRequest();
      request.setLoggingId(loggingId);

      final WebServiceTestResponse resp = this.classUnderTest
            .call(loggingId, this.restServers, "/v1/info", RestMethod.GET, request, new WebServiceTestResponse(), 1);

      checkResponse(resp, null, 0);
   }


   @Test
   public void testCallNoContentOk() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());

      setupHttpClient(true);
      setupHttpResponse(null, null, 200);

      replay();

      final WebServiceTestResponse resp = this.classUnderTest
            .call(loggingId, this.restServers, "/v1/info", RestMethod.GET, new WebServiceTestRequest(), new WebServiceTestResponse(), 1);

      checkResponse(resp, null, 0);
   }


   @Test
   public void testCallNoLoggingIdOk() throws MssException, ClientProtocolException, IOException {
      setupHttpClient(true);
      setupHttpResponse("ok", null, 200);

      replay();

      final WebServiceTestResponse resp = this.classUnderTest
            .call(null, this.restServers, "/v1/info", RestMethod.GET, new WebServiceTestRequest(), new WebServiceTestResponse(), 1);

      checkResponse(resp, null, 0);
   }


   @Test
   public void testCallNoLoggingInHeaderOk() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());

      setupHttpClient(true);
      setupHttpResponse("ok", null, 200);

      replay();

      final WebServiceTestRequest request = getRequest();

      final WebServiceTestResponse resp = this.classUnderTest
            .call(loggingId, this.restServers, "/v1/info", RestMethod.GET, request, new WebServiceTestResponse(), 1);

      checkResponse(resp, null, 0);
   }


   @Test
   public void testCallNotOk() throws ClientProtocolException, IOException, MssException {
      final String loggingId = Tools.getId(new Throwable());

      setupHttpClient(false);
      setupHttpClient(false);
      setupHttpClient(false);

      setupHttpClient(false);
      setupHttpClient(false);
      setupHttpClient(false);

      setupHttpClient(false);
      setupHttpClient(false);
      setupHttpClient(true);
      setupHttpResponse("ok", null, 404);

      replay();

      final WebServiceTestResponse resp = this.classUnderTest
            .call(loggingId, this.restServers, "/v1/info", RestMethod.GET, new WebServiceTestRequest(), new WebServiceTestResponse(), 3);

      checkResponse(resp, null, 6006);
   }


   @Test
   public void testCallNullClientResponse() throws ClientProtocolException, IOException, MssException {
      final String loggingId = Tools.getId(new Throwable());

      setupHttpClient(false);
      setupHttpClient(false);
      setupHttpClient(false);

      replay();

      final WebServiceTestResponse resp = this.classUnderTest
            .call(loggingId, this.restServers, "/v1/info", RestMethod.GET, new WebServiceTestRequest(), new WebServiceTestResponse(), 1);

      checkResponse(resp, null, 6005);
   }


   @Test
   public void testCallNullResponse() throws MssException {
      final String loggingId = Tools.getId(new Throwable());
      replay();
      assertNull(
            this.classUnderTest
                  .call(loggingId, this.restServers, "/v1/info", RestMethod.GET, new WebServiceTestRequest(), null, 1));
   }


   @Test
   public void testCallOk() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());

      setupHttpClient(true);
      setupHttpResponse("ok", null, 200);

      replay();

      final WebServiceTestResponse resp = this.classUnderTest
            .call(loggingId, this.restServers, "/v1/info", RestMethod.GET, new WebServiceTestRequest(), new WebServiceTestResponse(), 1);

      checkResponse(resp, null, 0);
   }


   @Test
   public void testCallRetry() throws ClientProtocolException, IOException, MssException {
      final String loggingId = Tools.getId(new Throwable());

      setupHttpClient(false);
      setupHttpClient(false);
      setupHttpClient(false);

      setupHttpClient(true);
      setupHttpResponse("ok", null, 200);

      replay();

      final WebServiceTestResponse resp = this.classUnderTest
            .call(loggingId, this.restServers, "/v1/info", RestMethod.GET, new WebServiceTestRequest(), new WebServiceTestResponse(), 3);

      checkResponse(resp, null, 0);
   }


   @Test
   public void testCallWithBinaryContentOk() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());

      this.classUnderTest.setBinaryContent();

      setupHttpClient(true);
      setupHttpResponse("ok", "binaryContent", 200);

      replay();

      final WebServiceTestResponse resp = this.classUnderTest
            .call(loggingId, this.restServers, "/v1/info", RestMethod.GET, new WebServiceTestRequest(), new WebServiceTestResponse(), 1);

      this.classUnderTest.unsetBinaryContent();
      checkResponse(resp, "binaryContent", 0);
   }


   @Test
   public void testDateFormat() {
      replay();
      assertEquals("yyyyMMdd'T'HHmmssSSSZ", this.classUnderTest.getDateFormat());
      this.classUnderTest.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      assertEquals("yyyy-MM-dd HH:mm:ss.SSS", this.classUnderTest.getDateFormat());
   }


   @Test
   public void testLogger() {
      replay();
      assertNotNull(WebServiceCaller.getLogger());
      WebServiceCaller.setLogger(LogManager.getRootLogger());
      assertNotNull(WebServiceCaller.getLogger());
   }


   private void verify() {
      EasyMock.verify(this.httpClientMock);
      EasyMock.verify(this.httpResponseMock);
   }
}
