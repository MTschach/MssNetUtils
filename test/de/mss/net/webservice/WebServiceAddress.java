package de.mss.net.webservice;

import java.io.Serializable;

public class WebServiceAddress implements Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   private String postCode;
   private String city;


   public String getPostCode() {
      return this.postCode;
   }


   public String getCity() {
      return this.city;
   }


   public void setPostCode(String p) {
      this.postCode = p;
   }


   public void setCity(String c) {
      this.city = c;
   }
}
