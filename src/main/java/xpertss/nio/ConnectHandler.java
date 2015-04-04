package xpertss.nio;

import java.io.IOException;

/**
 */
public interface ConnectHandler extends Selectable {

    public void handleConnect() throws IOException;

}
