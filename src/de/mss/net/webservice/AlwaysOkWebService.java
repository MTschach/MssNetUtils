package de.mss.net.webservice;

import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.jetty.server.Request;

import de.mss.net.rest.RestMethod;
import de.mss.utils.exception.MssException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class AlwaysOkWebService<R extends WebServiceRequest,
                                T extends WebServiceResponse>
      extends
      WebService<R, T> {

   private static final long serialVersionUID = 4249971099246833245L;

   public AlwaysOkWebService(Supplier<R> reqts, Supplier<T> rts) {
      super(reqts, rts);
   }


   @Override
   public RestMethod getMethod() {
      return RestMethod.UNKNOWN;
   }


   @Override
   public String getPath() {
      return "";
   }


   @Override
   public boolean handleRequest(
         String loggingId,
         String target,
         Map<String, String> params,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse) {

      try {
         writeResponse(loggingId, httpResponse, null);
      }
      catch (final MssException e) {
         handleException(loggingId, e, null, httpResponse);
      }
      httpResponse.setStatus(HttpServletResponse.SC_OK);

      return true;
   }
}
