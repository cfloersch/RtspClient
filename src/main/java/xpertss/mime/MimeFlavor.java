/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 4/7/11 12:19 PM
 * Copyright Manheim online
 */
package xpertss.mime;

import org.w3c.dom.Document;
import xpertss.lang.Objects;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

public class MimeFlavor<T> {

   public static final MimeFlavor<String> stringFlavor = new MimeFlavor<String>(String.class);
   public static final MimeFlavor<Reader> textFlavor = new MimeFlavor<Reader>(Reader.class);
   public static final MimeFlavor<Document> domFlavor = new MimeFlavor<Document>(Document.class);
   public static final MimeFlavor<String> jsonFlavor = new MimeFlavor<String>(String.class); // TODO Is there a better type (application/json)
   public static final MimeFlavor<InputStream> byteFlavor = new MimeFlavor<InputStream>(InputStream.class);
   public static final MimeFlavor<File> fileFlavor = new MimeFlavor<File>(File.class);


   private Class<T> cls;

   public MimeFlavor(Class<T> cls)
   {
      this.cls = Objects.notNull(cls, "cls");
   }

   public Class<T> getRepresentation()
   {
      return cls;
   }


   public boolean equals(Object o)
   {
      if(o instanceof MimeFlavor) {
         MimeFlavor f = (MimeFlavor) o;
         return cls.equals(f);
      }
      return false;
   }

   public int hashCode()
   {
      return cls.hashCode();
   }

   public String toString()
   {
      return "MimeFlavor<" + cls.getSimpleName() + ">";
   }

}
