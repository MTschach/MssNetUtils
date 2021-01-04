package de.mss.net.webservice;

public class WebServiceResponseChecks {

   private WebServiceResponseChecks() {}


   public static boolean isResponseOk(WebServiceResponse response) {
      return response != null
            && response.getStatusCode() != null
            && response.getStatusCode() / 100 == 2
            && response.getErrorCode() != null
            && response.getErrorCode() == 0;
   }
}
