package de.mss.net.webservice;


public class WebServiceResponse implements java.io.Serializable {

   private static final long serialVersionUID = -2408071715329783134L;
   private Integer           errorCode        = null;
   private Integer           statusCode       = null;
   private String            errorText        = null;
   private byte[]            binaryContent    = null;


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
      final StringBuilder sb = new StringBuilder();

      if (this.errorCode != null) {
         sb.append("errorCode {" + this.errorCode.toString() + "} ");
      }

      if (this.statusCode != null) {
         sb.append("statusCode {" + this.statusCode.toString() + "} ");
      }

      if (this.errorText != null) {
         sb.append("errorText {" + this.errorText + "} ");
      }

      if (this.binaryContent != null) {
         sb.append("binaryContent {" + this.binaryContent.length + " bytes} ");
      }

      return sb.toString();
   }
}
