package de.mss.net.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RestResponse {

   private int                 httpStatus    = 200;
   private String              content       = null;
   private byte[]              binaryContent = null;
   private Map<String, String> headerParams  = null;
   private String              redirectUrl   = null;


   public RestResponse(int s) {
      setHttpStatus(s);
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


   public byte[] getBinaryContent() {
      return this.binaryContent;
   }


   public String getContent() {
      return this.content;
   }


   public Map<String, String> getHeaderParams() {
      if (this.headerParams == null) {
         this.headerParams = new HashMap<>();
      }

      return this.headerParams;
   }


   public int getHttpStatus() {
      return this.httpStatus;
   }


   public String getRidirectUrl() {
      return this.redirectUrl;
   }


   public void setBinaryContent(byte[] c) {
      this.binaryContent = c;
   }


   public void setContent(String c) {
      this.content = c;
   }


   public void setHeaderParams(Map<String, String> u) {
      if (u == null) {
         return;
      }

      for (final Entry<String, String> entry : u.entrySet()) {
         addHeaderParam(entry.getKey(), entry.getValue());
      }
   }


   public void setHttpStatus(int s) {
      this.httpStatus = s;
   }


   public void setRedirectUrl(String newUrl) {
      this.redirectUrl = newUrl;
   }


   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();

      sb.append("HttpStatus {" + this.httpStatus + "} ");
      if (this.content != null) {
         sb.append("Content {" + this.content + "} ");
      }
      if (this.binaryContent != null) {
         sb.append("BinaryContent {binary data + " + this.binaryContent.length + " bytes} ");
      }
      if (this.headerParams != null) {
         sb.append("HeaderParams {" + writeParams(this.headerParams) + "} ");
      }
      if (this.redirectUrl != null) {
         sb.append("RedirectUrl {" + this.redirectUrl + "} ");
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
