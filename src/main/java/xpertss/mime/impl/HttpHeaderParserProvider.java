/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 1:51 PM
 */
package xpertss.mime.impl;

import xpertss.mime.Header;
import xpertss.mime.HeaderParser;
import xpertss.mime.spi.HeaderParserProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * This implementation breaks down impl headers into two main categories plus a few
 * header by header implementations. The two main categories include: SimpleHeaders
 * which have only a single unnamed value and ComplexHeaders which have multiple named
 * and unnamed values.
 * <p>
 * In addition to these two main categories which cover most of the possible impl
 * headers there are also a few header specific parsers for Cookies as an example
 * which do not conform to the standard parameter format.
 * <p>
 * The main reason for the two main categories is that the special character <b>,</b>
 * has two different meanings depending on the header. In some cases it denotes a
 * boundary between values such as is the case on the Allow header. In other cases it
 * denotes a separation of elements within a value such as its use in all Date related
 * headers. Due to this difference two header implementations needed to be developed to
 * parse that usage differently.
 */
public class HttpHeaderParserProvider implements HeaderParserProvider {

   private Map<String,HeaderParser> parsers = new HashMap<>();

   public HttpHeaderParserProvider()
   {
      // General Headers (Cache-Control, Connection, Date, Pragma, Trailer, Transfer-Encoding, Upgrade, Via, Warning)
      parsers.put("CACHE-CONTROL",        new ComplexHeaderParser("Cache-Control", Header.Type.General));
      parsers.put("CONNECTION",           new SimpleHeaderParser("Connection", Header.Type.General));
      parsers.put("DATE",                 new SimpleHeaderParser("Date", Header.Type.General));
      parsers.put("PRAGMA",               new SimpleHeaderParser("Pragma", Header.Type.General));
      parsers.put("TRAILER",              new ComplexHeaderParser("Trailer", Header.Type.General));
      parsers.put("TRANSFER-ENCODING",    new ComplexHeaderParser("Transfer-Encoding", Header.Type.General));
      parsers.put("UPGRADE",              new ComplexHeaderParser("Upgrade", Header.Type.General));
      parsers.put("VIA",                  new ComplexHeaderParser("Via", Header.Type.General));
      // TODO Warning


      // Request Headers (Accept, Accept-Charset, Accept-Encoding, Accept-Language, Authorization, Expect, From, Host,
      //                   If-Match, If-Modified-Since, If-None-Match, If-Range, If-Unmodified-Since, Max-Forwards,
      //                   Proxy-Authorization, Range, Referer, TE, User-Agent)
      parsers.put("ACCEPT",               new ComplexHeaderParser("Accept", Header.Type.Request));
      parsers.put("ACCEPT-CHARSET",       new ComplexHeaderParser("Accept-Charset", Header.Type.Request));
      parsers.put("ACCEPT-ENCODING",      new ComplexHeaderParser("Accept-Encoding", Header.Type.Request));
      parsers.put("ACCEPT-LANGUAGE",      new ComplexHeaderParser("Accept-Language", Header.Type.Request));
      parsers.put("EXPECT",               new ComplexHeaderParser("Expect", Header.Type.Request));
      parsers.put("FROM",                 new SimpleHeaderParser("From", Header.Type.Request));
      parsers.put("HOST",                 new SimpleHeaderParser("Host", Header.Type.Request));
      parsers.put("IF-MATCH",             new ComplexHeaderParser("If-Match", Header.Type.Request));
      parsers.put("IF-MODIFIED-SINCE",    new SimpleHeaderParser("If-Modified-Since", Header.Type.Request));
      parsers.put("IF-NONE-MATCH",        new ComplexHeaderParser("If-None-Match", Header.Type.Request));
      parsers.put("IF-RANGE",             new SimpleHeaderParser("If-Range", Header.Type.Request));
      parsers.put("IF-UNMODIFIED-SINCE",  new SimpleHeaderParser("If-Unmodified-Since", Header.Type.Request));
      parsers.put("MAX-FORWARDS",         new SimpleHeaderParser("Max-Forwards", Header.Type.Request));
      parsers.put("RANGE",                new ComplexHeaderParser("Range", Header.Type.Request));
      parsers.put("REFERER",              new SimpleHeaderParser("Referer", Header.Type.Request));
      parsers.put("TE",                   new ComplexHeaderParser("TE", Header.Type.Request));
      parsers.put("USER-AGENT",           new SimpleHeaderParser("User-Agent", Header.Type.Request));

      parsers.put("AUTHORIZATION",        new SimpleHeaderParser("Authorization", Header.Type.Request)); // kind of unique
      parsers.put("PROXY-AUTHORIZATION",  new SimpleHeaderParser("Proxy-Authorization", Header.Type.Request)); // kind of unique
      // TODO Cookie


      // Response Headers (Accept-Ranges, Age, ETag, Location, Proxy-Authenticate, Retry-After, Server, Vary,
      //                   WWW-Authenticate)
      parsers.put("ACCEPT-RANGES",        new ComplexHeaderParser("Accept-Ranges", Header.Type.Response));
      parsers.put("AGE",                  new SimpleHeaderParser("Age", Header.Type.Response));
      parsers.put("ETAG",                 new SimpleHeaderParser("ETag", Header.Type.Response));
      parsers.put("LOCATION",             new SimpleHeaderParser("Location", Header.Type.Response));
      parsers.put("RETRY-AFTER",          new SimpleHeaderParser("Retry-After", Header.Type.Response));
      parsers.put("SERVER",               new SimpleHeaderParser("Server", Header.Type.Response));
      parsers.put("VARY",                 new ComplexHeaderParser("Vary", Header.Type.Response));

      parsers.put("PROXY-AUTHENTICATE",   new SimpleHeaderParser("Proxy-Authenticate", Header.Type.Response)); // kind of unique
      parsers.put("WWW-AUTHENTICATE",     new SimpleHeaderParser("WWW-Authenticate", Header.Type.Response)); // kind of unique
      // TODO Set-Cookie and possibly Set-Cookie2


      // Entity Headers (Allow, Content-Encoding, Content-Length, Content-Location, Content-MD5, Content-Range,
      //                  Content-Type, Expires, Last-Modified)
      parsers.put("ALLOW",                new ComplexHeaderParser("Allow", Header.Type.Entity));
      parsers.put("CONTENT-ENCODING",     new ComplexHeaderParser("Content-Encoding", Header.Type.Entity));
      parsers.put("CONTENT-LANGUAGE",     new ComplexHeaderParser("Content-Language", Header.Type.Entity));
      parsers.put("CONTENT-LENGTH",       new SimpleHeaderParser("Content-Length", Header.Type.Entity));
      parsers.put("CONTENT-LOCATION",     new SimpleHeaderParser("Content-Location", Header.Type.Entity));
      parsers.put("CONTENT-MD5",          new SimpleHeaderParser("Content-MD5", Header.Type.Entity));
      parsers.put("CONTENT-RANGE",        new SimpleHeaderParser("Content-Range", Header.Type.Entity));
      parsers.put("CONTENT-TYPE",         new SimpleHeaderParser("Content-Type", Header.Type.Entity));
      parsers.put("EXPIRES",              new SimpleHeaderParser("Expires", Header.Type.Entity));
      parsers.put("LAST-MODIFIED",        new SimpleHeaderParser("Last-Modified", Header.Type.Entity));

   }

   public HeaderParser create(String name)
   {
      return parsers.get(name.toUpperCase());
   }



}
