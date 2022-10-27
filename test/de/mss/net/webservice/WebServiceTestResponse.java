package de.mss.net.webservice;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class WebServiceTestResponse extends WebServiceResponse {

   /**
    *
    */
   private static final long serialVersionUID = 1L;


   private String                  state;

   private List<WebServiceAddress> address;

   private EnumValForTest          enumVal;


   private Date validFrom;


   public List<WebServiceAddress> getAddress() {
      return this.address;
   }


   public EnumValForTest getEnumVal() {
      return this.enumVal;
   }


   public String getState() {
      return this.state;
   }


   public Date getValidFrom() {
      return this.validFrom;
   }


   public void setAdress(List<WebServiceAddress> l) {
      this.address = l;
   }


   public void setEnumVal(EnumValForTest e) {
      this.enumVal = e;
   }


   public void setState(String s) throws IOException {
      if ("ioex".equals(s)) {
         throw new IOException();
      }

      this.state = s;
   }


   public void setValidFrom(Date d) {
      this.validFrom = d;
   }

}
