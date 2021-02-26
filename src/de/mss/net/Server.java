package de.mss.net;

import de.mss.utils.exception.MssException;

public class Server {

   private Protocol protocol = Protocol.HTTP;
   private String   host;
   private Integer  port     = Integer.valueOf(80);
   private String   url;


   public Server(Protocol protocol, String host) {
      setProtocol(protocol);
      setHost(host);
   }


   public Server(Protocol protocol, String host, int port) {
      setProtocol(protocol);
      setHost(host);
      setPort(port);
   }


   public Server(Protocol protocol, String host, int port, String url) {
      setProtocol(protocol);
      setHost(host);
      setPort(port);
      setUrl(url);
   }


   public Server(Protocol protocol, String host, Integer port) {
      setProtocol(protocol);
      setHost(host);
      setPort(port);
   }


   public Server(Protocol protocol, String host, Integer port, String url) {
      setProtocol(protocol);
      setHost(host);
      setPort(port);
      setUrl(url);
   }


   public Server(String url) throws MssException {
      if (url == null) {
         return;
      }

      if (url.indexOf("://") >= 0) {
         final String[] u = url.split("://");
         setProtocol(u[0]);
         parseUrl(u[1]);
      } else {
         parseUrl(url);
      }
   }


   public Server(String protocol, String host) throws MssException {
      setProtocol(protocol);
      setHost(host);
   }


   public Server(String protocol, String host, int port) throws MssException {
      setProtocol(protocol);
      setHost(host);
      setPort(port);
   }


   public Server(String protocol, String host, int port, String url) throws MssException {
      setProtocol(protocol);
      setHost(host);
      setPort(port);
      setUrl(url);
   }


   public Server(String protocol, String host, Integer port) throws MssException {
      setProtocol(protocol);
      setHost(host);
      setPort(port);
   }


   public Server(String protocol, String host, Integer port, String url) throws MssException {
      setProtocol(protocol);
      setHost(host);
      setPort(port);
      setUrl(url);
   }


   public String getCompleteUrl() {
      //@formatter:off
      return (this.protocol == null ? "" : this.protocol.getProtocol() + "://") +
             (this.host == null ? "" : this.host) +
             ":" + this.port.toString() +
             (this.url == null ? "" : this.url);
      //@formatter:on
   }


   public String getHost() {
      return this.host;
   }


   public Integer getPort() {
      return this.port;
   }


   public Protocol getProtocol() {
      return this.protocol;
   }


   public String getUrl() {
      return this.url;
   }


   private void parseHostAndPort(String u) throws MssException {
      if (u.indexOf(':') >= 0) {
         final String[] parts = u.split(":");
         setHost(parts[0]);
         try {
            setPort(Integer.parseInt(parts[1]));
         }
         catch (final NumberFormatException e) {
            throw new MssException(
                  de.mss.net.exception.ErrorCodes.ERROR_NOT_PARSABLE,
                  e,
                  "the value '" + parts[1] + "' could not be parsed as port");
         }
      } else {
         setHost(u);
      }
   }


   private void parseUrl(String u) throws MssException {
      final int i = u.indexOf('/');

      if (i < 0) {
         parseHostAndPort(u);
      } else {
         parseHostAndPort(u.substring(0, i));
         setUrl(u.substring(i));
      }
   }


   public void setHost(String h) {
      this.host = h;
   }


   public void setPort(int p) {
      setPort(Integer.valueOf(p));
   }


   public void setPort(Integer p) {
      if (p == null || p.intValue() <= 0 || p.intValue() >= 65536) {
         this.port = Integer.valueOf(80);
      } else {
         this.port = p;
      }
   }


   public void setProtocol(Protocol p) {
      this.protocol = p;
   }


   public void setProtocol(String p) throws MssException {
      this.protocol = Protocol.getByProtocol(p);
   }


   public void setUrl(String u) {
      this.url = u;
   }
}

