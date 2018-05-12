/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXPending.java  
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

import org.snmp4j.agent.agentx.AgentXPDU;
import org.snmp4j.agent.agentx.subagent.AgentXRequest;
import org.snmp4j.agent.request.SnmpSubRequest;
import org.snmp4j.agent.request.SubRequestIterator;
import org.snmp4j.agent.request.SnmpRequest;

/**
 * The <code>AgentXPending</code> interface is implemented
 * by classes holding sub-agent AgentX request state information.
 * @author Frank Fock
 * @version 1.0
 */
public interface AgentXPending {

  AgentXPDU getAgentXPDU();

  AgentXRegEntry getRegistration();

  AgentXMasterSession getSession();

  boolean isPending();

  void setPending(boolean pending);

  SnmpRequest getRequest();

  SubRequestIterator<SnmpSubRequest> getReferences();

  /**
   * Set the timestamp to the current time (in milliseconds).
   */
  void updateTimestamp();

  /**
   * Gets the timestamp (in milliseconds) when {@link #updateTimestamp()}
   * had been called last.
   * @return
   *    {@link System#currentTimeMillis()} when {@link #updateTimestamp()}
   *    had been called or zero if it had not been called yet.
   */
  long getTimestamp();

  void setTimeout(int timeoutSeconds);

  /**
   * Gets the timeout seconds set for this pending AgentX request.
   * @return
   *    the timeout value in seconds.
   */
  int getTimeout();
}
