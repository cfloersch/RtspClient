/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/19/11 8:34 AM
 * Copyright XpertSoftware. All rights reserved.
 */
package xpertss.mime.impl;

import xpertss.mime.HeaderParser;
import xpertss.mime.HeaderTokenizer;
import xpertss.mime.MalformedException;
import xpertss.mime.Parameter;
import xpertss.utils.Utils;

public abstract class ParameterizedHeaderParser extends HeaderParser {

   protected Parameter doParameter(String rawParam) throws MalformedException
   {
      HeaderTokenizer h = new HeaderTokenizer(rawParam, HeaderTokenizer.MIME);
      StringBuffer buf = new StringBuffer();
      String name = null, value = null;
      boolean quoted = false;
      while(true) {
         HeaderTokenizer.Token token = h.next();
         switch(token.getType()) {
            case HeaderTokenizer.Token.EOF:
               value = Utils.trimAndClear(buf);
               return (quoted) ? new QuotedParameter(name, value) : new SimpleParameter(name, value);
            case HeaderTokenizer.Token.LWS:
               buf.append(" ");  // collapse whitespace
               continue;
            case '=':
               if(name != null) throw new MalformedException("malformed parameter");
               name = Utils.trimAndClear(buf);
               continue;
            case HeaderTokenizer.Token.QUOTEDSTRING:
               quoted = true;
            default:
               buf.append(token.getValue());
         }
      }
   }


}
