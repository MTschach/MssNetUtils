package de.mss.net.webservice;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import de.mss.net.rest.RestMethod;
import de.mss.net.rest.RestRequest;
import de.mss.net.rest.RestServer;
import de.mss.utils.exception.MssException;
import junit.framework.TestCase;


public class WebServiceCallerTest extends TestCase{

   private WebServiceTestRequest request;


   @Override
   public void setUp() throws Exception {
      super.setUp();

      this.request = new WebServiceTestRequest();
      this.request.birthdate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("29.02.1980 13:45:32");
      this.request.customerNumber = Integer.valueOf(34731);
      this.request.name = "Tester";
      this.request.sessionId = "abcd-efgh-12345678-IJKL";
      this.request.body = new WebServiceBody();
      this.request.body.setStreet("Waldweg");
      this.request.body.setNumber("1a");
      this.request.body.setAddress(new WebServiceAddress());
      this.request.body.getAddress().setCity("Zwickau");
      this.request.body.getAddress().setPostCode("08058");
      this.request.body.setContacts(new ArrayList<>());
      this.request.body.getContacts().add(new WebServiceContact("email", "x@y.z"));
      this.request.body.getContacts().add(new WebServiceContact("phone", "0123 / 456 78 - 9"));
   }
   

   @Test
   public void testJSonCallerRestRequest() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, MssException {
      WebServiceJsonCallerForTest<WebServiceTestRequest, WebServiceTestResponse> caller = new WebServiceJsonCallerForTest<>();
      
      List<Field> fields = FieldUtils.getAllFieldsList(this.request.getClass());
      
      RestRequest restRequest = caller.getRestRequestForTest(RestMethod.POST, this.request, fields);

      assertNotNull("Rest request is not null", restRequest);
   }


   @Test
   public void testJsonCallerUrl() throws MssException {
      WebServiceJsonCallerForTest<WebServiceTestRequest, WebServiceTestResponse> caller = new WebServiceJsonCallerForTest<>();

      List<Field> fields = FieldUtils.getAllFieldsList(this.request.getClass());

      String url = caller.prepareUrlForTest("/v1/customer/{customerNumber}/info", this.request, fields);

      assertEquals("prepared url", "/v1/customer/34731/info?name=Tester&birthdate=19800229T134532000%2B0100", url);
   }


