package de.mss.net.webservice;

import org.junit.Test;

import de.mss.utils.exception.MssException;
import junit.framework.TestCase;

public class CheckRequiredFieldsTest extends TestCase {

   private final CheckRequiredFieldsForTest classUnderTest = new CheckRequiredFieldsForTest();

   @Test
   public void testMaxStringLength() throws MssException {
      this.classUnderTest.checkMaxStringLengthForTest(null, 10, "blah");
      this.classUnderTest.checkMaxStringLengthForTest("blah", 10, "blah");
      try {
         this.classUnderTest.checkMaxStringLengthForTest("bliehblahblubb", 10, "blah");
      }
      catch (final MssException e) {
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_FIELD_TOO_LONG, e.getError());
      }
   }


   @Test
   public void testMinStringLength() throws MssException {
      this.classUnderTest.checkMinStringLengthForTest(null, 3, "blah");
      this.classUnderTest.checkMinStringLengthForTest("blah", 3, "blah");
      try {
         this.classUnderTest.checkMinStringLengthForTest("bliehblahblubb", 30, "blah");
      }
      catch (final MssException e) {
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_FIELD_TOO_SHORT, e.getError());
      }
   }


   @Test
   public void testStringLength() throws MssException {
      this.classUnderTest.checkStringLengthForTest(null, 10, "blah");
      this.classUnderTest.checkStringLengthForTest("blah", 4, "blah");
      try {
         this.classUnderTest.checkStringLengthForTest("bliehblahblubb", 4, "blah");
      }
      catch (final MssException e) {
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_FIELD_INVALID_LENGTH, e.getError());
      }
   }
}
