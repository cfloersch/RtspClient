package xpertss.rtsp;

import xpertss.lang.Objects;
import xpertss.lang.Strings;
import xpertss.mime.Headers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 */
public class RtspResponse {

   private InputStream entity = new ByteArrayInputStream(new byte[0]);

   private final String reason;
   private final Headers headers;
   private final RtspStatus status;

   RtspResponse(RtspStatus status, String reason, Headers headers)
   {
      this.status = Objects.notNull(status);
      this.reason = Strings.notEmpty(reason, "reason mus tbe defined");
      this.headers = Objects.notNull(headers);
   }


   public RtspStatus getStatus()
   {
      return status;
   }

   public String getStatusReason() { return reason; }

   public Headers getHeaders() { return headers; }


   public InputStream getEntityBody()
   {
      return entity;
   }

   public String toString()
   {
      return String.format("RTSP/1.0 %d %s", status.getCode(), reason);
   }



   RtspResponse withEntity(InputStream entity)
   {
      this.entity = Objects.ifNull(entity, this.entity);
      return this;
   }

   static RtspResponse create(List<String> lines)
   {
      String[] status = lines.remove(0).split("\\s+", 3);
      return new RtspResponse(RtspStatus.valueOf(Integer.parseInt(status[1])), status[2], Headers.create(lines));
   }

}
