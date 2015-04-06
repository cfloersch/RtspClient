/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 11:53 PM
 */
package xpertss.mime.impl;

import xpertss.mime.Header;
import xpertss.mime.HeaderValue;
import xpertss.lang.Objects;

public class SingleValueHeader implements Header {

   private final Type type;
   private final String name;
   private final HeaderValue[] value;

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
      return null;
   }

   public String toString()
   {
      return String.format("%s: %s", getName(), getValue());
   }

}
