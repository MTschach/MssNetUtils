package de.mss.net.exception;

import java.io.Serializable;

import de.mss.utils.exception.Error;

public class ErrorCodes implements Serializable {

   private static final long serialVersionUID                = 4521372659626589761L;

   private static final int  ERROR_CODE_BASE                 = 6000;
   public static final Error ERROR_UNABLE_TO_EXECUTE_REQUEST = new Error(ERROR_CODE_BASE + 0, "unable to execute rest request");
   public static final Error ERROR_PROTOCOL_NOT_SUPPORTED    = new Error(ERROR_CODE_BASE + 1, "protocol is not supported");
   public static final Error ERROR_METHOD_NOT_SUPPORTED      = new Error(ERROR_CODE_BASE + 2, "http method is not supported", 405);
   public static final Error ERROR_NOT_PARSABLE              = new Error(ERROR_CODE_BASE + 3, "value could not be parsed");
   public static final Error ERROR_NOT_MAPPABLE              = new Error(ERROR_CODE_BASE + 4, "value could not be mapped");
   public static final Error ERROR_NO_RESPONSE               = new Error(ERROR_CODE_BASE + 5, "no response received");
   public static final Error ERROR_NO_RESPONSE_WITH_ERROR    = new Error(ERROR_CODE_BASE + 6, "response contains error");
   public static final Error ERROR_PATH_PARAMETER_NOT_SET    = new Error(ERROR_CODE_BASE + 7, "a path parameter is not set");
   public static final Error ERROR_RESPONSE_NOT_WRITEABLE    = new Error(ERROR_CODE_BASE + 8, "response not writeable");
   public static final Error ERROR_REQUIRED_FIELD_MISSING    = new Error(ERROR_CODE_BASE + 9, "required field is missing");

   private ErrorCodes() {}
}
