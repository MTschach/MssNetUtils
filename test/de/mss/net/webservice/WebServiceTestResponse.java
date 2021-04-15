package de.mss.net.webservice;

import java.io.IOException;
import java.util.List;

public class WebServiceTestResponse extends WebServiceResponse {

   /**
    *
    */
   private static final long serialVersionUID = 1L;


   private String                  state;

   private List<WebServiceAddress> address;


   public List<WebServiceAddress> getAddress() {
      return this.address;
   }


   public String getState() {
      return this.state;
   }


   public void setAdress(List<WebServiceAddress> l) {
      this.address = l;
   }


   public void setState(String s) throws IOException {
      if ("ioex".equals(s)) {
         throw new IOException();
      }

      this.state = s;
   }

}
