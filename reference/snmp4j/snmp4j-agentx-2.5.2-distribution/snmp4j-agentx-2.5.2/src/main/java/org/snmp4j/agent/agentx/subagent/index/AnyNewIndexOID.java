/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AnyNewIndexOID.java  
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

package org.snmp4j.agent.agentx.subagent.index;

import org.snmp4j.smi.OID;

/**
 * The <code>AnyNewIndexOID</code> is a special OID subclass for shared table
 * indexes. This object can be used to create a locally unique index value
 * during sub-agent initialization that will be replaced by a globally unique
 * index when AgentX connection is being established.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class AnyNewIndexOID extends OID {

  public AnyNewIndexOID() {
  }

  public AnyNewIndexOID(int[] index) {
    super(index);
  }

  public AnyNewIndexOID(String index) {
    super(index);
  }

  public Object clone() {
    return new AnyNewIndexOID(getValue());
  }

}
