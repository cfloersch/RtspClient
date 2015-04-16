package xpertss.utils;

import xpertss.lang.Integers;
import xpertss.lang.Numbers;
import xpertss.lang.Strings;
import xpertss.mime.Headers;
import xpertss.rtsp.RtspResponse;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.concurrent.TimeUnit;

/**
 * Useful utilities
 */
public class Utils {

   /**
    * Creates and returns an {@link InetSocketAddress} that represents the authority
    * of the given {@link java.net.URI}. If the {@link java.net.URI} does not define
    * a port then the specified default port is used instead.
    *
    * @throws NullPointerException if uri is {@code null}
    * @throws IllegalArgumentException if the port is outside the range 1 - 65545
    */
   public static SocketAddress createSocketAddress(URI uri, int defPort)
   {
      String authority = uri.getAuthority();
      if(Strings.isEmpty(authority))
         throw new IllegalArgumentException("uri does not define an authority");
      String[] parts = authority.split(":");
      int port = defPort;
      if(parts.length == 2) {
         port = Integers.parse(parts[1], defPort);
         Numbers.within(1, 65545, port, String.format("%d is an invalid port", port));
      }
      return new InetSocketAddress(parts[0], port);
   }




   public static <T> T get(T[] items, int index)
   {
      if(items == null || index < 0) return null;
      return (items.length > index) ? items[index] : null;
   }


   public static String trimAndClear(StringBuilder buf)
   {
      return getAndClear(buf).trim();
   }

   public static String getAndClear(StringBuilder buf)
   {
      try {
         return buf.toString();
      } finally {
         buf.setLength(0);
      }
   }


   public static long computeTimeout(int millis)
   {
      if(millis == 0) millis = 60000;  // default timeout of 60 seconds
      return System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(millis);
   }






   public static String consume(CharBuffer buf, boolean trim)
   {
      buf.flip();
      int offset = 0; int end = buf.limit();
      if(trim) {
         while(offset < end && buf.get(offset) == ' ') offset++;
         while(end > offset && buf.get(end - 1) == ' ') end--;
      }
      char[] result = new char[end-offset];
      buf.position(offset);
      buf.get(result).clear();
      return new String(result);
   }


   public static boolean isWhiteSpace(char c)
   {
      return (c == '\t' || c == '\r' || c == '\n' || c == ' ');
   }


   public static int maxIfZero(int value)
   {
      return (value == 0) ? Integer.MAX_VALUE : value;
   }


   public static String getHeader(RtspResponse response, String name)
   {
      return Headers.toString(response.getHeaders().getHeader(name));
   }

}
