/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/15/11 1:15 PM
 */
package xpertss.mime;

/**
 * This class holds a MIME parameter (attribute-value pair) as defined in RFC-2231.
 * <p/>
 * Parameters may be either simple or named. A simple parameter will have a {@code null}
 * name.
 */
public interface Parameter {


   /**
    * Returns the parameter name as a String or {@code null} if this is a simple
    * parameter.
    */
   public String getName();

   /**
    * Returns the parameter value as a String.
    */
   public String getValue();


   /**
    * Returns this parameter as a fully formatted string.
    */
   public String toString();

}
