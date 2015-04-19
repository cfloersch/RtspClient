/**
 * Copyright 2015 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 4/17/2015
 */
package xpertss.utils;

import xpertss.threads.Threads;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This class will wrap a given class and proxy calls to it catching any runtime
 * exceptions that may be thrown. Those exceptions will be sent to the calling
 * thread's uncaught exception handler and null will be returned.
 * <p/>
 * This is principally to be used with classes who's members do not return values
 * such as event listeners.
 *
 * @param <T>
 */
public class SafeProxy<T> implements InvocationHandler {

   /**
    * Construct an instance of the SafeDispatcher compatible with the given listener class.
    */
   public static <T> T newInstance(Class<T> proxiedClass, T proxied)
   {
      return proxiedClass.cast(Proxy.newProxyInstance(proxiedClass.getClassLoader(),
                                                      new Class[] { proxiedClass },
                                                      new SafeProxy<T>(proxied)));
   }


   private final T proxied;


   private SafeProxy(T proxied)
   {
      this.proxied = proxied;
   }


   @Override
   public Object invoke(Object proxy, final Method method, final Object[] args)
      throws Throwable
   {
      if ("equals".equals(method.getName())) {
         return (args[0] == proxy);
      } else if("hashCode".equals(method.getName())) {
         return hashCode();
      } else if("toString".equals(method.getName())) {
         return toString();
      } else {
         try {
            return method.invoke(proxied, args);
         } catch(InvocationTargetException e) {
            Threads.report(e.getTargetException());
         } catch(Throwable t) {
            Threads.report(t);
         }
         return null;
      }
   }


   @Override
   public String toString()
   {
      return "SafeProxy<" + proxied.toString() + ">";
   }

}
