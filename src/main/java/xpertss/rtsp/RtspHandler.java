package xpertss.rtsp;

import java.nio.ByteBuffer;

/**
 * A handler receives events associated with the session. it is responsible
 * for coordinating the connection, the RTSP handshake, and processing or
 * forwarding data packets.
 */
public interface RtspHandler {

   // TODO Do we want an onOpen(RtspSession session) ???

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









   // TODO Do I want to enable throwing an IO exception here?
   // This will only be called for interleaved channels
   public void onData(RtspSession session, int channel, ByteBuffer data);

}
