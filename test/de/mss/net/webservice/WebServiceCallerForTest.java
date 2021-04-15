package de.mss.net.webservice;

import java.lang.reflect.Field;
import java.util.List;

import de.mss.net.rest.RestRequest;
import de.mss.utils.exception.MssException;

public class WebServiceCallerForTest<R extends WebServiceRequest, T extends WebServiceResponse> extends WebServiceCaller<R, T> {


   @Override
   protected void addPostParams(RestRequest restRequest, R request, List<Field> fields) throws MssException {
      // TODO Auto-generated method stub

   }


   @Override
   protected T parseContent(String content, T response) throws MssException {
      return "ok".equals(content) ? response : null;
   }
}
