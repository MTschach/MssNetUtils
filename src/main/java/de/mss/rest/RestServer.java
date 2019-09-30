package de.mss.rest;

import de.mss.net.AuthenticatedServer;
import de.mss.net.Server;
import de.mss.utils.exception.MssException;

public class RestServer {

   private de.mss.net.Server              server = null;
   private de.de.mss.net.AuthenticatedServer proxy  = null;

   public RestServer(String url) throws MssException {
      setServer(url);
   }


   public RestServer(String url, String proxyUrl) throws MssException {
      setServer(url);
      setProxy(proxyUrl);
   }
   

   public void setServer(String url) throws MssException {
      this.server = new Server(url);
   }


   public void setProxy(String url) throws MssException {
      this.proxy = new AuthenticatedServer(url);
   }


   public void setServer(Server s) {
      this.server = s;
   }


   public void setProxy(AuthenticatedServer p) {
      this.proxy = p;
   }


   public Server getServer() {
      return this.server;
   }


   public AuthenticatedServer getProxy() {
      return this.proxy;
   }
}
