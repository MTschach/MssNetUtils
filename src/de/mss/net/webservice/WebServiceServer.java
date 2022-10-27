package de.mss.net.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import de.mss.configtools.ConfigFile;
import de.mss.utils.exception.MssException;
import de.mss.utils.os.OsType;

public abstract class WebServiceServer {

   private static Logger logger = null;

   private static String getDottedName(String packageName) {
      if (packageName.indexOf('/') < 0) {
         return packageName;
      }

      return packageName.replaceAll("\\/", "\\.");
   }


   public static Logger getLogger() {
      if (logger == null) {
         logger = LogManager.getRootLogger();
      }

      return logger;
   }


   private static String getSlashedName(String packageName) {
      if (packageName.indexOf('.') < 0) {
         return packageName;
      }

      return packageName.replaceAll("\\.", "\\/");
   }


   @SuppressWarnings({"unchecked"})
   private static WebService<WebServiceRequest, WebServiceResponse> loadWebService(String packageName, String line)
         throws IllegalArgumentException,
         InvocationTargetException,
         NoSuchMethodException,
         SecurityException {
      String className = (packageName == null ? "" : packageName + ".") + (line == null ? "" : line);
      while (className.startsWith(".")) {
         className = className.substring(1);
      }

      getLogger().debug("loading class {}", className);

      if (!className.endsWith(".class")) {
         return null;
      }

      final String name = className.substring(0, className.length() - 6);
      try {
         final Class<?> clazz = Class.forName(name);
         final Object c = clazz.getDeclaredConstructor().newInstance();
         if (WebService.class.isInstance(c)) {
            return (WebService<WebServiceRequest, WebServiceResponse>)c;
         }
      }
      catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
         getLogger().log(Level.OFF, "exception while checking package", e);
      }

      return null;
   }


   public static void setLogger(Logger l) {
      logger = l;
   }


   private Server server = null;


   private ServerConnector connector = null;


   private Integer port = null;


   private ConfigFile cfg = null;


   public WebServiceServer(ConfigFile c) {
      setConfigFile(c);
      setPort(8080);
   }


   public WebServiceServer(ConfigFile c, Integer p) {
      setConfigFile(c);
      setPort(p);
   }


   public ConfigFile getConfigFile() {
      return this.cfg;
   }


   public Integer getPort() {
      return this.port;
   }


   protected abstract Map<String, WebService<WebServiceRequest, WebServiceResponse>> getServiceList();


   protected abstract void initApplication() throws MssException;


   private Map<String, WebService<WebServiceRequest, WebServiceResponse>> loadFromFileSystem(ClassLoader cl, String packageName, String pathPrefix) {
      getLogger().debug("load from file system");

      final Map<String, WebService<WebServiceRequest, WebServiceResponse>> list = new HashMap<>();

      final String dottedName = getDottedName(packageName);
      final String slashedName = getSlashedName(packageName);

      try (BufferedReader br = new BufferedReader(new InputStreamReader(cl.getResource(slashedName).openStream()))) {
         String line = null;
         while ((line = br.readLine()) != null) {
            final WebService<WebServiceRequest, WebServiceResponse> w = loadWebService(dottedName, line);
            if (w != null) {
               final String log = String.format("loading %s for %s%s", w.getClass().getName(), pathPrefix, w.getPath());
               w.setConfig(getConfigFile());
               getLogger().debug(log);
               list.put(w.getMethod() + " " + pathPrefix + w.getPath(), w);
            }
         }
      }
      catch (IOException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
         getLogger().error("error while walking package " + packageName, e);
      }

      return list;
   }


   private Map<String, WebService<WebServiceRequest, WebServiceResponse>> loadFromJar(String packageName, String pathPrefix) {
      getLogger().debug("loading from jar");

      final Map<String, WebService<WebServiceRequest, WebServiceResponse>> list = new HashMap<>();

      final URL jar = this.getClass().getProtectionDomain().getCodeSource().getLocation();
      final Path jarFile = Paths.get(jar.toString().substring("file:".length() + (OsType.getOsType() == OsType.WINDOWS ? 1 : 0)));
      try (
           FileSystem fs = FileSystems.newFileSystem(jarFile, (ClassLoader)null);
           DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(getSlashedName(packageName)));
      ) {
         for (final Path p : directoryStream) {
            final WebService<WebServiceRequest, WebServiceResponse> w = loadWebService(null, getDottedName(p.toString()));
            if (w != null) {
               final String log = String.format("loading %s for %s%s", w.getClass().getName(), pathPrefix, w.getPath());
               w.setConfig(getConfigFile());
               getLogger().debug(log);
               list.put(w.getMethod() + " " + pathPrefix + w.getPath(), w);
            }
         }
      }
      catch (IOException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
         getLogger().error(String.format("error while jar walking package '%s'", packageName), e);
      }

      return list;
   }


   protected Map<String, WebService<WebServiceRequest, WebServiceResponse>> loadWebServices(ClassLoader cl, String packageName, String pathPrefix) {
      final String slashedName = getSlashedName(packageName);

      URI uri = null;

      try {
         uri = cl.getResource(slashedName).toURI();
      }
      catch (final URISyntaxException e) {
         getLogger().error(String.format("error while loading package '%s'", packageName), e);
         return new HashMap<>();
      }

      if (uri.getScheme().contains("jar")) {
         return loadFromJar(packageName, pathPrefix == null ? "" : pathPrefix);
      }

      return loadFromFileSystem(cl, packageName, pathPrefix == null ? "" : pathPrefix);
   }


   public void run(String ip) {
      run(ip, null);
   }


   public void run(String ip, WebServiceRequestHandler handler) {
      try {
         initApplication();

         if (handler != null) {
            startServer(ip, handler);
         } else {
            startServer(ip);
         }

         Runtime.getRuntime().addShutdownHook(new Thread(() -> stopServer()));

         this.server.join();
      }
      catch (final Exception e) {
         getLogger().error("Error while running", e);
      }
      finally {
         shutDown();
      }
   }


   public void setConfigFile(ConfigFile c) {
      this.cfg = c;
   }


   public void setPort(int p) {
      this.port = Integer.valueOf(p);
   }


   public void setPort(Integer p) {
      this.port = p;
   }


   protected abstract void shutDown();


   protected void startServer(String ip) throws Exception {
      final WebServiceRequestHandler handler = new WebServiceRequestHandler();
      WebServiceRequestHandler.setLogger(getLogger());

      startServer(ip, handler);
   }


   protected void startServer(String ip, WebServiceRequestHandler handler) throws Exception {
      this.server = new Server();

      this.connector = new ServerConnector(this.server);
      this.connector.setPort(this.port.intValue());
      this.server.setConnectors(new Connector[] {this.connector});

      handler.addWebServices(getServiceList());

      this.server.setHandler(handler);

      this.server.start();
      getLogger().info("Server is running on {} : {}", ip, this.port);
   }


   public void stopServer() {
      getLogger().debug("Stopping Server");
      try {
         this.server.stop();
         if (this.connector != null) {
            this.connector.close();
         }
      }
      catch (final Exception e) {
         getLogger().error("Error while stopping server", e);
      }
      getLogger().debug("Server stopped");
   }
}
