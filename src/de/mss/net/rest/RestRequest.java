package de.mss.net.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.mss.utils.exception.MssException;

public class RestRequest {

   private RestMethod          method       = RestMethod.GET;
   private String              url          = null;
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


   public void addHeaderParam(String key, String value) {
      if (!de.mss.utils.Tools.isSet(key)) {
         return;
      }

      if (this.headerParams == null) {
         this.headerParams = new HashMap<>();
      }

      this.headerParams.put(key, value);
   }


   public void addPathParam(String key, String value) {
      if (!de.mss.utils.Tools.isSet(key)) {
         return;
      }

      if (this.pathParams == null) {
         this.pathParams = new HashMap<>();
      }

      this.pathParams.put(key, value);
   }


   public void addPostParam(String key, String value) {
      if (!de.mss.utils.Tools.isSet(key)) {
         return;
      }

      if (this.postParams == null) {
         this.postParams = new HashMap<>();
      }

      this.postParams.put(key, value);
   }


   public void addUrlParam(String key, String value) {
      if (!de.mss.utils.Tools.isSet(key)) {
         return;
      }

      if (this.urlParams == null) {
         this.urlParams = new HashMap<>();
      }

      this.urlParams.put(key, value);
   }


   public Map<String, String> getHeaderParams() {
      if (this.headerParams == null) {
         this.headerParams = new HashMap<>();
      }

      return this.headerParams;
   }


   public RestMethod getMethod() {
      return this.method;
   }


   public Map<String, String> getPathParams() {
      if (this.pathParams == null) {
         this.pathParams = new HashMap<>();
      }

      return this.pathParams;
   }


   public Map<String, String> getPostParams() {
      if (this.postParams == null) {
         this.postParams = new HashMap<>();
      }

      return this.postParams;
   }


   public String getUrl() {
      return this.url;
   }


   public Map<String, String> getUrlParams() {
      if (this.urlParams == null) {
         this.urlParams = new HashMap<>();
      }

      return this.urlParams;
   }


   public void setHeaderParams(Map<String, String> u) {
      if (u == null) {
         return;
      }

      for (final Entry<String, String> entry : u.entrySet()) {
         addHeaderParam(entry.getKey(), entry.getValue());
      }
   }


   public void setMethod(RestMethod m) {
      this.method = m;
   }


   public void setMethod(String m) throws MssException {
      this.method = RestMethod.getByMethod(m, () -> {
         return new MssException(de.mss.net.exception.ErrorCodes.ERROR_METHOD_NOT_SUPPORTED, "The method '" + m + "' is not supported");
      });
   }


   public void setPathParams(Map<String, String> u) {
      if (u == null) {
         return;
      }

      for (final Entry<String, String> entry : u.entrySet()) {
         addPathParam(entry.getKey(), entry.getValue());
      }
   }


   public void setPostParams(Map<String, String> u) {
      if (u == null) {
         return;
      }

      for (final Entry<String, String> entry : u.entrySet()) {
         addPostParam(entry.getKey(), entry.getValue());
      }
   }


   public void setUrl(String u) {
      this.url = u;
   }


   public void setUrlParams(Map<String, String> u) {
      if (u == null) {
         return;
      }

      for (final Entry<String, String> entry : u.entrySet()) {
         addUrlParam(entry.getKey(), entry.getValue());
      }
   }


   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();

      if (this.method != null) {
         sb.append("Method {" + this.method.toString() + "} ");
      }

      if (this.url != null) {
         sb.append("Url {" + this.url + "} ");
      }

      if (this.headerParams != null) {
         sb.append("HeaderParams " + writeParams(this.headerParams) + " ");
      }

      if (this.pathParams != null) {
         sb.append("PathParams " + writeParams(this.pathParams) + " ");
      }
      if (this.postParams != null) {
         sb.append("PostParams " + writeParams(this.postParams) + " ");
      }
      if (this.urlParams != null) {
         sb.append("UrlParams " + writeParams(this.urlParams) + " ");
      }
      return sb.toString();
   }


   private String writeParams(Map<String, String> params) {
      final StringBuilder sb = new StringBuilder();

      sb.append("size {" + params.size() + "} [");
      for (final Entry<String, String> p : params.entrySet()) {
         sb.append("{Key {" + p.getKey() + "} Value {" + p.getValue() + "}} ");
      }
      sb.append("]");

      return sb.toString();
   }
}
