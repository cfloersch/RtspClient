/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/14/11 11:24 AM
 * Copyright Manheim online
 */
package xpertss.nio;

import xpertss.nio.NioAction;

public interface NioService {

   public void execute(NioAction action);

   public boolean isSelectorThread();

}
