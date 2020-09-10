package de.mss.net.webservice;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class WebServiceTestRequest extends WebServiceRequest {

   private static final long serialVersionUID = 3968277551977019120L;

   @HeaderParam(value = "sessionId")
   public String         sessionId;

   @PathParam(value = "customerNumber")
   public Integer        customerNumber;

   @PathParam(value = "username")
   public String         userName;

   @QueryParam(value = "name")
   public String         name;

   @QueryParam(value = "checkinterval")
   public Integer        checkInterval;

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


   public String getUserName() {
      return this.userName;
   }


   public Integer getCheckInterval() {
      return this.checkInterval;
   }
}
