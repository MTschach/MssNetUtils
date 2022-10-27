package de.mss.net.webservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class WebServiceResponseTest {

   @Test
   public void testBinaryContent() {
      final WebServiceResponse resp = new WebServiceResponse();
      resp.setBinaryContent("binaryContent".getBytes());

      assertFalse(resp.hasErrorCode());
      assertFalse(resp.hasErrorText());
      assertFalse(resp.hasStatusCode());
      assertEquals(
            "BinaryContent {[ size {13} [0] {98} [1] {105} [2] {110} [3] {97} [4] {114} [5] {121} [6] {67} [7] {111} [8] {110} [9] {116} [10] {101} [11] {110} [12] {116} ] } ",
            resp.toString());
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
      assertEquals(
            "BinaryContent {[ size {13} [0] {98} [1] {105} [2] {110} [3] {97} [4] {114} [5] {121} [6] {67} [7] {111} [8] {110} [9] {116} [10] {101} [11] {110} [12] {116} ] } ErrorCode {123} ErrorText {ERRORTEXT} StatusCode {200} ",
            resp.toString());
   }


   @Test
   public void testErrorText() {
      final WebServiceResponse resp = new WebServiceResponse();
      resp.setBinaryContent("binaryContent".getBytes());
      resp.setErrorText("ERRORTEXT");

      assertFalse(resp.hasErrorCode());
      assertTrue(resp.hasErrorText());
      assertFalse(resp.hasStatusCode());
      assertEquals(
            "BinaryContent {[ size {13} [0] {98} [1] {105} [2] {110} [3] {97} [4] {114} [5] {121} [6] {67} [7] {111} [8] {110} [9] {116} [10] {101} [11] {110} [12] {116} ] } ErrorText {ERRORTEXT} ",
            resp.toString());
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
      assertEquals(
            "BinaryContent {[ size {13} [0] {98} [1] {105} [2] {110} [3] {97} [4] {114} [5] {121} [6] {67} [7] {111} [8] {110} [9] {116} [10] {101} [11] {110} [12] {116} ] } ErrorText {ERRORTEXT} StatusCode {200} ",
            resp.toString());
   }

}
