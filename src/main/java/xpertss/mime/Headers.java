/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 4/6/11 2:50 PM
 * Copyright Manheim online
 */
package xpertss.mime;

import xpertss.function.Predicate;
import xpertss.function.Predicates;
import xpertss.lang.Integers;
import xpertss.lang.Objects;
import xpertss.lang.Strings;
import xpertss.util.Iterables;
import xpertss.utils.Utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Headers is a utility class that manages RFC822 style src.
 * <p/>
 * <hr> <strong>A note on RFC822 and MIME src</strong><p>
 * <p/>
 * RFC822 and MIME header fields <strong>must</strong> contain only
 * US-ASCII characters. If a header contains non US-ASCII characters,
 * it must be encoded as per the rules in RFC 2047. Callers of the
 * <code>setHeader</code> and <code>addHeader</code> methods are
 * responsible for enforcing the MIME requirements for the specified
 * src.  In addition, these header fields must be folded (wrapped)
 * before being sent if they exceed the line length limitation for the
 * transport (1000 bytes for SMTP).  Received src may have been
 * folded.  The application is responsible for folding and unfolding
 * src as appropriate.
 */
public class Headers {

   public enum Type {
      Http, Rtsp, Mail
   }


   private List<Header> headers;
   private boolean readOnly;

   public Headers(Type type)
   {
      headers = new ArrayList<Header>();

      if (Type.Mail == type) {
         headers.add(new PositionHeader("Return-Path"));
         headers.add(new PositionHeader("Received"));
         headers.add(new PositionHeader("Message-Id"));
         headers.add(new PositionHeader("Resent-Date"));
         headers.add(new PositionHeader("Date"));
         headers.add(new PositionHeader("Resent-From"));
         headers.add(new PositionHeader("From"));
         headers.add(new PositionHeader("Reply-To"));
         headers.add(new PositionHeader("To"));
         headers.add(new PositionHeader("Subject"));
         headers.add(new PositionHeader("Cc"));
         headers.add(new PositionHeader("In-Reply-To"));
         headers.add(new PositionHeader("Resent-Message-Id"));
         headers.add(new PositionHeader("Errors-To"));
         headers.add(new PositionHeader("Mime-Version"));
         headers.add(new PositionHeader("Content-Type"));
         headers.add(new PositionHeader("Content-Transfer-Encoding"));
         headers.add(new PositionHeader("Content-MD5"));
         headers.add(new PositionHeader(":"));
         headers.add(new PositionHeader("Content-Length"));
         headers.add(new PositionHeader("Status"));
      } else if(type == Type.Http) {
         headers.add(new PositionHeader("Host"));
         headers.add(new PositionHeader("User-Agent"));
         headers.add(new PositionHeader("Date"));

         headers.add(new PositionHeader("Accept"));
         headers.add(new PositionHeader("Accept-Language"));
         headers.add(new PositionHeader("Accept-Encoding"));
         headers.add(new PositionHeader("Accept-Charset"));

         headers.add(new PositionHeader("Authorization"));
         headers.add(new PositionHeader("Proxy-Authorization"));

         headers.add(new PositionHeader("Referer"));
         headers.add(new PositionHeader("If-Modified-Since"));
         headers.add(new PositionHeader("If-None-Match"));

         headers.add(new PositionHeader("Transfer-Encoding"));

         headers.add(new PositionHeader("Content-Disposition"));
         headers.add(new PositionHeader("Content-Type"));
         headers.add(new PositionHeader("Content-Location"));
         headers.add(new PositionHeader("Content-MD5"));
         headers.add(new PositionHeader("Content-Language"));
         headers.add(new PositionHeader("Content-Encoding"));
         headers.add(new PositionHeader("Content-Length"));
         headers.add(new PositionHeader(":"));

         headers.add(new PositionHeader("Proxy-Connection"));
         headers.add(new PositionHeader("Connection"));
         headers.add(new PositionHeader("Pragma"));
         headers.add(new PositionHeader("Cache-Control"));
      } else if(type == Type.Rtsp) {
         headers.add(new PositionHeader("Server"));
         headers.add(new PositionHeader("CSeq"));
         headers.add(new PositionHeader("User-Agent"));
         headers.add(new PositionHeader("Date"));
         headers.add(new PositionHeader("Expires"));

         headers.add(new PositionHeader("Accept"));
         headers.add(new PositionHeader("Accept-Language"));
         headers.add(new PositionHeader("Accept-Encoding"));

         headers.add(new PositionHeader("Authorization"));

         headers.add(new PositionHeader("Referer"));
         headers.add(new PositionHeader("If-Modified-Since"));
         headers.add(new PositionHeader("If-Match"));
         headers.add(new PositionHeader("Last-Modified"));

         headers.add(new PositionHeader("Transport"));
         headers.add(new PositionHeader("Session"));
         headers.add(new PositionHeader("RTP-Info"));
         headers.add(new PositionHeader("Location"));
         headers.add(new PositionHeader("Range"));


         headers.add(new PositionHeader("Content-Base"));
         headers.add(new PositionHeader("Content-Type"));
         headers.add(new PositionHeader("Content-Location"));
         headers.add(new PositionHeader("Content-Language"));
         headers.add(new PositionHeader("Content-Encoding"));
         headers.add(new PositionHeader("Content-Length"));
         headers.add(new PositionHeader(":"));

         headers.add(new PositionHeader("Connection"));
         headers.add(new PositionHeader("Cache-Control"));

      }
   }

