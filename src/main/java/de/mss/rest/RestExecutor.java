package de.mss.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mss.utils.exception.MssException;

public class RestExecutor {

   private static Logger defaultLogger = LogManager.getLogger();


   private Logger           logger     = defaultLogger;

   private long             connectionTimeout = 10 * 1000;
   private long             requestTimeout    = 180 * 1000;


   private List<RestServer> serverList = null;

   public RestExecutor(RestServer server) {
      addServer(server);
   }


   public RestExecutor(RestServer server, Logger l) {
      addServer(server);
      setLogger(l);
   }


   public RestExecutor(List<RestServer> servers) {
      for (RestServer server : servers)
         addServer(server);
   }


   public RestExecutor(List<RestServer> servers, Logger l) {
      for (RestServer server : servers)
         addServer(server);

      setLogger(l);
   }


   public RestExecutor(RestServer[] servers) {
      for (RestServer server : servers)
         addServer(server);
   }


   public RestExecutor(RestServer[] servers, Logger l) {
      for (RestServer server : servers)
         addServer(server);

      setLogger(l);
   }


   public RestResponse executeRequest(String loggingId, RestRequest request, String bindAddress) throws MssException {
      for (RestServer server : this.serverList) {
         try {
            return executeRequest(loggingId, request, server, bindAddress);
         }
         catch (MssException e) {
            getLogger()
                  .debug(
                        de.mss.utils.Tools.formatLoggingId(loggingId)
                              + "could not execute request for server "
                              + (server.getServer() != null ? server.getServer().getUrl() : "null"),
                        e);
         }

      }
      throw new MssException(de.mss.utils.exception.ErrorCodes.ERROR_UNABLE_TO_EXECUTE_REQUEST);
   }


   public RestResponse executeRequest(String loggingId, RestRequest request, RestServer server, String bindAddress) throws MssException {
      if (request == null || server == null || server.getServer() == null)
         throw new MssException(de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM, "some required parameter are null");

      getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "executing request to " + server.getServer().getCompleteUrl());
      de.mss.utils.StopWatch stopWatch = new de.mss.utils.StopWatch();


      stopWatch.stop();
      getLogger()
            .debug(
                  de.mss.utils.Tools.formatLoggingId(loggingId)
                        + "executing request to "
                        + server.getServer().getCompleteUrl()
                        + " done ["
                        + stopWatch.getDuration()
                        + " ms]");

      throw new MssException(de.mss.utils.exception.ErrorCodes.ERROR_UNABLE_TO_EXECUTE_REQUEST);
   }


   public void addServer(RestServer server) {
      if (server == null)
         return;

      if (this.serverList == null)
         this.serverList = new ArrayList<>();

      this.serverList.add(server);
   }


   public void setLogger(Logger l) {
      if (l != null)
         this.logger = l;
   }


   public Logger getLogger() {
      return this.logger;
   }


   public void setConnectionTimeout(long sec) {
      this.connectionTimeout = sec * 1000;
   }


   public void setRequestTimeout(long sec) {
      this.requestTimeout = sec * 1000;
   }


   public long getConnectionTimeout() {
      return this.connectionTimeout / 1000;
   }


   public long getRequestTimeout() {
      return this.requestTimeout / 1000;
   }
}
