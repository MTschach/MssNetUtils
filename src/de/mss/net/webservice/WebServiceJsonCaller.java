package de.mss.net.webservice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import de.mss.net.rest.RestRequest;
import de.mss.net.serializer.JsonSerializerFactory;
import de.mss.utils.exception.MssException;

public class WebServiceJsonCaller<T extends WebServiceRequest, R extends WebServiceResponse> extends WebServiceCaller<T, R> {

   @Override
   protected void addPostParams(RestRequest restRequest, T request, List<Field> fields) throws MssException {
      for (final Field field : fields) {
         if (field.isAnnotationPresent(BodyParam.class)) {
            final String paramName = field.getAnnotationsByType(BodyParam.class)[0].value();
            try {
               final Object value = PropertyUtils.getProperty(request, paramName);
               if (value != null) {
                  restRequest.addPostParam(paramName, JsonSerializerFactory.getInstance().writeValueAsString(value));
               }
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


   @SuppressWarnings("unchecked")
   @Override
   protected R parseContent(String content, R response) throws MssException {
      R resp = null;

      try {
         resp = (R)JsonSerializerFactory.getInstance().readValue(content, response.getClass());
      }
      catch (final IOException e) {
         throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_NOT_PARSABLE, e);
      }

      return resp;
   }
}
