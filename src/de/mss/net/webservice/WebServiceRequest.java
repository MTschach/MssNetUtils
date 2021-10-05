package de.mss.net.webservice;

import javax.ws.rs.HeaderParam;

import de.mss.utils.exception.MssException;

public class WebServiceRequest implements java.io.Serializable {

   private static final long serialVersionUID = 7666097603777732487L;

   @HeaderParam(value = "loggingId")
   private String            loggingId        = null;

   protected void checkMaxStringLength(String s, int len, String name) throws MssException {
      if (s == null || s.length() <= len) {
         return;
      }

      throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_FIELD_TOO_LONG, "the field '" + name + "' is too long. maximum length is " + len);
   }


   protected void checkMinStringLength(String s, int len, String name) throws MssException {
      if (s == null || s.length() >= len) {
         return;
      }

      throw new MssException(
            de.mss.net.exception.ErrorCodes.ERROR_FIELD_TOO_SHORT,
            "the field '" + name + "' is too short. minimum length is " + len);
   }


   @SuppressWarnings("unused")
   public void checkRequiredFields() throws MssException {
      // nothing to do here
   }


   protected void checkStringLength(String s, int len, String name) throws MssException {
      if (s == null || s.length() == len) {
         return;
      }

      throw new MssException(
            de.mss.net.exception.ErrorCodes.ERROR_FIELD_INVALID_LENGTH,
            "the field '" + name + "' has an invalid length. (required length is " + len + ")");
   }


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
