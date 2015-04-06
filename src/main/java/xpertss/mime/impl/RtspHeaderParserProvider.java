package xpertss.mime.impl;

import xpertss.mime.Header;
import xpertss.mime.HeaderParser;
import xpertss.mime.spi.HeaderParserProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * A collection of headers found in the Rtsp specification.
 */
public class RtspHeaderParserProvider implements HeaderParserProvider {

   private Map<String,HeaderParser> parsers = new HashMap<String,HeaderParser>();

   // RTSP Headers not already defined in HTTP
   public RtspHeaderParserProvider()
   {
      parsers.put("BANDWIDTH",            new SimpleHeaderParser("Bandwidth", Header.Type.Request));
      parsers.put("BLOCKSIZE",            new SimpleHeaderParser("Blocksize", Header.Type.Request));
      parsers.put("CONFERENCE",           new SimpleHeaderParser("Conference", Header.Type.Request));
      parsers.put("CONTENT-BASE",         new SimpleHeaderParser("Content-Base", Header.Type.Entity));
      parsers.put("CSEQ",                 new SimpleHeaderParser("CSeq", Header.Type.General));
      parsers.put("PROXY-REQUIRE",        new SimpleHeaderParser("Proxy-Require", Header.Type.Request));
      parsers.put("PUBLIC",               new ComplexHeaderParser("Public", Header.Type.Response));
      parsers.put("REQUIRE",              new SimpleHeaderParser("Require", Header.Type.Request));
      parsers.put("RTP-INFO",             new ComplexHeaderParser("RTP-Info", Header.Type.Response));
      parsers.put("SCALE",                new SimpleHeaderParser("Scale", Header.Type.General));
      parsers.put("SPEED",                new SimpleHeaderParser("Speed", Header.Type.General));
      parsers.put("SESSION",              new SimpleHeaderParser("Session", Header.Type.General));
      parsers.put("TIMESTAMP",            new SimpleHeaderParser("Timestamp", Header.Type.General));
      parsers.put("TRANSPORT",            new ComplexHeaderParser("Transport", Header.Type.General));
      parsers.put("UNSUPPORTED",          new SimpleHeaderParser("Unsupported", Header.Type.Response));
   }

   public HeaderParser create(String name)
   {
      return parsers.get(name.toUpperCase());
   }

}
