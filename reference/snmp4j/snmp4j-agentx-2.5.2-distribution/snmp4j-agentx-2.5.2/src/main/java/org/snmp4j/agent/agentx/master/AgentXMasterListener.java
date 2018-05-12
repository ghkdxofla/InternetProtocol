/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXMasterListener.java  
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

import java.util.EventListener;

/**
 * The <code>AgentXMasterListener</code> interface ca be implemented to
 * react on sub-agent, session, and registration changes occurred at the
 * event emitting AgentX master agent.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface AgentXMasterListener extends EventListener {

  /**
   * The state of the master agents sub-agent connections, sessions, or
   * registrations has been changed. For details inspect the supplied event
   * object.
   *
   * @param event
   *    an AgentXMasterEvent object.
   */
  void masterChanged(AgentXMasterEvent event);

}
