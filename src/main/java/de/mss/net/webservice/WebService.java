package de.mss.net.webservice;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.eclipse.jetty.server.Request;

import de.mss.configtools.ConfigFile;
import de.mss.utils.exception.MssException;


public abstract class WebService implements java.io.Serializable {

   private static final long serialVersionUID = 4249971099246813771L;

   static Logger             baseLogger       = LogManager.getRootLogger();

   protected Logger logger     = baseLogger;

   protected ConfigFile cfg;


   public abstract String getPath();


   public void setConfig(ConfigFile c) {
      this.cfg = c;
   }


   public boolean handleRequest(
         String loggingId,
         String target,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse) {

      Marker m = MarkerManager.getMarker(de.mss.utils.Tools.formatLoggingId(loggingId));
      getLogger().debug(m, "handling request {}", target);

      de.mss.utils.StopWatch s = new de.mss.utils.StopWatch();

      int httpStatusCode = HttpServletResponse.SC_NOT_FOUND;

      try {
         switch (httpRequest.getMethod()) {
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
      catch (MssException e) {
         httpStatusCode = handleException(loggingId, e, httpResponse);
      }

      httpResponse.setStatus(httpStatusCode);
      s.stop();
      getLogger().debug(m, "response {}", httpResponse);
      getLogger().debug(m, "end handling request {} [took {}ms]", target, Long.valueOf(s.getDuration()));

      return true;
   }


   protected abstract int handleException(String loggingId, MssException e, HttpServletResponse httpResponse);


   protected Logger getLogger() {
      if (this.logger == null)
         this.logger = baseLogger;

      return this.logger;
   }


   public void setLogger(Logger l) {
      this.logger = l;
   }


   public int get(
         String loggingId,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws MssException {
      return HttpServletResponse.SC_NOT_FOUND;
   }


   public int post(
         String loggingId,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws MssException {
      return HttpServletResponse.SC_NOT_FOUND;
   }


   public int patch(
         String loggingId,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws MssException {
      return HttpServletResponse.SC_NOT_FOUND;
   }


   public int delete(
         String loggingId,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws MssException {
      return HttpServletResponse.SC_NOT_FOUND;
   }


   protected Map<String, String> getUrlParams(Request request) throws UnsupportedEncodingException {
      Map<String, String> ret = new HashMap<>();

      if (request != null && request.getRequestURI() != null && request.getRequestURI().indexOf('?') >= 0) {
         String[] params = request.getRequestURI().substring(request.getRequestURI().indexOf('?') + 1).split("&");
         for (String keyValue : params) {
            String[] kv = keyValue.split("=");
            ret.put(kv[0], URLDecoder.decode(kv[1], "application/x-www-form-urlencoded"));
         }
      }

      return ret;
   }
}
