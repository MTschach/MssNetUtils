package de.mss.net.call;

import java.util.function.Supplier;

import de.mss.net.webservice.WebServiceRequest;
import de.mss.net.webservice.WebServiceResponse;
import de.mss.utils.exception.Error;
import de.mss.utils.exception.MssException;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BaseCall<T extends WebServiceRequest, R extends WebServiceResponse> {

   protected Supplier<R> responseSupplier;
   protected Error       defaultError;


   public BaseCall(Supplier<R> response, Error error) {
      this.responseSupplier = response;
      this.defaultError = error;
   }


   public R action(String loggingId, T request) {
      try {
         R response = null;

         if (request == null) {
            throw new MssException(this.defaultError, "request must not be null");
         }

         request.checkRequiredFields();

         beforeAction(loggingId, request);

         checkRequest(loggingId, request);

         response = doAction(loggingId, request);

         response = afterAction(loggingId, request, response);

         return response;
      }
      catch (final MssException e) {
         return handleException(e);
      }
   }


   protected R afterAction(String loggingId, T request, R response) throws MssException {
      final R ret = response;

      if (ret == null) {
         throw new MssException(
               this.defaultError,
               "response must not be null");
      }

      ret.setErrorCode(Integer.valueOf(0));
      ret.setErrorText(null);
      ret.setStatusCode(Integer.valueOf(HttpServletResponse.SC_OK));

      return ret;
   }


   @SuppressWarnings("unused")
   protected void beforeAction(String loggingId, T request) throws MssException {
      // nothing to do here
   }


   @SuppressWarnings("unused")
   protected void checkRequest(String loggingId, T request) throws MssException {
      // nothing to do here
   }


   protected abstract R doAction(String loggingId, T request) throws MssException;


   public Error getError() {
      return this.defaultError;
   }


   protected String getErrorText(MssException e) {
      return e.getError().getErrorText();
   }


   private R handleException(MssException e) {
      final R response = this.responseSupplier.get();

      response.setStatusCode(Integer.valueOf(HttpServletResponse.SC_BAD_REQUEST));

      response.setErrorCode(Integer.valueOf(e.getError().getErrorCode()));
      response.setErrorText(getErrorText(e));

      if (e.getAltErrorCode() != 0) {
         response.setErrorCode(Integer.valueOf(e.getAltErrorCode()));
      }

      if (de.mss.utils.Tools.isSet(e.getAltErrorText())) {
         response.setErrorText(e.getAltErrorText());
      }

      return response;
   }
}
