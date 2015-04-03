/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/19/11 12:31 AM
 * Copyright XpertSoftware. All rights reserved.
 */
package xpertss.mime.impl;

import xpertss.mime.Parameter;
import xpertss.lang.Objects;

public class QuotedParameter implements Parameter {

   private String name;
   private String value;

   public QuotedParameter(String name, String value)
   {
      this.name = Objects.notNull(name, "name");
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
      return name + "=\"" + value + "\"";
   }


}
