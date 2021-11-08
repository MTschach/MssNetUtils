package de.mss.net.webservice;


public enum EnumValForTest implements IfRequestEnumeration {

   //@formatter:off
   SIMPLE         ("simple"            , 1),
   EXTENDED       ("extended"          , 2)
   ;
   //@formatter:on

   public static EnumValForTest getByApiVal(String a) {
      for (final EnumValForTest e : EnumValForTest.values()) {
         if (e.getApiValue().equals(a)) {
            return e;
         }
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
   public String getApiValue() {
      return this.apiVal;
   }


   public Integer getIntVal() {
      return this.intVal;
   }
}
