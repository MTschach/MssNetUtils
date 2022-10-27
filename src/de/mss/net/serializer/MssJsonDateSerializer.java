package de.mss.net.serializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import de.mss.utils.DateTimeFormat;
import de.mss.utils.Tools;

public class MssJsonDateSerializer extends StdDeserializer<Date> {

   private static final long serialVersionUID = -5299052194523318514L;


   public MssJsonDateSerializer() {
      this(null);
   }


   protected MssJsonDateSerializer(Class<?> vc) {
      super(vc);
   }


   @Override
   public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      final JsonNode node = jp.getCodec().readTree(jp);
      final String date = node.textValue();

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
            jp,
            "Unparseable date: \"" + date + "\". Supported formats: " + Arrays.toString(DateTimeFormat.values()));
   }

}
