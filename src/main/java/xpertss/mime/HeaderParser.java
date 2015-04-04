/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 11:09 AM
 */
package xpertss.mime;

import xpertss.mime.impl.RtspHeaderParserProvider;
import xpertss.mime.spi.HeaderParserProvider;

import java.util.ServiceLoader;

/**
 * This will parse the headers enough to determine the header name. It
 * will use that to load an appropriate parser from the service provider
 * framework for the header name. It will then delegate the parsing to
 * that provider.
 * <p>
 * TODO Improve the above documentation
 * <p/>
 * TODO Need to figure out this provider thing a bit better.
 */
public abstract class HeaderParser {

   private static ServiceLoader<HeaderParserProvider> loader =
         ServiceLoader.load(HeaderParserProvider.class, HeaderParserProvider.class.getClassLoader());

   private static HeaderParserProvider defProvider = new RtspHeaderParserProvider();


   /**
    * Parse a raw header and return an appropriate Header objects
    * representation.
    *
    * @param raw The raw header string from a request or response
    * @return A header object instance
    * @throws MalformedException If the header was malformed or incomplete
    * @throws NullPointerException If the raw header is null
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
    * Parse the given header value for the specified header name and
    * return an appropriate Header objects representation.
    *
    * @param name The name of the header
    * @param raw A string representing the raw value to be parsed
    * @return A header object instance
    * @throws MalformedException If the header value was malformed or incomplete
    * @throws NullPointerException If either the header or raw values are null
    */
   public static Header parse(String name, CharSequence raw) throws MalformedException
   {
      if(name == null) throw new NullPointerException("name can not be null");
      if(raw == null) throw new NullPointerException("raw can not be null");
      HeaderParser parser = defProvider.create(name);
      if(parser != null) return parser.doParse(raw);
      // revert to the service providers.
      for(HeaderParserProvider provider: loader) {
         parser = provider.create(name);
         if(parser != null) return parser.doParse(raw);
      }
      return new RawHeader(name, raw); // default to a RawHeader as nothing knows how to parse it
   }



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
