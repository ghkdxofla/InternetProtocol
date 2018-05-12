/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentX.java  
  _## 
  _##  Copyright (C) 2005-2014  Frank Fock (SNMP4J.org)
  _##  
  _##  This program is free software; you can redistribute it and/or modify
  _##  it under the terms of the GNU General Public License version 2 as 
  _##  published by the Free Software Foundation.
  _##
  _##  This program is distributed in the hope that it will be useful,
  _##  but WITHOUT ANY WARRANTY; without even the implied warranty of
  _##  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  _##  GNU General Public License for more details.
  _##
  _##  You should have received a copy of the GNU General Public License
  _##  along with this program; if not, write to the Free Software
  _##  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
  _##  MA  02110-1301  USA
  _##  
  _##########################################################################*/

package org.snmp4j.agent.agentx;

import java.io.IOException;
import java.util.*;

import org.snmp4j.TransportMapping;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.PduHandle;
import org.snmp4j.mp.PduHandleCallback;

/**
 * The <code>AgentX</code> class implements the AgentX protocol that sends
 * and receives AgentX PDUs over one or more transport mappings.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class AgentX implements AgentXCommandListener {

  private static final LogAdapter logger = LogFactory.getLogger(AgentX.class);

  private AgentXMessageDispatcher messageDispatcher;

  /**
   * The <code>pendingRequests</code> table contains pending requests
   * accessed trough the key <code>PduHandle</code>
   */
  private Map<PduHandle, PendingRequest> pendingRequests =
      Collections.synchronizedMap(new HashMap<PduHandle, PendingRequest>(50));

  /**
   * The <code>asyncRequests</code> table contains pending requests
   * accessed trough the key userObject
   */
  private Map<AsyncRequestKey, PduHandle> asyncRequests =
      Collections.synchronizedMap(new HashMap<AsyncRequestKey, PduHandle>(50));

  // Timer for timing out pending requests
  private Timer timer = new Timer(true);
  private ArrayList<AgentXCommandListener> commandListeners;

  public AgentX(AgentXMessageDispatcher dispatcher) {
    this.messageDispatcher = dispatcher;
    this.messageDispatcher.addCommandListener(this);
  }

  public void addTransportMapping(TransportMapping transport) {
    messageDispatcher.addTransportMapping(transport);
    transport.addTransportListener(messageDispatcher);
  }

  public void removeTransportMapping(TransportMapping transport) {
    messageDispatcher.removeTransportMapping(transport);
    transport.removeTransportListener(messageDispatcher);
  }

  /**
   * Removes a <code>AgentXCommandListener</code> from this AgentX session.
   * @param listener
   *    a previously added <code>AgentXCommandListener</code> instance.
   */
  public synchronized void removeCommandResponder(AgentXCommandListener listener) {
    ArrayList<AgentXCommandListener> l = commandListeners;
    if (l != null &&
        l.contains(listener)) {
      l = new ArrayList<AgentXCommandListener>(l);
      l.remove(listener);
      commandListeners = l;
    }
  }

  /**
   * Adds a <code>AgentXCommandListener</code> to this AgentX session.
   * The command responder will then be informed about incoming SNMP PDUs of
   * any kind that are not related to any outstanding requests of this SNMP
   * session.
   *
   * @param listener
   *    the <code>AgentXCommandListener</code> instance to be added.
   */
  public synchronized void addCommandResponder(AgentXCommandListener listener) {
    ArrayList<AgentXCommandListener> l = (commandListeners == null) ?
        new ArrayList<AgentXCommandListener>(2) : commandListeners;
    if (!l.contains(listener)) {
      l = new ArrayList<AgentXCommandListener>(l);
      l.add(listener);
    }
    commandListeners = l;
  }

  /**
   * Sends a <code>AgentXPDU</code> to the given target and returns the response
   * synchronously.
   * @param pdu
   *    a <code>AgentXPDU</code> instance.
   * @param target
   *    the AgentXTarget instance representing the target AgentX entity
   *    where to send the <code>pdu</code>.
   * @param transport
   *    specifies the <code>TransportMapping</code> to be used when sending
   *    the PDU. If <code>transport</code> is <code>null</code>, the associated
   *    message dispatcher will try to determine the transport mapping by the
   *    <code>target</code>'s address.
   * @return
   *    the received response encapsulated in a <code>AgentXResponseEvent</code>
   *    instance. To obtain the received response <code>AgentXPDU</code> call
   *    {@link AgentXResponseEvent#getResponse()}. If the request timed out,
   *    that method will return <code>null</code>.
   * @throws IOException
   *    if the message could not be sent.
   */
  public AgentXResponseEvent send(AgentXPDU pdu, AgentXTarget target,
                                  TransportMapping transport) throws IOException {
    SyncResponseListener syncResponse = new SyncResponseListener();
    if (!pdu.isConfirmedPDU()) {
      sendMessage(pdu, target, transport, null);
      return null;
    }
    synchronized (syncResponse) {
      PendingRequest request =
          new PendingRequest(null, syncResponse, target, pdu, target);
      sendMessage(pdu, target, transport, request);
      try {
        while (syncResponse.response == null) {
          syncResponse.wait();
        }
      }
      catch (InterruptedException iex) {
        logger.warn(iex);
        // ignore
      }
      if (syncResponse.response != null && logger.isDebugEnabled()) {
        logger.debug("Received AgentX response: "+syncResponse.response.getResponse());
      }
    }
    return syncResponse.response;
  }

  /**
   * Sends a <code>AgentXPDU</code> to the given target and returns the response
   * asynchronously.
   * @param pdu
   *    a <code>AgentXPDU</code> instance.
   * @param target
   *    the AgentXTarget instance representing the target AgentX entity
   *    where to send the <code>pdu</code>.
   * @param transport
   *    specifies the <code>TransportMapping</code> to be used when sending
   *    the PDU. If <code>transport</code> is <code>null</code>, the associated
   *    message dispatcher will try to determine the transport mapping by the
   *    <code>target</code>'s address.
   * @param userHandle
   *    an arbitrary user handle which is transparently returned to the response
   *    listener.
   * @param listener
   *    the listener that should be informed about the reponse or timeout.
   * @throws IOException
   *    if the message could not be sent.
   */
  public void send(AgentXPDU pdu, AgentXTarget target,
                   TransportMapping transport,
                   Object userHandle,
                   AgentXResponseListener listener) throws IOException {
    if (listener != null) {
      PendingRequest request =
          new PendingRequest(null, listener, userHandle,
                             pdu, target);
      sendMessage(pdu, target, transport, request);
    }
    else {
      sendMessage(pdu, target, transport, null);
    }
  }

  /**
   * Actually sends a PDU to a target and returns a handle for the sent PDU.
   * @param pdu
   *    the <code>PDU</code> instance to be sent.
   * @param target
   *    a <code>AgentXTarget</code> instance denoting the target AgentX entity.
   * @param transport
   *    the (optional) transport mapping to be used to send the request.
   *    If <code>transport</code> is <code>null</code> a suitable transport
   *    mapping is determined from the <code>target</code> address.
   * @param pduHandleCallback
   *    an optional callback instance that is informed (if not
   *    <code>null</code>) about the newly assigned PduHandle just before the
   *    message is sent out.
   * @throws IOException
   *    if the transport fails to send the PDU or the if the message cannot
   *    be BER encoded.
   * @return PduHandle
   *    that uniquely identifies the sent PDU for further reference.
   */
  protected PduHandle sendMessage(AgentXPDU pdu, AgentXTarget target,
                                  TransportMapping transport,
                                  PduHandleCallback<AgentXPDU> pduHandleCallback)
      throws IOException
  {
    return messageDispatcher.send(transport, target.getAddress(), pdu,
                           pduHandleCallback);
  }

  public void processCommand(AgentXCommandEvent event) {
    AgentXPDU pdu = event.getCommand();
    PduHandle handle = new PduHandle(pdu.getPacketID());
    if (pdu.getType() == AgentXPDU.AGENTX_RESPONSE_PDU) {
      event.setProcessed(true);
      PendingRequest request;
      if (logger.isDebugEnabled()) {
        logger.debug("Removing pending request with handle " + handle);
      }
      request = pendingRequests.remove(handle);
      if (request == null) {
        if (logger.isWarnEnabled()) {
          logger.warn("Received response that cannot be matched to any " +
                      "outstanding request, address=" +
                      event.getPeerAddress() +
                      ", packetID=" + pdu.getPacketID());
        }
      }
      else {
        // return response
        request.finished = true;
        request.listener.onResponse(new AgentXResponseEvent(this,
            request.target,
            event.getPeerAddress(),
            request.pdu,
            (AgentXResponsePDU)pdu,
            request.userObject));
      }
    }
    else {
      if (logger.isDebugEnabled()) {
        logger.debug("Fire process PDU event: " + event.toString());
      }
      fireProcessPdu(event);
    }
  }

  /**
   * Fires a <code>CommandResponderEvent</code> event to inform listeners about
   * a received PDU. If a listener has marked the event as processed further
   * listeners will not be informed about the event.
   * @param event
   *    a <code>CommandResponderEvent</code>.
   */
  protected void fireProcessPdu(AgentXCommandEvent event) {
    if (commandListeners != null) {
      ArrayList<AgentXCommandListener> listeners = commandListeners;
      for (AgentXCommandListener listener : listeners) {
        listener.processCommand(event);
        // if event is marked as processed the event is not forwarded to
        // remaining listeners
        if (event.isProcessed()) {
          return;
        }
      }
    }
  }


  class AsyncRequestKey {
    private AgentXPDU request;
    private AgentXResponseListener listener;

    public AsyncRequestKey(AgentXPDU request, AgentXResponseListener listener) {
      this.request = request;
      this.listener = listener;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj argument;
     *   <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
      if (obj instanceof AsyncRequestKey) {
        AsyncRequestKey other = (AsyncRequestKey) obj;
        return (request.equals(other.request) && listener.equals(other.listener));
      }
      return false;
    }

    public int hashCode() {
      return request.hashCode();
    }
  }

  class PendingRequest extends TimerTask implements PduHandleCallback<AgentXPDU> {

    protected PduHandle key;
    protected AgentXResponseListener listener;
    protected Object userObject;

    protected AgentXPDU pdu;
    protected AgentXTarget target;

    private volatile boolean finished = false;


    public PendingRequest(PduHandle key,
                          AgentXResponseListener listener,
                          Object userObject,
                          AgentXPDU pdu,
                          AgentXTarget target) {
      this.key = key;
      this.userObject = userObject;
      this.listener = listener;
      this.pdu = pdu;
      this.target = target;
    }

    protected void registerRequest(PduHandle handle) {
    }

    public synchronized void run() {
      pendingRequests.remove(key);
      // request timed out
      if (!finished) {
        if (logger.isDebugEnabled()) {
          logger.debug("AgentX request timed out: " + key.getTransactionID());
        }
        finished = true;
        if (listener != null) {
          listener.onResponse(new AgentXResponseEvent(AgentX.this, target, null,
              pdu, null, userObject));
        }
      }
    }

    public synchronized boolean setFinished() {
      boolean currentState = finished;
      this.finished = true;
      return currentState;
    }

    public synchronized void pduHandleAssigned(PduHandle handle, AgentXPDU pdu) {
      if (key == null) {
        key = handle;
        if (logger.isDebugEnabled()) {
          logger.debug("New pending request "+pdu+" with handle " + handle);
        }
        registerRequest(handle);
        pendingRequests.put(handle, this);
        long delay = target.getTimeout();
        if (delay < 1000) {
          delay = AgentXProtocol.DEFAULT_TIMEOUT_SECONDS*1000;
        }
        timer.schedule(this, delay);
      }
    }
  }

  class AsyncPendingRequest extends PendingRequest {

    public AsyncPendingRequest(PduHandle key,
                               AgentXResponseListener listener,
                               Object userObject,
                               AgentXPDU pdu,
                               AgentXTarget target) {
      super(key, listener, userObject, pdu, target);
    }

    protected void registerRequest(PduHandle handle) {
      asyncRequests.put(new AsyncRequestKey(pdu, listener), handle);
    }
  }

  class SyncResponseListener implements AgentXResponseListener {

    private AgentXResponseEvent response = null;

    public synchronized void onResponse(AgentXResponseEvent event) {
      this.response = event;
      this.notify();
    }

    public AgentXResponseEvent getResponse() {
      return response;
    }

  }

  public AgentXMessageDispatcher getMessageDispatcher() {
    return messageDispatcher;
  }
}
