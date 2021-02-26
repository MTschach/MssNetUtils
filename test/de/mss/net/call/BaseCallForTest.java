package de.mss.net.call;

import java.util.function.Supplier;

import de.mss.net.webservice.WebServiceTestRequest;
import de.mss.net.webservice.WebServiceTestResponse;
import de.mss.utils.exception.Error;
import de.mss.utils.exception.MssException;

public class BaseCallForTest extends BaseCall<WebServiceTestRequest, WebServiceTestResponse> {

   public BaseCallForTest(Supplier<WebServiceTestResponse> response, Error error) {
      super(response, error);
   }


   @Override
   protected WebServiceTestResponse doAction(String loggingId, WebServiceTestRequest request) throws MssException {
      if (loggingId.toLowerCase().contains("errornull")) {
         throw new MssException(-123);
      } else if (loggingId.toLowerCase().contains("null")) {
         return null;
      }

      return new WebServiceTestResponse();
   }


   public Error getDefaultError() {
      return this.defaultError;
   }


   public Supplier<WebServiceTestResponse> getResponseSupplier() {
      return this.responseSupplier;
   }
}
