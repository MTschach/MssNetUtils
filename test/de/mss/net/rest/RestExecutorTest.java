package de.mss.net.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import de.mss.net.Server;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;
import junit.framework.TestCase;

public class RestExecutorTest extends TestCase {

   private CloseableHttpClient   httpClientMock;
   private CloseableHttpResponse httpResponseMock;


   private RestRequest getRestRequest() {
      final RestRequest req = new RestRequest(RestMethod.GET);

      return req;
   }


   private void replay() {
      EasyMock.replay(this.httpClientMock);
      EasyMock.replay(this.httpResponseMock);
   }


   @Override
   public void setUp() throws Exception {
      super.setUp();
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


   private void setupHttpResponse(String encoding, String content, byte[] binaryContent, int statusCode) {
      final StatusLine statLine = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), statusCode, null);
      EasyMock.expect(this.httpResponseMock.getStatusLine()).andReturn(statLine).anyTimes();

      final BasicHttpEntity entity = new BasicHttpEntity();
      if (encoding != null) {
         entity.setContentEncoding(encoding);
      }

      if (content != null) {
         entity.setContent(new ByteArrayInputStream(content.getBytes()));
      }
      if (binaryContent != null) {
         entity.setContent(new ByteArrayInputStream(binaryContent));
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


   @Test
   public void test() throws MssException {
      final RestServer s = new RestServer("http://localhost:8080/v1/info");

      assertNotNull(new RestExecutor(s));
      assertNotNull(new RestExecutor(s, true));
      assertNotNull(new RestExecutor(s, LogManager.getLogger()));
      assertNotNull(new RestExecutor(s, LogManager.getLogger(), true));
      assertNotNull(new RestExecutor(new RestServer[] {s}));
      assertNotNull(new RestExecutor(new RestServer[] {s}, false));
      assertNotNull(new RestExecutor(new RestServer[] {s}, LogManager.getLogger()));
      assertNotNull(new RestExecutor(new RestServer[] {s}, LogManager.getLogger(), false));
      final List<RestServer> sl = new ArrayList<>();
      sl.add(s);
      assertNotNull(new RestExecutor(sl));
      assertNotNull(new RestExecutor(sl, true));
      assertNotNull(new RestExecutor(sl, LogManager.getLogger()));
      assertNotNull(new RestExecutor(sl, LogManager.getLogger(), true));
      assertNotNull(new RestExecutor((RestServer)null));

      assertNotNull(new RestExecutor(s).getConnectionTimeout());
      assertNotNull(new RestExecutor(s).getRequestTimeout());
      assertNotNull(new RestExecutor(s, LogManager.getLogger()).getLogger());
   }


   @Test
   public void testExecute() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      setupHttpResponse("UTF-8", "content\nwith new line", null, 200);
      setupHttpClient(true);

      replay();

      final RestResponse resp = e.executeRequest(loggingId, getRestRequest(), null);

      verify();

      assertNotNull(resp);
      assertEquals(Integer.valueOf(200), Integer.valueOf(resp.getHttpStatus()));
      assertEquals("content\nwith new line", resp.getContent());
      assertNull(resp.getBinaryContent());
   }


