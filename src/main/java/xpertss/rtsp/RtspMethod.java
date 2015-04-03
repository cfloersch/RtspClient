package xpertss.rtsp;

import java.net.URI;

/**
 */
public enum RtspMethod {

   /**
    * The OPTIONS method represents a request for information about the
    * communication options available on the request/response chain
    * identified by the Request-URI. This method allows the client to
    * determine the options and/or requirements associated with a resource,
    * or the capabilities of a server, without implying a resource action
    * or initiating a resource retrieval.
    */
   Options(false),

   /**
    * When sent from client to server, ANNOUNCE posts the description of a
    * presentation or media object identified by the request URL to a
    * server. When sent from server to client, ANNOUNCE updates the session
    * description in real-time.
    */
   Announce(true) {
      @Override public RtspRequest createRequest(URI uri)
      {
         throw new UnsupportedOperationException("announce is not yet supported");
      }
   },

   /**
    * The DESCRIBE method retrieves the description of a presentation or
    * media object identified by the request URL from a server. It may use
    * the Accept header to specify the description formats that the client
    * understands. The server responds with a description of the requested
    * resource. The DESCRIBE reply-response pair constitutes the media
    * initialization phase of RTSP.
    */
   Describe(false),

   /**
    * The SETUP request for a URI specifies the transport mechanism to be
    * used for the streamed media.
    */
   Setup(false),

   /**
    * The PLAY method tells the server to start sending data via the
    * mechanism specified in SETUP. A client MUST NOT issue a PLAY request
    * until any outstanding SETUP requests have been acknowledged as
    * successful.
    */
   Play(false),

   /**
    * This method initiates recording a range of media data according to
    * the presentation description.
    */
   Record(false) {
      @Override public RtspRequest createRequest(URI uri)
      {
         throw new UnsupportedOperationException("record not yet supported");
      }
   },

   /**
    * The PAUSE request causes the stream delivery to be interrupted
    * (halted) temporarily. If the request URL names a stream, only
    * playback and recording of that stream is halted. For example, for
    * audio, this is equivalent to muting. If the request URL names a
    * presentation or group of streams, delivery of all currently active
    * streams within the presentation or group is halted.
    */
   Pause(false),

   /**
    * The TEARDOWN request stops the stream delivery for the given URI,
    * freeing the resources associated with it. If the URI is the
    * presentation URI for this presentation, any RTSP session identifier
    * associated with the session is no longer valid. Unless all transport
    * parameters are defined by the session description, a SETUP request
    * has to be issued before the session can be played again.
    */
   Teardown(false);



   private boolean supportsEntity;

   private RtspMethod(boolean supportsEntity)
   {
      this.supportsEntity = supportsEntity;
   }


   /**
    * Create a request using the current request method to the default uri
    * specified when the session was opened.
    */
   public RtspRequest createRequest()
   {
      return createRequest(null);
   }

   /**
    * Create a request using the current request method to the specified uri.
    */
   public RtspRequest createRequest(URI uri)
   {
      return new RtspRequest(this, uri);
   }

   /**
    * Allows an entity to be sent to the server.
    */
   public boolean supportsEntity() { return supportsEntity; }


}
