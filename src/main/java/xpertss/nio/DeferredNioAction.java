/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/26/11 2:19 PM
 * Copyright XpertSoftware. All rights reserved.
 */
package xpertss.nio;

/**
 * An extension to NioAction who's execution will be deferred until the next
 * loop of the reactor thread. By default execute calls into the reactor will
 * execute immediately if the calling thread is the reactor thread. This can
 * sometimes be undesirable as you want some other action to occur first. By
 * implementing the DeferredNioAction interface the action will be enqueued
 * for the next loop allowing all other operations to proceed in the meantime.
 */
public interface DeferredNioAction extends NioAction  {
}
