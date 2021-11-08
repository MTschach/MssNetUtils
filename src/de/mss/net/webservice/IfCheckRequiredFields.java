package de.mss.net.webservice;

import java.io.Serializable;

import de.mss.utils.exception.MssException;

public interface IfCheckRequiredFields extends Serializable {

   public void checkRequiredFields() throws MssException;
}
