package de.mss.net.webservice;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mss.net.rest.RestExecutor;
import de.mss.net.rest.RestMethod;
import de.mss.net.rest.RestRequest;
import de.mss.net.rest.RestResponse;
import de.mss.net.rest.RestServer;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;
import jakarta.servlet.http.HttpServletResponse;

public abstract class WebServiceCaller<T extends WebServiceRequest, R extends WebServiceResponse> {

   private static Logger logger = null;

   private static List<Field> getCallableFields(Class<? extends WebServiceRequest> clazz) {
      final List<Field> ret = new ArrayList<>();
      for (final Field f : FieldUtils.getAllFieldsList(clazz)) {
         if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
            ret.add(f);
         }
      }

      return ret;
   }


   public static Logger getLogger() {
      if (logger == null) {
         logger = LogManager.getRootLogger();
      }

      return logger;
   }


   public static void setLogger(Logger l) {
      logger = l;
   }


   protected static void sleep(long millies) {
      try {
         Thread.sleep(millies);
      }
      catch (final InterruptedException e) {
         getLogger().debug("error while callinig rest server", e);
         Thread.currentThread().interrupt();
      }
   }


   private boolean binaryContent = false;


   private String dateFormat = "yyyyMMdd'T'HHmmssSSSZ";


   protected abstract void addPostParams(RestRequest restRequest, T request, List<Field> fields) throws MssException;


   private R call(String loggingId, RestServer server, RestRequest restRequest, R responseClass, int maxRetries) throws MssException {

      if (!restRequest.getHeaderParams().containsKey(WebServiceRequestHandler.HEADER_LOGGING_ID)) {
         restRequest.addHeaderParam(WebServiceRequestHandler.HEADER_LOGGING_ID, loggingId);
      }
      final RestExecutor exec = new RestExecutor(server, this.binaryContent);
      int tries = maxRetries;
      RestResponse resp = null;
      do {
         resp = exec.executeRequest(loggingId, restRequest, null);
         tries-- ;
         sleep(250);
      }
      while (tries > 0 && (resp == null || !isOkStatus(resp.getHttpStatus())));

      if (resp == null) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_NO_RESPONSE, "no response received");
      }

      if (!isOkStatus(resp.getHttpStatus())) {
         throw new MssException(
               de.mss.net.exception.ErrorCodes.ERROR_NO_RESPONSE_WITH_ERROR,
               "non OK status received " + (resp.getContent() != null ? resp.getContent() : ""));
      }

      R response = null;
      final int httpStatus = resp.getHttpStatus();

      if (resp.getContent() != null) {
         response = parseContent(resp.getContent(), responseClass);
      }

      if (response == null) {
         response = responseClass;
      }

      if (resp.getBinaryContent() != null) {
         response.setBinaryContent(resp.getBinaryContent());
      }

      response.setErrorCode(Integer.valueOf(0));
      response.setStatusCode(httpStatus);

      return response;
   }


   public R call(String loggingId, RestServer[] servers, String url, RestMethod method, T request, R responseClass, int maxRetries)
         throws MssException {

      String logId = loggingId;
      if (!Tools.isSet(logId)) {
         logId = UUID.randomUUID().toString();
      }
      getLogger().debug(Tools.formatLoggingId(logId) + "Request: " + request);

      final List<Field> fields = getCallableFields(request.getClass());
      final RestRequest restRequest = getRestRequest(method, request, fields);
      restRequest.setUrl(prepareUrl(url, request, fields));

      final R response = responseClass;

      if (response == null) {
         return null;
      }


      for (final RestServer server : servers) {
         try {
            final R resp = call(logId, server, restRequest, responseClass, maxRetries);

            getLogger().debug(Tools.formatLoggingId(logId) + "Response: " + resp);

            return resp;
         }
         catch (final MssException e) {
            getLogger().debug(Tools.formatLoggingId(logId) + "error while callinig rest server", e);
            response.setErrorCode(Integer.valueOf(e.getAltErrorCode() != 0 ? e.getAltErrorCode() : e.getError().getErrorCode()));
            response.setErrorText(e.getAltErrorText() != null ? e.getAltErrorText() : e.getError().getErrorText());
            response.setStatusCode(Integer.valueOf(HttpServletResponse.SC_BAD_REQUEST));
         }
      }

      return response;
   }


   public String getDateFormat() {
      return this.dateFormat;
   }


   protected RestRequest getRestRequest(RestMethod method, T request, List<Field> fields)
         throws MssException {
      final RestRequest req = new RestRequest(method);

      for (final Field field : fields) {
         if (field.isAnnotationPresent(HeaderParam.class)) {
            final String paramName = field.getAnnotationsByType(HeaderParam.class)[0].value();
            final String value = getStringValue(request, field.getName());
            if (Tools.isSet(value)) {
               req.addHeaderParam(paramName, value);
            }
         }
      }

      addPostParams(req, request, fields);

      return req;
   }


   protected String getStringValue(T request, String fieldName) throws MssException {
      if (request == null || !Tools.isSet(fieldName)) {
         return null;
      }

      try {
         final Object prop = PropertyUtils.getProperty(request, fieldName);

         if (prop == null) {
            return null;
         } else if (prop instanceof String) {
            return (String)prop;
         } else if (prop instanceof BigDecimal) {
            return ((BigDecimal)prop).toString();
         } else if (prop instanceof BigInteger) {
            return ((BigInteger)prop).toString();
         } else if (prop instanceof Double) {
            return ((Double)prop).toString();
         } else if (prop instanceof Float) {
            return ((Float)prop).toString();
         } else if (prop instanceof Integer) {
            return ((Integer)prop).toString();
         } else if (prop instanceof java.util.Date) {
            return new SimpleDateFormat(this.dateFormat).format((java.util.Date)prop);
         } else if (prop instanceof IfRequestEnumeration) {
            return ((IfRequestEnumeration)prop).getApiValue();
         } else {
            return prop.toString();
         }
      }
      catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_NOT_MAPPABLE, e, "could not get value of field '" + fieldName + "'");
      }
   }


   private boolean isOkStatus(int httpStatus) {
      return httpStatus / 100 == 2;
   }


   protected abstract R parseContent(String content, R response) throws MssException;


   protected String prepareUrl(String url, T request, List<Field> fields) {
      String ret = url;
      final StringBuilder urlParams = new StringBuilder();

      for (final Field field : fields) {
         try {
            final String value = getStringValue(request, field.getName());

            if (field.isAnnotationPresent(PathParam.class)) {
               final String paramName = field.getAnnotationsByType(PathParam.class)[0].value();
               if (!Tools.isSet(value)) {
                  throw new MssException(
                        de.mss.net.exception.ErrorCodes.ERROR_PATH_PARAMETER_NOT_SET,
                        "the path parameter '" + paramName + "' is not set");
               }
               ret = ret.replace("{" + paramName + "}", value);
            } else if (field.isAnnotationPresent(QueryParam.class) && Tools.isSet(value)) {
               final String paramName = field.getAnnotationsByType(QueryParam.class)[0].value();
               if (urlParams.length() == 0) {
                  urlParams.append("?");
               } else {
                  urlParams.append("&");
               }

               urlParams.append(paramName);
               urlParams.append("=");
               urlParams.append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
            }
         }
         catch (UnsupportedEncodingException | MssException e) {
            getLogger().log(Level.OFF, e);
         }
      }

      return ret + urlParams.toString();
   }


   public void setBinaryContent() {
      this.binaryContent = true;
   }


   public void setDateFormat(String f) {
      this.dateFormat = f;
   }


   public void unsetBinaryContent() {
      this.binaryContent = false;
   }
}
