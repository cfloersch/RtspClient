package xpertss.rtsp;

import xpertss.SourceException;
import xpertss.lang.Numbers;
import xpertss.lang.Objects;
import xpertss.lang.Range;
import xpertss.lang.Strings;
import xpertss.media.MediaChannel;
import xpertss.media.MediaConsumer;
import xpertss.mime.Headers;
import xpertss.net.SocketOptions;
import xpertss.sdp.MediaDescription;
import xpertss.sdp.SessionDescription;
import xpertss.sdp.SessionParser;
import xpertss.utils.Utils;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.TreeSet;

import static xpertss.net.OptionalSocketOptions.SO_TIMEOUT;
import static xpertss.rtsp.RtspMethod.Describe;
import static xpertss.rtsp.RtspMethod.Pause;
import static xpertss.rtsp.RtspMethod.Play;
import static xpertss.rtsp.RtspMethod.Setup;
import static xpertss.rtsp.RtspMethod.Teardown;
import static xpertss.rtsp.RtspState.*;
import static xpertss.rtsp.RtspStatus.*;

public class RtspPlayer {

   private final TreeSet<MediaChannel> channels = new TreeSet<>();
   private final MediaConsumer consumer;
   private final RtspClient client;

   private volatile RtspState state = Stopped;
   private RtspSession session;
   private int connectTimeout;
   private int readTimeout;
   private URI base;



   public RtspPlayer(RtspClient client, MediaConsumer consumer)
   {
      this.client = Objects.notNull(client);
      this.consumer = Objects.notNull(consumer);
   }




   public void setConnectTimeout(int connectTimeout)
   {
      this.connectTimeout = Numbers.gte(0, connectTimeout, "connectTimeout must not be negative");
   }

   public int getConnectTimeout()
   {
      return connectTimeout;
   }




   public void setReadTimeout(int readTimeout)
   {
      this.readTimeout = Numbers.gte(0, readTimeout, "readTimeout must not be negative");
   }

   public int getReadTimeout()
   {
      return readTimeout;
   }





   public SessionDescription getSessionDescription()
   {
      return getSessionDescription(session);
   }




   public RtspState getState()
   {
      return state;
   }




   /**
    * Connect to the remote media server and setup the media streams.
    */
   public void start(URI uri)
   {
      if(state == Stopped) {
         state = Activating;
         session = client.open(new RtspPlaybackHandler(), uri);

         SocketOptions.set(session, SO_TIMEOUT, readTimeout);

         session.connect(connectTimeout);
      }
   }

   /**
    * Play the configured media streams.
    */
   public void play()
   {
      if(state == Paused) {
         state = Activating;
         RtspRequest request = Play.createRequest(base);
         Headers headers = request.getHeaders();
         setSessionId(session, headers);
         headers.setHeader("Range", "npt=0.000-");
         session.execute(request, new DefaultResponseHandler() {
            @Override public void onOkResponse(RtspSession session, RtspResponse response) throws IOException {
               state = Active;
            }
         });
      }
   }

   /**
    * Pause the configured media streams.
    */
   public void pause()
   {
      if(state == Active) {
         state = Pausing;
         RtspRequest request = Pause.createRequest(base);
         Headers headers = request.getHeaders();
         setSessionId(session, headers);
         session.execute(request, new DefaultResponseHandler() {
            @Override public void onOkResponse(RtspSession session, RtspResponse response) throws IOException {
               state = Paused;
            }
         });
      }
   }

   /**
    * Stop playback, tear down the configured streams and disconnect.
    */
   public void stop()
   {
      if(!Objects.isOneOf(state, Stopped, Stopping)) {
         state = Stopping;
         RtspRequest request = Teardown.createRequest(base);
         Headers headers = request.getHeaders();
         setSessionId(session, headers);
         headers.setHeader("Connection", "close");
         session.execute(request, new DefaultResponseHandler() {
            @Override public void onOkResponse(RtspSession session, RtspResponse response) throws IOException {
               consumer.destroyChannels();
               session.close();
            }
         });
         session = null;
      }
   }







