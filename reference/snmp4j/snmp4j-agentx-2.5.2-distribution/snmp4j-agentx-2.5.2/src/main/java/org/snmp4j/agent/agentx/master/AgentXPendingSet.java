/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXPendingSet.java  
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

import org.snmp4j.agent.agentx.AgentXPDU;
import java.util.LinkedList;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.agent.request.SnmpSubRequest;
import java.util.List;
import org.snmp4j.agent.request.SnmpRequest;
import org.snmp4j.agent.request.Request;
import org.snmp4j.agent.agentx.AgentXTestSetPDU;
import org.snmp4j.agent.agentx.AgentXCommitSetPDU;
import org.snmp4j.agent.agentx.AgentXCleanupSetPDU;
import org.snmp4j.agent.agentx.AgentXUndoSetPDU;
import org.snmp4j.agent.request.SubRequest;
import org.snmp4j.agent.request.SubRequestIterator;
import org.snmp4j.agent.request.SubRequestIteratorSupport;

public class AgentXPendingSet extends AbstractAgentXPending {

  private AgentXMasterSession session;
  private List<AgentXSetVB> vbs = new LinkedList<AgentXSetVB>();
  private AgentXPDU requestPDU;

  public AgentXPendingSet(AgentXRegEntry regEntry, SnmpRequest request) {
    super(regEntry, request);
    this.session = regEntry.getSession();
  }

  public synchronized void add(SnmpSubRequest reference, VariableBinding vb) {
    vbs.add(new AgentXSetVB(vb, reference));
    // invalidate pdu
    requestPDU = null;
  }

  public AgentXPDU getAgentXPDU() {
    if (requestPDU == null) {
      createRequestPDU();
    }
    return requestPDU;
  }

  private void createRequestPDU() {
    AgentXPDU reqPDU = null;
    SnmpRequest request = (SnmpRequest)
        vbs.get(0).getReferenceSubRequest().getRequest();
    switch (request.getPhase()) {
      case Request.PHASE_INIT:
      case Request.PHASE_1PC:
      case Request.PHASE_2PC_PREPARE: {
        reqPDU =
            new AgentXTestSetPDU(request.getContext(), (VariableBinding[])
                                 vbs.toArray(new VariableBinding[vbs.size()]));
        break;
      }
      case Request.PHASE_2PC_COMMIT: {
        reqPDU = new AgentXCommitSetPDU();
        break;
      }
      case Request.PHASE_2PC_CLEANUP: {
        reqPDU = new AgentXCleanupSetPDU();
        break;
      }
      default: {
        reqPDU = new AgentXUndoSetPDU();
        break;
      }
    }
    reqPDU.setSessionID(session.getSessionID());
    reqPDU.setTransactionID(request.getTransactionID());
    this.requestPDU = reqPDU;
  }

  public AgentXMasterSession getSession() {
    return session;
  }

  public SubRequestIterator<SnmpSubRequest> getReferences() {
    return new SetSubRequestIterator();
  }

  public String toString() {
    return getClass().getName()+"["+super.toStringMembers()+",vbs="+vbs+"]";
  }

  public class AgentXSetVB extends VariableBinding {
    private SnmpSubRequest referenceSubRequest;

    public AgentXSetVB(VariableBinding vb, SnmpSubRequest reference) {
      super(vb.getOid(), vb.getVariable());
      this.referenceSubRequest = reference;
    }

    public SnmpSubRequest getReferenceSubRequest() {
      return referenceSubRequest;
    }
  }

  class SetSubRequestIterator extends SubRequestIteratorSupport<SnmpSubRequest> {
    protected SetSubRequestIterator() {
      super(AgentXPendingSet.this.vbs.iterator());
    }

    protected SnmpSubRequest mapToSubRequest(Object element) {
      return ((AgentXSetVB)element).getReferenceSubRequest();
    }
  }


}
