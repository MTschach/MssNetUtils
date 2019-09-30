package de.mss.net.exception;

import java.io.Serializable;

import de.mss.utils.exception.Error;
import de.mss.utils.exception.MssException;

public class ErrorCodes implements Serializable {

   private static final long serialVersionUID                 = 4521372659626589761L;

   private static final int  ERROR_CODE_BASE                 = 6000;
   public static final Error ERROR_UNABLE_TO_EXECUTE_REQUEST = new Error(ERROR_CODE_BASE + 0, "unable to execute rest request");
   public static final Error ERROR_PROTOCOL_NOT_SUPPORTED    = new Error(ERROR_CODE_BASE + 1, "protocol is not supported");
   public static final Error ERROR_METHOD_NOT_SUPPORTED      = new Error(ERROR_CODE_BASE + 2, "http method is not supported");
   public static final Error ERROR_NOT_PARSABLE              = new Error(ERROR_CODE_BASE + 3, "value could not be parsed");

   public ErrorCodes() throws MssException {
      throw new MssException(
            new Error(
                  de.mss.utils.exception.ErrorCodes.ERROR_NOT_INSTANCABLE.getErrorCode(),
                  de.mss.utils.exception.ErrorCodes.ERROR_NOT_INSTANCABLE.getErrorText() + " (" + getClass().getName() + ")"));
   }
}
