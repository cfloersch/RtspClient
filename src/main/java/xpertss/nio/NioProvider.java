/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/14/11 12:59 PM
 * Copyright Manheim online
 */
package xpertss.nio;

public interface NioProvider {

   public Thread newThread(Runnable r);

   public void serviceException(Exception error);

}
