package de.mss.net.webservice;

import java.lang.reflect.Field;
import java.util.List;

import de.mss.net.rest.RestMethod;
import de.mss.net.rest.RestRequest;
import de.mss.utils.exception.MssException;

public class WebServiceJsonCallerForTest<T extends WebServiceRequest, R extends WebServiceResponse> extends WebServiceJsonCaller<T, R> {

   public RestRequest getRestRequestForTest(RestMethod method, T request, List<Field> fields)
         throws MssException {
      return getRestRequest(method, request, fields);
   }


   public R parseContentTest(String content, R response) throws MssException {
      return super.parseContent(content, response);
   }


   public String prepareUrlForTest(String url, T request, List<Field> fields) {
      return prepareUrl(url, request, fields);
   }
}
