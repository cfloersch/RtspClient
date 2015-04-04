/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: Aug 12, 2007
 * Time: 11:19:27 PM
 */

package xpertss.nio;

import java.io.IOException;
import java.nio.channels.Selector;

public interface NioAction {

   public void execute(Selector selector)
      throws IOException;

   public Selectable getSelectable();

}