   private static final String TRANSPORT = "RTP/AVP/TCP;unicast;interleaved=%d-%d";

   private void setupChannel(RtspSession session) throws IOException
   {
      final MediaChannel channel = channels.pollFirst();
      if(channel == null) {
         startPlayback(session);
      } else {
         String control = channel.getControl();
         URI target = (Strings.isEmpty(control)) ? base : base.resolve(control);
         RtspRequest request = Setup.createRequest(target);
         Headers headers = request.getHeaders();

         final Range<Integer> channels = channel.getChannels();
         headers.setHeader("Transport", String.format(TRANSPORT,
                                                      channels.getLower(),
                                                      channels.getUpper()));
         setSessionId(session, headers);

         session.execute(request, new DefaultResponseHandler() {
            @Override
            public void onOkResponse(RtspSession session, RtspResponse response) throws IOException {
               consumer.createChannel(channel);
               session.setAttribute("session.id", Utils.getHeader(response, "Session"));
               setupChannel(session);
            }
         });
      }
   }




   private void startPlayback(RtspSession session) throws IOException
   {
      RtspRequest request = Play.createRequest(base);
      Headers headers = request.getHeaders();
      setSessionId(session, headers);
      headers.setHeader("Range", "npt=0.000-");
      session.execute(request, new DefaultResponseHandler() {
         @Override public void onOkResponse(RtspSession session, RtspResponse response) throws IOException {
            state = Active;
         }
      });
   }




   private static void setSessionId(RtspSession session, Headers headers)
   {
      String sessionId = (String) session.getAttribute("session.id");
      if(!Strings.isEmpty(sessionId)) headers.setHeader("Session", sessionId);
   }

   private static SessionDescription getSessionDescription(RtspSession session)
   {
      return (session == null) ? null : (SessionDescription)
                     session.getAttribute("session.description");
   }


   private class RtspPlaybackHandler implements RtspHandler {

      @Override
      public void onConnect(RtspSession session)
      {
         RtspRequest request = Describe.createRequest();
         Headers headers = request.getHeaders();
         headers.setHeader("Accept", "application/sdp");
         session.execute(request, new DefaultResponseHandler() {
            @Override public void onOkResponse(RtspSession session, RtspResponse response) throws IOException {
               Headers headers = response.getHeaders();

               String baseUri = Headers.toString(headers.getHeader("Content-Base"));
               base = (Strings.isEmpty(baseUri)) ? session.getResource() : URI.create(baseUri);

               String contentType = Headers.toString(headers.getHeader("Content-Type"));
               if (Strings.equal("application/sdp", contentType)) {
                  SessionParser parser = new SessionParser();
                  SessionDescription sessionDescription = parser.parse(response.getEntityBody());
                  session.setAttribute("session.description", sessionDescription);

                  MediaDescription[] medias = consumer.select(sessionDescription);
                  if(Objects.isEmpty(medias)) throw new ProtocolException("missing media resource");
                  channels.clear();
                  for(int i = 0; i < medias.length; i++) {
                     channels.add(new MediaChannel(medias[i], new Range<>(i * 2, i * 2 + 1)));
                  }

                  setupChannel(session);
               } else {
                  throw new ProtocolException("unexpected entity type received");
               }
            }
         });
      }

      @Override
      public void onClose(RtspSession session)
      {
         state = Stopped;
      }

      @Override
      public void onFailure(RtspSession session, Exception e)
      {
         state = Stopped;
         consumer.handle(e);
      }




      @Override
      public void onData(RtspSession session, int channelId, ByteBuffer data) throws IOException
      {
         consumer.consume(channelId, data);
      }


   }



   private static class DefaultResponseHandler implements RtspResponseHandler {

      @Override public void onResponse(RtspSession session, RtspResponse response) throws IOException {
         if(response.getStatus() != Ok)
            throw new SourceException(response.getStatus().getCode(), response.getStatusReason());
         onOkResponse(session, response);
      }

      protected void onOkResponse(RtspSession session, RtspResponse response) throws IOException
      {
      }
   }
}
