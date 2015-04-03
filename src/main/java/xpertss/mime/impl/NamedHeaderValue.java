/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/18/11 9:45 PM
 * Copyright XpertSoftware. All rights reserved.
 */
package xpertss.mime.impl;

import xpertss.mime.HeaderValue;
import xpertss.mime.Parameter;
import xpertss.lang.Objects;

public class NamedHeaderValue implements HeaderValue {

   private String name;
   private String value;
   private Parameter[] params;

   public NamedHeaderValue(String name, String value, Parameter[] params)
   {
      this.name = Objects.notNull(name, "name");
      this.value = Objects.notNull(value, "value");
      this.params = Objects.notNull(params, "params");
   }

   public String getName()
   {
      return name;
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
         if(name.equalsIgnoreCase(param.getName())) return param;
      }
      return null;
   }

   public String toString()
   {
      StringBuffer buf = new StringBuffer(getName()).append("=").append(value);
      for(Parameter param : params) {
         buf.append("; ").append(param.toString());
      }
      return buf.toString();
   }

}
