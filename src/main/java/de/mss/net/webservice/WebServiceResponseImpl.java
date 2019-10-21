package de.mss.net.webservice;


public abstract class WebServiceResponseImpl implements WebServiceResponse {

   private static final long serialVersionUID = -2408071715329783134L;
   private Integer errorCode  = null;
   private Integer statusCode = null;
   private String  errorText  = null;

   @Override
   public void setErrorCode(Integer e) {
      this.errorCode = e;
   }


   @Override
   public void setErrorText(String e) {
      this.errorText = e;
   }


   @Override
   public void setStatusCode(Integer i) {
      this.statusCode = i;
   }


   @Override
   public Integer getErrorCode() {
      return this.errorCode;
   }


   @Override
   public String getErrorText() {
      return this.errorText;
   }


   @Override
   public Integer getStatusCode() {
      return this.statusCode;
   }


   @Override
   public boolean hasErrorCode() {
      return (this.errorCode != null && this.errorCode.intValue() != 0);
   }


   @Override
   public boolean hasErrorText() {
      return (this.errorText != null && this.errorText.length() > 0);
   }


   @Override
   public boolean hasStatusCode() {
      return (this.statusCode != null);
   }

}
