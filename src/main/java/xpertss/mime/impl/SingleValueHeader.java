/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/18/11 11:53 PM
 * Copyright XpertSoftware. All rights reserved.
 */
package xpertss.mime.impl;

import xpertss.mime.Header;
import xpertss.mime.HeaderValue;
import xpertss.lang.Objects;

public class SingleValueHeader implements Header {

   private Type type;
   private String name;
   private HeaderValue[] value;

   public SingleValueHeader(String name, Type type, HeaderValue value)
   {
      this.name = Objects.notNull(name, "name");
      this.type = Objects.notNull(type, "type");
      this.value = new HeaderValue[] { value };
   }

   public String getName()
   {
      return name;
   }

   public String getValue()
   {
      return value[0].toString();
   }

   public Type getType()
   {
      return type;
   }

   public int size()
   {
      return 1;
   }

   public HeaderValue getValue(int index)
   {
      return value[index];
   }

   public HeaderValue getValue(String name)
   {
      if(name == null) throw new NullPointerException("name may not be null");
      return null;
   }

   public String toString()
   {
      StringBuffer buf = new StringBuffer(getName());
      buf.append(": ").append(getValue());
      return buf.toString();
   }

}
