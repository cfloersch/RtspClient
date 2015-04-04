/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/23/11 9:23 AM
 */
package xpertss.nio;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface NioReader {

   public boolean readFrom(ByteBuffer src) throws IOException;

}
