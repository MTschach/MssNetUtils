package de.mss.webservice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class WebServiceJsonDataBuilder<T extends Object> extends WebServiceDataBuilder<T> {

   private static ObjectMapper restObjMapper = null;
   static {
      restObjMapper = new ObjectMapper().configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   }

   public WebServiceJsonDataBuilder() {
      // nothing to do here
   }


   @Override
   protected void setOtherValue(T clazz, Field field, String value)
         throws IllegalAccessException,
         InvocationTargetException,
         IOException {

      if (clazz == null || field == null || value == null)
         return;

      //      Method m = getSetMethod(clazz, field.getName());
      //
      //      if (m == null)
      //         return;

      BeanUtils.setProperty(clazz, field.getName(), restObjMapper.readValue(value, field.getType()));

      //      m.invoke(clazz.getClass(), restObjMapper.readValue(value, field.getType()));
   }

}
