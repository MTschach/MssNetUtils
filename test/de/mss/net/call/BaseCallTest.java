package de.mss.net.call;

import org.junit.Test;

import de.mss.net.webservice.WebServiceTestRequest;
import de.mss.net.webservice.WebServiceTestResponse;
import de.mss.utils.Tools;
import junit.framework.TestCase;

public class BaseCallTest extends TestCase {

   private BaseCallForTest                    classUnderTest = null;
   private WebServiceTestRequest              request        = null;
   private final de.mss.utils.exception.Error defaultError   = de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED;

   private void checkResponse(WebServiceTestResponse response) {
      checkResponse(response, 0, 200);
   }


   private void checkResponse(WebServiceTestResponse response, Integer errorCode, Integer statusCode) {
      assertNotNull("Response is not null", response);
      assertEquals("ErrorCode", errorCode, response.getErrorCode());
      assertEquals("StatusCode", statusCode, response.getStatusCode());
      if (errorCode.intValue() == 0) {
         assertNull("ErrorText", response.getErrorText());
      } else {
         assertNotNull("ErrorText", response.getErrorText());
      }
      assertNotNull(this.classUnderTest.getError());
   }


   @Override
   public void setUp() throws Exception {
      super.setUp();

      this.classUnderTest = new BaseCallForTest(WebServiceTestResponse::new, this.defaultError);
      this.request = new WebServiceTestRequest();
   }


   @Override
   public void tearDown() throws Exception {
      super.tearDown();
   }


   @Test
   public void testErrorNull() {
      final String loggingId = Tools.getId(new Throwable());
      final WebServiceTestResponse response = this.classUnderTest.action(loggingId, this.request);

      checkResponse(response, -123, 400);
   }


   @Test
   public void testNullRequest() {
      final String loggingId = Tools.getId(new Throwable());
      final WebServiceTestResponse response = this.classUnderTest.action(loggingId, null);

      checkResponse(response, this.defaultError.getErrorCode(), 400);
   }


   @Test
   public void testNullResponse() {
      final String loggingId = Tools.getId(new Throwable());
      final WebServiceTestResponse response = this.classUnderTest.action(loggingId, this.request);

      checkResponse(response, this.defaultError.getErrorCode(), 400);
   }


   @Test
   public void testOk() {
      final String loggingId = Tools.getId(new Throwable());
      final WebServiceTestResponse response = this.classUnderTest.action(loggingId, this.request);

      checkResponse(response);
   }

}
