/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXTarget.java  
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

import org.snmp4j.smi.Address;

public class AgentXTarget implements Serializable {

  private Address address;
  private long timeout = AgentXProtocol.DEFAULT_TIMEOUT_SECONDS * 1000;

  public AgentXTarget(Address address) {
    this.address = address;
  }

  public AgentXTarget(Address address, byte timeout) {
    this(address);
    this.timeout = timeout*1000;
  }

  public AgentXTarget(Address address, long timeout) {
    this(address);
    this.timeout = timeout;
  }

  public long getTimeout() {
    return timeout;
  }

  public Address getAddress() {
    return address;
  }
}
