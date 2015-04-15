package xpertss.rtsp;

import org.junit.Test;

import static org.junit.Assert.*;

public class RtspResponseTest {

   @Test
   public void testResponseSplit()
   {
      String response = "500 Internal Error";
      String[] parts = response.split("\\s+", 2);
      assertEquals(2, parts.length);
      assertEquals("500", parts[0]);
      assertEquals("Internal Error", parts[1]);
   }


}