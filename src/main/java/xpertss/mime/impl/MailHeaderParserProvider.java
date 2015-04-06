package xpertss.mime.impl;

import xpertss.mime.HeaderParser;
import xpertss.mime.spi.HeaderParserProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cfloersch on 4/6/2015.
 */
public class MailHeaderParserProvider implements HeaderParserProvider {

   private Map<String,HeaderParser> parsers = new HashMap<>();

   public MailHeaderParserProvider()
   {
      // TODO Add Mail Headers
      // https://tools.ietf.org/html/rfc2045
      // https://tools.ietf.org/html/rfc2046
      // https://tools.ietf.org/html/rfc2047
      // https://tools.ietf.org/html/rfc1123
      // https://tools.ietf.org/html/rfc822
   }

   @Override
   public HeaderParser create(String name)
   {
      return parsers.get(name.toUpperCase());
   }

}
