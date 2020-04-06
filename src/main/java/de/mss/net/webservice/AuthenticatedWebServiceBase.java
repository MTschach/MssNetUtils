package de.mss.net.webservice;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import org.eclipse.jetty.server.Request;

import de.mss.utils.exception.MssException;

public abstract class AuthenticatedWebServiceBase extends WebService {

   private static final long serialVersionUID = -8849334025941752837L;

@Override
   public boolean handleRequest(
         String loggingId,
         String target,
         Map<String, String> pathParams,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse) {

      getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "handling request " + target);
      de.mss.utils.StopWatch s = new de.mss.utils.StopWatch();

      int httpStatusCode = HttpServletResponse.SC_NOT_FOUND;

      try {
         switch (httpRequest.getMethod()) {
         case HttpMethod.DELETE:
               httpStatusCode = delete(loggingId, pathParams, baseRequest, httpRequest, httpResponse);
            break;
         case HttpMethod.GET:
               httpStatusCode = get(loggingId, pathParams, baseRequest, httpRequest, httpResponse);
            break;
         case HttpMethod.PATCH:
               httpStatusCode = patch(loggingId, pathParams, baseRequest, httpRequest, httpResponse);
            break;
         case HttpMethod.POST:
               httpStatusCode = post(loggingId, pathParams, baseRequest, httpRequest, httpResponse);
            break;

         default:
               httpStatusCode = HttpServletResponse.SC_NOT_FOUND;
            break;
      }
      }
      catch (MssException e) {
         getLogger().error(de.mss.utils.Tools.formatLoggingId(loggingId), e);
      }

      httpResponse.setStatus(httpStatusCode);
      s.stop();
      getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "response " + httpResponse);
      getLogger().debug(de.mss.utils.Tools.formatLoggingId(loggingId) + "end handling request + " + target + " [took " + s.getDuration() + "ms]");

      return true;
   }
}
