package de.mss.net.webservice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import de.mss.net.exception.ErrorCodes;
import de.mss.utils.exception.MssException;

public abstract class WebServiceDataBuilder<T extends Object> {

   private <E extends Enum<E> & IfRequestEnumeration> IfRequestEnumeration getEnumValue(String value, Class<IfRequestEnumeration> type)
         throws MssException {
      if (value == null) {
         return null;
      }

      for (final IfRequestEnumeration c : type.getEnumConstants()) {
         if (c.getApiValue().equals(value)) {
            return c;
         }
      }

      throw new MssException(de.mss.net.exception.ErrorCodes.ERROR_INVALID_ENUM_VALUE, "the value '" + value + "' ist not valid");
   }


   public T parseData(Map<String, String> params, T clazz)
         throws IllegalAccessException,
         InvocationTargetException,
         IOException,
         MssException {
      if (clazz == null) {
         return null;
      }

      final T ret = clazz;

      final Field[] fields = FieldUtils.getAllFields(clazz.getClass());
      for (final Field field : fields) {

         if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
            continue;
         }

         String fieldName = field.getName();
         if (field.isAnnotationPresent(PathParam.class)) {
            fieldName = field.getAnnotationsByType(PathParam.class)[0].value();
            if (!de.mss.utils.Tools.isSet(params.get(fieldName))) {
               throw new MssException(ErrorCodes.ERROR_PATH_PARAMETER_NOT_SET, "the path parameter " + fieldName + " is not set");
            }
         } else if (field.isAnnotationPresent(QueryParam.class)) {
            fieldName = field.getAnnotationsByType(QueryParam.class)[0].value();
         } else if (field.isAnnotationPresent(HeaderParam.class)) {
            fieldName = field.getAnnotationsByType(HeaderParam.class)[0].value();
         } else if (field.isAnnotationPresent(BodyParam.class)) {
            fieldName = field.getAnnotationsByType(BodyParam.class)[0].value();
         }

         if (field.getType().isAssignableFrom(String.class)) {
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         } else if (field.getType().isAssignableFrom(Boolean.class)) {
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         } else if (field.getType().isAssignableFrom(BigDecimal.class)) {
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         } else if (field.getType().isAssignableFrom(Double.class)) {
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         } else if (field.getType().isAssignableFrom(Float.class)) {
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         } else if (field.getType().isAssignableFrom(Integer.class)) {
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         } else if (field.getType().isAssignableFrom(BigInteger.class)) {
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         } else if (field.getType().isAssignableFrom(java.util.Date.class)) {
            BeanUtils.setProperty(ret, field.getName(), params.get(fieldName));
         } else if (field.getType().isEnum() && IfRequestEnumeration.class.isAssignableFrom(field.getType())) {
            BeanUtils.setProperty(ret, field.getName(), getEnumValue(params.get(fieldName), (Class<IfRequestEnumeration>)field.getType()));
         } else {
            setOtherValue(ret, field, params.get(fieldName));
         }
      }

      return ret;
   }


   protected abstract void setOtherValue(T clazz, Field field, String value)
         throws IllegalAccessException,
         InvocationTargetException,
         IOException;


   public abstract String writeData(T clazz) throws IOException;
}