   @Test
   public void testExecuteBinary() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s, true);

      setupHttpResponse(null, null, "content\nwith new line".getBytes(), 200);
      setupHttpClient(true);

      replay();

      final RestResponse resp = e.executeRequest(loggingId, getRestRequest(), null);

      verify();

      assertNotNull(resp);
      assertEquals(Integer.valueOf(200), Integer.valueOf(resp.getHttpStatus()));
      assertNull(resp.getContent());
      assertEquals("content\nwith new line", new String(resp.getBinaryContent()));
   }


   @Test
   public void testExecuteNullRequest() throws MssException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      replay();

      try {
         e.executeRequest(loggingId, null, s, null);
         fail("no exception was thrown");
      }
      catch (final MssException ex) {
         assertEquals(de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM, ex.getError());
      }

      verify();
   }


   @Test
   public void testExecuteNullServer() throws MssException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      replay();

      try {
         e.executeRequest(loggingId, getRestRequest(), null, null);
         fail("no exception was thrown");
      }
      catch (final MssException ex) {
         assertEquals(de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM, ex.getError());
      }

      verify();
   }


   @Test
   public void testExecuteNullServerHost() throws MssException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);
      s.setServer((Server)null);

      replay();

      try {
         e.executeRequest(loggingId, getRestRequest(), s, null);
         fail("no exception was thrown");
      }
      catch (final MssException ex) {
         assertEquals(de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM, ex.getError());
      }

      verify();
   }


   @Test
   public void testExecuteUnsupportedMethod() throws MssException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      final RestRequest req = getRestRequest();
      req.setMethod(RestMethod.UNKNOWN);

      replay();

      try {
         e.executeRequest(loggingId, req, null);
         fail("no exception was thrown");
      }
      catch (final MssException ex) {
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_UNABLE_TO_EXECUTE_REQUEST, ex.getError());
         assertTrue(MssException.class.isInstance(ex.getCause()));
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED, ((MssException)ex.getCause()).getError());
      }

      verify();
   }


   @Test
   public void testExecuteWithAdditionalServer() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);
      e.addServer(new RestServer("https://localhost/v2/info"));

      setupHttpResponse(null, "content\nwith new line", null, 200);
      setupHttpClient(true);

      replay();

      final RestResponse resp = e.executeRequest(loggingId, getRestRequest(), null);

      verify();

      assertNotNull(resp);
      assertEquals(Integer.valueOf(200), Integer.valueOf(resp.getHttpStatus()));
      assertEquals("content\nwith new line", resp.getContent());
      assertNull(resp.getBinaryContent());
   }


   @Test
   public void testExecuteWithoutLoggingId() throws MssException, ClientProtocolException, IOException {
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      setupHttpClient(false);
      setupHttpClient(false);
      setupHttpClient(false);

      final RestRequest req = getRestRequest();
      req.setMethod(RestMethod.POST);

      replay();

      final RestResponse resp = e.executeRequest(null, req, null);

      verify();

      assertNull(resp);
   }


   @Test
   public void testExecuteWithoutLoggingId1() throws MssException, ClientProtocolException, IOException {
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      setupHttpClient(false);
      setupHttpClient(false);
      setupHttpClient(false);

      final RestRequest req = getRestRequest();
      req.setMethod(RestMethod.POST);

      replay();

      final RestResponse resp = e.executeRequest(null, req, s, null);

      verify();

      assertNull(resp);
   }


   @Test
   public void testExecuteWithoutMethod() throws MssException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      final RestRequest req = getRestRequest();
      req.setMethod((RestMethod)null);

      replay();

      try {
         e.executeRequest(loggingId, req, null);
         fail("no exception was thrown");
      }
      catch (final MssException ex) {
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_UNABLE_TO_EXECUTE_REQUEST, ex.getError());
         assertTrue(MssException.class.isInstance(ex.getCause()));
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED, ((MssException)ex.getCause()).getError());
      }

      verify();
   }


   @Test
   public void testExecuteWithParams() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      final RestRequest request = getRestRequest();
      request.addHeaderParam("headerparam", "headervalue");
      request.addPathParam("pathparam", "pathvalue");
      request.addPostParam("postparam", "postvalue");
      request.addUrlParam("urlparam", "urlvalue");

      setupHttpResponse("UTF-8", "content\nwith new line", null, 200);
      setupHttpClient(true);

      replay();

      final RestResponse resp = e.executeRequest(loggingId, request, null);

      verify();

      assertNotNull(resp);
      assertEquals(Integer.valueOf(200), Integer.valueOf(resp.getHttpStatus()));
      assertEquals("content\nwith new line", resp.getContent());
      assertNull(resp.getBinaryContent());
   }


   @Test
   public void testExecuteWithProxy() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info", "http://proxy:8080");
      s.getProxy().setPassword("passwd");
      s.getProxy().setUserName("user");
      final RestExecutor e = new RestExecutor(s);

      setupHttpResponse("UTF-8", "content\nwith new line", null, 200);
      setupHttpClient(true);

      replay();

      final RestResponse resp = e.executeRequest(loggingId, getRestRequest(), null);

      verify();

      assertNotNull(resp);
      assertEquals(Integer.valueOf(200), Integer.valueOf(resp.getHttpStatus()));
      assertEquals("content\nwith new line", resp.getContent());
      assertNull(resp.getBinaryContent());
   }


   @Test
   public void testExecuteWithProxyNoPass() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info", "http://proxy:8080");
      s.getProxy().setUserName("user");
      final RestExecutor e = new RestExecutor(s);

      setupHttpResponse("UTF-8", "content\nwith new line", null, 200);
      setupHttpClient(true);

      replay();

      final RestResponse resp = e.executeRequest(loggingId, getRestRequest(), null);

      verify();

      assertNotNull(resp);
      assertEquals(Integer.valueOf(200), Integer.valueOf(resp.getHttpStatus()));
      assertEquals("content\nwith new line", resp.getContent());
      assertNull(resp.getBinaryContent());
   }


   @Test
   public void testExecuteWithProxyNoUser() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info", "http://proxy:8080");
      s.getProxy().setPassword("passwd");
      final RestExecutor e = new RestExecutor(s);

      setupHttpResponse("UTF-8", "content\nwith new line", null, 200);
      setupHttpClient(true);

      replay();

      final RestResponse resp = e.executeRequest(loggingId, getRestRequest(), null);

      verify();

      assertNotNull(resp);
      assertEquals(Integer.valueOf(200), Integer.valueOf(resp.getHttpStatus()));
      assertEquals("content\nwith new line", resp.getContent());
      assertNull(resp.getBinaryContent());
   }


   @Test
   public void testExecuteWithRedirect() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      setupHttpResponse("UTF-8", "content\nwith new line", null, 302);
      setupHttpClient(true);

      final RestRequest req = getRestRequest();
      req.setMethod(RestMethod.DELETE);

      replay();

      final RestResponse resp = e.executeRequest(loggingId, req, null);

      verify();

      assertNotNull(resp);
      assertEquals(Integer.valueOf(302), Integer.valueOf(resp.getHttpStatus()));
      assertEquals("content\nwith new line", resp.getContent());
      assertNull(resp.getBinaryContent());
   }


   @Test
   public void testExecuteWithRedirectNull() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      setupHttpResponse("UTF-8", "content\nwith new line", null, 301);
      setupHttpClient(true);

      final RestRequest req = getRestRequest();
      req.setMethod(RestMethod.DELETE);

      replay();

      final RestResponse resp = e.executeRequest(loggingId, req, null);

      verify();

      assertNotNull(resp);
      assertEquals(Integer.valueOf(301), Integer.valueOf(resp.getHttpStatus()));
      assertEquals("content\nwith new line", resp.getContent());
      assertNull(resp.getBinaryContent());
   }


   @Test
   public void testExecuteWithRetry() throws MssException, ClientProtocolException, IOException {
      final String loggingId = Tools.getId(new Throwable());
      final RestServer s = new RestServer("http://localhost:8080/v1/info");
      final RestExecutor e = new RestExecutor(s);

      setupHttpClient(false);
      setupHttpClient(false);
      setupHttpClient(false);

      final RestRequest req = getRestRequest();
      req.setMethod(RestMethod.PATCH);

      replay();

      final RestResponse resp = e.executeRequest(loggingId, req, null);

      verify();

      assertNull(resp);
   }


   @Test
   public void testIsRedirect() {
      assertTrue(RestExecutor.isRedirect(301));
      assertTrue(RestExecutor.isRedirect(302));
      assertTrue(RestExecutor.isRedirect(303));
      assertTrue(RestExecutor.isRedirect(307));
      assertFalse(RestExecutor.isRedirect(306));
   }


   @Test
   public void testTimeouts() throws MssException {
      final RestExecutor r = new RestExecutor(new RestServer("http://localhost:8080/v1/info"));

      assertEquals(10, r.getConnectionTimeout());
      assertEquals(180, r.getRequestTimeout());

      r.setConnectionTimeout(1);
      r.setRequestTimeout(18);

      assertEquals(1, r.getConnectionTimeout());
      assertEquals(18, r.getRequestTimeout());
   }


   private void verify() {
      EasyMock.verify(this.httpClientMock);
      EasyMock.verify(this.httpResponseMock);
   }
}
