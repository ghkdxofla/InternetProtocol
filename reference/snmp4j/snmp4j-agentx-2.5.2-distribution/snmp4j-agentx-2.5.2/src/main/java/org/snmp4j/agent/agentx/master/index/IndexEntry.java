/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - IndexEntry.java  
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

package org.snmp4j.agent.agentx.master.index;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

public class IndexEntry implements Comparable {
  private int sessionID;
  private Variable indexValue;

  public IndexEntry(int sessionID, Variable indexValue) {
    this.sessionID = sessionID;
    this.indexValue = indexValue;
}

  public int getSessionID() {
    return sessionID;
  }

  public Variable getIndexValue() {
    return indexValue;
  }

  public int hashCode() {
    return indexValue.hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof IndexEntry) {
      return ((indexValue.equals(((IndexEntry)o).indexValue)) &&
          (sessionID == ((IndexEntry)o).sessionID));
    }
    else if (o instanceof Variable) {
      return indexValue.equals(o);
    }
    return false;
  }

  public int compareTo(Object o) {
    OID indexOID = indexValue.toSubIndex(true);
    if (o instanceof IndexEntry) {
      return indexOID.compareTo(((IndexEntry)o).indexValue.toSubIndex(true));
    }
    else if (o instanceof Variable) {
      return indexOID.compareTo(((Variable)o).toSubIndex(true));
    }
    throw new ClassCastException(o.getClass()+" != (IndexEntry or Variable)");
  }

}
