/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - PingListener.java  
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

package org.snmp4j.agent.agentx.event;

import java.util.EventListener;

/**
 * A <code>PingListener</code> is interested in receiving PingEvent objects
 * to control the ability of an AgentX peer (session) of processing requests.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface PingListener extends EventListener {

  /**
   * A AgentX ping PDU has been sent and probably a corresponding response
   * received.
   * @param event
   *    the PingEvent generated. Depending on the ping history and local
   *    constraints a listener may set the closeSession and resetSession
   *    attributes to indicate that the session is no longer functional
   *    and should be closed or resetted.
   */
  void pinged(PingEvent event);

}