   @Test
   public void testJsonCallerUrlOtherDateFormat() throws MssException {
      WebServiceJsonCallerForTest<WebServiceTestRequest, WebServiceTestResponse> caller = new WebServiceJsonCallerForTest<>();
      caller.setDateFormat("dd-MM-yyyy HH:mm:ss");

      List<Field> fields = FieldUtils.getAllFieldsList(this.request.getClass());

      String url = caller.prepareUrlForTest("/v1/customer/{customerNumber}/info", this.request, fields);

      assertEquals("prepared url", "/v1/customer/34731/info?name=Tester&birthdate=29-02-1980+13%3A45%3A32", url);
   }


//   @Test
   //   public void test() throws MssException {
   //      RestServer[] servers = new RestServer[] {new RestServer("http://localhost:38080")};
   //      WebServiceJsonCaller<WebServiceTestRequest, WebServiceTestResponse> caller = new WebServiceJsonCaller<>();
   //
   //      WebServiceTestRequest req = new WebServiceTestRequest();
   //      req.setLoggingId("lala");
   //      req.customerNumber = 1234567;
   //      req.userName = "mtschach";
   //      req.checkInterval = 25;
   //
   //      caller.call("lala", servers, "v1/{username}/checkCounter", RestMethod.GET, req, new WebServiceTestResponse(), 1);
   //
   //   }
   //
   //   @Test
//   public void testJsonCaller()
//         throws MssException,
//         IllegalAccessException,
//         InvocationTargetException,
//         NoSuchMethodException,
//         ClientProtocolException,
//         IOException {
//      WebServiceJsonCaller<WebServiceTestRequest, WebServiceTestResponse> caller = new WebServiceJsonCaller<>();
//      
//      String loggingId = Tools.getId(new Throwable());
//      
//
//      /*
//       *       HttpClient httpClientMock = (HttpClient)EasyMock
//            .createMockBuilder(HttpClient.class)
//            .withConstructor(HttpClientParams.class, HttpConnectionManager.class)
//            .withArgs(paramsMock, new SimpleHttpConnectionManager())
//            .addMockedMethod("getHostConfiguration")
//            .addMockedMethod("getParams")
//            .addMockedMethod("getHttpConnectionManager")
//            .addMockedMethod("executeMethod", HttpMethod.class)
//            .createMock();
//      EasyMock.expect(httpClientMock.getHostConfiguration()).andReturn(hostConfigurationMock);
//      EasyMock.expect(httpClientMock.getParams()).andReturn(paramsMock).times(3);
//      EasyMock.expect(httpClientMock.getHttpConnectionManager()).andReturn(new SimpleHttpConnectionManager());
//      HttpMethod method = new PostMethod("http://localhost:8080/onlinebank_rest/iftristan/readString");
//      ((PostMethod)method).setRequestEntity(new StringRequestEntity(bodyParameter, "text/plain", "UTF-8"));
//      EasyMock.expect(httpClientMock.executeMethod(method)).andReturn(statusCode);
//      
//      RestRequest req = new RestRequest()
//            .setMethod(RestMethod.Post)
//            .setUrlExt("/iftristan/readString")
//            .addHeaderParam("content-type", "text/plain")
//            .addHeaderParam("charset", "UTF-8")
//            .setBodyParameter(bodyParameter);
//      
//      exec = (RestRequestExecutor)EasyMock
//            .createMockBuilder(RestRequestExecutor.class)
//            .addMockedMethod("getHttpClient")
//            .addMockedMethod("getMethod", String.class, RestRequest.class, String.class)
//            .addMockedMethod("buildRestResponse", HttpMethod.class, int.class)
//            .createMock();
//      
//      exec.servers = restServers;
//      exec.lg = new XLoggerTestImpl();
//      exec.timeOutDefault = timeout;
//      
//      RestResponse expectedResponse = new RestResponse(statusCode, method.getResponseBodyAsString());
//      expectedResponse.content = bodyParameter;
//      
//      
//      EasyMock.expect(exec.getHttpClient()).andReturn(httpClientMock);
//      EasyMock.expect(exec.getMethod(trackingId, req, exec.servers[0].getPath())).andReturn(method);
//      EasyMock.expect(exec.buildRestResponse(method, statusCode)).andReturn(expectedResponse);
//      
//      EasyMock.replay(httpClientMock, exec, hostConfigurationMock, paramsMock);
//      
//       */
//
//      Capture<HttpHost> captureHost = EasyMock.newCapture();
//      Capture<HttpUriRequest> captureRequest = EasyMock.newCapture();
//
//      CloseableHttpResponse httpResponseMock = EasyMock.createMock(CloseableHttpResponse.class);
//      httpResponseMock.close();
//      EasyMock.expectLastCall().anyTimes();
//      
//      CloseableHttpClient httpClientMock = EasyMock.createMock(CloseableHttpClient.class);
//      EasyMock.expect(httpClientMock.execute(EasyMock.capture(captureHost), EasyMock.capture(captureRequest))).andReturn(httpResponseMock);
//      httpClientMock.close();
//      EasyMock.expectLastCall().anyTimes();
//
//      HttpClientFactory.initializeHttpClientFactory(httpClientMock);
//
//      EasyMock.replay(httpClientMock, httpResponseMock);
//
//      WebServiceTestResponse response = caller
//            .call(loggingId, setUpRestServers(1), "/v1/customer/{customerNumber}/info", RestMethod.PATCH, this.request, 1);
//
//      assertNotNull("Response is not null", response);
//   }


   private RestServer[] setUpRestServers(int count) throws MssException {
      List<RestServer> list = new ArrayList<>();

      int curr = 1;
      while (curr <= count) {
         list.add(new RestServer("Http://127.0.0." + curr + ":1234", null));
         curr++ ;
      }

      return list.toArray(new RestServer[list.size()]);
   }
}
