package de.mss.net.webservice;

import java.io.Serializable;

public interface WebServiceRequest extends Serializable {

   public void setLoggingId(String l);


   public String getLoggingId();
}
