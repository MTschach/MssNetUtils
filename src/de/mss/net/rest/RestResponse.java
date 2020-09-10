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


   public void setHttpStatus(int s) {
      this.httpStatus = s;
   }


   public void setContent(String c) {
      this.content = c;
   }


   public void setBinaryContent(byte[] c) {
      this.binaryContent = c;
   }


   public void setHeaderParams(Map<String, String> u) {
      if (u == null)
         return;

      for (Entry<String, String> entry : u.entrySet()) {
         addHeaderParam(entry.getKey(), entry.getValue());
      }
   }


   public void addHeaderParam(String key, String value) {
      if (!de.mss.utils.Tools.isSet(key))
         return;

      if (this.headerParams == null)
         this.headerParams = new HashMap<>();

      this.headerParams.put(key, value);
   }


   public void setRedirectUrl(String newUrl) {
      this.redirectUrl = newUrl;
   }


   public int getHttpStatus() {
      return this.httpStatus;
   }


   public String getContent() {
      return this.content;
   }


   public byte[] getBinaryContent() {
      return this.binaryContent;
   }


   public Map<String, String> getHeaderParams() {
      if (this.headerParams == null)
         this.headerParams = new HashMap<>();

      return this.headerParams;
   }


   public String getRidirectUrl() {
      return this.redirectUrl;
   }
}
