package de.mss.net.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import de.mss.utils.Tools;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class WebServiceRequestHandler extends AbstractHandler {

   private static Logger      logger            = null;

   public static final String HEADER_LOGGING_ID = "loggingId";

   @SuppressWarnings("resource")
   private static Map<String, String> getBodyParams(Request request, Map<String, String> params) {
      Map<String, String> ret = params;
      if (ret == null) {
         ret = new HashMap<>();
      }

      if ("POST".equalsIgnoreCase(request.getMethod())) {
         try {
            ret.put("body", request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
         }
         catch (final IOException e) {
            Tools.doNullLog(e);
         }
      }

      return ret;
   }


   private static Map<String, String> getHeaderParams(Request request, Map<String, String> params) {
      Map<String, String> ret = params;
      if (ret == null) {
         ret = new HashMap<>();
      }

      final Enumeration<String> names = request.getHeaderNames();
      while (names.hasMoreElements()) {
         final String n = names.nextElement();
         ret.put(n, request.getHeader(n));
      }

      return ret;
   }


   public static Logger getLogger() {
      if (logger == null) {
         logger = LogManager.getRootLogger();
      }

      return logger;
   }


   private static Map<String, String> getUrlParams(Request request, Map<String, String> params) {
      Map<String, String> ret = params;
      if (ret == null) {
         ret = new HashMap<>();
      }

      final Enumeration<String> names = request.getParameterNames();
      while (names.hasMoreElements()) {
         final String n = names.nextElement();
         ret.put(n, request.getParameter(n));
      }

      return ret;
   }


   private static boolean matches(String key, String target) {
      final String[] keys = key.split("/");
      final String[] targets = target.split("/");

      if (keys.length != targets.length) {
         return false;
      }

      for (int i = 0; i < keys.length; i++ ) {
         if (!keys[i].matches("\\{\\w+\\}") && !keys[i].equals(targets[i])) {
            return false;
         }
      }

      return true;
   }


   public static void setLogger(Logger l) {
      logger = l;
   }


   Map<String, WebService<WebServiceRequest, WebServiceResponse>> serviceList = null;


   private final List<SpecialWebServiceRequestHandler> specialHandlers = new ArrayList<>();


   private List<String> headersToCopy = new ArrayList<>();


   protected void addCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
      response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
      response.addHeader("Access-Control-Request-Methods", "*");
      response.addHeader("Access-Control-Allow-Headers", "*");
   }


   public void addHeaderToCopy(String name) {
      if (!this.headersToCopy.contains(name)) {
         this.headersToCopy.add(name);
      }
   }


   public void addSpecialHandler(SpecialWebServiceRequestHandler handler) {
      this.specialHandlers.add(handler);
   }


   public void addWebService(String target, WebService<WebServiceRequest, WebServiceResponse> service) {
      initServiceList();

      this.serviceList.put(target, service);
   }


   public void addWebServices(Map<String, WebService<WebServiceRequest, WebServiceResponse>> list) {
      initServiceList();

      for (final Entry<String, WebService<WebServiceRequest, WebServiceResponse>> entry : list.entrySet()) {
         this.serviceList.put(entry.getKey(), entry.getValue());
      }
   }


   public void clearWebServices() {
      this.serviceList = new HashMap<>();
   }


   private void copyHeaders(Request request, HttpServletResponse response) {
      for (final String name : this.headersToCopy) {
         final String val = request.getHeader(name);
         if (val != null) {
            response.addHeader(name, val);
         }
      }

   }


   private String findTarget(String target) {
      initServiceList();

      for (final SpecialWebServiceRequestHandler sh : this.specialHandlers) {
         final String t = sh.findTarget(target);
         if (t != null) {
            return t;
         }
      }

      if (this.serviceList.containsKey(target)) {
         return target;
      }

      for (final Entry<String, WebService<WebServiceRequest, WebServiceResponse>> entry : this.serviceList.entrySet()) {
         if (matches(entry.getKey(), target)) {
            return entry.getKey();
         }
      }

      return null;
   }


   private WebService<WebServiceRequest, WebServiceResponse> findWebService(String target) {
      initServiceList();

      for (final SpecialWebServiceRequestHandler sh : this.specialHandlers) {
         final WebService<WebServiceRequest, WebServiceResponse> ws = sh.findWebService(target);
         if (ws != null) {
            return ws;
         }
      }

      if (this.serviceList.containsKey(target)) {
         return this.serviceList.get(target);
      }

      for (final Entry<String, WebService<WebServiceRequest, WebServiceResponse>> entry : this.serviceList.entrySet()) {
         if (matches(entry.getKey(), target)) {
            return entry.getValue();
         }
      }

      return null;
   }


   private Map<String, String> getPathParams(String target) {
      final Map<String, String> ret = new HashMap<>();

      final String originTarget = findTarget(target);
      final String[] originParts = originTarget.split("/");
      final String[] targetParts = target.split("/");

      for (int i = 0; i < originParts.length; i++ ) {
         if (originParts[i].matches("\\{\\w+\\}")) {
            ret.put(originParts[i].replaceAll("\\{", "").replaceAll("\\}", ""), targetParts[i]);
         }
      }

      return ret;
   }


   @Override
   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
         throws IOException,
         ServletException {

      String loggingId = null;
      String method = "";
      if (request != null) {
         loggingId = request.getHeader(HEADER_LOGGING_ID);
         method = request.getMethod();
      }

      if (!de.mss.utils.Tools.isSet(loggingId)) {
         loggingId = UUID.randomUUID().toString();
      }

      response.setHeader(HEADER_LOGGING_ID, loggingId);
      response.setHeader("Access-Control-Expose-Headers", HEADER_LOGGING_ID);
      addCorsHeaders(request, response);
      copyHeaders(baseRequest, response);

      final WebService<WebServiceRequest, WebServiceResponse> service = findWebService(method + " " + target);
      if (service == null) {
         response.setStatus(HttpServletResponse.SC_NOT_FOUND);
         baseRequest.setHandled(true);
         return;
      }

      handleRequest(loggingId, target, service, baseRequest, request, response);
   }


   private void handleRequest(
         String loggingId,
         String target,
         WebService<WebServiceRequest, WebServiceResponse> webService,
         Request baseRequest,
         HttpServletRequest request,
         HttpServletResponse response) {

      Map<String, String> params = getPathParams(request.getMethod() + " " + target);
      params = getHeaderParams(baseRequest, params);
      params = getUrlParams(baseRequest, params);
      params = getBodyParams(baseRequest, params);

      webService.handleRequest(loggingId, target, params, baseRequest, request, response);

      baseRequest.setHandled(true);
   }


   private void initServiceList() {
      if (this.serviceList == null) {
         this.serviceList = new HashMap<>();
      }
   }


   public void removeWebService(String target) {
      initServiceList();

      if (this.serviceList.containsKey(target)) {
         this.serviceList.remove(target);
      }
   }


   public void setHeadersToCopy(List<String> l) {
      this.headersToCopy = l;

      if (this.headersToCopy == null) {
         this.headersToCopy = new ArrayList<>();
      }
   }
}
