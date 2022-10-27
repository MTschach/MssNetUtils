package de.mss.net.webservice;

import java.util.HashMap;
import java.util.Map;

import de.mss.utils.logging.Logable;
import de.mss.utils.logging.LoggingUtil;

public class WebServiceResponse extends CheckRequiredFields implements Logable {

   private static final long serialVersionUID = -2408071715329783134L;
   private Integer           errorCode        = null;
   private Integer           statusCode       = null;
   private String            errorText        = null;
   private byte[]            binaryContent    = null;


   @Override
   public Map<String, String> doLogging() {
      Map<String, String> ret = new HashMap<>();
      ret = LoggingUtil.addLogging("ErrorCode", this.errorCode, ret);
      ret = LoggingUtil.addLogging("StatusCode", this.statusCode, ret);
      ret = LoggingUtil.addLogging("ErrorText", this.errorText, ret);
      ret = LoggingUtil.addLogging("BinaryContent", this.binaryContent, ret);
      return ret;
   }


   public byte[] getBinaryContent() {
      return this.binaryContent;
   }


   public Integer getErrorCode() {
      return this.errorCode;
   }


   public String getErrorText() {
      return this.errorText;
   }


   public Integer getStatusCode() {
      return this.statusCode;
   }


   public boolean hasErrorCode() {
      return this.errorCode != null && this.errorCode.intValue() != 0;
   }


   public boolean hasErrorText() {
      return this.errorText != null && this.errorText.length() > 0;
   }


   public boolean hasStatusCode() {
      return this.statusCode != null;
   }


   public void setBinaryContent(byte[] b) {
      this.binaryContent = b;
   }


   public void setErrorCode(Integer e) {
      this.errorCode = e;
   }


   public void setErrorText(String e) {
      this.errorText = e;
   }


   public void setStatusCode(Integer i) {
      this.statusCode = i;
   }


   @Override
   public String toString() {
      return LoggingUtil.getLogString(doLogging());
   }
}
