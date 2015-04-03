package xpertss.utils;

import org.junit.Test;
import xpertss.util.Version;

import static org.junit.Assert.*;

public class UserAgentTest {

   @Test
   public void testUserAgent()
   {
      String userAgent = UserAgent.create("MMP", new Version(2,1)).toString();
      assertTrue(userAgent.startsWith("MMP/2.1"));
      assertTrue(userAgent.contains("XpertRTSP/"));
      assertTrue(userAgent.contains(System.getProperty("os.arch")));
      assertTrue(userAgent.contains("Java"));
   }

   @Test
   public void testUserAgentNoCpu()
   {
      UserAgent agent = UserAgent.create("MMP", new Version(2,1));
      agent.includeCpu(false);
      String userAgent = agent.toString();
      assertTrue(userAgent.startsWith("MMP/2.1"));
      assertTrue(userAgent.contains("XpertRTSP/"));
      assertFalse(userAgent.contains(System.getProperty("os.arch")));
      assertTrue(userAgent.contains("Java"));
   }

   @Test
   public void testUserAgentNoJava()
   {
      UserAgent agent = UserAgent.create("MMP", new Version(2,1));
      agent.includeJava(false);
      String userAgent = agent.toString();
      assertTrue(userAgent.startsWith("MMP/2.1"));
      assertTrue(userAgent.contains("XpertRTSP/"));
      assertTrue(userAgent.contains(System.getProperty("os.arch")));
      assertFalse(userAgent.contains("Java"));
   }

   @Test
   public void testUserAgentNoLibrary()
   {
      UserAgent agent = UserAgent.create("MMP", new Version(2,1));
      agent.includeLibrary(false);
      String userAgent = agent.toString();
      assertTrue(userAgent.startsWith("MMP/2.1"));
      assertFalse(userAgent.contains("XpertRTSP/"));
      assertTrue(userAgent.contains(System.getProperty("os.arch")));
      assertTrue(userAgent.contains("Java"));
   }

}