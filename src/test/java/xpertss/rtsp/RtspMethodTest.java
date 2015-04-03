package xpertss.rtsp;

import org.junit.Test;
import xpertss.lang.Booleans;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

public class RtspMethodTest {



   @Test
   public void testBooleansDefaultUnboxedValue()
   {
      ConcurrentMap<String,Boolean> map = new ConcurrentHashMap<>();
      assertFalse(Booleans.isTrue(map.get("NonExistent")));
      assertFalse(Booleans.isTrue(map.remove("NonExistent")));
      assertFalse(map.replace("NonExistent", false, true));
      //assertFalse(map.replace(null, false, true));
   }

}