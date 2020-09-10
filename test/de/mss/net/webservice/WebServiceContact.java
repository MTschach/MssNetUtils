package de.mss.net.webservice;


public class WebServiceContact implements java.io.Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   private String type;
   private String number;


   public WebServiceContact(String t, String n) {
      this.type = t;
      this.number = n;
   }


   public String getType() {
      return this.type;
   }


   public String getNumer() {
      return this.number;
   }


   public void setType(String t) {
      this.type = t;
   }


   public void setNumber(String n) {
      this.number = n;
   }
}