   private Headers(List<Header> headers)
   {
      this.headers = Objects.notNull(headers);
      this.readOnly = true;
   }


   /**
    * Return an enumeration of the headers in their predefined order.
    */
   public Enumeration<Header> headers()
   {
      Predicate<Object> filter = Predicates.instanceOf(PositionHeader.class);
      return new FilteringEnumeration(headers, filter);
   }

   public boolean contains(String name)
   {
      return getHeader(name) != null;
   }


   /**
    * This will return the first header with the given name.
    */
   public Header getHeader(String name)
   {
      for(Header header : headers) {
         if(Strings.equalIgnoreCase(header.getName(), name)) {
            if(!(header instanceof PositionHeader)) return header;
         }
      }
      return null;
   }

   /**
    * This will return all the header with the given name in the order they
    * were found.
    */
   public Header[] getHeaders(String name)
   {
      List<Header> results = new ArrayList<Header>();
      for(Header header : headers) {
         if(Strings.equalIgnoreCase(header.getName(), name)) {
            if(!(header instanceof PositionHeader)) results.add(header);
         }
      }
      return results.toArray(new Header[results.size()]);
   }


   /**
    * This will add the named header to the end of the set of existing
    * headers with the same name or its default position within the set
    * if no header with the same name pre-exists.
    *
    * @throws MalformedException If the header name or value are malformed
    */
   public void addHeader(String name, String value) throws MalformedException
   {
      if(readOnly) throw new IllegalStateException("src are readonly");
      Header newHeader = HeaderParser.parse(name, value);
      int pos = 0;
      for(int i = headers.size() - 1; i >= 0; i--) {
         Header header = headers.get(i);
         if(Strings.equalIgnoreCase(header.getName(), name)) {
            headers.add(i + 1, newHeader);
            return;
         } else if(header.getName().equals(":")) {
            pos = i;
         }
      }
      headers.add(pos + 1, newHeader);
   }

   /**
    * This will add the named header to its default position within the set
    * removing any previously existing headers with the same name.
    *
    * @throws MalformedException If the header name or value are malformed
    */
   public void setHeader(String name, String value) throws MalformedException
   {
      if(readOnly) throw new IllegalStateException("src are readonly");
      Header newHeader = HeaderParser.parse(name, value);
      int pos = 0;
      for(int i = headers.size() - 1; i >= 0; i--) {
         Header header = headers.get(i);
         if(Strings.equalIgnoreCase(header.getName(), name)) {
            if(header instanceof PositionHeader) {
               headers.add(i + 1, newHeader);
               return;
            } else {
               headers.remove(i);
            }
         } else if(Strings.equal(header.getName(), ":")) {
            pos = i;
         }
      }
      headers.add(pos + 1, newHeader);
   }


