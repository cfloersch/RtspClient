/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 11:55 PM
 */
package xpertss.mime.impl;

import xpertss.mime.Header;
import xpertss.mime.HeaderValue;
import xpertss.lang.Objects;

public class ComplexValueHeader implements Header {

   private String name;
   private Type type;
   private HeaderValue[] values;

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
      StringBuffer buf = new StringBuffer();
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
      if(name == null) throw new NullPointerException("name may not be null");
      for(HeaderValue value : values) if(name.equalsIgnoreCase(value.getName())) return value;
      return null;
   }

   public String toString()
   {
      StringBuffer buf = new StringBuffer(getName());
      buf.append(": ").append(getValue());
      return buf.toString();
   }

}
