/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXRegEntry.java  
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

import org.snmp4j.agent.agentx.AgentXRegion;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.OID;

public class AgentXRegEntry implements Comparable<AgentXRegEntry> {

  private AgentXMasterSession session;
  private AgentXRegion region;
  private int priority;
  private OctetString context;
  private int timeout;
  private OID id;

  public AgentXRegEntry(AgentXMasterSession session,
                        AgentXRegion region,
                        int priority,
                        OctetString context,
                        int timeout) {
    this.session = session;
    this.region = region;
    this.priority = priority;
    this.context = context;
    if (this.context == null) {
      this.context = new OctetString();
    }
    this.timeout = timeout;
  }

  public OctetString getContext() {
    return context;
  }

  public int getPriority() {
    return priority;
  }

  public AgentXRegion getRegion() {
    return region;
  }

  public AgentXMasterSession getSession() {
    return session;
  }

  public int getSpecific() {
    return region.getLowerBound().size();
  }

  public int getTimeout() {
    return timeout;
  }

  public OID getId() {
    return id;
  }

  /**
   * Compares this object with the specified object for order.
   *
   * @param other the AgentXRegEntry to be compared.
   * @return a negative integer, zero, or a positive integer as this object is
   *   less than, equal to, or greater than the specified object.
   */
  public int compareTo(AgentXRegEntry other) {
    int diff = other.getSpecific() - getSpecific();
    if (diff == 0) {
      diff = getPriority() - other.getPriority();
    }
/* The below is NOT correct since two registrations with the same specific
   subtree and priority must be deemed equal.
    if (diff == 0) {
      diff = getRegion().compareTo(other.getRegion());
    }
*/
    return diff;
  }

  public boolean equals(Object obj) {
    if (obj instanceof AgentXRegEntry) {
      AgentXRegEntry other = (AgentXRegEntry)obj;
      return session.equals(other.session) &&
          region.equals(other.region) &&
          (compareTo(other) == 0);
    }
    return false;
  }

  public int hashCode() {
    return session.getSessionID() + region.getLowerBound().hashCode();
  }

  public void setId(OID id) {
    this.id = id;
  }

  public String toString() {
    return getClass().getName()+"[region="+region+
        ",priority="+priority+
        ",context="+context+
        ",timeout="+timeout+
        ",id="+id+
        ",session="+session+"]";
  }

}
