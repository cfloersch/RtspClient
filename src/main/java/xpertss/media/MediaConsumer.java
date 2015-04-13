package xpertss.media;

import xpertss.sdp.MediaDescription;
import xpertss.sdp.SessionDescription;

import java.nio.ByteBuffer;

/**
 * A media consumer is responsible for interfacing with an RtspPlayer and
 * providing it information about which media streams to setup, and to
 * actually process the media streams that result from playback.
 */
public interface MediaConsumer {

   /**
    * Given a session description return the media descriptions the
    * consumer wishes to be setup for playback.
    */
   public MediaDescription[] select(SessionDescription sdp);


   /**
    * For each media description desired this will be called by the
    * player to inform it that the channel has been successfully
    * setup and to identify the channel identifiers associated with
    * it.
    */
   public void createChannel(MediaChannel channel);

   /**
    * Called to indicate that the previously created channels should
    * be destroyed as they are no longer valid.
    */
   public void destroyChannels();



   /**
    * Once playback has begun the actual channel data will be delivered
    * to the consumer via this method. The channelId will be one of the
    * identifiers previously communicated to the newChannel callback.
    */
   public void consume(int channelId, ByteBuffer data);


   /**
    * Called to notify the consumer of an error that has terminated
    * playback.
    */
   public void handle(Throwable t);

}
