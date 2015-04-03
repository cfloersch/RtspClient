package xpertss.rtsp;

import java.io.IOException;

/**
 */
public interface RtspResponseHandler {

   public void onResponse(RtspSession session, RtspResponse response) throws IOException;

}
