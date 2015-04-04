/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/23/11 9:24 AM
 */
package xpertss.nio;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface NioWriter {

   public boolean writeTo(ByteBuffer dst) throws IOException;

}
