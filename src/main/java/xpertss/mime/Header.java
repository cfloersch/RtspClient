/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/15/11 1:08 PM
 */
package xpertss.mime;

/**
 * Represents a Mime header field.
 * <p/>
 * The Mime header fields follow the format defined by Section 3.1 of RFC 822.
 * Each header field consists of a name followed by a colon (":") and the field
 * value. Field names are case-insensitive. The field value MAY be preceded by
 * any amount of LWS, though a single SP is preferred.
 *
 *<pre>
 *     message-header = field-name ":" [ field-value ]
 *     field-name     = token
 *     field-value    = *( field-content | LWS )
 *     field-content  = &lt;the OCTETs making up the field-value
 *                      and consisting of either *TEXT or combinations
 *                      of token, separators, and quoted-string&gt;
 *</pre><p>
 * A Header may represent a single value or multiple values. Each value may be
 * either named or simple. Additionally, each value may or may not have associated
 * parameters which themselves may be named or not. This interface attempts to
 * define a means to access all of the various header parts.
 */
public interface Header {

   /**
    * Http defines four types of headers. All headers not specifically
    * defined to be general, request, or response are treated as entity
    * headers.
    */
   public enum Type {
      /**
       * A general header is one that may be included in either the request or
       * response and is applied to the overall message itself.
       */
      General,

      /**
       * A request header is applied only to impl requests and applies to the
       * message overall.
       */
      Request,

      /**
       * A response header is applied only to impl responses and applies to the
       * message overall.
       */
      Response,

      /**
       * An entity header defines the entity contained within the message.
       */
      Entity,

      /**
       * A raw header is any header for which a defined parser was not found.
       * These are typically non-standard headers and will always return an
       * unparsed raw string when getValue is called.
       */
      Raw
   }


   /**
    * Returns the header name as a String.
    */
   public String getName();

   /**
    * Returns a fully formatted value line including all values and their
    * associated parameters. This method  provides an easier means of access
    * to simple values.
    */
   public String getValue();

   /**
    * Returns the header type this header represents.
    */
   public Type getType();


   /**
    * Returns the number of values this header contains.
    */
   public int size();

   /**
    * Returns the header value at the specified index.
    */
   public HeaderValue getValue(int index);

   /**
    * Returns the specified named header value if it exists.
    */
   public HeaderValue getValue(String name);


   /**
    * Return a formatted string representation of this header and its values
    * and parameters.
    */
   public String toString();


}
