/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: Aug 12, 2007
 * Time: 11:28:38 PM
 */

package xpertss.nio;

import java.io.IOException;

public interface AcceptHandler extends Selectable {

   public void handleAccept() throws IOException;

}
