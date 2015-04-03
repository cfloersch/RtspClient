package xpertss.nio;

import java.io.IOException;

/**
 * A {@link Selectable} subclass which wishes to be periodically checked so
 * that it may validate timeouts and other state.
 */
public interface Checkable extends Selectable {

   public void handleCheckup() throws IOException;

}
