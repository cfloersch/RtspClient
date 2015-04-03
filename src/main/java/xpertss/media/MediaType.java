package xpertss.media;

import xpertss.sdp.MediaDescription;

/**
 */
public enum MediaType {

   Audio, Video;


   public static MediaType parse(MediaDescription desc)
   {
      String mediaType = desc.getMedia().getType();
      if("Audio".equalsIgnoreCase(mediaType)) {
         return Audio;
      } else if("Video".equalsIgnoreCase(mediaType)) {
         return Video;
      }
      return null;
   }
}
