package de.mss.net.webservice;

import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.jetty.server.Request;

import de.mss.net.rest.RestMethod;
import de.mss.utils.exception.MssException;
import jakarta.servlet.http.HttpServletResponse;

public class WebServiceForTest extends WebService<WebServiceTestRequest, WebServiceTestResponse> {

   private static final long serialVersionUID = 2972782037461318009L;

   public static Map<String, String> getUrlParamsForTest(Request request) {
      return getUrlParams(request);
   }

   private RestMethod method = RestMethod.GET;


   public WebServiceForTest(Supplier<WebServiceTestRequest> reqts, Supplier<WebServiceTestResponse> rts) {
      super(reqts, rts);
   }


   public void checkRequestForTest(String loggingId, WebServiceTestRequest req) throws MssException {
      super.checkRequest(loggingId, req);
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


   public int handleExceptionForTest(String loggingId, MssException e, WebServiceTestResponse resp, HttpServletResponse httpResponse) {
      return super.handleException(loggingId, e, resp, httpResponse);
   }


   public void setMethod(RestMethod m) {
      if (m != null) {
         this.method = m;
      }
   }
}
