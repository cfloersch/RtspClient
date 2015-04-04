/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 3/18/11 9:42 PM
 */
package xpertss.mime.impl;

import xpertss.lang.Objects;
import xpertss.mime.Parameter;

// TODO At present I am supporting simple parameters as values. I treat them as
// unnamed parameters. This may have negative side effects on the HeaderValue
// that contains them. It might be better to name the parameter the value and
// leave the value null or maybe both name and value should be the value??

// It would appear that a null name does not have a negative impact on the
// HeaderValue. It will index right over them when accessing them by name.
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
