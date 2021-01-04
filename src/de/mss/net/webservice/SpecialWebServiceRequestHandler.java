package de.mss.net.webservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class SpecialWebServiceRequestHandler {

   private static Logger logger = null;

   public abstract WebService<WebServiceRequest, WebServiceResponse> findWebService(String target);


   public abstract String findTarget(String target);


   public static Logger getLogger() {
      if (logger == null) {
         logger = LogManager.getRootLogger();
      }

      return logger;
   }


   public static void setLogger(Logger l) {
      logger = l;
   }
}
