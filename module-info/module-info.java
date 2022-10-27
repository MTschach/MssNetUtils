module mss.netutils {

   exports de.mss.net;
   exports de.mss.net.call;
   exports de.mss.net.exception;
   exports de.mss.net.rest;
   exports de.mss.net.serializer;
   exports de.mss.net.webservice;

   requires transitive mss.utils;
   requires transitive mss.configtools;
   requires transitive org.apache.commons.beanutils;
   requires transitive java.xml;
   requires org.apache.httpcomponents.httpcore;
   requires transitive com.fasterxml.jackson.databind;
   requires transitive com.fasterxml.jackson.core;
   requires transitive org.eclipse.jetty.server;
   requires transitive java.ws.rs;
   requires transitive com.fasterxml.jackson.annotation;
}
