/**
 * Copyright XpertSoftware All rights reserved.
 *
 * Date: 4/3/2015
 */
package xpertss.net;

import java.net.SocketOption;

/**
 * Defines a set socket options useful for sessions that support SSL.
 * <p/>
 * The {@link SocketOption#name name} of each socket option defined by this class
 * is its field name.
 */
public final class SSLSocketOptions {

   private SSLSocketOptions() { }



   /**
    * Enable or disable client authentication requests.
    * <p>
    * The value of this socket option is an SslClientAuth that dictates whether a server
    * socket is required to request client authentication during the handshake.
    * <p>
    * This is only valid on SSL enabled acceptors.
    */
   public static final SocketOption<SslClientAuth> SSL_CLIENT_AUTH =
      new TlsSocketOption<SslClientAuth>("SSL_CLIENT_AUTH", SslClientAuth.class);

   /**
    * Set the cipher suites SSL sockets will support during negotiation.
    * <p>
    * The value of this socket option is a String[] that specifies the SSL cipher suites
    * the socket will accept during handshaking. This list must be supported by the
    * SSLContext that was configured for use. Only cipher suites in this list will be
    * negotiated during the handshake. Peers that do not support any of the cipher
    * suites in this list will not be able to establish a connection.
    * <p>
    * This is only valid on SSL enabled connectors and acceptors.
    */
   public static final SocketOption<String[]> SSL_CIPHER_SUITES =
      new TlsSocketOption<String[]>("SSL_CIPHER_SUITES", String[].class);

   /**
    * Set the protocols SSL sockets will support during negotiation.
    * <p>
    * The value of this socket option is a String[] that specifies the protocols the
    * socket will accept. This list must be supported by the SSLContext that was
    * configured for use. Protocols in this case means SSLv3, TLSv1, SSLv2 as examples.
    * Peers that do not support any of the protocols in this list will not be able to
    * establish a connection.
    * <p>
    * This is only valid on SSL enabled connectors and acceptors.
    */
   public static final SocketOption<String[]> SSL_PROTOCOLS =
      new TlsSocketOption<String[]>("SSL_PROTOCOLS", String[].class);




   private static class TlsSocketOption<T> implements java.net.SocketOption<T> {
      private final String name;
      private final Class<T> type;
      TlsSocketOption(String name, Class<T> type) {
         this.name = name;
         this.type = type;
      }
      @Override public String name() { return name; }
      @Override public Class<T> type() { return type; }
      @Override public String toString() { return name; }
   }


}
