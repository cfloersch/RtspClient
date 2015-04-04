/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/23/11 1:39 PM
 */
package xpertss.nio;

/**
 */
public class NioStats {

   private long time = System.currentTimeMillis();
   private long count;

   public void record(long amount)
   {
      if(amount > 0) {
         time = System.currentTimeMillis();
         count += amount;
      }
   }

   public long getCount()
   {
      return count;
   }

   public long getTime()
   {
      return time;
   }

}
