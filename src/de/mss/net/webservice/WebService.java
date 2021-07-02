package de.mss.net.webservice;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.eclipse.jetty.server.Request;

import de.mss.configtools.ConfigFile;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public abstract class WebService<R extends WebServiceRequest, T extends WebServiceResponse> implements java.io.Serializable {

   private static final long serialVersionUID = 4249971099246813771L;

   static Logger             baseLogger       = LogManager.getRootLogger();

   public static WebServiceResponse getDefaultOkResponse() {
      final WebServiceResponse resp = new WebServiceResponse();

      resp.setStatusCode(HttpServletResponse.SC_OK);

      return resp;
   }


   protected static Map<String, String> getUrlParams(Request request) {
      final Map<String, String> ret = new HashMap<>();

      if (request != null && request.getRequestURI() != null && request.getRequestURI().indexOf('?') >= 0) {
         final String[] params = request.getRequestURI().substring(request.getRequestURI().indexOf('?') + 1).split("&");
         for (final String keyValue : params) {
            final String[] kv = keyValue.split("=");
            ret.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
         }
      }

      return ret;
   }

   protected Logger      logger = baseLogger;
   protected ConfigFile  cfg;

   protected Supplier<R> requestTypeSupplier;


   protected Supplier<T> returnTypeSupplier;


   public WebService(Supplier<R> reqts, Supplier<T> rts) {
      this.requestTypeSupplier = reqts;
      this.returnTypeSupplier = rts;
   }


   @SuppressWarnings("unused")
   protected void afterAction(String loggingId, R req, T resp) throws MssException {
      // nothing to be done here
   }


   @SuppressWarnings("unused")
   protected void beforeAction(String loggingId, R req) throws MssException {
      // nothing to be done here
   }


   protected void checkRequest(String loggingId, R req) throws MssException {
      if (req == null) {
         throw new MssException(de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM, "request must not be null");
      }
   }


   protected Logger getLogger() {
      if (this.logger == null) {
         this.logger = baseLogger;
      }

      return this.logger;
   }


   public abstract String getMethod();


   protected R getParsedRequest(String loggingId, Map<String, String> params, Request baseRequest) throws MssException {
      final WebServiceJsonDataBuilder<R> in = new WebServiceJsonDataBuilder<>();
      try {
         final R req = in.parseData(params, this.requestTypeSupplier.get());
         getLogger().debug(Tools.formatLoggingId(loggingId) + "Request: " + req);
         return req;
      }
      catch (IllegalAccessException | InvocationTargetException | IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_NOT_PARSABLE, e, "could not parse request");
      }
   }


   public abstract String getPath();


   protected int handleException(String loggingId, MssException e, T resp, HttpServletResponse httpResponse) {
      int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
      if (e.getError().getStatusCode() != null) {
         statusCode = e.getError().getStatusCode().intValue();
      } else if (resp != null && resp.getStatusCode() != null) {
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


   @SuppressWarnings("unused")
   protected T handleRequest(String loggingId, R req) throws MssException {
      return this.returnTypeSupplier.get();
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


   public void setConfig(ConfigFile c) {
      this.cfg = c;
   }


   public void setLogger(Logger l) {
      this.logger = l;
   }


   private boolean useErrorCode(Integer error) {
      return error != null && error.intValue() != 0 && error.intValue() / 100 != 2;
   }


   @SuppressWarnings("resource")
   protected int writeResponse(String loggingId, HttpServletResponse httpResponse, T resp) throws MssException {
      if (resp == null) {
         return HttpServletResponse.SC_OK;
      }

      try {
         int httpStatus = HttpServletResponse.SC_OK;

         if (useErrorCode(resp.getErrorCode())) {
            httpStatus = resp.getErrorCode().intValue();
         } else if (useErrorCode(resp.getStatusCode())) {
            httpStatus = resp.getStatusCode().intValue();
         } else {
            resp.setErrorCode(0);
            resp.setErrorText(null);
            resp.setStatusCode(httpStatus);
         }

         getLogger().debug(Tools.formatLoggingId(loggingId) + "Response: " + resp);

         httpResponse.getWriter().write(new WebServiceJsonDataBuilder<T>().writeData(resp));
         httpResponse.getWriter().flush();
         httpResponse.getWriter().close();

         return httpStatus;
      }
      catch (final IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_RESPONSE_NOT_WRITEABLE, e, "could not write response");
      }
   }
}
