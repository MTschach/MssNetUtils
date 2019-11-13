package de.mss.net.webservice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Map;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

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
         
         if (java.lang.reflect.Modifier.isStatic(field.getModifiers()))
            continue;

         String fieldName = field.getName();
         if (field.isAnnotationPresent(PathParam.class))
            fieldName = field.getAnnotationsByType(PathParam.class)[0].value();
         else if (field.isAnnotationPresent(QueryParam.class))
            fieldName = field.getAnnotationsByType(QueryParam.class)[0].value();
         else if (field.isAnnotationPresent(HeaderParam.class))
             fieldName = field.getAnnotationsByType(HeaderParam.class)[0].value();
         else if (field.isAnnotationPresent(BodyParam.class))
             fieldName = field.getAnnotationsByType(BodyParam.class)[0].value();

         if (field.getType().isAssignableFrom(String.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         else if (field.getType().isAssignableFrom(Boolean.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         else if (field.getType().isAssignableFrom(BigDecimal.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         else if (field.getType().isAssignableFrom(Double.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         else if (field.getType().isAssignableFrom(Float.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         else if (field.getType().isAssignableFrom(Integer.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         else if (field.getType().isAssignableFrom(java.util.Date.class))
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         else
            setOtherValue(ret, field, params.get(fieldName));
      }

      return ret;
   }


   public abstract String writeData(T clazz) throws IOException;
}
