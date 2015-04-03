/**
 * Copyright XpertSoftware All rights reserved.
 * All rights reserved.
 *
 * User: floersh
 * Date: Aug 12, 2007
 * Time: 11:17:40 PM
 */

package xpertss.nio;

import java.nio.channels.SelectableChannel;

public interface Selectable {

   public SelectableChannel getChannel();

   public void shutdown(Exception ex);

}
