package de.mss.net.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.mss.utils.exception.MssException;

public class RestRequestTest {

   @Test
   public void test() {
      final RestRequest r = new RestRequest(RestMethod.GET);

      assertEquals(RestMethod.GET, r.getMethod());
      assertEquals("Method {GET} ", r.toString());
      assertNotNull(r.getHeaderParams());
      assertNotNull(r.getPathParams());
      assertNotNull(r.getPostParams());
      assertNotNull(r.getUrlParams());
      assertNull(r.getUrl());
      assertEquals("Method {GET} HeaderParams size {0} [] PathParams size {0} [] PostParams size {0} [] UrlParams size {0} [] ", r.toString());
   }


   @Test
   public void testHeaderParam() {
      final RestRequest r = new RestRequest(RestMethod.GET);

      r.addHeaderParam("param1", "value1");
      assertEquals(1, r.getHeaderParams().size());
      r.addHeaderParam(null, "null");
      assertEquals(1, r.getHeaderParams().size());
      final Map<String, String> m = new HashMap<>();
      m.put("param2", "value2");
      r.setHeaderParams(null);
      assertEquals(1, r.getHeaderParams().size());
      r.setHeaderParams(m);
      assertEquals(2, r.getHeaderParams().size());
      assertEquals("Method {GET} HeaderParams size {2} [{Key {param1} Value {value1}} {Key {param2} Value {value2}} ] ", r.toString());
   }


   @SuppressWarnings("unused")
   @Test
   public void testInvalid() {
      try {
         new RestRequest("INVLID");
         fail("no exception was thrown");
      }
      catch (final MssException e) {
         assertEquals(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED, e.getError());
      }
   }


   @Test
   public void testPathParam() {
      final RestRequest r = new RestRequest(RestMethod.GET);

      r.addPathParam("param1", "value1");
      assertEquals(1, r.getPathParams().size());
      r.addPathParam(null, "null");
      assertEquals(1, r.getPathParams().size());
      final Map<String, String> m = new HashMap<>();
      m.put("param2", "value2");
      r.setPathParams(null);
      assertEquals(1, r.getPathParams().size());
      r.setPathParams(m);
      assertEquals(2, r.getPathParams().size());
      assertEquals("Method {GET} PathParams size {2} [{Key {param1} Value {value1}} {Key {param2} Value {value2}} ] ", r.toString());
   }


   @Test
   public void testPost() throws MssException {
      final RestRequest r = new RestRequest("POST");

      assertEquals(RestMethod.POST, r.getMethod());
      assertNotNull(r.getHeaderParams());
      assertNotNull(r.getPathParams());
      assertNotNull(r.getPostParams());
      assertNotNull(r.getUrlParams());
      assertNull(r.getUrl());
   }


   @Test
   public void testPostParam() {
      final RestRequest r = new RestRequest(RestMethod.GET);

      r.addPostParam("param1", "value1");
      assertEquals(1, r.getPostParams().size());
      r.addPostParam(null, "null");
      assertEquals(1, r.getPostParams().size());
      final Map<String, String> m = new HashMap<>();
      m.put("param2", "value2");
      r.setPostParams(null);
      assertEquals(1, r.getPostParams().size());
      r.setPostParams(m);
      assertEquals(2, r.getPostParams().size());
      assertEquals("Method {GET} PostParams size {2} [{Key {param1} Value {value1}} {Key {param2} Value {value2}} ] ", r.toString());
   }


   @Test
   public void testUrl() {
      final RestRequest r = new RestRequest(RestMethod.GET);

      assertNull(r.getUrl());
      r.setUrl("http://localhost:8080/v1/info");
      assertEquals("http://localhost:8080/v1/info", r.getUrl());
      assertEquals("Method {GET} Url {http://localhost:8080/v1/info} ", r.toString());
   }


   @Test
   public void testUrlParam() {
      final RestRequest r = new RestRequest(RestMethod.GET);

      r.addUrlParam("param1", "value1");
      assertEquals(1, r.getUrlParams().size());
      r.addUrlParam(null, "null");
      assertEquals(1, r.getUrlParams().size());
      final Map<String, String> m = new HashMap<>();
      m.put("param2", "value2");
      r.setUrlParams(null);
      assertEquals(1, r.getUrlParams().size());
      r.setUrlParams(m);
      assertEquals(2, r.getUrlParams().size());
      assertEquals("Method {GET} UrlParams size {2} [{Key {param1} Value {value1}} {Key {param2} Value {value2}} ] ", r.toString());
   }

}
