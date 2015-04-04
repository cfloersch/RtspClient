/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: Aug 12, 2007
 * Time: 11:33:54 PM
 */

package xpertss.nio;

import java.io.IOException;

public interface DataHandler extends Selectable {

   public void handleRead() throws IOException;
   public void handleWrite() throws IOException;

}
