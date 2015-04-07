package xpertss.mime.impl;

import xpertss.mime.Header;
import xpertss.mime.HeaderParser;
import xpertss.mime.spi.HeaderParserProvider;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MailHeaderParserProvider implements HeaderParserProvider {

   private Map<String,HeaderParser> parsers = new HashMap<>();

   public MailHeaderParserProvider()
   {
      // rfc822
      parsers.put("RESENT-MESSAGE-ID",          new SimpleHeaderParser("Resent-Message-ID", Header.Type.General));
      parsers.put("IN-REPLY-TO",                new ComplexHeaderParser("In-Reply-To", Header.Type.General));
      parsers.put("MESSAGE-ID",                 new SimpleHeaderParser("Message-ID", Header.Type.General));
      parsers.put("REFERENCES",                 new ComplexHeaderParser("References", Header.Type.General));

      parsers.put("RETURN-PATH",                new SimpleHeaderParser("Return-Path", Header.Type.General));
      parsers.put("RECEIVED",                   new SimpleHeaderParser("Received", Header.Type.General));
      parsers.put("RESENT-DATE",                new SimpleHeaderParser("Resent-Date", Header.Type.General));

      parsers.put("RESENT-SENDER",              new SimpleHeaderParser("Resent-Sender", Header.Type.General));
      parsers.put("RESENT-FROM",                new SimpleHeaderParser("Resent-From", Header.Type.General));
      parsers.put("REPLY-TO",                   new SimpleHeaderParser("Reply-To", Header.Type.General));
      parsers.put("SENDER",                     new SimpleHeaderParser("Sender", Header.Type.General));


      parsers.put("TO",                         new ComplexHeaderParser("To", Header.Type.General));
      parsers.put("CC",                         new ComplexHeaderParser("Cc", Header.Type.General));

      parsers.put("SUBJECT",                    new SimpleHeaderParser("Subject", Header.Type.General));
      parsers.put("COMMENTS",                   new SimpleHeaderParser("Comments", Header.Type.General));
      parsers.put("KEYWORDS",                   new ComplexHeaderParser("Keywords", Header.Type.General));



      // Resent-Sender
      // Resent-To
      // Resent-Reply-To




      // rfc2045
      parsers.put("MIME-VERSION",               new SimpleHeaderParser("MIME-Version", Header.Type.General));
      parsers.put("CONTENT-ID",                 new SimpleHeaderParser("Content-ID", Header.Type.General));
      parsers.put("CONTENT-TRANSFER-ENCODING",  new SimpleHeaderParser("Content-Transfer-Encoding", Header.Type.General));
      parsers.put("CONTENT-DESCRIPTION",        new SimpleHeaderParser("Content-Description", Header.Type.General));
      parsers.put("CONTENT-DISPOSITION",        new SimpleHeaderParser("Content-Disposition", Header.Type.General));
      parsers.put("CONTENT-DURATION",           new SimpleHeaderParser("Content-Duration", Header.Type.General));




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
