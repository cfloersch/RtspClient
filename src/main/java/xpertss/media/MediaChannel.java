package xpertss.media;

import xpertss.lang.Objects;
import xpertss.lang.Range;
import xpertss.sdp.Attribute;
import xpertss.sdp.MediaDescription;

/**
 * A media channel is a channel setup by the RTSP process to transmit
 * or receive media data.
 * <p/>
 * The media channel is intended to convey information to a consumer
 * about the channel identifiers used for the media and the type of
 * media to expect on those channels.
 */
public class MediaChannel implements Comparable<MediaChannel> {

   private final MediaDescription desc;
   private final Range<Integer> channels;

   public MediaChannel(MediaDescription desc, Range<Integer> channels)
   {
      this.desc = Objects.notNull(desc);
      this.channels = Objects.notNull(channels);
   }

   /**
    * The full media description of the data this channel will
    * process.
    */
   public MediaDescription getMedia()
   {
      return desc;
   }

   /**
    * A control identifier for the channel.
    */
   public String getControl()
   {
      Attribute attr = desc.getAttribute("control");
      return (attr != null) ? attr.getValue() : null;
   }


   /**
    * The media type this channel will process.
    */
   public MediaType getType()
   {
      return MediaType.parse(desc);
   }

   /**
    * The channel identifiers over which this media will be transmitted.
    * <p/>
    * The media data is usually transmitted over the lower of the two
    * channel identifiers while control information for the channel is
    * transmitted over the higher channel.
    */
   public Range<Integer> getChannels()
   {
      return channels;
   }


   /**
    * Compares this media channel to another based on its channel identifiers.
    */
   @Override
   public int compareTo(MediaChannel o)
   {
      return channels.compareTo(o.channels);
   }

}
