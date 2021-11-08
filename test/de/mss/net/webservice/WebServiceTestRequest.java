package de.mss.net.webservice;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class WebServiceTestRequest extends WebServiceRequest {

   private static final long serialVersionUID = 3968277551977019120L;

   @HeaderParam(value = "sessionId")
   public String             sessionId;

   @PathParam(value = "customerNumber")
   public Integer            customerNumber;

   @PathParam(value = "username")
   public String             userName;

   @QueryParam(value = "name")
   public String             name;

   @QueryParam(value = "checkinterval")
   public Integer            checkInterval;

   @QueryParam(value = "birthdate")
   public java.util.Date     birthdate;

   @BodyParam(value = "body")
   public WebServiceBody     body;

   @QueryParam(value = "bigVal")
   public BigInteger         bigVal;

   @QueryParam(value = "bigDval")
   public BigDecimal         bigDval;

   @QueryParam(value = "doubleVal")
   public Double             doubleVal;

   @QueryParam(value = "floatVal")
   public Float              floatVal;

   @QueryParam(value = "boolVal")
   public Boolean            boolVal;

   @QueryParam(value = "enumVal")
   public EnumValForTest     enumVal;


   public BigDecimal getBigDval() {
      return this.bigDval;
   }


   public BigInteger getBigVal() {
      return this.bigVal;
   }


   public java.util.Date getBirthdate() {
      return this.birthdate;
   }


   public WebServiceBody getBody() {
      return this.body;
   }


   public Boolean getBoolVal() {
      return this.boolVal;
   }


   public Integer getCheckInterval() {
      return this.checkInterval;
   }


   public Integer getCustomerNumber() {
      return this.customerNumber;
   }


   public Double getDoubleVal() {
      return this.doubleVal;
   }


   public EnumValForTest getEnumVal() {
      return this.enumVal;
   }


   public Float getFloatVal() {
      return this.floatVal;
   }


   public String getName() {
      return this.name;
   }


   public String getSessionId() {
      return this.sessionId;
   }


   public String getUserName() {
      return this.userName;
   }


   public void setBigDval(BigDecimal i) {
      this.bigDval = i;
   }


   public void setBigVal(BigInteger i) {
      this.bigVal = i;
   }


   public void setBirthday(java.util.Date d) {
      this.birthdate = d;
   }


   public void setBoolVal(Boolean v) {
      this.boolVal = v;
   }


   public void setCheckInterval(Integer i) {
      this.checkInterval = i;
   }


   public void setCustomerNumber(Integer c) {
      this.customerNumber = c;
   }


   public void setDoubleVal(Double i) {
      this.doubleVal = i;
   }


   public void setEnumVal(EnumValForTest e) {
      this.enumVal = e;
   }


   public void setFloatVal(Float i) {
      this.floatVal = i;
   }


   public void setName(String n) throws IOException {
      if ("exception".equalsIgnoreCase(n)) {
         throw new IOException();
      }

      this.name = n;
   }


   public void setSessionId(String s) {
      this.sessionId = s;
   }


   public void setUserName(String u) {
      this.userName = u;
   }
}
