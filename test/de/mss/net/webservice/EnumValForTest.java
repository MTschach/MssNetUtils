package de.mss.net.webservice;

import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import de.mss.utils.exception.MssException;

public enum EnumValForTest implements IfRequestEnumeration {

   //@formatter:off
//   @JsonProperty("simple")
   SIMPLE         ("simple"            , 1),
//   @JsonProperty("extended")
   EXTENDED       ("extended"          , 2)
   ;
   //@formatter:on

   @JsonCreator
   public static EnumValForTest getByApiVal(String a) throws MssException {
      return getByApiVal(a, null);
   }


   public static <E extends MssException> EnumValForTest getByApiVal(String a, Supplier<E> throwException) throws E {
      for (final EnumValForTest e : EnumValForTest.values()) {
         if (e.getApiValue().equals(a)) {
            return e;
         }
      }

      if (throwException != null) {
         throw throwException.get();
      }

      return null;
   }


   String  apiVal;

   Integer intVal;


   private EnumValForTest(String a, Integer i) {
      this.apiVal = a;
      this.intVal = i;
   }


   @Override
   @JsonValue
   public String getApiValue() {
      return this.apiVal;
   }


   public Integer getIntVal() {
      return this.intVal;
   }


   @Override
   public String toString() {
      return this.apiVal;
   }

}
