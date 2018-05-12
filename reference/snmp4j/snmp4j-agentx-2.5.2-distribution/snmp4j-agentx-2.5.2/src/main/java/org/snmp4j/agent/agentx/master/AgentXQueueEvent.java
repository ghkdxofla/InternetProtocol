/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXQueueEvent.java  
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

import java.util.EventObject;

public class AgentXQueueEvent extends EventObject {

  public static final int TIMEOUT = 1;

  private AgentXPending queueElement;
  private int type;

  public AgentXQueueEvent(Object source, int type, AgentXPending queueElement) {
    super(source);
    this.type = type;
    this.queueElement = queueElement;
  }

  public AgentXPending getQueueElement() {
    return queueElement;
  }

  public int getType() {
    return type;
  }

}
