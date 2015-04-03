package xpertss.media;

import xpertss.lang.Objects;
import xpertss.lang.Range;
import xpertss.sdp.Attribute;
import xpertss.sdp.MediaDescription;

/**
 *
 */
public class MediaChannel implements Comparable<MediaChannel> {

   private MediaDescription desc;
   private Range<Integer> channels;

   public MediaChannel(MediaDescription desc, Range<Integer> channels)
   {
      this.desc = Objects.notNull(desc);
      this.channels = Objects.notNull(channels);
   }

   public MediaDescription getMedia()
   {
      return desc;
   }

   public String getControl()
   {
      Attribute attr = desc.getAttribute("control");
      return (attr != null) ? attr.getValue() : null;
   }


   public MediaType getType()
   {
      return MediaType.parse(desc);
   }

   public Range<Integer> getChannels()
   {
      return channels;
   }


   @Override
   public int compareTo(MediaChannel o)
   {
      return channels.compareTo(o.channels);
   }

}
