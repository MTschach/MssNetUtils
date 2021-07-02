package de.mss.net.webservice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import de.mss.net.serializer.JsonSerializerFactory;


public class WebServiceJsonDataBuilder<T extends Object> extends WebServiceDataBuilder<T> {

   public WebServiceJsonDataBuilder() {
      // nothing to do here
   }


   @Override
   protected void setOtherValue(T clazz, Field field, String value)
         throws IllegalAccessException,
         InvocationTargetException,
         IOException {

      if (clazz == null || field == null || value == null) {
         return;
      }

      BeanUtils.setProperty(clazz, field.getName(), JsonSerializerFactory.getInstance().readValue(value, field.getType()));
   }


   @Override
   public String writeData(T clazz) throws IOException {
      return JsonSerializerFactory.getInstance().writeValueAsString(clazz);
   }
}
