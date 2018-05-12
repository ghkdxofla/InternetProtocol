/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXSession.java  
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
import java.nio.ByteOrder;
import java.util.List;

public class AgentXSession implements Serializable {

  private int sessionID;
  private ByteOrder byteOrder;
  private byte timeout = AgentXProtocol.DEFAULT_TIMEOUT_SECONDS;
  private volatile int consecutiveTimeouts;
  private AgentXPeer peer;
  private List agentCaps;
  private volatile boolean closed;

  public AgentXSession(int sessionID) {
    this.sessionID = sessionID;
    this.consecutiveTimeouts = 0;
  }

  public List getAgentCaps() {
    return agentCaps;
  }

  public ByteOrder getByteOrder() {
    return byteOrder;
  }

  public AgentXPeer getPeer() {
    return peer;
  }

  public int getSessionID() {
    return sessionID;
  }

  public byte getTimeout() {
    return timeout;
  }

  public void clearConsecutiveTimeouts() {
    consecutiveTimeouts = 0;
  }

  public synchronized void incConsecutiveTimeouts() {
    consecutiveTimeouts++;
  }

  public int getConsecutiveTimeouts() {
    return consecutiveTimeouts;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setAgentCaps(List agentCaps) {
    this.agentCaps = agentCaps;
  }

  public void setByteOrder(ByteOrder byteOrder) {
    this.byteOrder = byteOrder;
  }

  public void setPeer(AgentXPeer peer) {
    this.peer = peer;
  }

  public void setSessionID(int sessionID) {
    this.sessionID = sessionID;
  }

  public void setTimeout(byte timeout) {
    this.timeout = timeout;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

  public AgentXTarget createAgentXTarget() {
    AgentXTarget target =
        new AgentXTarget(getPeer().getAddress(),
                         getTimeout()*1000);
    return target;
  }

  public boolean equals(Object obj) {
    if (obj instanceof AgentXSession) {
      return ((AgentXSession)obj).sessionID == sessionID;
    }
    return false;
  }

  public int hashCode() {
    return sessionID;
  }

  public String toString() {
    return getClass().getName()+"[peer="+peer+",sessionID="+sessionID+
        ",byteOrder="+byteOrder+",timeout="+timeout+
        ",consecutiveTimeouts="+consecutiveTimeouts+
        ",agentCaps="+agentCaps+",closed="+closed+"]";
  }

}
