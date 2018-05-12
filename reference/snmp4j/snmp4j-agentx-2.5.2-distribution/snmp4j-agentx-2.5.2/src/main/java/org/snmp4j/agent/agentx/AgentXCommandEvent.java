/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXCommandEvent.java  
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

import java.util.EventObject;

import org.snmp4j.TransportMapping;
import org.snmp4j.TransportStateReference;
import org.snmp4j.smi.Address;

public class AgentXCommandEvent extends EventObject {

  private Address peerAddress;
  private TransportMapping peerTransport;
  private AgentXPDU command;
  private AgentXMessageDispatcher dispatcher;
  private boolean processed;
  private AgentXParseException exception;
  private TransportStateReference tmStateReference;

  public AgentXCommandEvent(Object source,
                            AgentXMessageDispatcher dispatcher,
                            Address peerAddress,
                            TransportMapping peerTransport,
                            AgentXPDU command,
                            TransportStateReference tmStateReference) {
    super(source);
    this.dispatcher = dispatcher;
    this.peerTransport = peerTransport;
    this.peerAddress = peerAddress;
    this.command = command;
    this.tmStateReference = tmStateReference;
  }

  public AgentXCommandEvent(Object source,
                            AgentXMessageDispatcher dispatcher,
                            Address peerAddress,
                            TransportMapping peerTransport,
                            AgentXParseException exception,
                            TransportStateReference tmStateReference) {
    super(source);
    this.dispatcher = dispatcher;
    this.peerTransport = peerTransport;
    this.peerAddress = peerAddress;
    this.exception = exception;
    this.tmStateReference = tmStateReference;
  }

  public AgentXPDU getCommand() {
    return command;
  }

  public Address getPeerAddress() {
    return peerAddress;
  }

  public TransportMapping getPeerTransport() {
    return peerTransport;
  }

  public AgentXMessageDispatcher getDispatcher() {
    return dispatcher;
  }

  public boolean isProcessed() {
    return processed;
  }

  public boolean isException() {
    return (exception != null);
  }

  public AgentXParseException getException() {
    return exception;
  }

  public void setProcessed(boolean done) {
    this.processed = done;
  }

  public TransportStateReference getTmStateReference() {
    return tmStateReference;
  }

  @Override
  public String toString() {
    return "AgentXCommandEvent[" +
        "peerAddress=" + peerAddress +
        ", peerTransport=" + peerTransport +
        ", command=" + command +
        ", dispatcher=" + dispatcher +
        ", processed=" + processed +
        ", exception=" + exception +
        ", tmStateReference=" + tmStateReference +
        ']';
  }
}
