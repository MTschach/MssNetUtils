package de.mss.net.rest;

import java.util.function.Supplier;

import de.mss.utils.exception.MssException;

public enum RestMethod {

   //@formatter:off
   UNKNOWN        (""),
   GET            ("GET"),
   POST           ("POST"),
   PATCH          ("PATCH"),
   DELETE         ("DELETE")
   ;
   //@formatter:on


   public static <T extends MssException> RestMethod getByMethod(String m, Supplier<T> throwException) throws T {
      for (final RestMethod method : RestMethod.values()) {
         if (method.getMethod().equalsIgnoreCase(m)) {
            return method;
         }
      }

      if (throwException != null) {
         throw throwException.get();
      }

      return null;
   }

   private String method = null;


   private RestMethod(String m) {
      this.method = m;
   }


   public String getMethod() {
      return this.method;
   }
}
