package de.mss.net.webservice;

import java.util.List;

public class WebServiceBody implements java.io.Serializable {

   /**
    * 
    */
   private static final long       serialVersionUID = 1L;
   private String                  street;
   private String                  number;

   private List<WebServiceContact> contacts;
   private WebServiceAddress       address;


   public String getStreet() {
      return this.street;
   }


   public String getNumber() {
      return this.number;
   }


   public List<WebServiceContact> getContacts() {
      return this.contacts;
   }


   public WebServiceAddress getAddress() {
      return this.address;
   }


   public void setStreet(String s) {
      this.street = s;
   }


   public void setNumber(String n) {
      this.number = n;
   }


   public void setContacts(List<WebServiceContact> l) {
      this.contacts = l;
   }


   public void setAddress(WebServiceAddress a) {
      this.address = a;
   }
}
