package de.mss.net.webservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

public class OptionWebServiceRequestHandlerTest {

   @Test
   public void testFindTarget() {
      assertEquals("myTarget", new OptionWebServiceRequestHandler().findTarget("myTarget"));
   }


   @Test
   public void testFindWebService() {
      final OptionWebServiceRequestHandler o = new OptionWebServiceRequestHandler();

      assertNull(o.findWebService(null));
      assertNull(o.findWebService("blieh"));
      assertNotNull(o.findWebService("options/for/test"));

   }


   @Test
   public void testLogger() {
      assertNotNull(SpecialWebServiceRequestHandler.getLogger());
      SpecialWebServiceRequestHandler.setLogger(LogManager.getFormatterLogger());
      assertNotNull(SpecialWebServiceRequestHandler.getLogger());
   }
}
