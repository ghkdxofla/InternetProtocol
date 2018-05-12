/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AbstractAgentXPending.java  
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

package org.snmp4j.agent.agentx.master;

import org.snmp4j.agent.agentx.AgentXProtocol;
import org.snmp4j.agent.request.SnmpRequest;
import java.util.Date;

public abstract class AbstractAgentXPending implements AgentXPending {

  protected AgentXRegEntry registration;
  private SnmpRequest request;
  private boolean pending = true;
  private long timestamp = 0L;
  private int timeout = AgentXProtocol.DEFAULT_TIMEOUT_SECONDS;

  public AbstractAgentXPending(AgentXRegEntry registration,
                               SnmpRequest request) {
    this.registration = registration;
    this.request = request;
  }

  public void updateTimestamp() {
    timestamp = System.currentTimeMillis();
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimeout(int timeoutSeconds) {
    this.timeout = timeoutSeconds;
  }

  public void setPending(boolean pending) {
    this.pending = pending;
  }

  public int getTimeout() {
    return timeout;
  }

  public AgentXRegEntry getRegistration() {
    return registration;
  }

  public boolean isPending() {
    return pending;
  }

  public SnmpRequest getRequest() {
    return request;
  }

  public String toString() {
    return getClass().getName()+"["+toStringMembers()+"]";
  }

  protected final String toStringMembers() {
    return "registration="+registration+",request="+request+",pending="+
        pending+",timestamp="+new Date(timestamp)+",timeout="+timeout;
  }
}
