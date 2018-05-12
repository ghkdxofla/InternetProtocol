/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - MasterContextInfo.java  
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

import java.io.Serializable;

import org.snmp4j.agent.mo.snmp.SysUpTime;
import org.snmp4j.smi.OctetString;

/**
 * An AgentX master agent needs to manage certain context related information
 * which this class holds. The most important of this information is the
 * context's up time (derived from the sysUpTime object of the context - if
 * present).
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MasterContextInfo implements Serializable, Comparable {

  private OctetString context;
  private SysUpTime upTime;

  public MasterContextInfo(OctetString context, SysUpTime contextUpTime) {
    this.context = context;
    this.upTime = contextUpTime;
  }

  public int compareTo(Object o) {
    return context.compareTo(((MasterContextInfo)o).context);
  }

  public OctetString getContext() {
    return context;
  }

  public SysUpTime getUpTime() {
    return upTime;
  }

}
