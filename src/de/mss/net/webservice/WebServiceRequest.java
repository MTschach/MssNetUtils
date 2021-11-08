package de.mss.net.webservice;

import javax.ws.rs.HeaderParam;

public class WebServiceRequest extends CheckRequiredFields {

   private static final long serialVersionUID = 7666097603777732487L;

   @HeaderParam(value = "loggingId")
   private String            loggingId        = null;

   public String getLoggingId() {
      return this.loggingId;
   }


   public void setLoggingId(String l) {
      this.loggingId = l;
   }


   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();

      if (this.loggingId != null) {
         sb.append("loggingId: " + this.loggingId);
      }

      return sb.toString();
   }
}
