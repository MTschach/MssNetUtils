package de.mss.net.webservice;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.jetty.server.Request;

import de.mss.net.rest.RestMethod;
import de.mss.utils.exception.MssException;

public class WebServiceForTest extends WebService<WebServiceTestRequest, WebServiceTestResponse> {

   private static final long serialVersionUID = 2972782037461318009L;

   public static Map<String, String> getUrlParamsForTest(Request request) throws UnsupportedEncodingException {
      return getUrlParams(request);
   }

   private RestMethod method = RestMethod.GET;


   public WebServiceForTest(Supplier<WebServiceTestRequest> reqts, Supplier<WebServiceTestResponse> rts) {
      super(reqts, rts);
   }


   @Override
   public String getMethod() {
      return this.method.getMethod();
   }


   public WebServiceTestRequest getParsedRequestForTest(String loggingId, Map<String, String> params, Request baseRequest) throws MssException {
      return super.getParsedRequest(loggingId, params, baseRequest);
   }


   @Override
   public String getPath() {
      return "v1/info";
   }


   public void setMethod(RestMethod m) {
      if (m != null) {
         this.method = m;
      }
   }
}
