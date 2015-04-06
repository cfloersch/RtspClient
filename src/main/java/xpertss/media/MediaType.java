package xpertss.media;

import xpertss.sdp.MediaDescription;

/**
 * An Enumeration of Standard SDP Media Types.
 */
public enum MediaType {

   Audio, Video, Text, Application, Message;


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
