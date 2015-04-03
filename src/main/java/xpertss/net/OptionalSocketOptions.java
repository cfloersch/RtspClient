/**
 * Created By: cfloersch
 * Date: 4/3/2015
 * Copyright 2013 XpertSoftware
 */
package xpertss.net;

import java.net.SocketOption;

public final class OptionalSocketOptions {

   /**
    * A socket option which gives an implementation hints as to how it would
    * like it to timeout read operations. This is heavily implementation
    * dependent. For example a protocol that uses requests and responses may
    * use this to timeout a request if it is not received quickly enough.
    */
   public static final SocketOption<Integer> SO_TIMEOUT =
      new StdSocketOption<Integer>("SO_TIMEOUT", Integer.class);

   /**
    * A socket option which dictates the maximum number of pending incoming
    * connections to queue in the backlog before rejecting them outright.
    * This would in most cases only apply to an Acceptor.
    */
   public static final SocketOption<Integer> TCP_BACKLOG =
      new StdSocketOption<Integer>("TCP_BACKLOG", Integer.class);


   private static class StdSocketOption<T> implements SocketOption<T> {
      private final String name;
      private final Class<T> type;
      StdSocketOption(String name, Class<T> type) {
         this.name = name;
         this.type = type;
      }
      @Override public String name() { return name; }
      @Override public Class<T> type() { return type; }
      @Override public String toString() { return name; }
   }

}
