package xpertss.rtsp;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A handler receives events associated with the session. It is responsible
 * for coordinating the connection, the RTSP handshake, and processing or
 * forwarding data packets.
 */
public interface RtspHandler {

   /**
    * Called to indicate the connection sequence has completed.
    */
   public void onConnect(RtspSession session);

   /**
    * Called to indicate the disconnection sequence has completed.
    */
   public void onClose(RtspSession session);

   /**
    * Called to indicate a failure on the session and to indicate
    * its closure.
    */
   public void onFailure(RtspSession session, Exception e);


   /**
    * Called to indicate data read from an interleaved channel.
    */
   public void onData(RtspSession session, int channel, ByteBuffer data) throws IOException;

}
