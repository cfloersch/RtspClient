/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 4/3/2015
 */
package xpertss.net;

import xpertss.nio.NioSession;

import java.net.SocketOption;

public class SocketOptions {


   public static <T> boolean set(NioSession session, SocketOption<T> option, T value)
   {
      try { session.setOption(option, value); } catch(Exception e) { return false; }
      return true;
   }

}
