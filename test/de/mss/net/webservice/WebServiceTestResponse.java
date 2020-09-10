package de.mss.net.webservice;

import java.util.List;

public class WebServiceTestResponse extends WebServiceResponse {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;


   private String                  state;

   private List<WebServiceAddress> address;


   public void setState(String s) {
      this.state = s;
   }


   public String getState() {
      return this.state;
   }


   public void setAdress(List<WebServiceAddress> l) {
      this.address = l;
   }


   public List<WebServiceAddress> getAddress() {
      return this.address;
   }

}
