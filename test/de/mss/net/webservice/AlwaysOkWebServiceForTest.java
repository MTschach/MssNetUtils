package de.mss.net.webservice;

import java.util.function.Supplier;

import de.mss.utils.exception.MssException;
import jakarta.servlet.http.HttpServletResponse;

public class AlwaysOkWebServiceForTest<T extends WebServiceTestResponse> extends AlwaysOkWebService<WebServiceTestRequest, T> {

   private static final long serialVersionUID = 1L;


   public AlwaysOkWebServiceForTest(Supplier<T> rts) {
      super(WebServiceTestRequest::new, rts);
   }


   @Override
   protected int writeResponse(String loggingId, HttpServletResponse httpResponse, T resp) throws MssException {
      throw new MssException(de.mss.utils.exception.ErrorCodes.ERROR_DB_CLOSE_FAILURE);
   }

}
