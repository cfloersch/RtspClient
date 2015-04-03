/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/17/11 10:19 AM
 * Copyright Manheim online
 */
package xpertss.mime;

/**
 * Many HTTP headers support the concept of multiple values separated by commas.
 * A HeaderValue represents just one of those values along with its associated
 * parameters.
 * <p>
 * Example of simple multi-valued headers:
 * <p><pre>
 *    If-Match: "xyzzy", "r2d2xxxx", "c3piozzzz"
 *    Upgrade: HTTP/2.0, SHTTP/1.3, IRC/6.9, RTA/x11
 * </pre><p>
 * An individual header value may be simple as shown above or it may be a named
 * value as in the following example:
 * <p><pre>
 *    Cache-Control: max-age=0
 * </pre><p>
 * Additionally, a header may combine simple and named values as follows:
 * <p><pre>
 *    Cache-Control: private, max-age=0
 * </pre><p>
 * In the example the simple header value will have a null name field and the
 * value field will have the value private. The named value will have a name
 * field of max-age and a value field of 0.
 *
 */
public interface HeaderValue {


   /**
    * Ths will return the value's name or null if it is not a named value.
    */
   public String getName();

   /**
    * This will return the value's value.
    */
   public String getValue();





   /**
    * Returns the number of parameters this header value contains.
    */
   public int size();

   /**
    * Returns the parameter at the specified index.
    */
   public Parameter getParameter(int index);

   /**
    * Returns the specified named parameter if it exists.
    */
   public Parameter getParameter(String name);



   /**
    * Return a formatted string representation of this header value and its
    * parameters.
    */
   public String toString();

}
