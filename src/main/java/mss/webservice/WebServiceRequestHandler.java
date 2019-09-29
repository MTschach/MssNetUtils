package de.mss.webservice;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class WebServiceRequestHandler extends AbstractHandler {

   Map<String, WebService>    serviceList       = null;

   public static final String HEADER_LOGGING_ID = "LOGGING-ID";

   @Override
   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
         throws IOException,
         ServletException {

      String loggingId = null;
      if (request != null) {
         loggingId = request.getHeader(HEADER_LOGGING_ID);
      }

      if (!de.mss.utils.Tools.isSet(loggingId))
         loggingId = UUID.randomUUID().toString();

      response.setHeader(HEADER_LOGGING_ID, loggingId);
      response.setHeader("Access-Control-Expose-Headers", HEADER_LOGGING_ID);

      WebService service = findWebService(target);
      if (service == null) {
         response.setStatus(HttpServletResponse.SC_NOT_FOUND);
         baseRequest.setHandled(true);
         return;
      }

      handleRequest(loggingId, target, service, baseRequest, request, response);
   }


   private String findTarget(String target) {
      initServiceList();

      if (this.serviceList.containsKey(target))
         return target;

      for (Entry<String, WebService> entry : this.serviceList.entrySet()) {
         if (matches(entry.getKey(), target)) {
            return entry.getKey();
         }
      }

      return null;
   }


   private WebService findWebService(String target) {
      initServiceList();

      if (this.serviceList.containsKey(target))
         return this.serviceList.get(target);

      for (Entry<String, WebService> entry : this.serviceList.entrySet()) {
         if (matches(entry.getKey(), target)) {
            return entry.getValue();
         }
      }

      return null;
   }


   private boolean matches(String key, String target) {
      String[] keys = key.split("/");
      String[] targets = target.split("/");

      if (keys.length != targets.length)
         return false;

      for (int i = 0; i < keys.length; i++ ) {
         if (!keys[i].matches("\\{\\w+\\}") && !keys[i].equals(targets[i]))
            return false;
      }

      return true;
   }


   private
         void
         handleRequest(
               String loggingId,
               String target,
               WebService webService,
               Request baseRequest,
               HttpServletRequest request,
               HttpServletResponse response) {

      Map<String, String> params = getPathParams(target);
      params = getHeaderParams(baseRequest, params);
      params = getUrlParams(baseRequest, params);

      webService.handleRequest(loggingId, target, params, baseRequest, request, response);
      baseRequest.setHandled(true);
   }


   private Map<String, String> getHeaderParams(Request request, Map<String, String> params) {
      Map<String, String> ret = params;
      if (ret == null)
         ret = new HashMap<>();

      Enumeration<String> names = request.getHeaderNames();
      while (names.hasMoreElements()) {
         String n = names.nextElement();
         ret.put(n, request.getHeader(n));
      }

      return ret;
   }


   private Map<String, String> getUrlParams(Request request, Map<String, String> params) {
      Map<String, String> ret = params;
      if (ret == null)
         ret = new HashMap<>();

      Enumeration<String> names = request.getParameterNames();
      while (names.hasMoreElements()) {
         String n = names.nextElement();
         ret.put(n, request.getParameter(n));
      }

      return ret;
   }


   private Map<String, String> getPathParams(String target) {
      Map<String, String> ret = new HashMap<>();

      String originTarget = findTarget(target);
      String[] originParts = originTarget.split("/");
      String[] targetParts = target.split("/");

      for (int i = 0; i < originParts.length; i++ ) {
         if (originParts[i].matches("\\{\\w+\\}")) {
            ret.put(originParts[i].replaceAll("\\{", "").replaceAll("\\}", ""), targetParts[i]);
         }
      }

      return ret;
   }


   public void addWebService(String target, WebService service) {
      initServiceList();

      this.serviceList.put(target, service);
   }


   public void addWebServices(Map<String, WebService> list) {
      initServiceList();

      for (Entry<String, WebService> entry : list.entrySet())
         this.serviceList.put(entry.getKey(), entry.getValue());
   }


   public void clearWebServices() {
      this.serviceList = new HashMap<>();
   }


   public void removeWebService(String target) {
      initServiceList();

      if (this.serviceList.containsKey(target))
         this.serviceList.remove(target);
   }


   private void initServiceList() {
      if (this.serviceList == null)
         this.serviceList = new HashMap<>();
   }


}
