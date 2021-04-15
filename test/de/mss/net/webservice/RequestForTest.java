package de.mss.net.webservice;

import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpInput;
import org.eclipse.jetty.server.Request;

public class RequestForTest extends Request {

   private String url;

   public RequestForTest(HttpChannel channel, HttpInput input) {
      super(channel, input);
   }


   public RequestForTest(String u) {
      super(null, null);
      this.url = u;
   }


   @Override
   public String getRequestURI() {
      return this.url;
   }
}
