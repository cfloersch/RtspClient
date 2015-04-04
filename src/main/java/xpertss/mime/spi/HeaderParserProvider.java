/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 11:16 AM
 */
package xpertss.mime.spi;

import xpertss.mime.HeaderParser;

public interface HeaderParserProvider {

   /**
    * Return a HeaderParser implementation for the named header
    * or null if this provider does not support the given header.
    *
    * @param name The header name for which a parser is desired
    * @return A parser capable of parsing the named header or null
    */
   public HeaderParser create(String name);

}
