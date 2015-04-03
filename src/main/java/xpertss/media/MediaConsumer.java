package xpertss.media;

import xpertss.sdp.MediaDescription;
import xpertss.sdp.SessionDescription;

import java.nio.ByteBuffer;
import java.util.SortedSet;

/**
 */
public interface MediaConsumer {

   public MediaDescription[] select(SessionDescription sdp);

   public void newChannel(MediaChannel channel);

   public void consume(int channelId, ByteBuffer data);

   public void handle(Throwable t);

}
