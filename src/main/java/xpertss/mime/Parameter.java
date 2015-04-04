/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/15/11 1:15 PM
 */
package xpertss.mime;

/**
 * TODO Java docs should be consume here about named parameters
 */
public interface Parameter {


   /**
    * Returns the parameter name as a String.
    */
   public String getName();

   /**
    * Returns the parameter value as a String or null if no value was defined.
    */
   public String getValue();


   /**
    * Returns this parameter as a fully formatted string.
    */
   public String toString();

}
