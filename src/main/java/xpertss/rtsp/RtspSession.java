package xpertss.rtsp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xpertss.io.Buffers;
import xpertss.io.NIOUtils;
import xpertss.lang.Booleans;
import xpertss.lang.Integers;
import xpertss.lang.Numbers;
import xpertss.lang.Objects;
import xpertss.lang.Strings;
import xpertss.mime.Headers;
import xpertss.nio.Checkable;
import xpertss.nio.ConnectHandler;
import xpertss.nio.DataHandler;
import xpertss.nio.NioAction;
import xpertss.nio.NioReader;
import xpertss.nio.NioService;
import xpertss.nio.NioSession;
import xpertss.nio.NioStats;
import xpertss.nio.NioWriter;
import xpertss.nio.ReadyState;
import xpertss.nio.Selectable;
import xpertss.utils.UserAgent;
import xpertss.utils.Utils;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static java.nio.channels.SelectionKey.*;
import static xpertss.net.OptionalSocketOptions.SO_TIMEOUT;
import static xpertss.nio.ReadyState.*;
import static xpertss.rtsp.RtspMethod.*;

/**
 * This RtpSession impl is designed to support a streaming player not a
 * streaming producer.
 */
public class RtspSession implements NioSession, DataHandler, ConnectHandler, Checkable {


   private final ConcurrentMap<String,Object> attributes = new ConcurrentHashMap<>();
   private final Deque<ResponseReader> readers = new ConcurrentLinkedDeque<>();
   private final Queue<NioWriter> writeQueue = new ConcurrentLinkedQueue<>();
   private final ByteBuffer writeBuf = ByteBuffer.allocate(8192);
   private final ByteBuffer readBuf = ByteBuffer.allocate(8192);
   private final long createTime = System.currentTimeMillis();
   private final ReadManager readManager = new ReadManager();
   private final Logger log = LoggerFactory.getLogger(getClass());
   private final NioStats write = new NioStats();
   private final NioStats read = new NioStats();
   private final SocketAddress address;
   private final SocketChannel channel;
   private final RtspHandler handler;
   private final NioService service;
   private final String userAgent;
   private final URI target;

   private volatile ReadyState readyState = Open;
   private Set<SocketOption<?>> valid;
   private long timeoutConnection;
   private NioReader lastReader;
   private int readTimeout= 0;
   private int sequence = 0;


   RtspSession(NioService service, RtspHandler handler, URI target, UserAgent userAgent)
   {
      this.service = Objects.notNull(service);
      this.handler = Objects.notNull(handler);
      this.target = Objects.notNull(target);
      this.address = Utils.createSocketAddress(target, 554);
      this.channel = NIOUtils.openTcpSocket(false);
      this.userAgent = Objects.notNull(userAgent).toString();
   }



// Rtsp functions

   public URI getResource()
   {
      return target;
   }

   public void execute(RtspRequest request, RtspResponseHandler handler)
   {
      Headers headers = request.getHeaders();
      headers.setHeader("CSeq", Integer.toString(++sequence));
      headers.setIfNotSet("User-Agent", userAgent);
      writeQueue.offer(request.createWriter(target));
      readers.offer(new ResponseReader(handler, request.getMethod()));
      service.execute(new RegisterAction(OP_WRITE));
   }


   public void write(int channelId, ByteBuffer data)
   {
      Numbers.within(0, 255, channelId, "channelId outside range 0 - 255");
      Numbers.within(0, 65545, data.remaining(), "data buffer is too large");
      writeQueue.offer(new ChannelWriter(data, channelId));
      service.execute(new RegisterAction(OP_WRITE));
   }









// Base session functions

   /**
    * Connect this session to the target endpoint. Timeout the attempt after
    * the given timeout period measured in milliseconds has passed.
    */
   public void connect(int timeout)
   {
      if(channel.isOpen()) {
         readyState = Connecting;
         this.timeoutConnection = Utils.computeTimeout(timeout);
         service.execute(new ConnectAction(channel, address));
      }
   }

