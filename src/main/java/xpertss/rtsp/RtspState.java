package xpertss.rtsp;

/**
 * An enumeration of Rtsp States.
 */
public enum RtspState {

   /**
    * The player or producer is not connected.
    */
   Stopped,

   /**
    * The player or producer are in the process of inactivating
    */
   Pausing,

   /**
    * The player or producer is connected and setup but not
    * actively playing or recording media.
    */
   Paused,

   /**
    * The player or producer are in the process of starting
    * playback or recording.
    */
   Activating,

   /**
    * The player or producer are actively playing or recording
    * media content.
    */
   Active,

   /**
    * The player or producer is in the process of tearing down
    * the session and disconnecting.
    */
   Stopping

}
