package xpertss.utils;

import xpertss.lang.Objects;
import xpertss.lang.Strings;
import xpertss.util.Config;
import xpertss.util.Platform;
import xpertss.util.Version;

import java.util.BitSet;

/**
 * The UserAgent class encapsulates the process creating a UserAgent string from
 * the underlying platform.
 * <p/>
 * A typical UserAgent string would look something like this:
 * <pre>
 *   MMP/1.0 (Windows/6.1; amd64; Java/1.7) XpertRTSP/1.0
 * </pre>
 * In the above example the actual user agent application is identified by MMP/1.0.
 * The contents in the parenthesis are the OS name/version followed by the CPU
 * Architecture followed by the version of Java the client is running on. Finally,
 * the last portion represents this RTSP library and version.
 * <p/>
 * This class allows the user some control over the user agent sent to the server
 * with each request. For full control you may set the User-Agent header yourself
 * for each call or you may extend this class and override the {@link #toString}
 * method..
 */
public class UserAgent {

   private final BitSet bitset = new BitSet(3);

   private String appName;
   private Version appVersion;

   /**
    * Accessible zero argument constructor for class wishing to extend and
    * override this implementation.
    */
   protected UserAgent() { }

   private UserAgent(String appName, Version appVersion)
   {
      this.appName = Strings.notEmpty(appName, "appName must be defined");
      this.appVersion = Objects.notNull(appVersion, "appVersion");
   }


   /**
    * Include the name of this RTSP library and its version in the user
    * agent header sent to the server with each request.
    */
   public void includeLibrary(boolean value)
   {
      bitset.set(0, !value);
   }

   /**
    * Returns {@code true} if the library portion is included in the user
    * agent string sent to the server with each request.
    */
   public boolean isLibraryIncluded()
   {
      return !bitset.get(0);
   }



   /**
    * Include the version of Java this library is running on in the user
    * agent header sent to the server with each request.
    */
   public void includeJava(boolean value)
   {
      bitset.set(1, !value);
   }

   /**
    * Returns {@code true} if the Java portion is included in the user
    * agent string sent to the server with each request.
    */
   public boolean isJavaIncluded()
   {
      return !bitset.get(1);
   }



   /**
    * Include the type of CPU this user agent is running on in the user
    * agent header sent to the server with each request.
    */
   public void includeCpu(boolean value)
   {
      bitset.set(2, !value);
   }

   /**
    * Returns {@code true} if the cpu portion is included in the user
    * agent string sent to the server with each request.
    */
   public boolean isCpuIncluded()
   {
      return !bitset.get(2);
   }


   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      builder.append(String.format("%s/%s", appName, appVersion));

      String osName = System.getProperty("os.name").split("\\s+")[0];
      Version osVersion = Platform.osVersion();

      builder.append(String.format(" (%s/%s", osName, osVersion));
      if(!bitset.get(2)) {
         String osArch = System.getProperty("os.arch");
         builder.append(String.format("; %s", osArch));
      }
      if(!bitset.get(1)) {
         Version vmVersion = Platform.javaVersion();
         builder.append(String.format("; Java/%s", vmVersion));
      }
      builder.append(")");
      if(!bitset.get(0)) {
         Config config = Config.load("version.properties", false);
         String libVersion = config.getProperty("version");
         builder.append(String.format(" XpertRTSP/%s", libVersion));
      }

      return builder.toString();
   }


   /**
    * Create a default UserAgent object with the specified application
    * name and version.
    *
    * @param appName The application name
    * @param appVersion The application version
    */
   public static UserAgent create(String appName, Version appVersion)
   {
      return new UserAgent(appName, appVersion);
   }

}
