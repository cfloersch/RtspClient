/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 11:09 AM
 */
package xpertss.mime;

import xpertss.mime.spi.HeaderParserProvider;

import java.util.ServiceLoader;

/**
 * HeaderParser will parse MIME headers into Header objects.
 * <p/>
 * This utilizes a ServiceProvider framework where a parser for each header
 * is loaded to actually parse the header contents. This class simply parses
 * the header name so that it may be used to locate a suitable value parser.
 *
 * @see xpertss.mime.spi.HeaderParserProvider
 */
public abstract class HeaderParser {

   private static ServiceLoader<HeaderParserProvider> loader =
         ServiceLoader.load(HeaderParserProvider.class, HeaderParserProvider.class.getClassLoader());


   /**
    * Parse a raw header and return an appropriate Header object representation.
    *
    * @param raw The raw header string from a request or response
    * @return A header object instance
    * @throws MalformedException If the header was malformed or incomplete
    * @throws NullPointerException If the raw header is {@code null}
    */
   public static Header parse(CharSequence raw) throws MalformedException
   {
      if(raw == null) throw new NullPointerException("raw can not be null");
      HeaderTokenizer h = new HeaderTokenizer(raw, HeaderTokenizer.MIME);
      HeaderTokenizer.Token token = h.next();
      if(token.getType() != HeaderTokenizer.Token.ATOM) throw new MalformedException("expected header name");
      String name = token.getValue();
      if((token = h.next()).getType() != ':') throw new MalformedException("expected header delimiter");
      if((token = h.next()).getType() != HeaderTokenizer.Token.LWS) throw new MalformedException("expected header separator");
      return parse(name, h.getRemainder());
   }

   /**
    * Parse the given header value for the specified header name and return an
    * appropriate Header objects representation.
    *
    * @param name The name of the header
    * @param rawValue A string representing the raw value to be parsed
    * @return A header object instance
    * @throws MalformedException If the header value was malformed or incomplete
    * @throws NullPointerException If either the header or rawValue values are {@code null}
    */
   public static Header parse(String name, CharSequence rawValue) throws MalformedException
   {
      if(name == null) throw new NullPointerException("name can not be null");
      if(rawValue == null) throw new NullPointerException("rawValue can not be null");
      // revert to the service providers.
      for(HeaderParserProvider provider: loader) {
         HeaderParser parser = provider.create(name);
         if(parser != null) return parser.doParse(rawValue);
      }
      return new RawHeader(name, rawValue); // default to a RawHeader as nothing knows how to parse it
   }


   /**
    * Service provider implementations should implement this method to parse a
    * raw header value into a Header object.
    *
    * @param raw The raw header value to parse.
    * @return A header object representing the parsed header value
    * @throws MalformedException If the header value was malformed or incomplete
    */
   protected abstract Header doParse(CharSequence raw) throws MalformedException;

   private static class RawHeader implements Header {

      private String name;
      private String value;

      private RawHeader(String name, CharSequence value)
      {
         this.name = name;
         this.value = value.toString();
      }

      public String getName()
      {
         return name;
      }

      public String getValue()
      {
         return value;
      }

      public Type getType()
      {
         return Type.Raw;
      }

      public int size()
      {
         return 0;
      }

      public HeaderValue getValue(int index)
      {
         return null;
      }

      public HeaderValue getValue(String name)
      {
         return null;
      }

      public String toString()
      {
         return name + ": " + value;
      }
   }
}
