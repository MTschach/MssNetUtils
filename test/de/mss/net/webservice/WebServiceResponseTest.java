package de.mss.net.webservice;

import org.junit.Test;

import junit.framework.TestCase;

public class WebServiceResponseTest extends TestCase {

   @Test
   public void testBinaryContent() {
      final WebServiceResponse resp = new WebServiceResponse();
      resp.setBinaryContent("binaryContent".getBytes());

      assertFalse(resp.hasErrorCode());
      assertFalse(resp.hasErrorText());
      assertFalse(resp.hasStatusCode());
      assertEquals("binaryContent {13 bytes} ", resp.toString());
   }


   @Test
   public void testErrorCode() {
      final WebServiceResponse resp = new WebServiceResponse();
      resp.setBinaryContent("binaryContent".getBytes());
      resp.setErrorText("ERRORTEXT");
      resp.setErrorCode(123);
      resp.setStatusCode(200);

      assertTrue(resp.hasErrorCode());
      assertTrue(resp.hasErrorText());
      assertTrue(resp.hasStatusCode());
      assertEquals("errorCode {123} statusCode {200} errorText {ERRORTEXT} binaryContent {13 bytes} ", resp.toString());
   }


   @Test
   public void testErrorText() {
      final WebServiceResponse resp = new WebServiceResponse();
      resp.setBinaryContent("binaryContent".getBytes());
      resp.setErrorText("ERRORTEXT");

      assertFalse(resp.hasErrorCode());
      assertTrue(resp.hasErrorText());
      assertFalse(resp.hasStatusCode());
      assertEquals("errorText {ERRORTEXT} binaryContent {13 bytes} ", resp.toString());
   }


   @Test
   public void testNoErrorCode() {
      final WebServiceResponse resp = new WebServiceResponse();

      assertFalse(resp.hasErrorCode());
      assertFalse(resp.hasErrorText());
      assertFalse(resp.hasStatusCode());
      assertEquals("", resp.toString());

      resp.setErrorCode(0);
      resp.setErrorText("");
      assertFalse(resp.hasErrorCode());
      assertFalse(resp.hasErrorText());
   }


   @Test
   public void testStatusCode() {
      final WebServiceResponse resp = new WebServiceResponse();
      resp.setBinaryContent("binaryContent".getBytes());
      resp.setErrorText("ERRORTEXT");
      resp.setStatusCode(200);

      assertFalse(resp.hasErrorCode());
      assertTrue(resp.hasErrorText());
      assertTrue(resp.hasStatusCode());
      assertEquals("statusCode {200} errorText {ERRORTEXT} binaryContent {13 bytes} ", resp.toString());
   }

}
