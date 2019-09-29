package de.mss.webservice;


public interface WebServiceResponse extends WebServiceTransferObject {

   public void setErrorCode(Integer e);


   public void setErrorText(String e);


   public void setStatusCode(Integer i);


   public Integer getErrorCode();


   public String getErrorText();


   public Integer getStatusCode();


   public boolean hasErrorCode();


   public boolean hasErrorText();


   public boolean hasStatusCode();
}
