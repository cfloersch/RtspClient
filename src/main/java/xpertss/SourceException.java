package xpertss;

import java.net.ProtocolException;

/**
 * A simple exception to encapsulate a RTSP/HTTP response code and
 * reason.
 */
public class SourceException extends ProtocolException {

   private int code;

   public SourceException(int code, String reason)
   {
      super(reason);
      this.code = code;
   }

   public int getCode()
   {
      return code;
   }

}
