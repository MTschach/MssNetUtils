package de.mss.net.webservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.eclipse.jetty.server.Request;

import de.mss.configtools.ConfigFile;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;


public abstract class WebService<R extends WebServiceRequest, T extends WebServiceResponse> implements java.io.Serializable {

   private static final long serialVersionUID = 4249971099246813771L;

   static Logger             baseLogger       = LogManager.getRootLogger();

   protected Logger          logger           = baseLogger;

   protected ConfigFile      cfg;

   protected Supplier<R>     requestTypeSupplier;
   protected Supplier<T>     returnTypeSupplier;

   public abstract String getPath();


   public abstract String getMethod();


   public WebService(Supplier<R> reqts, Supplier<T> rts) {
      this.requestTypeSupplier = reqts;
      this.returnTypeSupplier = rts;
   }


   protected R getParsedRequest(String loggingId, Map<String, String> params, Request baseRequest) throws MssException {
      final WebServiceJsonDataBuilder<R> in = new WebServiceJsonDataBuilder<>();
      try {
         return in.parseData(params, this.requestTypeSupplier.get());
      }
      catch (IllegalAccessException | InvocationTargetException | IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_NOT_PARSABLE, e, "could not parse request");
      }
   }


   protected int writeResponse(String loggingId, HttpServletResponse httpResponse, T resp) throws MssException {
      if (resp == null) {
         return HttpServletResponse.SC_OK;
      }

      try {
         int httpStatus = HttpServletResponse.SC_OK;

         if (resp.getErrorCode() != null && resp.getErrorCode() != HttpServletResponse.SC_OK) {
            httpStatus = resp.getErrorCode();
         } else if (resp.getStatusCode() != null && resp.getStatusCode() != HttpServletResponse.SC_OK) {
            httpStatus = resp.getStatusCode();
         } else {
            resp.setErrorCode(null);
            resp.setErrorText(null);
            resp.setStatusCode(null);
         }

         httpResponse.getWriter().write(new WebServiceJsonDataBuilder<T>().writeData(resp));
         httpResponse.getWriter().flush();
         httpResponse.getWriter().close();

         return httpStatus;
      }
      catch (final IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_RESPONSE_NOT_WRITEABLE, e, "could not write response");
      }
   }


   @SuppressWarnings("unused")
   protected void beforeAction(String loggingId, R req) throws MssException {
      // nothing to be done here
   }


   @SuppressWarnings("unused")
   protected void afterAction(String loggingId, R req, T resp) throws MssException {
      // nothing to be done here
   }


   protected void checkRequest(String loggingId, R req) throws MssException {
      if (req == null) {
         throw new MssException(de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM, "request must not be null");
      }
   }


   public void setConfig(ConfigFile c) {
      this.cfg = c;
   }


   public boolean handleRequest(
         String loggingId,
         String target,
         Map<String, String> params,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse) {

      final Marker m = MarkerManager.getMarker(de.mss.utils.Tools.formatLoggingId(loggingId));
      getLogger().debug(m, "handling request {}", target);

      final de.mss.utils.StopWatch s = new de.mss.utils.StopWatch();

      int httpStatusCode = HttpServletResponse.SC_NOT_FOUND;

      T resp = null;

      try {
         final R req = getParsedRequest(loggingId, params, baseRequest);

         checkRequest(loggingId, req);

         beforeAction(loggingId, req);

         resp = handleRequest(loggingId, req);

         afterAction(loggingId, req, resp);

         httpStatusCode = writeResponse(loggingId, httpResponse, resp);
      }
      catch (final MssException e) {
         httpStatusCode = handleException(loggingId, e, resp, httpResponse);
      }

      httpResponse.setStatus(httpStatusCode);

      s.stop();
      getLogger().debug(m, "response {}", httpResponse);
      getLogger().debug(m, "end handling request {} [took {}ms]", target, Long.valueOf(s.getDuration()));

      return true;
   }


   @SuppressWarnings("unused")
   protected T handleRequest(String loggingId, R req) throws MssException {
      return this.returnTypeSupplier.get();
   }


   protected int handleException(String loggingId, MssException e, T resp, HttpServletResponse httpResponse) {
      int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
      if (resp != null && resp.getStatusCode() != null) {
         statusCode = resp.getStatusCode().intValue();
      }

      httpResponse.setStatus(statusCode);
      try {
         httpResponse.sendError(statusCode, e.toString());
      }
      catch (final IOException e1) {
         Tools.doNullLog(e1);
      }
      return statusCode;
   }


   protected Logger getLogger() {
      if (this.logger == null) {
         this.logger = baseLogger;
      }

      return this.logger;
   }


   public void setLogger(Logger l) {
      this.logger = l;
   }


   protected static Map<String, String> getUrlParams(Request request) throws UnsupportedEncodingException {
      final Map<String, String> ret = new HashMap<>();

      if (request != null && request.getRequestURI() != null && request.getRequestURI().indexOf('?') >= 0) {
         final String[] params = request.getRequestURI().substring(request.getRequestURI().indexOf('?') + 1).split("&");
         for (final String keyValue : params) {
            final String[] kv = keyValue.split("=");
            ret.put(kv[0], URLDecoder.decode(kv[1], "application/x-www-form-urlencoded"));
         }
      }

      return ret;
   }


   public static WebServiceResponse getDefaultOkResponse() {
      final WebServiceResponse resp = new WebServiceResponse();

      resp.setStatusCode(HttpServletResponse.SC_OK);

      return resp;
   }
}
