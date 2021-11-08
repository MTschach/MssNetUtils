package de.mss.net.webservice;

import de.mss.utils.exception.MssException;

public abstract class CheckRequiredFields implements IfCheckRequiredFields {

   private static final long serialVersionUID = -681294041762164317L;

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


   @Override
   public void checkRequiredFields() throws MssException {
      // nothing to do here at the moment
   }


   protected void checkStringLength(String s, int len, String name) throws MssException {
      if (s == null || s.length() == len) {
         return;
      }

      throw new MssException(
            de.mss.net.exception.ErrorCodes.ERROR_FIELD_INVALID_LENGTH,
            "the field '" + name + "' has an invalid length. (required length is " + len + ")");
   }
}
