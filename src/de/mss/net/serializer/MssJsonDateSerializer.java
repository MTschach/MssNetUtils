package de.mss.net.serializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;

import de.mss.utils.DateTimeFormat;
import de.mss.utils.Tools;

public class MssJsonDateSerializer extends StdDeserializer<Date> {

   protected MssJsonDateSerializer(Class<?> vc) {
      super(vc);
   }


   @Override
   public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      final JsonNode node = jp.getCodec().readTree(jp);
      final String date = node.getTextValue();

      if (date == null) {
         return null;
      }

      for (final DateTimeFormat dtf : DateTimeFormat.values()) {
         try {
            return new SimpleDateFormat(dtf.getFormat()).parse(date);
         }
         catch (final ParseException e) {
            Tools.doNullLog(e);
         }
      }
      throw new JsonParseException(
            "Unparseable date: \"" + date + "\". Supported formats: " + Arrays.toString(DateTimeFormat.values()),
            jp.getCurrentLocation());
   }

}
