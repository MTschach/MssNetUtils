package de.mss.net.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
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

   private static Logger   logger    = null;


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


   public static Logger getLogger() {
      if (logger == null)
         logger = LogManager.getLogger("default");

      return logger;
   }


   public static void setLogger(Logger l) {
      logger = l;
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
      getLogger().info("Server is running on {} : {}", ip, this.port);
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


   protected Map<String, WebService> loadWebServices(ClassLoader cl, String packageName) {
      Map<String, WebService> list = new HashMap<>();

      String dottedName = getDottedName(packageName);
      String slashedName = getSlashedName(packageName);

      try (BufferedReader br = new BufferedReader(new InputStreamReader(cl.getResource(slashedName).openStream()))) {
         String line = null;
         while ((line = br.readLine()) != null) {
            WebService w = loadWebService(dottedName, line);
            if (w != null) {
               String log = String.format("loading %s for /v1%s", w.getClass().getName(), w.getPath());
               w.setConfig(getConfigFile());
               getLogger().debug(log);
               list.put("/v1" + w.getPath(), w);
            }
         }
      }
      catch (IOException e) {
         getLogger().error("error while walking package " + packageName, e);
      }

      return list;
   }


   private WebService loadWebService(String packageName, String line) {
      if (!line.endsWith(".class"))
         return null;

      String name = line.substring(0, line.length() - 6);
      try {
         Class<?> clazz = Class.forName(packageName + "." + name);
         Object c = clazz.newInstance();
         if (WebService.class.isInstance(c))
            return (WebService)c;
      }
      catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
         getLogger().log(Level.OFF, "exception while checking package", e);
      }

      return null;
   }


   private String getDottedName(String packageName) {
      if (packageName.indexOf('/') < 0)
         return packageName;

      return packageName.replaceAll("\\/", "\\.");
   }


   private String getSlashedName(String packageName) {
      if (packageName.indexOf('.') < 0)
         return packageName;

      return packageName.replaceAll("\\.", "\\/");
   }
}
