/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 2/26/11 11:25 AM
 * Copyright XpertSoftware. All rights reserved.
 */
package xpertss.net;

/**
 * An enumeration of SSL Client Authentication options.
 */
public enum SslClientAuth {

   /**
    * Request client authentication but proceed if it is not provided.
    */
   Want,

   /**
    * Request client authentication and terminate negotiation if it is not
    * provided.
    */
   Need,

   /**
    * Do not request client authentication.
    */
   None

}
