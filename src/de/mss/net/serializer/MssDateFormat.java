package de.mss.net.serializer;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.mss.utils.DateTimeFormat;
import de.mss.utils.DateTimeTools;
import de.mss.utils.Tools;
import de.mss.utils.exception.MssException;

public class MssDateFormat extends DateFormat {

   /**
    *
    */
   private static final long serialVersionUID = 6281431641066144976L;


   @Override
   public MssDateFormat clone() {
      /* Since we always delegate all work to child DateFormat instances,
       * let's NOT call super.clone(); this is bit unusual, but makes
       * sense here to avoid unnecessary work.
       */
      return new MssDateFormat();
   }


   @Override
   public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {

      return new StringBuffer(new SimpleDateFormat(DateTimeFormat.DATE_TIMESTAMP_FORMAT_UTC.getFormat()).format(date));
   }


   @Override
   public Date parse(String date) throws ParseException {
      try {
         return DateTimeTools.parseString2Date(date);
      }
      catch (final MssException e) {
         throw new ParseException("Unparseable date: \"" + date + "\".", e.getErrorCode());
      }
   }


   @Override
   public Date parse(String date, ParsePosition pos) {
      try {
         return DateTimeTools.parseString2Date(date);
      }
      catch (final MssException e) {
         Tools.doNullLog(e);
      }
      return null;
   }
}
