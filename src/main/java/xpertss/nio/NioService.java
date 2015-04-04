/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/14/11 11:24 AM
 */
package xpertss.nio;

public interface NioService {

   public void execute(NioAction action);

   public boolean isSelectorThread();

}