   /**
    * Close the session.
    */
   public void close()
   {
      if(channel.isOpen()) {
         readyState = Closing;
         service.execute(new CloseAction());
      }
   }





   @Override
   public ReadyState getReadyState()
   {
      return readyState;
   }






   @Override
   public SocketAddress getRemoteAddress()
   {
      return channel.socket().getRemoteSocketAddress();
   }

   @Override
   public SocketAddress getLocalAddress()
   {
      return channel.socket().getLocalSocketAddress();
   }

   @Override
   public SocketAddress getServiceAddress()
   {
      return address;
   }





   @Override
   public <T> RtspSession setOption(SocketOption<T> option, T value) throws IOException
   {
      if(option == SO_TIMEOUT) {
         this.readTimeout = Utils.maxIfZero(Numbers.gte(0, (Integer) value, "timeout must not be negative"));
      } else if(channel.supportedOptions().contains(option)) {
         channel.setOption(option, value);
      }
      return this;
   }


   @Override
   public <T> T getOption(SocketOption<T> option) throws IOException
   {
      if(option == SO_TIMEOUT) {
         return option.type().cast(this.readTimeout);
      } else if(channel.supportedOptions().contains(option)) {
         return channel.getOption(option);
      }
      throw new UnsupportedOperationException();
   }


   @Override
   public Set<SocketOption<?>> supportedOptions()
   {
      if(valid == null) {
         Set<SocketOption<?>> set = new HashSet<>();
         set.addAll(channel.supportedOptions());
         set.add(SO_TIMEOUT);
         valid = Collections.unmodifiableSet(set);
      }
      return valid;
   }












   @Override
   public long getCreationTime()
   {
      return createTime;
   }

   @Override
   public long getLastIoTime()
   {
      return Math.max(read.getTime(), write.getTime());
   }

   @Override
   public long getLastReadTime()
   {
      return read.getTime();
   }

   @Override
   public long getLastWriteTime()
   {
      return write.getTime();
   }





   @Override
   public long getBytesWritten()
   {
      return write.getCount();
   }

   @Override
   public long getBytesRead()
   {
      return read.getCount();
   }







   @Override
   public Object getAttribute(String key)
   {
      return attributes.get(key);
   }

   @Override
   public void setAttribute(String key, Object value)
   {
      attributes.put(key, value);
   }

   @Override
   public void removeAttribute(String key)
   {
      attributes.remove(key);
   }

   @Override
   public boolean hasAttribute(String key)
   {
      return attributes.containsKey(key);
   }

   @Override
   public Set<String> getAttributeKeys()
   {
      return Collections.unmodifiableSet(attributes.keySet());
   }






   @Override
   public void handleConnect() throws IOException
   {
      channel.finishConnect();
      service.execute(new UnregisterAction(OP_CONNECT));
      handler.onConnect(this);
      readyState = Connected;
      service.execute(new RegisterAction(OP_READ));
   }

   @Override
   public void handleRead() throws IOException
   {
      int result = channel.read(readBuf);
      if(result < 0) {
         throw new EOFException("peer closed connection");
      } else {
         read.record(result);
         readBuf.flip();
         while(readBuf.hasRemaining()) {
            if(lastReader != null) {
               if(lastReader.readFrom(readBuf)) {
                  lastReader = null;
               }
            } else if(readBuf.get(readBuf.position()) == 0x24) {
               if(readBuf.remaining() < 4) break;
               lastReader = new DataReader();
               if(lastReader.readFrom(readBuf)) {
                  lastReader = null;
               }
            } else {
               // TODO We need to be prepared for a spontaneous RTSP Response block
               // while reading channelized data. This might for example be the sever
               // telling us it is timing out our session due to lack of keep alive.
               lastReader = readers.poll();
               if(lastReader != null) {
                  if (lastReader.readFrom(readBuf)) {
                     lastReader = null;
                  }
               } else {
                  // We probably should have some sort of ResponseReader here
                  String str = Buffers.toHexString(readBuf, readBuf.position(), Math.min(readBuf.remaining(), 10));
                  log.error("Unexpected data: " + str);
                  throw new ProtocolException("unexpected read received");
               }
            }
         }
         readBuf.compact();
      }
   }

