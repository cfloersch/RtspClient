package xpertss.nio;

import java.io.IOException;

/**
 * Created by cfloersch on 3/16/2015.
 */
public interface ConnectHandler extends Selectable {

    public void handleConnect() throws IOException;

}
