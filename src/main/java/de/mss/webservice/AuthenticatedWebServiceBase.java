package de.mss.webservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;

public class AuthenticatedWebServiceBase implements WebService {

   static Logger    baseLogger = LogManager.getLogger("default");

   protected Logger      logger     = baseLogger;

   @Override
   public boolean handleRequest(
         String loggingId,
         String target,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse) {

      getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "handling request " + target);
      de.mss.utils.StopWatch s = new de.mss.utils.StopWatch();

      int httpStatusCode = HttpServletResponse.SC_NOT_FOUND;

      try {
      switch (baseRequest.getMethod()) {
         case HttpMethod.DELETE:
               httpStatusCode = delete(loggingId, pathParams, baseRequest, httpRequest, httpResponse);
            break;
         case HttpMethod.GET:
            httpStatusCode = get(loggingId, pathParams, baseRequest, httpRequest, httpResponse);
            break;
         case HttpMethod.PATCH:
            httpStatusCode = patch(loggingId, pathParams, baseRequest, httpRequest, httpResponse);
            break;
         case HttpMethod.POST:
            httpStatusCode = post(loggingId, pathParams, baseRequest, httpRequest, httpResponse);
            break;

         default:
               httpStatusCode = HttpServletResponse.SC_NOT_FOUND;
            break;
      }
      }
      catch (IOException e) {
         getLogger().error(de.mss.utils.Tools.formatLoggingId(loggingId), e);
      }

      httpResponse.setStatus(httpStatusCode);
      s.stop();
      getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "response " + httpResponse);
      getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "end handling request + " + target + " [took " + s.getDuration() + "ms]");

      return true;
   }


   protected Logger getLogger() {
      if (this.logger == null)
         this.logger = baseLogger;

      return this.logger;
   }


   public void setLogger(Logger l) {
      this.logger = l;
   }


   @Override
   public int get(
         String loggingId,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws IOException {
      return HttpServletResponse.SC_NOT_FOUND;
   }


   @Override
   public int post(
         String loggingId,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws IOException {
      return HttpServletResponse.SC_NOT_FOUND;
   }


   @Override
   public int patch(
         String loggingId,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws IOException {
      return HttpServletResponse.SC_NOT_FOUND;
   }


   @Override
   public int delete(
         String loggingId,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws IOException {
      return HttpServletResponse.SC_NOT_FOUND;
   }


   protected Map<String, String> getUrlParams(Request request) throws UnsupportedEncodingException {
      Map<String, String> ret = new HashMap<>();

      if (request != null && request.getOriginalURI() != null && request.getOriginalURI().indexOf("?") > 0) {
         String[] params = request.getOriginalURI().substring(request.getOriginalURI().indexOf("?") + 1).split("&");
         for (String keyValue : params) {
            String[] kv = keyValue.split("=");
            ret.put(kv[0], URLDecoder.decode(kv[1], "application/x-www-form-urlencoded"));
         }
      }

      return ret;
   }


   protected Map<String, String> getHeaderParams(Request request) {
      Map<String, String> ret = new HashMap<>();

      Enumeration<String> names = request.getHeaderNames();
      while (names.hasMoreElements()) {
         String n = names.nextElement();
         ret.put(n, request.getHeader(n));
      }


      return ret;
   }
}
