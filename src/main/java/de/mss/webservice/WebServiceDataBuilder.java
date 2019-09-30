package de.mss.webservice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public abstract class WebServiceDataBuilder<T extends Object> {

   protected abstract void setOtherValue(T clazz, Field field, String value)
         throws IllegalAccessException,
         InvocationTargetException,
         IOException;


   public T parseData(Map<String, String> params, T clazz)
         throws InstantiationException,
         IllegalAccessException,
         InvocationTargetException,
         ParseException,
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


   private <T> void setStringValue(Class<T> clazz, String fieldName, String fieldValue)
         throws IllegalAccessException,
         InvocationTargetException {
      if (clazz == null || !de.mss.utils.Tools.isSet(fieldName) || fieldValue == null)
         return;

      Method m = getSetMethod(clazz, fieldName);
      if (m == null)
         return;

      m.invoke(clazz, fieldValue);
   }


   private <T> void setBooleanValue(Class<T> clazz, String fieldName, String fieldValue)
         throws IllegalAccessException,
         InvocationTargetException {
      if (clazz == null || !de.mss.utils.Tools.isSet(fieldName) || fieldValue == null)
         return;

      Method m = getSetMethod(clazz, fieldName);
      if (m == null)
         return;

      m.invoke(clazz, Boolean.valueOf(de.mss.utils.Tools.isTrue(fieldValue)));
   }


   private <T> void setBigDecimalValue(Class<T> clazz, String fieldName, String fieldValue)
         throws IllegalAccessException,
         InvocationTargetException {
      if (clazz == null || !de.mss.utils.Tools.isSet(fieldName) || fieldValue == null)
         return;

      Method m = getSetMethod(clazz, fieldName);
      if (m == null)
         return;

      m.invoke(clazz, new BigDecimal(fieldValue));
   }


   private <T> void setDoubleValue(Class<T> clazz, String fieldName, String fieldValue)
         throws IllegalAccessException,
         InvocationTargetException {
      if (clazz == null || !de.mss.utils.Tools.isSet(fieldName) || fieldValue == null)
         return;

      Method m = getSetMethod(clazz, fieldName);
      if (m == null)
         return;

      m.invoke(clazz, new Double(fieldValue));
   }


   private <T> void setFloatValue(Class<T> clazz, String fieldName, String fieldValue)
         throws IllegalAccessException,
         InvocationTargetException {
      if (clazz == null || !de.mss.utils.Tools.isSet(fieldName) || fieldValue == null)
         return;

      Method m = getSetMethod(clazz, fieldName);
      if (m == null)
         return;

      m.invoke(clazz, new Float(fieldValue));
   }


   private <T> void setIntegerValue(Class<T> clazz, String fieldName, String fieldValue)
         throws IllegalAccessException,
         InvocationTargetException {
      if (clazz == null || !de.mss.utils.Tools.isSet(fieldName) || fieldValue == null)
         return;

      Method m = getSetMethod(clazz, fieldName);
      if (m == null)
         return;

      m.invoke(clazz.getClass(), new Integer(fieldValue));
   }


   private <T> void setDateValue(Class<T> clazz, String fieldName, String fieldValue)
         throws IllegalAccessException,
         InvocationTargetException,
         ParseException {
      if (clazz == null || !de.mss.utils.Tools.isSet(fieldName) || fieldValue == null)
         return;

      Method m = getSetMethod(clazz, fieldName);
      if (m == null)
         return;

      java.util.Date value = null;
      for (de.mss.utils.DateTimeFormat format : de.mss.utils.DateTimeFormat.values()) {
         try {
            value = new SimpleDateFormat(format.getFormat()).parse(fieldValue);
            break;
         }
         catch (ParseException e) {
            LogManager.getLogger().log(Level.OFF, e);
         }
      }

      if (value == null)
         throw new ParseException("The value '" + fieldValue + "' could not be parsed as Date", 0);

      m.invoke(clazz, value);
   }


   protected <T> Method getSetMethod(Class<T> clazz, String fieldName) {
      return getMethod(clazz, "set", fieldName);
   }


   private <T> Method getMethod(Class<T> clazz, String prefix, String fieldName) {
      if (clazz == null || !de.mss.utils.Tools.isSet(prefix) || !de.mss.utils.Tools.isSet(fieldName))
         return null;

      String methodName = prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

      Method[] methods = clazz.getMethods();
      for (Method m : methods) {
         if (m.getName().equals(methodName))
            return m;
      }

      return null;
   }
}
