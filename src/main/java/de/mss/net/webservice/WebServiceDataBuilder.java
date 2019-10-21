package de.mss.net.webservice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

public abstract class WebServiceDataBuilder<T extends Object> {

   protected abstract void setOtherValue(T clazz, Field field, String value)
         throws IllegalAccessException,
         InvocationTargetException,
         IOException;


   public T parseData(Map<String, String> params, T clazz)
         throws IllegalAccessException,
         InvocationTargetException,
         IOException {
      if (clazz == null)
         return null;

      T ret = clazz;

      Field[] fields = FieldUtils.getAllFields(clazz.getClass());
      for (Field field : fields) {
         if (field.getType().isAssignableFrom(String.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(field.getName()));
         else if (field.getType().isAssignableFrom(Boolean.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(field.getName()));
         else if (field.getType().isAssignableFrom(BigDecimal.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(field.getName()));
         else if (field.getType().isAssignableFrom(Double.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(field.getName()));
         else if (field.getType().isAssignableFrom(Float.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(field.getName()));
         else if (field.getType().isAssignableFrom(Integer.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(field.getName()));
         else if (field.getType().isAssignableFrom(java.util.Date.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(field.getName()));
         else
            setOtherValue(ret, field, params.get(field.getName()));
      }

      return ret;
   }


   public abstract String writeData(T clazz) throws IOException;
}
