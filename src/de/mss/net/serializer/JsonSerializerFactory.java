package de.mss.net.serializer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonSerializerFactory {

   private static ObjectMapper instance = null;

   public static ObjectMapper getInstance() {
      if (instance == null) {
         instance = new ObjectMapper().configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         instance.setSerializationInclusion(Include.NON_NULL);
         //         instance.setDateFormat(new SimpleDateFormat(DateTimeFormat.DATE_TIMESTAMP_FORMAT_UTC.getFormat()));
         instance.setDateFormat(new MssDateFormat());
         final SimpleModule module = new SimpleModule("DateDeserializer", new Version(1, 0, 0, "", "", ""));
         module.addDeserializer(java.util.Date.class, new MssJsonDateSerializer());
         instance.registerModule(module);
      }

      return instance;
   }


   private JsonSerializerFactory() {}

}
