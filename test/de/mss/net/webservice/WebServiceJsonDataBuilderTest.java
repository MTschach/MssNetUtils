package de.mss.net.webservice;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.mss.net.exception.ErrorCodes;
import de.mss.utils.DateTimeTools;
import de.mss.utils.exception.MssException;
import junit.framework.TestCase;


public class WebServiceJsonDataBuilderTest extends TestCase {

   private WebServiceJsonDataBuilder<WebServiceTestRequest> classUnderTest;
   private Map<String, String>                              params;

   @Override
   public void setUp() throws Exception {
      super.setUp();

      this.classUnderTest = new WebServiceJsonDataBuilder<>();
      this.params = new HashMap<>();
      this.params.put("sessionId", "sessionId");
      this.params.put("customerNumber", "12345");
      this.params.put("username", "username");
      this.params.put("name", "name");
      this.params.put("checkInterval", "30");
      this.params.put("birthDate", "2020-01-01");
      this.params.put("bigVal", "123456");
      this.params.put("bigDval", "123456.78");
      this.params.put("doubleVal", "1.23");
      this.params.put("floatVal", "2.34");
      this.params.put("boolVal", "true");
      this.params.put("body", "");
   }


   @Override
   public void tearDown() throws Exception {
      super.tearDown();
   }


   @Test
   public void testClassNull() throws IllegalAccessException, InvocationTargetException, IOException, MssException {
      assertNull(this.classUnderTest.parseData(this.params, null));
   }


   @Test
   public void testMissingPathParam() throws IllegalAccessException, InvocationTargetException, IOException {
      this.params.remove("customerNumber");
      try {
         this.classUnderTest.parseData(this.params, new WebServiceTestRequest());
         fail("no exception was thrown");
      }
      catch (final MssException e) {
         assertEquals("ErrorCode", ErrorCodes.ERROR_PATH_PARAMETER_NOT_SET, e.getError());
      }
   }


   @Test
   public void testWriteData() throws MssException, IOException {
      final WebServiceTestRequest req = new WebServiceTestRequest();
      req.setBirthday(DateTimeTools.parseString2Date("2020-01-02"));
      req.setCheckInterval(30);
      req.setName("a name");

      assertEquals(
            "{\"name\":\"a name\",\"checkInterval\":30,\"birthdate\":\"2020-01-02T00:00:00 +0100\"}",
            this.classUnderTest.writeData(req));
   }
}
