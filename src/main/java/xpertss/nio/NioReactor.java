/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/14/11 12:53 PM
 */
package xpertss.nio;

import xpertss.io.NIOUtils;
import xpertss.lang.Objects;
import xpertss.threads.Threads;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NioReactor implements Runnable, NioService {

   private final List<NioAction> actions = Collections.synchronizedList(new ArrayList<NioAction>());
   private volatile Thread thread;
   private NioProvider provider;
   private Selector selector;

   public NioReactor(NioProvider provider)
   {
      this.provider = Objects.notNull(provider, "provider");
   }


   public void execute(NioAction action)
   {
      synchronized(this) {
         if(thread != Thread.currentThread() || action instanceof DeferredNioAction) {
            if(thread == null) activate();
            actions.add(action);
            selector.wakeup();
         } else {
            executeNow(action);
         }
      }
   }

   public boolean isSelectorThread()
   {
      return Thread.currentThread() == thread;
   }



   /**
    * Returns true if this server is active, false otherwise.
    */
   public boolean isActive()
   {
      return (thread != null);
   }




   /**
    * Waits for this server to shutdown.
    */
   public void await()
   {
      Threads.join(thread);
   }

   /**
    * Wait for the specified amount of time for this server to shutdown. This
    * will return false if this returned because it timed out before the server
    * completely shutdown, otherwise it will return true.
    *
    * @param timeout the time to wait
    * @param unit the unit the timeout value is measured with
    * @return True if the server shutdown within the allotted time
    */
   public boolean await(long timeout, TimeUnit unit)
   {
      Threads.join(thread, timeout, unit);
      return (thread == null);
   }





   private void executeNow(NioAction action)
   {
      try {
         action.execute(selector);
      } catch(Exception ex) {
         action.getSelectable().shutdown(ex);
      }
   }


   private void passivate()
   {
      synchronized(this) {
         thread = null;
         NIOUtils.close(selector);
         selector = null;
      }
   }

   private void activate()
   {
      selector = NIOUtils.openSelector();
      thread = provider.newThread(this);
      thread.start();
   }


   public void run()
   {
      do {
         try {

            int count = selector.select(10);

            // Now execute any pending NIO Actions
            while(actions.size() > 0) executeNow(actions.remove(0));

            // Process NIO events
            if(count > 0) {
               Set<SelectionKey> selectedKeys = selector.selectedKeys();
               for(SelectionKey sk : selectedKeys) {
                  if(sk.attachment() instanceof Selectable) {
                     Selectable select = (Selectable) sk.attachment();
                     try {
                        if(select instanceof AcceptHandler) {
                           AcceptHandler handler = (AcceptHandler) select;
                           if(sk.isValid() && sk.isAcceptable()) handler.handleAccept();
                        } else if(select instanceof DataHandler) {
                           DataHandler handler = (DataHandler) select;
                           if(sk.isValid() && sk.isReadable()) handler.handleRead();
                           if(sk.isValid() && sk.isWritable()) handler.handleWrite();
                           if(select instanceof ConnectHandler) {
                              ConnectHandler connectable = (ConnectHandler) select;
                              if(sk.isValid() && sk.isConnectable()) connectable.handleConnect();
                           }
                        } else {
                           assert false : select.getClass().getName();
                        }
                     } catch(Exception ex) {
                        select.shutdown(ex);
                     }
                  } else {
                     assert sk.attachment() != null : "selection key with no attachment";
                     assert false : sk.attachment().getClass().getName();
                  }
               }
               selectedKeys.clear();
            }


            // Now check the status of all user sockets
            Set<SelectionKey> keys = selector.keys();
            for(SelectionKey key : keys) {
               if(key.isValid() && key.attachment() instanceof Checkable) {
                  Checkable session = (Checkable) key.attachment();
                  try { session.handleCheckup(); } catch(Exception ex) { session.shutdown(ex); }
               }
            }

         } catch(Exception e) {
            provider.serviceException(e);
         }
      } while(!selector.keys().isEmpty() || !actions.isEmpty());
      passivate();
   }


}
