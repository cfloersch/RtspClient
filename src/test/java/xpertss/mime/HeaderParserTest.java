package xpertss.mime;

import org.junit.Test;

import static org.junit.Assert.*;

public class HeaderParserTest {


   @Test
   public void testTransport()
   {
      Header header = HeaderParser.parse("Transport", "RTP/AVP/TCP;unicast;interleaved=0-1");
      assertNotNull(header.getValue(0));
      assertNotNull(header.getValue(0).getParameter("interleaved"));
      assertEquals("unicast", header.getValue(0).getParameter(0).getValue());
   }

   @Test
   public void testComplexWithEquals()
   {
      Header header = HeaderParser.parse("RTP-Info", "url=rtsp://stream.manheim.com:999/AVAIL.sdp/trackID=2,url=rtsp://stream.manheim.com:999/AVAIL.sdp/trackID=5");
      assertEquals(2, header.size());

      HeaderValue valueOne = header.getValue(0);
      assertEquals("rtsp://stream.manheim.com:999/AVAIL.sdp/trackID=2", valueOne.getValue());

      HeaderValue valueTwo = header.getValue(1);
      assertEquals("rtsp://stream.manheim.com:999/AVAIL.sdp/trackID=5", valueTwo.getValue());

      // TODO How would I access these HeaderValues by name when they both have the same name
   }

   @Test
   public void testSplit()
   {
      String[] parts = "url=rtsp://stream.manheim.com:999/AVAIL.sdp/trackID=2".split("=", 2);
      assertEquals(2, parts.length);
   }

}