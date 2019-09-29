package de.mss.rest;

import de.mss.utils.exception.MssException;

public enum RestMethod {

   //@formatter:off
   GET            ("GET"),
   POST           ("POST"),
   PATCH          ("PATCH"),
   DELETE         ("DELETE")
   ;
   //@formatter:on


   private String method = null;

   private RestMethod(String m) {
      this.method = m;
   }


   public String getMethod() {
      return this.method;
   }


   public static RestMethod getByMethod(String m) throws MssException {
      for (RestMethod method : RestMethod.values()) {
         if (method.getMethod().equalsIgnoreCase(m))
            return method;
      }

      throw new de.mss.utils.exception.MssException(
            de.mss.utils.exception.ErrorCodes.ERROR_PROTOCOL_NOT_SUPPORTED,
            "the method '" + m + "' is not supported");
   }
}
