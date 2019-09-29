package de.mss.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.mss.utils.exception.MssException;

public class RestRequest {

   private RestMethod          method = RestMethod.GET;
   private Map<String, String> urlParams    = null;
   private Map<String, String> headerParams = null;
   private Map<String, String> postParams   = null;
   private Map<String, String> pathParams   = null;


   public RestRequest(RestMethod method) {
      setMethod(method);
   }


   public RestRequest(String method) throws MssException {
      setMethod(method);
   }


   public void setMethod(String m) throws MssException {
      this.method = RestMethod.getByMethod(m);
   }


   public void setMethod(RestMethod m) {
      this.method = m;
   }


   public void setUrlParams(Map<String, String> u) {
      if (u == null)
         return;

      for (Entry<String, String> entry : u.entrySet()) {
         addUrlParam(entry.getKey(), entry.getValue());
      }
   }


   public void setHeaderParams(Map<String, String> u) {
      if (u == null)
         return;

      for (Entry<String, String> entry : u.entrySet()) {
         addHeaderParam(entry.getKey(), entry.getValue());
      }
   }


   public void setPathParams(Map<String, String> u) {
      if (u == null)
         return;

      for (Entry<String, String> entry : u.entrySet()) {
         addPathParam(entry.getKey(), entry.getValue());
      }
   }


   public void setPostParams(Map<String, String> u) {
      if (u == null)
         return;

      for (Entry<String, String> entry : u.entrySet()) {
         addPostParam(entry.getKey(), entry.getValue());
      }
   }


   public void addUrlParam(String key, String value) {
      if (!de.mss.utils.Tools.isSet(key))
         return;

      if (this.urlParams == null)
         this.urlParams = new HashMap<>();

      this.urlParams.put(key, value);
   }


   public void addHeaderParam(String key, String value) {
      if (!de.mss.utils.Tools.isSet(key))
         return;

      if (this.headerParams == null)
         this.headerParams = new HashMap<>();

      this.headerParams.put(key, value);
   }


   public void addPathParam(String key, String value) {
      if (!de.mss.utils.Tools.isSet(key))
         return;

      if (this.pathParams == null)
         this.pathParams = new HashMap<>();

      this.pathParams.put(key, value);
   }


   public void addPostParam(String key, String value) {
      if (!de.mss.utils.Tools.isSet(key))
         return;

      if (this.postParams == null)
         this.postParams = new HashMap<>();

      this.postParams.put(key, value);
   }


   public Map<String, String> getUrlParams() {
      if (this.urlParams == null)
         this.urlParams = new HashMap<>();

      return this.urlParams;
   }


   public Map<String, String> getHeaderParams() {
      if (this.headerParams == null)
         this.headerParams = new HashMap<>();

      return this.headerParams;
   }


   public Map<String, String> getPathParams() {
      if (this.pathParams == null)
         this.pathParams = new HashMap<>();

      return this.pathParams;
   }


   public Map<String, String> getPostParams() {
      if (this.postParams == null)
         this.postParams = new HashMap<>();

      return this.postParams;
   }


   public RestMethod getMethod() {
      return this.method;
   }
}
