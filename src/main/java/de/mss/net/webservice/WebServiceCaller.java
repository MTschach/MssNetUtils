package de.mss.net.webservice;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletResponse;
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

public abstract class WebServiceCaller<T extends WebServiceRequest, R extends WebServiceResponse> {

   private String dateFormat = "yyyyMMdd'T'HHmmssSSSZ";

   private static Logger logger     = null;

   protected abstract void addPostParams(RestRequest restRequest, T request, Field[] fields) throws MssException;


   protected abstract R parseContent(String content) throws MssException;


   public R call(String loggingId, RestServer[] servers, String url, RestMethod method, T request, int maxRetries)
         throws MssException {

      Field[] fields = FieldUtils.getAllFields(request.getClass());
      RestRequest restRequest = getRestRequest(method, request, fields);
      String finalUrl = prepareUrl(url, request, fields);

      for (RestServer server : servers)
         try {
            return call(loggingId, server, restRequest, finalUrl, maxRetries);
         }
         catch (MssException e) {
            getLogger().debug("error while callinig rest server", e);
         }

      return null;
   }


   private R call(String loggingId, RestServer server, RestRequest restRequest, String url, int maxRetries) throws MssException {

      server.getServer().setUrl(url);
      RestExecutor exec = new RestExecutor(server);
      int tries = maxRetries;
      RestResponse resp = null;
      do {
         resp = exec.executeRequest(loggingId, restRequest, null);
         tries-- ;
         sleep(250);
      }
      while (tries > 0 && (resp == null || resp.getHttpStatus() != HttpServletResponse.SC_OK));

      if (resp == null)
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_NO_RESPONSE, "no response received");

      if (resp.getHttpStatus() != HttpServletResponse.SC_OK)
         throw new MssException(
               de.mss.net.exception.ErrorCodes.ERROR_NO_RESPONSE_WITH_ERROR,
               "non OK status received " + (resp.getContent() != null ? resp.getContent() : ""));

      R response = null;

      if (resp.getContent() != null)
         response = parseContent(resp.getContent());

      if (response == null) {
         @SuppressWarnings("unchecked")
         Class<R> clazz = ((Class<R>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
         try {
            response = clazz.newInstance();
         }
         catch (InstantiationException | IllegalAccessException e) {
            throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_NOT_PARSABLE, e);
         }
      }
      
      if (resp.getBinaryContent() != null)
         response.setBinaryContent(resp.getBinaryContent());

      return response;
   }


   protected void sleep(long millies) {
      try {
         Thread.sleep(millies);
      }
      catch (InterruptedException e) {
         getLogger().debug("error while callinig rest server", e);
         Thread.currentThread().interrupt();
      }
   }


   protected RestRequest getRestRequest(RestMethod method, T request, Field[] fields)
         throws MssException {
      RestRequest req = new RestRequest(method);

      for (Field field : fields) {
         if (field.isAnnotationPresent(HeaderParam.class)) {
            String paramName = field.getAnnotationsByType(HeaderParam.class)[0].value();
            String value = getStringValue(request, field.getName());
            if (Tools.isSet(value))
               req.addHeaderParam(paramName, value);
         }
      }

      addPostParams(req, request, fields);

      return req;
   }


   protected String prepareUrl(String url, T request, Field[] fields) {
      String ret = url;
      StringBuilder urlParams = new StringBuilder();

      for (Field field : fields) {
         try {
            String value = getStringValue(request, field.getName());

            if (field.isAnnotationPresent(PathParam.class)) {
               String paramName = field.getAnnotationsByType(PathParam.class)[0].value();
               if (!Tools.isSet(value))
                  throw new MssException(
                        de.mss.net.exception.ErrorCodes.ERROR_PATH_PARAMETER_NOT_SET,
                        "the path parameter '" + paramName + "' is not set");
               ret = ret.replace("{" + paramName + "}", value);
            } else if (field.isAnnotationPresent(QueryParam.class) && Tools.isSet(value)) {
               String paramName = field.getAnnotationsByType(QueryParam.class)[0].value();
               if (urlParams.length() == 0)
                  urlParams.append("?");
               else
                  urlParams.append("&");

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


   protected String getStringValue(T request, String fieldName) throws MssException {
      if (request == null || !Tools.isSet(fieldName))
         return null;

      try {
         Object prop = PropertyUtils.getProperty(request, fieldName);

         if (prop == null)
            return null;
         else if (prop instanceof String)
            return (String)prop;
         else if (prop instanceof BigDecimal)
            return ((BigDecimal)prop).toString();
         else if (prop instanceof BigInteger)
            return ((BigInteger)prop).toString();
         else if (prop instanceof Double)
            return ((Double)prop).toString();
         else if (prop instanceof Float)
            return ((Float)prop).toString();
         else if (prop instanceof Integer)
            return ((Integer)prop).toString();
         else if (prop instanceof java.util.Date)
            return new SimpleDateFormat(this.dateFormat).format((java.util.Date)prop);
         else
            return prop.toString();
      }
      catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_NOT_MAPPABLE, e, "could not get value of field '" + fieldName + "'");
      }
   }


   public String getDateFormat() {
      return this.dateFormat;
   }


   public void setDateFormat(String f) {
      this.dateFormat = f;
   }


   public static Logger getLogger() {
      if (logger == null)
         logger = LogManager.getLogger("default");

      return logger;
   }


   public static void setLogger(Logger l) {
      logger = l;
   }
}
