package de.mss.net.webservice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;

import de.mss.net.rest.RestRequest;
import de.mss.utils.exception.MssException;

public class WebServiceJsonCaller<T extends WebServiceRequest, R extends WebServiceResponse> extends WebServiceCaller<T, R> {

   private static ObjectMapper restObjMapper = null;
   static {
      restObjMapper = new ObjectMapper().configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   }


   @Override
   protected void addPostParams(RestRequest restRequest, T request, List<Field> fields) throws MssException {
      for (Field field : fields) {
         if (field.isAnnotationPresent(BodyParam.class)) {
            String paramName = field.getAnnotationsByType(BodyParam.class)[0].value();
            try {
               Object value = PropertyUtils.getProperty(request, paramName);
               if (value != null)
                  restRequest.addPostParam(paramName, restObjMapper.writeValueAsString(value));
            }
            catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
               throw new MssException(
                     de.mss.net.exception.ErrorCodes.ERROR_NOT_MAPPABLE,
                     e,
                     "value of '" + paramName + "' could not be mapped to JSon");
            }
         }
      }
   }


   @Override
   protected R parseContent(String content, R response) throws MssException {
      R resp = null;

      try {
         resp = (R)restObjMapper.readValue(content, response.getClass());
      }
      catch (IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_NOT_PARSABLE, e);
      }

      return resp;
   }
}
