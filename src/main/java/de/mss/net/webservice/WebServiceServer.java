package de.mss.webservice;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import de.mss.configtools.ConfigFile;

public abstract class WebServiceServer {

   private Server          server    = null;
   private ServerConnector connector = null;
   private Integer         port      = null;
   private ConfigFile      cfg       = null;

   private Logger          logger    = null;


   protected abstract void initApplication();


   protected abstract void shutDown();


   protected abstract Map<String, WebService> getServiceList();


   public WebServiceServer(ConfigFile c) {
      setConfigFile(c);
      setPort(8080);
   }


   public WebServiceServer(ConfigFile c, Integer p) {
      setConfigFile(c);
      setPort(p);
   }


   public void run(String ip) {
      try {
         initApplication();

         startServer(ip);

         Runtime.getRuntime().addShutdownHook(new Thread(() -> stopServer()));

         this.server.join();
      }
      catch (Exception e) {
         getLogger().error("Error while running", e);
      }
      finally {
         shutDown();
      }
   }


   public void setPort(int p) {
      this.port = Integer.valueOf(p);
   }


   public void setPort(Integer p) {
      this.port = p;
   }


   public void setConfigFile(ConfigFile c) {
      this.cfg = c;
   }


   public ConfigFile getConfigFile() {
      return this.cfg;
   }


   public Integer getPort() {
      return this.port;
   }


   public Logger getLogger() {
      if (this.logger == null)
         this.logger = LogManager.getLogger("default");

      return this.logger;
   }


   protected void startServer(String ip) throws Exception {
      this.server = new Server();

      this.connector = new ServerConnector(this.server);
      this.connector.setPort(this.port.intValue());
      this.server.setConnectors(new Connector[] {this.connector});

      WebServiceRequestHandler handler = new WebServiceRequestHandler();

      handler.addWebServices(getServiceList());

      this.server.setHandler(handler);

      this.server.start();
      getLogger().info("Server is running on " + ip + ":" + this.port.toString());
   }


   public void stopServer() {
      getLogger().debug("Stopping Server");
      try {
         this.server.stop();
         if (this.connector != null)
            this.connector.close();
      }
      catch (Exception e) {
         getLogger().error("Error while stopping server", e);
      }
      getLogger().debug("Server stopped");
   }

}
