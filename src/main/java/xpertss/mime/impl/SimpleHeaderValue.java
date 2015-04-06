/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 9:30 PM
 */
package xpertss.mime.impl;

import xpertss.lang.Strings;
import xpertss.mime.HeaderValue;
import xpertss.mime.Parameter;
import xpertss.lang.Objects;

public class SimpleHeaderValue implements HeaderValue {

   private final Parameter[] params;
   private final String value;

   public SimpleHeaderValue(String value, Parameter[] params)
   {
      this.value = Objects.notNull(value, "value");
      this.params = Objects.notNull(params, "params");
   }


   public String getName()
   {
      return null;
   }

   public String getValue()
   {
      if(value.indexOf('"') == 0 && value.lastIndexOf('"') == value.length() - 1) {
         return value.substring(1, value.length() - 1);
      }
      return value;
   }

   public int size()
   {
      return params.length;
   }

   public Parameter getParameter(int index)
   {
      return params[index];
   }

   public Parameter getParameter(String name)
   {
      for(Parameter param : params) {
         if(Strings.equalIgnoreCase(name, param.getName())) return param;
      }
      return null;
   }

   public String toString()
   {
      StringBuilder buf = new StringBuilder(value);
      for(Parameter param : params) {
         buf.append("; ").append(param.toString());
      }
      return buf.toString();
   }



}
