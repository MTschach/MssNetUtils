package de.mss.net.webservice;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import de.mss.net.rest.RestMethod;
import de.mss.net.rest.RestRequest;
import de.mss.utils.exception.MssException;
import junit.framework.TestCase;


public class WebServiceCallerTest extends TestCase {

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
   public void testJSonCallerRestRequest() throws MssException {
      final String loggingId = UUID.randomUUID().toString();
      this.request.setLoggingId(loggingId);
      final WebServiceJsonCallerForTest<WebServiceTestRequest, WebServiceTestResponse> caller = new WebServiceJsonCallerForTest<>();

      final List<Field> fields = FieldUtils.getAllFieldsList(this.request.getClass());

      final RestRequest restRequest = caller.getRestRequestForTest(RestMethod.POST, this.request, fields);

      assertNotNull("Rest request is not null", restRequest);
      assertEquals("TestRest request", "loggingId: " + loggingId, this.request.toString());
      this.request.checkRequiredFields();
   }


   @Test
   public void testJsonCallerUrl() {
      final WebServiceJsonCallerForTest<WebServiceTestRequest, WebServiceTestResponse> caller = new WebServiceJsonCallerForTest<>();

      final List<Field> fields = FieldUtils.getAllFieldsList(this.request.getClass());

      final String url = caller.prepareUrlForTest("/v1/customer/{customerNumber}/info", this.request, fields);

      assertEquals("prepared url", "/v1/customer/34731/info?name=Tester&birthdate=19800229T134532000%2B0100", url);
   }


   @Test
   public void testJsonCallerUrlOtherDateFormat() {
      final WebServiceJsonCallerForTest<WebServiceTestRequest, WebServiceTestResponse> caller = new WebServiceJsonCallerForTest<>();
      caller.setDateFormat("dd-MM-yyyy HH:mm:ss");

      final List<Field> fields = FieldUtils.getAllFieldsList(this.request.getClass());

      final String url = caller.prepareUrlForTest("/v1/customer/{customerNumber}/info", this.request, fields);

      assertEquals("prepared url", "/v1/customer/34731/info?name=Tester&birthdate=29-02-1980+13%3A45%3A32", url);
   }
}
