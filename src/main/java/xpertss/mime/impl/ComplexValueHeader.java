/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 11:55 PM
 */
package xpertss.mime.impl;

import xpertss.lang.Strings;
import xpertss.mime.Header;
import xpertss.mime.HeaderValue;
import xpertss.lang.Objects;

public class ComplexValueHeader implements Header {

   private final String name;
   private final Type type;
   private final HeaderValue[] values;

   public ComplexValueHeader(String name, Type type, HeaderValue[] values)
   {
      this.name = Objects.notNull(name, "name");
      this.type = Objects.notNull(type, "type");
      this.values = Objects.notNull(values, "values");
   }

   public String getName()
   {
      return name;
   }

   public String getValue()
   {
      StringBuilder buf = new StringBuilder();
      for(HeaderValue value : values) {
         if(buf.length() > 0) buf.append(", ");
         buf.append(value.toString());
      }
      return buf.toString();
   }

   public Type getType()
   {
      return type;
   }

   public int size()
   {
      return values.length;
   }

   public HeaderValue getValue(int index)
   {
      return values[index];
   }

   public HeaderValue getValue(String name)
   {
      for(HeaderValue value : values)
         if(Strings.equalIgnoreCase(name, value.getName())) return value;
      return null;
   }

   public String toString()
   {
      return String.format("%s: %s", getName(), getValue());
   }

}
