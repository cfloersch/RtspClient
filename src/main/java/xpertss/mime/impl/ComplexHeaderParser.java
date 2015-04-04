/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 9:19 PM
 */
package xpertss.mime.impl;

import xpertss.mime.Header;
import xpertss.mime.HeaderTokenizer;
import xpertss.mime.HeaderValue;
import xpertss.mime.MalformedException;
import xpertss.mime.Parameter;
import xpertss.lang.Objects;
import xpertss.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static xpertss.mime.HeaderTokenizer.MIME;

/**
 * A complex header is any header which likely contains multiple parts separated
 * by a comma. This may include both named and unnamed parts.
 * <pre>
 *    Range: npt=0.000-
 *    RTP-Info: url=rtsp://stream.manheim.com:999/AVAIL.sdp/trackID=2,url=rtsp://stream.manheim.com:999/AVAIL.sdp/trackID=5
 *    Public: DESCRIBE, SETUP, TEARDOWN, PLAY, PAUSE, OPTIONS, ANNOUNCE, RECORD
 *    If-Match: "etag1", "etag2", "etag3"
 *    Accept: text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c
 * </pre>
 */
public class ComplexHeaderParser extends ParameterizedHeaderParser {

   private String name;
   private Header.Type type;

   protected ComplexHeaderParser(String name, Header.Type type)
   {
      this.name = Objects.notNull(name, "name");
      this.type = Objects.notNull(type, "type");
   }


   @Override
   protected Header doParse(CharSequence raw)
      throws MalformedException
   {
      HeaderTokenizer h = new HeaderTokenizer(raw, MIME);
      List<HeaderValue> values = new ArrayList<HeaderValue>();
      List<String> parts = new ArrayList<String>();
      StringBuffer buf = new StringBuffer();
      boolean complete = false;

      // First break them up into individual value pairs
      while(!complete) {
         HeaderTokenizer.Token token = h.next();
         switch(token.getType()) {
            case HeaderTokenizer.Token.EOF:
               parts.add(Utils.trimAndClear(buf));
               values.add(create(parts));
               complete = true;
               break;
            case HeaderTokenizer.Token.LWS:
               buf.append(" ");  // collapse whitespace
               continue;
            case ';':
               parts.add(Utils.trimAndClear(buf));
               continue;
            case ',':
               parts.add(Utils.trimAndClear(buf));
               values.add(create(parts));
               continue;
            case HeaderTokenizer.Token.QUOTEDSTRING:
               buf.append('"').append(token.getValue()).append('"');
               continue;
            case HeaderTokenizer.Token.COMMENT:
               buf.append('(').append(token.getValue()).append(')');
               continue;
            default:
               buf.append(token.getValue());
         }
      }
      return new ComplexValueHeader(name, type, values.toArray(new HeaderValue[values.size()]));
   }

   private HeaderValue create(List<String> parts) throws MalformedException
   {
      String[] valueParts = parts.remove(0).split("=", 2);
      if(valueParts.length == 1) return new SimpleHeaderValue(valueParts[0], createParams(parts));
      return new NamedHeaderValue(valueParts[0], valueParts[1], createParams(parts));
   }


   private Parameter[] createParams(List<String> parts) throws MalformedException
   {
      Parameter[] params = new Parameter[parts.size()];
      String part = null; int i = 0;
      while(!parts.isEmpty() && (part = parts.remove(0)) != null) {
         params[i++] = doParameter(part);
      }
      return params;
   }

}
