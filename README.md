# RtspClient
A simple RTSP Client implementation using Java NIO

The rtsp-client is a Java NIO implementation of a RTSP client. At present it only
supports player clients but a future release will add publishing clients. The API
is extensible so customization is possible.

Example;

````
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

````
