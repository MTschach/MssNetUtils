package de.mss.net.call;

import java.util.function.Supplier;

import javax.servlet.http.HttpServletResponse;

import de.mss.net.webservice.WebServiceRequest;
import de.mss.net.webservice.WebServiceResponse;
import de.mss.utils.exception.Error;
import de.mss.utils.exception.MssException;

public abstract class BaseCall<T extends WebServiceRequest, R extends WebServiceResponse> {

   protected Supplier<R> responseSupplier;
   protected Error       defaultError;


   public BaseCall(Supplier<R> response, Error error) {
      this.responseSupplier = response;
      this.defaultError = error;
   }


   protected abstract R doAction(String loggingId, T request) throws MssException;


   public R action(String loggingId, T request) {
      try {
         R response = null;

         if (request == null) {
            throw new MssException(de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM, "request must not be null");
         }

         request.checkRequiredFields();

         checkRequest(loggingId, request);

         beforeAction(loggingId, request);

         response = doAction(loggingId, request);

         response = afterAction(loggingId, request, response);

         return response;
      }
      catch (final MssException e) {
         return handleException(e);
      }
   }


   @SuppressWarnings("unused")
   protected void beforeAction(String loggingId, T request) throws MssException {
      // nothing to do here
   }


   protected void checkRequest(String loggingId, T request) throws MssException {
      // nothing to do here
   }


   protected R afterAction(String loggingId, T request, R response) throws MssException {
      final R ret = response;

      if (ret == null) {
         throw new MssException(
               de.mss.utils.exception.ErrorCodes.ERROR_INVALID_PARAM,
               "response must not be null");
      }

      ret.setErrorCode(Integer.valueOf(0));
      ret.setErrorText(null);
      ret.setStatusCode(Integer.valueOf(HttpServletResponse.SC_OK));

      return ret;
   }


   private R handleException(MssException e) {
      final R response = this.responseSupplier.get();

      response.setStatusCode(Integer.valueOf(HttpServletResponse.SC_BAD_REQUEST));

      if (e.getError() != null) {
         response.setErrorCode(Integer.valueOf(e.getError().getErrorCode()));
         response.setErrorText(e.getError().getErrorText());
      } else {
         response.setErrorCode(Integer.valueOf(this.defaultError.getErrorCode()));
         response.setErrorText(this.defaultError.getErrorText());
      }

      if (e.getAltErrorCode() != 0) {
         response.setErrorCode(Integer.valueOf(e.getAltErrorCode()));
      }

      if (de.mss.utils.Tools.isSet(e.getAltErrorText())) {
         response.setErrorText(e.getAltErrorText());
      }

      return response;
   }


   public Error getError() {
      return this.defaultError;
   }
}
