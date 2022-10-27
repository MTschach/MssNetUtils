package de.mss.net.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;

import de.mss.utils.logging.Logable;
import de.mss.utils.logging.LoggingUtil;

public class WebServiceRequest extends CheckRequiredFields implements Logable {

   private static final long serialVersionUID = 7666097603777732487L;

   @HeaderParam(value = "loggingId")
   private String            loggingId        = null;

   @QueryParam(value = "language")
   private String            language;

   @Override
   public Map<String, String> doLogging() {
      Map<String, String> ret = new HashMap<>();
      ret = LoggingUtil.addLogging("LoggingId", this.loggingId, ret);
      ret = LoggingUtil.addLogging("Language", this.language, ret);
      return ret;
   }


   public String getLanguage() {
      return this.language;
   }


   public String getLoggingId() {
      return this.loggingId;
   }


   public void setLanguage(String v) {
      this.language = v;
   }


   public void setLoggingId(String l) {
      this.loggingId = l;
   }


   @Override
   public String toString() {
      return de.mss.utils.logging.LoggingUtil.getLogString(doLogging());
   }
}
