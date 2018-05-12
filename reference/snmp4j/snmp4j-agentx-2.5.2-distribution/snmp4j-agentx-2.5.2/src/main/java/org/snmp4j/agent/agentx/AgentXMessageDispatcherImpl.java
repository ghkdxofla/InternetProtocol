/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXMessageDispatcherImpl.java  
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

import java.nio.*;
import java.util.*;

import org.snmp4j.*;
import org.snmp4j.smi.*;
import java.io.IOException;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.mp.PduHandle;
import org.snmp4j.mp.PduHandleCallback;

public class AgentXMessageDispatcherImpl implements AgentXMessageDispatcher {

  private static final LogAdapter logger =
      LogFactory.getLogger(AgentXMessageDispatcherImpl.class);

  private List<TransportMapping> transportMappings = new ArrayList<TransportMapping>();
  private List<AgentXCommandListener> commandListener = new ArrayList<AgentXCommandListener>();
  private volatile int nextPacketID = 0;

  public AgentXMessageDispatcherImpl() {
  }

  public synchronized int getNextPacketID() {
    int nextID = ++nextPacketID;
    if (nextID <= 0) {
      nextID = nextPacketID = 1;
    }
    return nextID;
  }

  protected PduHandle createPduHandle() {
    return new PduHandle(getNextPacketID());
  }

  public synchronized void addTransportMapping(TransportMapping transport) {
    transportMappings.add(transport);
    transport.addTransportListener(this);
  }

  public Collection<TransportMapping> getTransportMappings() {
    return new ArrayList<TransportMapping>(transportMappings);
  }

  public void processMessage(TransportMapping sourceTransport,
                             Address incomingAddress,
                             ByteBuffer wholeMessage,
                             TransportStateReference tmStateReference) {
    try {
      AgentXPDU pdu = AgentXPDU.decode(wholeMessage);
      AgentXCommandEvent event =
          new AgentXCommandEvent(this, this,
                                 incomingAddress, sourceTransport, pdu,
                                 tmStateReference);
      fireCommandEvent(event);
    }
    catch (IOException ex) {
      if (logger.isDebugEnabled()) {
        ex.printStackTrace();
      }
      logger.warn(ex);
      if (ex instanceof AgentXParseException) {
        // exception can be handled on AgentX protocol level
        AgentXCommandEvent event =
            new AgentXCommandEvent(this, this,
                                   incomingAddress, sourceTransport,
                                   (AgentXParseException)ex,
                                   tmStateReference);
        fireCommandEvent(event);
      }
    }
  }

  public TransportMapping removeTransportMapping(TransportMapping transport) {
    if (transportMappings.remove(transport)) {
      transport.removeTransportListener(this);
      return transport;
    }
    return null;
  }

  public PduHandle send(TransportMapping transport,
                        Address address, AgentXPDU message,
                        PduHandleCallback<AgentXPDU> callback) throws IOException {
    PduHandle handle;
    if (message instanceof AgentXResponsePDU) {
      handle = new PduHandle(message.getPacketID());
    }
    else {
      handle = createPduHandle();
      message.setPacketID(handle.getTransactionID());
    }
    if (callback != null) {
      callback.pduHandleAssigned(handle, message);
    }
    if (transport != null) {
      sendPDU(address, message, transport);
      return handle;
    }
    else {
      for (TransportMapping t : transportMappings) {
        if (address.getClass().equals(t.getSupportedAddressClass())) {
          sendPDU(address, message, t);
          return handle;
        }
      }
    }
    return null;
  }

  private void sendPDU(Address address, AgentXPDU message,
                       TransportMapping transport) throws IOException {
    ByteBuffer buf =
        ByteBuffer.allocate(message.getPayloadLength() +
                            AgentXProtocol.HEADER_LENGTH);
    message.encode(buf);
    send(address, transport, buf, null);
  }

  @SuppressWarnings("unchecked")
  public void send(Address address, TransportMapping transport,
                   ByteBuffer message,
                   TransportStateReference tmStateReference) throws IOException {
    message.flip();
    byte[] bytes = new byte[message.limit()];
    message.get(bytes);
    transport.sendMessage(address, bytes, tmStateReference);
  }

  protected synchronized void fireCommandEvent(AgentXCommandEvent event) {
    for (AgentXCommandListener aCommandListener : commandListener) {
      aCommandListener.processCommand(event);
      if (event.isProcessed()) {
        return;
      }
    }
  }

  public synchronized void addCommandListener(AgentXCommandListener l) {
    commandListener.add(l);
  }

  public synchronized void removeCommandListener(AgentXCommandListener l) {
    commandListener.remove(l);
  }
}