   @Override
   public void handleWrite() throws IOException
   {
      NioWriter writer = writeQueue.peek();
      if(writer != null) {
         if(writer.writeTo(writeBuf)) writeQueue.remove();
      }
      writeBuf.flip();
      if(writeBuf.hasRemaining()) {
         write.record(channel.write(writeBuf));
      } else if(writeQueue.isEmpty()) {
         service.execute(new UnregisterAction(SelectionKey.OP_WRITE));
      }
      writeBuf.compact();
   }


   @Override
   public void handleCheckup() throws IOException
   {
      int timeout = Utils.maxIfZero(readTimeout);
      if(readyState == Connecting && System.nanoTime() >= timeoutConnection) {
         throw new ConnectException("connection timed out");
      } else if(readyState == Connected) {
         ResponseReader reader = readers.peek();
         if(reader != null && reader.getWaitingTime() >= timeout) {
            throw new SocketTimeoutException("response timed out");
         } else if(readManager.isReaderActive()) {
            if(System.currentTimeMillis() - read.getTime() >= timeout) {
               throw new SocketTimeoutException("read timed out");
            }
         }
      }
   }


   @Override
   public SelectableChannel getChannel()
   {
      return channel;
   }

   @Override
   public void shutdown(Exception ex)
   {
      NIOUtils.close(channel);
      try {
         if (ex == null) handler.onClose(this);
         else handler.onFailure(this, ex);
      } finally {
         readyState = Closed;
      }
   }




   private class ConnectAction implements NioAction {

      private final SocketAddress address;
      private final SocketChannel socket;

      public ConnectAction(SocketChannel socket, SocketAddress address)
      {
         this.socket = Objects.notNull(socket, "socket");
         this.address = Objects.notNull(address, "address");
      }

      public void execute(Selector selector) throws IOException
      {
         SelectableChannel channel = getChannel();
         if(channel != null && channel.isOpen()) {
            SelectionKey sk = channel.keyFor(selector);
            if(sk == null) {
               channel.register(selector, OP_CONNECT, RtspSession.this);
               socket.connect(address);
            }
         }
      }

      public Selectable getSelectable()
      {
         return RtspSession.this;
      }

   }

   private class CloseAction implements NioAction {

      public void execute(Selector selector)
      {
         SelectableChannel channel = getChannel();
         if(channel != null && channel.isOpen()) {
            shutdown(null);
         }
      }

      public Selectable getSelectable()
      {
         return RtspSession.this;
      }

   }

   private class RegisterAction implements NioAction {

      private int ops;

      private RegisterAction(int ops)
      {
         this.ops = ops;
      }

      public void execute(Selector selector) throws IOException
      {
         SelectableChannel channel = getChannel();
         if(channel != null && channel.isOpen()) {
            SelectionKey sk = channel.keyFor(selector);
            if(sk == null) {
               channel.register(selector, ops, RtspSession.this);
            } else {
               sk.interestOps(sk.interestOps() | ops);
            }
         }
      }

      public Selectable getSelectable()
      {
         return RtspSession.this;
      }
   }

   private class UnregisterAction implements NioAction {

      private int ops;

      private UnregisterAction(int ops)
      {
         this.ops = ops;
      }

      public void execute(Selector selector)
      {
         SelectableChannel channel = getChannel();
         if(channel != null) {
            SelectionKey sk = channel.keyFor(selector);
            if(sk != null && sk.isValid()) {
               sk.interestOps(sk.interestOps() & ~ops);
            }
         }
      }

      public Selectable getSelectable()
      {
         return RtspSession.this;
      }

   }





   private class ChannelWriter implements NioWriter {

      private ByteBuffer data;
      private ChannelWriter(ByteBuffer src, int channelId)
      {
         data = ByteBuffer.allocate(src.remaining() + 4);
         data.put((byte)0x24).put((byte)channelId).putShort((short)src.remaining());
         Buffers.copyTo(src, data);
         data.flip();
      }

