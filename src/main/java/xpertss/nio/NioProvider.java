/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/14/11 12:59 PM
 */
package xpertss.nio;

public interface NioProvider {

   public Thread newThread(Runnable r);

   public void serviceException(Exception error);

}
