package de.mss.net.call;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.mss.net.webservice.WebServiceTestRequest;
import de.mss.net.webservice.WebServiceTestResponse;
import de.mss.utils.Tools;

public class BaseCallTest {

   private BaseCallForTest                    classUnderTest = null;
   private WebServiceTestRequest              request        = null;
   private final de.mss.utils.exception.Error defaultError   = de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED;

   private void checkResponse(WebServiceTestResponse response) {
      checkResponse(response, 0, 200);
   }


   private void checkResponse(WebServiceTestResponse response, Integer errorCode, Integer statusCode) {
      assertNotNull(response);
      assertEquals(errorCode, response.getErrorCode());
      assertEquals(statusCode, response.getStatusCode());
      if (errorCode.intValue() == 0) {
         assertNull(response.getErrorText());
      } else {
         assertNotNull(response.getErrorText());
      }
      assertNotNull(this.classUnderTest.getError());
   }


   @BeforeEach
   public void setUp() throws Exception {
      this.classUnderTest = new BaseCallForTest(WebServiceTestResponse::new, this.defaultError);
      this.request = new WebServiceTestRequest();
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
