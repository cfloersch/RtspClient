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

   public RtspHeaderParserProvider()
   {
      parsers.put("ACCEPT",               new ComplexHeaderParser("Accept", Header.Type.Request));
      parsers.put("ACCEPT-ENCODING",      new ComplexHeaderParser("Accept-Encoding", Header.Type.Request));
      parsers.put("ACCEPT-LANGUAGE",      new ComplexHeaderParser("Accept-Language", Header.Type.Request));
      parsers.put("ALLOW",                new ComplexHeaderParser("Allow", Header.Type.Entity));
      parsers.put("AUTHORIZATION",        new SimpleHeaderParser("Authorization", Header.Type.Request));
      parsers.put("BANDWIDTH",            new SimpleHeaderParser("Bandwidth", Header.Type.Request));
      parsers.put("BLOCKSIZE",            new SimpleHeaderParser("Blocksize", Header.Type.Request));
      parsers.put("CACHE-CONTROL",        new ComplexHeaderParser("Cache-Control", Header.Type.General));
      parsers.put("CONFERENCE",           new SimpleHeaderParser("Conference", Header.Type.Request));
      parsers.put("CONNECTION",           new SimpleHeaderParser("Connection", Header.Type.General));
      parsers.put("CONTENT-BASE",         new SimpleHeaderParser("Content-Base", Header.Type.Entity));
      parsers.put("CONTENT-ENCODING",     new ComplexHeaderParser("Content-Encoding", Header.Type.Entity));
      parsers.put("CONTENT-LANGUAGE",     new ComplexHeaderParser("Content-Language", Header.Type.Entity));
      parsers.put("CONTENT-LENGTH",       new SimpleHeaderParser("Content-Length", Header.Type.Entity));
      parsers.put("CONTENT-LOCATION",     new SimpleHeaderParser("Content-Location", Header.Type.Entity));
      parsers.put("CONTENT-TYPE",         new SimpleHeaderParser("Content-Type", Header.Type.Entity));
      parsers.put("CSEQ",                 new SimpleHeaderParser("CSeq", Header.Type.General));
      parsers.put("DATE",                 new SimpleHeaderParser("Date", Header.Type.General));
      parsers.put("EXPIRES",              new SimpleHeaderParser("Expires", Header.Type.Entity));
      parsers.put("FROM",                 new SimpleHeaderParser("From", Header.Type.Request));
      parsers.put("IF-MATCH",             new ComplexHeaderParser("If-Match", Header.Type.Request));
      parsers.put("IF-MODIFIED-SINCE",    new SimpleHeaderParser("If-Modified-Since", Header.Type.Request));
      parsers.put("LAST-MODIFIED",        new SimpleHeaderParser("Last-Modified", Header.Type.Entity));
      parsers.put("LOCATION",             new SimpleHeaderParser("Location", Header.Type.Response));
      parsers.put("PROXY-AUTHENTICATE",   new SimpleHeaderParser("Proxy-Authenticate", Header.Type.Response));
      parsers.put("PROXY-REQUIRE",        new SimpleHeaderParser("Proxy-Require", Header.Type.Request));
      parsers.put("PUBLIC",               new ComplexHeaderParser("Public", Header.Type.Response));
      parsers.put("RANGE",                new ComplexHeaderParser("Range", Header.Type.Request));
      parsers.put("REFERER",              new SimpleHeaderParser("Referer", Header.Type.Request));       // yes the spec has this mis-spelled
      parsers.put("RETRY-AFTER",          new SimpleHeaderParser("Retry-After", Header.Type.Response));
      parsers.put("REQUIRE",              new SimpleHeaderParser("Require", Header.Type.Request));
      parsers.put("RTP-INFO",             new ComplexHeaderParser("RTP-Info", Header.Type.Response));  // TODO Should be ComplexHeaderParser
      parsers.put("SCALE",                new SimpleHeaderParser("Scale", Header.Type.General));
      parsers.put("SPEED",                new SimpleHeaderParser("Speed", Header.Type.General));
      parsers.put("SERVER",               new SimpleHeaderParser("Server", Header.Type.Response));
      parsers.put("SESSION",              new SimpleHeaderParser("Session", Header.Type.General));
      parsers.put("TIMESTAMP",            new SimpleHeaderParser("Timestamp", Header.Type.General));
      parsers.put("TRANSPORT",            new ComplexHeaderParser("Transport", Header.Type.General));
      parsers.put("UNSUPPORTED",          new SimpleHeaderParser("Unsupported", Header.Type.Response));
      parsers.put("USER-AGENT",           new SimpleHeaderParser("User-Agent", Header.Type.Request));
      parsers.put("VARY",                 new ComplexHeaderParser("Vary", Header.Type.Response));
      parsers.put("VIA",                  new ComplexHeaderParser("Via", Header.Type.General));
      parsers.put("WWW-AUTHENTICATE",     new SimpleHeaderParser("WWW-Authenticate", Header.Type.Response));
   }

   public HeaderParser create(String name)
   {
      return parsers.get(name.toUpperCase());
   }

}
