package xpertss.rtsp;

import xpertss.io.Buffers;
import xpertss.lang.Objects;
import xpertss.lang.Strings;
import xpertss.mime.Headers;
import xpertss.nio.NioWriter;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import static xpertss.io.Charsets.*;
import static xpertss.rtsp.RtspMethod.*;


/**
 */
public class RtspRequest {

   private final Headers headers = new Headers(Headers.Type.Rtsp);
   private final RtspMethod method;
   private final URI target;

   RtspRequest(RtspMethod method, URI target)
   {
      if(!Objects.isOneOf(method, Options, Describe, Setup, Play, Pause, Teardown))
         throw new UnsupportedOperationException("method not yet supported");
      this.method = Objects.notNull(method, "method");
      this.target = target;
   }




   public RtspMethod getMethod()
   {
      return method;
   }

   public Headers getHeaders()
   {
      return headers;
   }







   NioWriter createWriter(URI uri)
   {
      // TODO Check that target and uri have same authority
      return new RequestWriter(uri);
   }

   private class RequestWriter implements NioWriter {

      private final URI uri;
      private ByteBuffer encoded;
      private RequestWriter(URI uri)
      {
         this.uri = Objects.notNull(uri);
      }

      @Override
      public boolean writeTo(ByteBuffer dst) throws IOException
      {
         if(encoded == null) encoded = encode();
         Buffers.copyTo(encoded, dst);
         return !encoded.hasRemaining();
      }

      private ByteBuffer encode() throws IOException
      {
         StringBuilder builder = new StringBuilder();
         builder.append(Strings.toUpper(method.name())).append(" ");
         builder.append((target == null) ? uri : target);
         builder.append(" ").append("RTSP/1.0").append("\r\n");
         builder.append(headers);
         builder.append("\r\n");
         return US_ASCII.encode(builder.toString());
      }

   }



}
