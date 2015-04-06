package xpertss.rtsp;

import org.junit.Test;
import xpertss.function.Consumer;
import xpertss.lang.Range;
import xpertss.media.MediaChannel;
import xpertss.media.MediaConsumer;
import xpertss.media.MediaType;
import xpertss.sdp.MediaDescription;
import xpertss.sdp.SessionDescription;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static xpertss.nio.ReadyState.Closed;
import static xpertss.nio.ReadyState.Closing;
import static xpertss.nio.ReadyState.Connected;
import static xpertss.nio.ReadyState.Connecting;
import static xpertss.nio.ReadyState.Open;

public class RtspClientTest {


   @Test
   public void testClientConnectSequence()
   {
      RtspHandler handler = mock(RtspHandler.class);
      RtspClient client = new RtspClient();
      RtspSession session = client.open(handler, URI.create("rtsp://stream.manheim.com:999/AVAIL.sdp"));

      assertEquals(Open, session.getReadyState());
      session.connect(500);
      assertEquals(Connecting, session.getReadyState());

      while(session.getReadyState() == Connecting);

      verify(handler, times(1)).onConnect(same(session));
      assertEquals(Connected, session.getReadyState());

      session.close();
      assertEquals(Closing, session.getReadyState());

      while(session.getReadyState() == Closing);
      assertEquals(Closed, session.getReadyState());

      verify(handler, times(1)).onClose(same(session));
   }


   @Test
   public void testClientConnectSequenceTimeout()
   {
      RtspHandler handler = mock(RtspHandler.class);
      RtspClient client = new RtspClient();
      RtspSession session = client.open(handler, URI.create("rtsp://10.0.0.1:999/AVAIL.sdp"));

      assertEquals(Open, session.getReadyState());
      session.connect(100);
      assertEquals(Connecting, session.getReadyState());

      while(session.getReadyState() == Connecting);

      verify(handler, times(1)).onFailure(same(session), any(ConnectException.class));
      assertEquals(Closed, session.getReadyState());
   }

   @Test
   public void testClientConnectSequenceUnknownHost()
   {
      RtspHandler handler = mock(RtspHandler.class);
      RtspClient client = new RtspClient();
      RtspSession session = client.open(handler, URI.create("rtsp://doesnot.exist.host.com:999/AVAIL.sdp"));

      assertEquals(Open, session.getReadyState());
      session.connect(100);
      assertEquals(Connecting, session.getReadyState());

      while(session.getReadyState() == Connecting);

      verify(handler, times(1)).onFailure(same(session), any(UnknownHostException.class));
      assertEquals(Closed, session.getReadyState());
   }





   @Test
   public void testRtspPlayer()
   {
      RtspClient client = new RtspClient();
      RtspPlayer player = new RtspPlayer(client, new MediaConsumer() {

         private Map<Integer,Consumer<ByteBuffer>> channels = new HashMap<>();

         @Override
         public MediaDescription[] select(SessionDescription sdp)
         {
            return sdp.getMediaDescriptions();
         }

         @Override
         public void newChannel(MediaChannel channel)
         {
            final MediaType type = channel.getType();
            Range<Integer> range = channel.getChannels();
            channels.put(range.getLower(), new Consumer<ByteBuffer>() {
               long start = System.nanoTime();
               @Override public void apply(ByteBuffer data) {
                  long millis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                  int seq = (int) (data.getShort(2) & 0xffff);
                  long ts = (long) (data.getInt(4) & 0xffffffff);
                  System.out.println(String.format("%s: %010d {seq=%05d, ts=%d, size=%04d}", type.name(), millis, seq, ts, data.remaining()));
               }
            });
            channels.put(range.getUpper(), new Consumer<ByteBuffer>() {
               @Override
               public void apply(ByteBuffer byteBuffer) {
                  System.out.println(String.format("%s - RTP Sender Report Received", type.name()));
               }
            });
         }

         @Override
         public void consume(int channelId, ByteBuffer data)
         {
            Consumer<ByteBuffer> handler = channels.get(channelId);
            if(handler == null)  System.out.println(String.format("Received packet on unknown channel %d", channelId));
            handler.apply(data);
         }

         @Override
         public void handle(Throwable t)
         {
            t.printStackTrace();
         }


      });

      player.setReadTimeout(5000);
      player.start(URI.create("rtsp://stream.manheim.com:999/AVAIL.sdp"));
      assertEquals(RtspState.Activating, player.getState());
      client.await(3, TimeUnit.SECONDS);
      assertEquals(RtspState.Active, player.getState());
      player.pause();
      assertEquals(RtspState.Pausing, player.getState());
      client.await(3, TimeUnit.SECONDS);
      assertEquals(RtspState.Paused, player.getState());
      player.play();
      assertEquals(RtspState.Activating, player.getState());
      client.await(3, TimeUnit.SECONDS);
      assertEquals(RtspState.Active, player.getState());
      player.stop();
      assertEquals(RtspState.Stopping, player.getState());
      client.await();
      assertEquals(RtspState.Stopped, player.getState());
   }



}