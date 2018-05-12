/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXPeer.java  
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

import java.io.Serializable;

import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;

public class AgentXPeer implements Serializable {

  private TransportMapping transport;
  private Address address;
  private long connectionTime;
  private int timeout;
  private Object id;
  private volatile boolean closing;
  private volatile int parseErrors = 0;

  public AgentXPeer(TransportMapping transport, Address address) {
    this.transport = transport;
    this.address = address;
  }

  public boolean isClosing() {
    return closing;
  }

  public long getConnectionTime() {
    return connectionTime;
  }

  public Object getId() {
    return id;
  }

  public int getTimeout() {
    return timeout;
  }

  public TransportMapping getTransport() {
    return transport;
  }

  public Address getAddress() {
    return address;
  }

  public int getParseErrors() {
    return parseErrors;
  }

  public void incParseErrors() {
    this.parseErrors++;
  }

  public void setClosing(boolean closing) {
    this.closing = closing;
  }

  public void setConnectionTime(long connectionTime) {
    this.connectionTime = connectionTime;
  }

  public void setId(Object id) {
    this.id = id;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public String toString() {
    return getClass().getName()+"[transport="+transport+",address="+address+
        ",connectionTime="+connectionTime+",timeout="+timeout+
        ",id="+id+",closing="+closing+",parseErrors="+parseErrors+"]";
  }

}
