package de.mss.net.webservice;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class WebServiceResponseChecksTest {

   @Test
   public void test() {
      assertFalse(WebServiceResponseChecks.isResponseOk(null));
      final WebServiceResponse resp = new WebServiceResponse();
      assertFalse(WebServiceResponseChecks.isResponseOk(resp));
      resp.setStatusCode(1);
      assertFalse(WebServiceResponseChecks.isResponseOk(resp));
      resp.setStatusCode(199);
      assertFalse(WebServiceResponseChecks.isResponseOk(resp));
      resp.setStatusCode(300);
      assertFalse(WebServiceResponseChecks.isResponseOk(resp));
      resp.setStatusCode(200);
      assertFalse(WebServiceResponseChecks.isResponseOk(resp));
      resp.setErrorCode(1);
      assertFalse(WebServiceResponseChecks.isResponseOk(resp));
      resp.setErrorCode(0);
      assertTrue(WebServiceResponseChecks.isResponseOk(resp));
   }

}