      @Override
      public boolean writeTo(ByteBuffer dst) throws IOException
      {
         Buffers.copyTo(data, dst);
         return !(data.hasRemaining());
      }
   }



   private class ResponseReader implements NioReader {

      private final CharBuffer lineBuffer = CharBuffer.allocate(2048);  // limit header/request line length to 2k
      private final List<String> lines = new ArrayList<>();
      private final long createTime = System.nanoTime();
      private final RtspResponseHandler handler;
      private final RtspMethod method;

      private RtspResponse response;
      private ByteBuffer entity;

      public ResponseReader(RtspResponseHandler handler, RtspMethod method)
      {
         this.handler = Objects.notNull(handler);
         this.method = Objects.notNull(method);
      }

      public long getWaitingTime()
      {
         return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - createTime);
      }


      @Override
      public boolean readFrom(ByteBuffer src) throws IOException
      {
         if(response == null) response = readResponse(src);
         if (response != null && readEntity(src)) {
            if(response.getStatus() == RtspStatus.Ok)
               readManager.eval(method, response.getHeaders());
            handler.onResponse(RtspSession.this, response);
            return true;
         }
         return false;
      }

      private RtspResponse readResponse(ByteBuffer src) throws IOException
      {
         while(readLine(src, lineBuffer)) {
            if(lineBuffer.position() == 0) {
               if(lines.isEmpty()) throw new IOException("premature end of response");
               return RtspResponse.create(lines);
            } else if(lines.size() < 30) {
               lines.add(Utils.consume(lineBuffer, false));
            } else {
               throw new ProtocolException("max number of lines exceeded");
            }
         }
         return null;
      }

      private boolean readEntity(ByteBuffer src) throws IOException
      {
         if(entity == null) {
            long length = response.getHeaders().getContentLength();
            if(length > 8196L) throw new ProtocolException("response entity too large");
            if(length <= 0) return true;
            entity = ByteBuffer.allocate(Integers.safeCast(length));
         }
         Buffers.copyTo(src, entity);
         if (entity.hasRemaining()) return false;
         response = response.withEntity(Buffers.newInputStream(entity));
         return true;
      }


      private boolean readLine(ByteBuffer src, CharBuffer dst)
      {
         while(src.hasRemaining()) {
            char c = (char) (src.get() & 0xff);
            if(c == '\n') {
               return true;
            } else if(c != '\r') {
               dst.append(c);
            }
         }
         return false;
      }


   }

   private class DataReader implements NioReader {

      private ByteBuffer data;
      private int channelId;

      @Override
      public boolean readFrom(ByteBuffer src) throws IOException
      {
         if(data == null) {
            if (src.get() != 0x24) throw new ProtocolException("expected interleaved data");
            channelId = src.get();
            int len = src.getShort();
            data = ByteBuffer.allocate(len);
         }
         Buffers.copyTo(src, data);
         if (data.hasRemaining()) return false;
         data.flip();
         handler.onData(RtspSession.this, channelId, data.asReadOnlyBuffer());
         return true;
      }
   }



   private class ReadManager {

      private final ConcurrentMap<String, Boolean> channels = new ConcurrentHashMap<>();
      private int count;


      public void eval(RtspMethod method, Headers response)
      {
         if(method == Setup) {
            String transport = Headers.toString(response.getHeader("Transport"));
            if(Strings.contains(transport, "interleaved")) {
               String sessionId = Headers.toString(response.getHeader("Session"));
               if(sessionId != null) channels.put(sessionId, false);
            }
         } else if(method == Play) {
            String sessionId = Headers.toString(response.getHeader("Session"));
            if(sessionId != null && channels.replace(sessionId, false, true)) count++;
         } else if(method == Pause) {
            String sessionId = Headers.toString(response.getHeader("Session"));
            if(sessionId != null && channels.replace(sessionId, true, false)) count--;
         } else if(method == Teardown) {
            String sessionId = Headers.toString(response.getHeader("Session"));
            if(Booleans.isTrue(channels.remove(sessionId))) count--;
         }
      }

      public boolean isReaderActive()
      {
         return count > 0;
      }

   }

}
