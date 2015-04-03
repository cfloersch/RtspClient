package xpertss.rtsp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xpertss.lang.Objects;
import xpertss.nio.NioProvider;
import xpertss.nio.NioReactor;
import xpertss.threads.Threads;
import xpertss.util.Version;
import xpertss.utils.UserAgent;

import java.net.URI;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 */
public class RtspClient {


   private Log log = LogFactory.getLog(getClass());

   // Product name OS/version Java/version
   private final NioReactor reactor;
   private ThreadFactory factory;
   private UserAgent userAgent;


   public RtspClient()
   {
      this.reactor = new NioReactor(new RtspNioProvider());
   }






   /**
    * Call this to have a configured (and attached to the reactor service)
    * session. It will not be connected initially.
    *
    * @throws NullPointerException if uri is {@code null}
    * @throws IllegalArgumentException if the uri is invalid
    */
   public RtspSession open(RtspHandler handler, URI uri)
   {
      if(!Objects.notNull(uri).getScheme().equals("rtsp"))
         throw new IllegalArgumentException("only supports rtsp urls");
      return new RtspSession(reactor, handler, uri, getUserAgent());
   }


   



   /**
    * Set the UserAgent string this client will submit to servers with each
    * request.
    */
   public void setUserAgent(UserAgent userAgent)
   {
      this.userAgent = Objects.notNull(userAgent, "userAgent");
   }

   /**
    * Get the UserAgent string this client is submitting to servers with each
    * request.
    */
   public UserAgent getUserAgent()
   {
      return (userAgent == null) ? UserAgent.create("Aries", new Version(1,0)) : userAgent;
   }



   /**
    * Set the thread factory this HttpServer will use to obtain threads from.
    * <p>
    * It is advised that the thread factory should create daemon threads with
    * a priority slightly above NORM_PRIORITY.
    *
    * @param factory The thread factory this server should obtain threads from
    * @throws IllegalStateException if this is called on an active server
    */
   public void setThreadFactory(ThreadFactory factory)
   {
      if(isActive()) throw new IllegalStateException("server is active");
      this.factory = factory;
   }

   /**
    * Returns the thread factory this HttpServer will obtain threads from.
    */
   public ThreadFactory getThreadFactory()
   {
      return factory;
   }







   /**
    * Returns true if this server is active, false otherwise.
    */
   public boolean isActive()
   {
      return reactor.isActive();
   }


   /**
    * Waits for this server to shutdown.
    */
   public void await()
   {
      reactor.await();
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
      return reactor.await(timeout, unit);
   }









   private class RtspNioProvider implements NioProvider {

      public Thread newThread(Runnable r)
      {
         ThreadFactory factory = getThreadFactory();
         if(factory == null) {
            factory = Threads.newThreadFactory("RtspReactor", Thread.NORM_PRIORITY + 1, true);
         }
         return factory.newThread(r);
      }

      public void serviceException(Exception error)
      {
         log.warn("NIO Error reported", error);
      }
   }

}