   /**
    * This will add the named header to its default position within the set
    * if and only if there are no existing headers with the given name.
    *
    * @throws MalformedException If the header name or value are malformed
    */
   public boolean setIfNotSet(String name, String value) throws MalformedException
   {
      if(readOnly) throw new IllegalStateException("src are readonly");
      Header newHeader = HeaderParser.parse(name, value);
      int pos = 0;
      for(int i = headers.size() - 1; i >= 0; i--) {
         Header header = headers.get(i);
         if(Strings.equalIgnoreCase(header.getName(), name)) {
            if(header instanceof PositionHeader) {
               headers.add(i + 1, newHeader);
               return true;
            } else{
               return false;
            }
         } else if(Strings.equal(header.getName(), ":")) {
            pos = i;
         }
      }
      headers.add(pos + 1, newHeader);
      return true;
   }

   /**
    * This will remove all src with the given name returning the number
    * removed.
    */
   public int remove(String name)
   {
      int count = 0;
      for(int i = headers.size() - 1; i >= 0; i--) {
         Header header = headers.get(i);
         if(Strings.equalIgnoreCase(header.getName(), name)) {
            if(header instanceof PositionHeader) {
               return count;
            } else {
               headers.remove(i);
               count++;
            }
         } else if(count > 0) {
            return count;
         }
      }
      return count;
   }




// Helpers

   /**
    * Helper method to retrieve the Content-Length header value as a long.
    * <p/>
    * This will return -1 if the Content-Header header is not defined.
    */
   public long getContentLength()
   {
      Header contentLength = getHeader("Content-Length");
      return Integers.parse(toString(contentLength), -1);
   }









   @Override
   public boolean equals(Object obj)
   {
      if(obj instanceof Headers) {
         Headers o = (Headers) obj;
         return Objects.equal(headers, o.headers);
      }
      return false;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(headers);
   }


   /**
    * Returns the set of headers with one header per line
    */
   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      for(Enumeration<Header> e = headers(); e.hasMoreElements(); ) {
         builder.append(e.nextElement()).append("\r\n");
      }
      return builder.toString();
   }












   public static Headers create(List<String> raw)
   {
      List<Header> headers = new ArrayList<Header>();
      StringBuffer header = new StringBuffer();
      for(int i = raw.size() - 1; i >= 0; i--) {
         header.insert(0, raw.get(i));
         if(!Utils.isWhiteSpace(header.charAt(0))) {
            headers.add(0, HeaderParser.parse(Utils.getAndClear(header)));
         }
      }
      return new Headers(headers);
   }

   public static String toString(Header header)
   {
      return (header == null) ? null : header.getValue();
   }



   private static class FilteringEnumeration implements Enumeration<Header> {

      private final List<Header> src;
      private final Predicate<Object> filter;

      private Header  next;
      private int     i;

      private FilteringEnumeration(List<Header> headers, Predicate<Object> filter)
      {
         this.src = Objects.notNull(headers);
         this.filter = Objects.notNull(filter);
         this.next = findNext();
      }


      @Override public boolean hasMoreElements()
      {
         return next != null;
      }

      @Override public Header nextElement()
      {
         if(next == null) {
            throw new NoSuchElementException("enumeration exhausted");
         }
         Header result = next;
         next = findNext();
         return result;
      }

      private Header findNext()
      {
         while(i < src.size()) {
            Header header = src.get(i++);
            if(!filter.apply(header)) return header;
         }
         return null;
      }
   }

   private static class PositionHeader implements Header {

      private final String name;

      private PositionHeader(String name)
      {
         this.name = Objects.notNull(name);
      }

      @Override
      public String getName()
      {
         return name;
      }

      @Override
      public String getValue()
      {
         return null;
      }

      @Override
      public Type getType()
      {
         return Type.Raw;
      }

      @Override
      public int size()
      {
         return 0;
      }

      @Override
      public HeaderValue getValue(int index)
      {
         return null;
      }

      @Override
      public HeaderValue getValue(String name)
      {
         return null;
      }

   }
}
