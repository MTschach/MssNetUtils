package de.mss.net;

import de.mss.utils.exception.MssException;

public class AuthenticatedServer extends Server {

   String userName     = null;
   String userPassword = null;

   public AuthenticatedServer(String url) throws MssException {
      super(url);
   }


   public AuthenticatedServer(String url, String user, String password) throws MssException {
      super(url);
      setUserName(user);
      setPassword(password);
   }


   public AuthenticatedServer(String protocol, String host) throws MssException {
      super(protocol, host);
   }


   public AuthenticatedServer(String protocol, String host, String user, String password) throws MssException {
      super(protocol, host);
      setUserName(user);
      setPassword(password);
   }


   public AuthenticatedServer(Protocol protocol, String host) {
      super(protocol, host);
   }


   public AuthenticatedServer(Protocol protocol, String host, String user, String password) {
      super(protocol, host);
      setUserName(user);
      setPassword(password);
   }


   public AuthenticatedServer(String protocol, String host, int port) throws MssException {
      super(protocol, host, port);
   }


   public AuthenticatedServer(String protocol, String host, int port, String user, String password) throws MssException {
      super(protocol, host, port);
      setUserName(user);
      setPassword(password);
   }


   public AuthenticatedServer(Protocol protocol, String host, int port) {
      super(protocol, host, port);
   }


   public AuthenticatedServer(Protocol protocol, String host, int port, String user, String password) {
      super(protocol, host, port);
      setUserName(user);
      setPassword(password);
   }


   public AuthenticatedServer(String protocol, String host, Integer port) throws MssException {
      super(protocol, host, port);
   }


   public AuthenticatedServer(String protocol, String host, Integer port, String user, String password) throws MssException {
      super(protocol, host, port);
      setUserName(user);
      setPassword(password);
   }


   public AuthenticatedServer(Protocol protocol, String host, Integer port) {
      super(protocol, host, port);
   }


   public AuthenticatedServer(Protocol protocol, String host, Integer port, String user, String password) {
      super(protocol, host, port);
      setUserName(user);
      setPassword(password);
   }


   public AuthenticatedServer(String protocol, String host, int port, String url) throws MssException {
      super(protocol, host, port, url);
   }


   public AuthenticatedServer(String protocol, String host, int port, String url, String user, String password) throws MssException {
      super(protocol, host, port, url);
      setUserName(user);
      setPassword(password);
   }


   public AuthenticatedServer(Protocol protocol, String host, int port, String url) {
      super(protocol, host, port, url);
   }


   public AuthenticatedServer(Protocol protocol, String host, int port, String url, String user, String password) {
      super(protocol, host, port, url);
      setUserName(user);
      setPassword(password);
   }


   public AuthenticatedServer(String protocol, String host, Integer port, String url) throws MssException {
      super(protocol, host, port, url);
   }


   public AuthenticatedServer(String protocol, String host, Integer port, String url, String user, String password) throws MssException {
      super(protocol, host, port, url);
      setUserName(user);
      setPassword(password);
   }


   public AuthenticatedServer(Protocol protocol, String host, Integer port, String url) {
      super(protocol, host, port, url);
   }


   public AuthenticatedServer(Protocol protocol, String host, Integer port, String url, String user, String password) {
      super(protocol, host, port, url);
      setUserName(user);
      setPassword(password);
   }


   public void setUserName(String user) {
      this.userName = user;
   }


   public void setPassword(String password) {
      this.userPassword = password;
   }


   public String getUser() {
      return this.userName;
   }


   public String getPassword() {
      return this.userPassword;
   }
}

