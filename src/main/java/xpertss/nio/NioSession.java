package xpertss.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.util.Set;

/**
 */
public interface NioSession {





   /**
    * Get the current ready status of this session.
    */
   public ReadyState getReadyState();




   /**
    * Returns the socket address of the remote peer.
    *
    * @return The socket address of the remote peer.
    */
   public SocketAddress getRemoteAddress();

   /**
    * Returns the socket address of local machine which is associated with this
    * session.
    *
    * @return The local socket address for this session.
    */
   public SocketAddress getLocalAddress();

   /**
    * This will return the listen address that established this IOSession if it
    * is a server side session. Otherwise, if it is a client side session this
    * will return the same value as {@link #getRemoteAddress()}.
    *
    * @return The service address associated with this session.
    */
   public SocketAddress getServiceAddress();






   /**
    * Sets the value of a socket option.
    *
    * @param   name The socket option
    * @param   value The value of the socket option. A value of {@code null}
    *                may be a valid value for some socket options.
    * @return  This session
    *
    * @throws  UnsupportedOperationException If the socket option is not
    *             supported by this session
    * @throws  IllegalArgumentException If the value is not a valid value
    *             for this socket option
    * @throws IOException If an I/O error occurs
    * @see java.net.StandardSocketOptions
    * @see xpertss.net.OptionalSocketOptions
    * @see xpertss.net.SSLSocketOptions
    */
   <T> NioSession setOption(SocketOption<T> name, T value) throws IOException;

   /**
    * Returns the value of a socket option.
    *
    * @param   name The socket option
    * @return  The value of the socket option. A value of {@code null} may
    *          be a valid value for some socket options.
    * @throws  UnsupportedOperationException If the socket option is not
    *          supported by this channel
    * @throws  IOException If an I/O error occurs
    * @see java.net.StandardSocketOptions
    * @see xpertss.net.OptionalSocketOptions
    * @see xpertss.net.SSLSocketOptions
    */
   <T> T getOption(SocketOption<T> name) throws IOException;

   /**
    * Returns a set of the socket options supported by this channel.
    * <p/>
    * This method will continue to return the set of options even after
    * the session has been closed.
    *
    * @return  A set of the socket options supported by this session
    */
   Set<SocketOption<?>> supportedOptions();









   /**
    * Returns the time in millis when this session was created. The time is
    * always measured as the number of milliseconds since midnight, January
    * 1, 1970 UTC.
    *
    * @return The time in millis when this session was created.
    */
   public long getCreationTime();

   /**
    * Returns the time in millis when the last I/O operation occurred. The
    * time is always measured as the number of milliseconds since midnight,
    * January 1, 1970 UTC.
    *
    * @return The time in millis when the last I/O operation occured.
    */
   public long getLastIoTime();

   /**
    * Returns the time in millis when the last read operation occurred. The
    * time is always measured as the number of milliseconds since midnight,
    * January 1, 1970 UTC.
    *
    * @return The time in millis when the last read operation occurred
    */
   public long getLastReadTime();

   /**
    * Returns the time in millis when the lst write operation occurred. The
    * time is always measured as the number of milliseconds since midnight,
    * January 1, 1970 UTC.
    *
    * @return The time in millis when the lst write operation occurred
    */
   public long getLastWriteTime();





   /**
    * Return the number of bytes written to this session.
    */
   public long getBytesWritten();

   /**
    * Return the number of bytes read from this session.
    */
   public long getBytesRead();





   /**
    * Returns the value of user-defined attribute of this session.
    *
    * @param key the key of the attribute
    * @return <tt>null</tt> if there is no attribute with the specified key
    */
   public Object getAttribute(String key);

   /**
    * Sets a user-defined attribute.
    *
    * @param key the key of the attribute
    * @param value the value of the attribute
    */
   public void setAttribute(String key, Object value);

   /**
    * Removes a user-defined attribute with the specified key.
    *
    * @param key The key identifying the attribute to remove
    */
   public void removeAttribute(String key);

   /**
    * Returns <tt>true</tt> if this session contains the attribute with
    * the specified <tt>key</tt>.
    *
    * @param key The key identifying the attribute to check existance of
    * @return true if the named attribute exists in this session
    */
   public boolean hasAttribute(String key);

   /**
    * Returns the set of keys of all user-defined attributes.
    *
    * @return A set of attribute keys currently defined
    */
   public Set<String> getAttributeKeys();




}
