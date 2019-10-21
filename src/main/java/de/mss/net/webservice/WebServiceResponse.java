package de.mss.net.webservice;

import java.io.Serializable;

public interface WebServiceResponse extends Serializable {

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
