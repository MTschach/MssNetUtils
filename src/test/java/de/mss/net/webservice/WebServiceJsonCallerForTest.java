package de.mss.net.webservice;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import de.mss.net.rest.RestMethod;
import de.mss.net.rest.RestRequest;
import de.mss.utils.exception.MssException;

public class WebServiceJsonCallerForTest<T extends WebServiceRequest, R extends WebServiceResponse> extends WebServiceJsonCaller<T, R> {

   public RestRequest getRestRequestForTest(RestMethod method, T request, List<Field> fields)
         throws IllegalAccessException,
         InvocationTargetException,
         NoSuchMethodException,
         MssException {
      return getRestRequest(method, request, fields);
   }


   public String prepareUrlForTest(String url, T request, List<Field> fields)
         throws MssException {
      return prepareUrl(url, request, fields);
   }
}
