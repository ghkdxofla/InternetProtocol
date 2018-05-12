/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXRegion.java  
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

import org.snmp4j.agent.DefaultMOScope;
import org.snmp4j.smi.OID;

public class AgentXRegion extends DefaultMOScope implements Comparable {

  private boolean singleOID;
  private byte rangeSubID;

  public AgentXRegion(OID lowerBound, OID upperBound) {
    super(lowerBound, true, upperBound, false);
  }

  public AgentXRegion(AgentXRegion other) {
    super(other.getLowerBound(), other.isLowerIncluded(),
          other.getUpperBound(), other.isUpperIncluded());
    this.singleOID = other.singleOID;
    this.rangeSubID = other.rangeSubID;
  }

  public byte getRangeSubID() {
    return rangeSubID;
  }

  public boolean isSingleOID() {
    return singleOID;
  }

  public void setRangeSubID(byte rangeSubID) {
    this.rangeSubID = rangeSubID;
  }

  public void setSingleOID(boolean singleOID) {
    this.singleOID = singleOID;
  }

  public int getUpperBoundSubID() {
    if (rangeSubID != 0) {
      return upperBound.get(rangeSubID-1);
    }
    return 0;
  }

  public boolean isRange() {
    return (rangeSubID > 0);
  }

  public boolean isEmpty() {
    return (lowerBound.compareTo(upperBound) >= 0);
  }

  public int getLowerBoundSubID() {
    if (rangeSubID != 0) {
      return lowerBound.get(rangeSubID-1);
    }
    return 0;
  }

  public int compareTo(Object o) {
    AgentXRegion other = (AgentXRegion)o;
    int c = lowerBound.compareTo(other.lowerBound);
    if (c == 0) {
      c = upperBound.compareTo(other.upperBound);
      if (c == 0) {
        c = rangeSubID - other.rangeSubID;
      }
    }
    return c;
  }

  public String toString() {
    return getClass().getName()+"[lowerBound="+lowerBound+
        ",lowerIncluded="+lowerIncluded+
        ",upperBound="+upperBound+
        ",upperIncluded="+upperIncluded+
        ",rangeSubID="+rangeSubID+
        ",upperBoundSubID="+getUpperBoundSubID()+"]";
  }
}
