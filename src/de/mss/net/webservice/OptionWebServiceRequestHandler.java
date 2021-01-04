package de.mss.net.webservice;

public class OptionWebServiceRequestHandler extends SpecialWebServiceRequestHandler {

   @Override
   public WebService<WebServiceRequest, WebServiceResponse> findWebService(String target) {
      if (target != null && target.toUpperCase().startsWith("OPTIONS")) {
         return new AlwaysOkWebService<>(WebServiceRequest::new, WebServiceResponse::new);
      }
      return null;
   }


   @Override
   public String findTarget(String target) {
      return target;
   }
}
