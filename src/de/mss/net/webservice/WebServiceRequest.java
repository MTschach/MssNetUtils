package de.mss.net.webservice;

import javax.ws.rs.HeaderParam;

import de.mss.utils.exception.MssException;

public abstract class WebServiceRequest implements java.io.Serializable {

   private static final long serialVersionUID = 7666097603777732487L;

   @HeaderParam(value = "loggingId")
   private String            loggingId        = null;

   public void setLoggingId(String l) {
      this.loggingId = l;
   }


   public String getLoggingId() {
      return this.loggingId;
   }


   @SuppressWarnings("unused")
   public void checkRequiredFields() throws MssException {
      // nothing to do here
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
