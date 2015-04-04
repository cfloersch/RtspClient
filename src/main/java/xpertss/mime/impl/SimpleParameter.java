/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 9:42 PM
 */
package xpertss.mime.impl;

import xpertss.lang.Objects;
import xpertss.mime.Parameter;

public class SimpleParameter implements Parameter {

   private String name;
   private String value;

   public SimpleParameter(String name, String value)
   {
      this.name = name;
      this.value = Objects.notNull(value, "value");
   }


   public String getName()
   {
      return name;
   }

   public String getValue()
   {
      return value;
   }

   public String toString()
   {
      return (name == null) ? value : name + "=" + value;
   }


}
