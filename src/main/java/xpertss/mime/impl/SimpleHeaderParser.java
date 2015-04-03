/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/18/11 1:52 PM
 * Copyright Manheim online
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
import static xpertss.mime.HeaderTokenizer.Token;

/**
 * A simple header parser parses simple impl headers. A simple header is one that has only
 * a single unnamed value. Some examples include, Date, Server, Content-Length, Expires,
 * Age, Content-Type, Location, Referrer, etc.
 */
public class SimpleHeaderParser extends ParameterizedHeaderParser {

   private String name;
   private Header.Type type;

   public SimpleHeaderParser(String name, Header.Type type)
   {
      this.name = Objects.notNull(name, "name");
      this.type = Objects.notNull(type, "type");
   }


   @Override
   protected Header doParse(CharSequence raw)
      throws MalformedException
   {
      HeaderTokenizer h = new HeaderTokenizer(raw, MIME);
      List<String> parts = new ArrayList<String>();
      StringBuffer buf = new StringBuffer();
      boolean complete = false;

      while(!complete) {
         HeaderTokenizer.Token token = h.next();
         switch(token.getType()) {
            case Token.EOF:
               parts.add(Utils.trimAndClear(buf));
               complete = true;
               break;
            case Token.LWS:
               buf.append(" ");  // collapse whitespace
               continue;
            case ';':
               parts.add(Utils.trimAndClear(buf));
               continue;
            case Token.QUOTEDSTRING:
               buf.append('"').append(token.getValue()).append('"');
               continue;
            case Token.COMMENT:
               buf.append('(').append(token.getValue()).append(')');
               continue;
            default:
               buf.append(token.getValue());
         }
      }
      return new SingleValueHeader(name, type, create(parts));
   }


   private HeaderValue create(List<String> parts) throws MalformedException
   {
      if(parts.size() == 1) return new SimpleHeaderValue(parts.get(0), new Parameter[0]);
      Parameter[] params = new Parameter[parts.size() - 1];
      for(int i = 1; i < parts.size(); i++) params[i-1] = doParameter(parts.get(i));
      return new SimpleHeaderValue(parts.get(0), params);

   }


}
