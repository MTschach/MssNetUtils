package de.mss.net.webservice;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class WebServiceTestRequest extends WebServiceRequest {

   @HeaderParam(value = "sessionId")
   public String         sessionId;

   @PathParam(value = "customerNumber")
   public Integer        customerNumber;

   @QueryParam(value = "name")
   public String         name;

   @QueryParam(value = "birthdate")
   public java.util.Date birthdate;

   @BodyParam(value = "body")
   public WebServiceBody body;


   public String getSessionId() {
      return this.sessionId;
   }


   public Integer getCustomerNumber() {
      return this.customerNumber;
   }


   public String getName() {
      return this.name;
   }


   public java.util.Date getBirthdate() {
      return this.birthdate;
   }


   public WebServiceBody getBody() {
      return this.body;
   }

}
