/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/21/11 11:09 AM
 * Copyright Manheim online
 */
package xpertss.mime;

public class MalformedException extends RuntimeException {

   public MalformedException()
   {
   }

   public MalformedException(String message)
   {
      super(message);
   }

   public MalformedException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public MalformedException(Throwable cause)
   {
      super(cause);
   }
}
