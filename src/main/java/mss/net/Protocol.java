package de.mss.net;

import de.mss.utils.exception.MssException;

public enum Protocol {

   //@formatter:off
   HTTP              ("http"                 , false),
   HTTPS             ("https"                , true)
   ;
   //@formatter:on

   private String  protocol;
   private boolean useSsl;

   private Protocol(String p, boolean s) {
      this.protocol = p;
      this.useSsl = s;
   }


   public String getProtocol() {
      return this.protocol;
   }


   public boolean useSsl() {
      return this.useSsl;
   }


   public static Protocol getByProtocol(String p) throws MssException {
      for (Protocol r : Protocol.values()) {
         if (r.getProtocol().equalsIgnoreCase(p))
            return r;
      }

      throw new de.mss.utils.exception.MssException(
            de.mss.utils.exception.ErrorCodes.ERROR_PROTOCOL_NOT_SUPPORTED,
            "the protocol '" + p + "' is not supported");
   }
}
