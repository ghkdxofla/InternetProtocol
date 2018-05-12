/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXMasterSession.java  
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

import org.snmp4j.agent.agentx.AgentXProtocol;
import org.snmp4j.agent.agentx.AgentXSession;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import java.util.Map;
import java.util.HashMap;

/**
 * The <code>AgentXMasterSession</code> extends the base session
 * {@link AgentXSession} by agent capability information and
 * sysObjectID as well as sysDescr.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class AgentXMasterSession extends AgentXSession {

  private int agentXVersion = AgentXProtocol.VERSION_1_0 & 0xFF;
  private AgentXQueue queue;
  private OID oid;
  private OctetString descr;
  private Map<OID, OID> agentCaps;

  public AgentXMasterSession(int sessionID, AgentXQueue queue,
                             OID oid, OctetString descr) {
    super(sessionID);
    this.queue = queue;
    this.oid = oid;
    this.descr = descr;
  }

  public AgentXQueue getQueue() {
    return queue;
  }

  public OID getOid() {
    return oid;
  }

  public OctetString getDescr() {
    return descr;
  }

  public int getAgentXVersion() {
    return agentXVersion;
  }

  public void setAgentXVersion(int agentXVersion) {
    this.agentXVersion = agentXVersion;
  }

  public synchronized void addAgentCaps(OID sysORID, OID agentCapsIndex) {
    if (agentCaps == null) {
      agentCaps = new HashMap<OID, OID>(10);
    }
    agentCaps.put(sysORID, agentCapsIndex);
  }

  public synchronized OID removeAgentCaps(OID sysORID) {
    if (agentCaps != null) {
      return agentCaps.remove(sysORID);
    }
    return null;
  }

}
