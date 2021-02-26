package de.mss.net.rest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;

public class RestResponseTest extends TestCase {

   private RestResponse response;

   @Override
   public void setUp() throws Exception {
      super.setUp();

      this.response = new RestResponse(200);
      this.response.setContent("content");
   }


   @Test
   public void test() {
      assertEquals("HttpStatus {200} Content {content} ", this.response.toString());

      assertNull(this.response.getBinaryContent());
      assertEquals("content", this.response.getContent());
      assertEquals(200, this.response.getHttpStatus());
      assertNotNull(this.response.getHeaderParams());
      assertTrue(this.response.getHeaderParams().isEmpty());
      assertNull(this.response.getRidirectUrl());
      assertEquals("HttpStatus {200} Content {content} HeaderParams {size {0} []} ", this.response.toString());
   }


   @Test
   public void testBinaryContent() {
      this.response.setBinaryContent("content".getBytes());
      this.response.setContent(null);
      assertEquals("content", new String(this.response.getBinaryContent()));
      assertNull(this.response.getContent());
      assertEquals(200, this.response.getHttpStatus());
      assertNotNull(this.response.getHeaderParams());
      assertTrue(this.response.getHeaderParams().isEmpty());
      assertNull(this.response.getRidirectUrl());
      assertEquals("HttpStatus {200} BinaryContent {binary data + 7 bytes} HeaderParams {size {0} []} ", this.response.toString());
   }


   @Test
   public void testHeaderParams() {
      assertNull(this.response.getBinaryContent());
      assertEquals("content", this.response.getContent());
      assertEquals(200, this.response.getHttpStatus());
      assertNull(this.response.getRidirectUrl());

      this.response.addHeaderParam("param1", "value1");
      assertFalse(this.response.getHeaderParams().isEmpty());
      assertEquals(1, this.response.getHeaderParams().size());

      this.response.addHeaderParam(null, null);
      assertEquals(1, this.response.getHeaderParams().size());

      this.response.setHeaderParams(null);
      assertEquals(1, this.response.getHeaderParams().size());

      final Map<String, String> m = new HashMap<>();
      m.put("param2", "value2");
      this.response.setHeaderParams(m);
      assertEquals(2, this.response.getHeaderParams().size());
      assertEquals(
            "HttpStatus {200} Content {content} HeaderParams {size {2} [{Key {param1} Value {value1}} {Key {param2} Value {value2}} ]} ",
            this.response.toString());
   }


   @Test
   public void testRedirectUrl() {
      this.response.setRedirectUrl("https://localhost:443/");
      assertNull(this.response.getBinaryContent());
      assertEquals("content", this.response.getContent());
      assertEquals(200, this.response.getHttpStatus());
      assertNotNull(this.response.getHeaderParams());
      assertTrue(this.response.getHeaderParams().isEmpty());
      assertEquals("https://localhost:443/", this.response.getRidirectUrl());
      assertEquals("HttpStatus {200} Content {content} HeaderParams {size {0} []} RedirectUrl {https://localhost:443/} ", this.response.toString());
   }
}
