package de.mss.net.serializer;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class JsonSerializerFactory {

   private static ObjectMapper instance = null;

   public static ObjectMapper getInstance() {
      if (instance == null) {
         instance = new ObjectMapper().configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         instance.setSerializationInclusion(Inclusion.NON_NULL);
         //         instance.setDateFormat(new SimpleDateFormat(DateTimeFormat.DATE_TIMESTAMP_FORMAT_UTC.getFormat()));
         instance.setDateFormat(new MssDateFormat());
      }

      return instance;
   }


   private JsonSerializerFactory() {}

}
