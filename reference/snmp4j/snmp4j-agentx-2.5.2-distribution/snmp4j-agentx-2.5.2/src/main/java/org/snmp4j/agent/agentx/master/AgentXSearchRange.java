/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXSearchRange.java  
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

import org.snmp4j.agent.DefaultMOScope;
import org.snmp4j.agent.request.SnmpSubRequest;
import org.snmp4j.smi.OID;

public class AgentXSearchRange extends DefaultMOScope {

  private SnmpSubRequest referenceSubRequest;

  public AgentXSearchRange(OID lowerBound, boolean lowerIncluded,
                           OID upperBound, boolean upperIncluded,
                           SnmpSubRequest reference) {
    super(lowerBound, lowerIncluded, upperBound, upperIncluded);
    this.referenceSubRequest = reference;
  }

  public SnmpSubRequest getReferenceSubRequest() {
    return referenceSubRequest;
  }

}
