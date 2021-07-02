package de.mss.net.serializer;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import de.mss.utils.DateTimeFormat;

public class JsonSerializerFactory {

   private static ObjectMapper instance = null;

   private JsonSerializerFactory() {}


   public static ObjectMapper getInstance() {
      if (instance == null) {
         instance = new ObjectMapper().configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         instance.setSerializationInclusion(Inclusion.NON_NULL);
         instance.setDateFormat(new SimpleDateFormat(DateTimeFormat.DATE_TIMESTAMP_FORMAT_UTC.getFormat()));
      }

      return instance;
   }

}
