package de.mss.net.webservice;


public abstract class WebServiceRequestImpl implements WebServiceRequest {

   private static final long serialVersionUID = 7666097603777732487L;

   private String            loggingId        = null;

   @Override
   public void setLoggingId(String l) {
      this.loggingId = l;
   }


   @Override
   public String getLoggingId() {
      return this.loggingId;
   }
}
