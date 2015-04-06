/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/19/11 12:31 AM
 */
package xpertss.mime.impl;

import xpertss.mime.Parameter;
import xpertss.lang.Objects;

// TODO Must a quoted parameter be a name/value pair or could it be simply a value??
public class QuotedParameter implements Parameter {

   private final String name;
   private final String value;

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
