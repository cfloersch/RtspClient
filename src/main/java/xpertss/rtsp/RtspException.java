package xpertss.rtsp;

import java.net.ProtocolException;

/**
 *
 */
public class RtspException extends ProtocolException {

   public enum Type {
      Url, Network, Source, Carrier, Audio, Video, Authentication
   }


}
