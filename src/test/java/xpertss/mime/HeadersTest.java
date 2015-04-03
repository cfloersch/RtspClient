package xpertss.mime;

import org.junit.Test;

import java.util.Enumeration;

import static org.junit.Assert.*;

public class HeadersTest {


   @Test
   public void testEmpty()
   {
      Headers headers = new Headers(Headers.Type.Rtsp);
      assertNull(headers.getHeader("Content-Type"));
      assertFalse(headers.headers().hasMoreElements());
   }

   @Test
   public void testGetSet()
   {
      Headers headers = new Headers(Headers.Type.Rtsp);
      assertNull(headers.getHeader("Content-Length"));
      headers.setHeader("Content-Length", "200");
      assertNotNull(headers.getHeader("Content-Length"));
      assertEquals("200", Headers.toString(headers.getHeader("Content-Length")));
      assertTrue(headers.headers().hasMoreElements()); // should have an element
      assertTrue(headers.contains("Content-Length")); // should be found
      assertTrue(headers.contains("Content-length")); // case doesn't matter
   }

   @Test
   public void testOrdering()
   {
      Headers headers = new Headers(Headers.Type.Rtsp);
      headers.setHeader("Connection", "5");
      headers.setHeader("CSeq", "1");
      headers.setHeader("Content-Base", "3");
      headers.setHeader("Vary", "4");
      headers.setHeader("User-Agent", "2");

      Enumeration<Header> e = headers.headers();
      assertEquals("1", Headers.toString(e.nextElement()));
      assertEquals("2", Headers.toString(e.nextElement()));
      assertEquals("3", Headers.toString(e.nextElement()));
      assertEquals("4", Headers.toString(e.nextElement()));
      assertEquals("5", Headers.toString(e.nextElement()));
   }

   @Test
   public void testMultipleHeaders()
   {
      Headers headers = new Headers(Headers.Type.Rtsp);
      headers.addHeader("Received", "from localhost");
      headers.addHeader("Received", "from gateway");

      Enumeration<Header> e = headers.headers();
      assertEquals("from localhost", Headers.toString(e.nextElement()));
      assertEquals("from gateway", Headers.toString(e.nextElement()));
      assertFalse(e.hasMoreElements());

      assertEquals(2, headers.getHeaders("Received").length);
      assertEquals(2, headers.getHeaders("received").length);

      assertEquals(2, headers.remove("Received"));
      assertEquals(0, headers.remove("Received"));
   }

   @Test
   public void testSetIfNotSet()
   {
      Headers headers = new Headers(Headers.Type.Rtsp);
      headers.setIfNotSet("User-Agent", "one");
      headers.setIfNotSet("User-Agent", "two");
      assertEquals("one", Headers.toString(headers.getHeader("User-Agent")));
      assertEquals(1, headers.getHeaders("User-Agent").length);

      assertEquals(1, headers.remove("User-Agent"));
   }

}