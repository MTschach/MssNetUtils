package de.mss.net.webservice;

import de.mss.utils.exception.MssException;

public class CheckRequiredFieldsForTest extends CheckRequiredFields {

   private static final long serialVersionUID = 1L;

   public void checkMaxStringLengthForTest(String s, int len, String name) throws MssException {
      super.checkMaxStringLength(s, len, name);
   }


   public void checkMinStringLengthForTest(String s, int len, String name) throws MssException {
      super.checkMinStringLength(s, len, name);
   }


   public void checkStringLengthForTest(String s, int len, String name) throws MssException {
      super.checkStringLength(s, len, name);
   }
}
