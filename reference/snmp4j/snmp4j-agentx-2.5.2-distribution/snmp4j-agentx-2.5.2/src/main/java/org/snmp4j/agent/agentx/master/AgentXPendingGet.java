/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXPendingGet.java  
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

import org.snmp4j.agent.agentx.AgentXGetNextPDU;
import org.snmp4j.agent.agentx.subagent.AgentXRequest;
import org.snmp4j.agent.request.*;

import java.util.LinkedList;
import org.snmp4j.smi.OID;
import org.snmp4j.agent.MOScope;
import java.util.Collection;
import org.snmp4j.PDU;
import org.snmp4j.agent.agentx.AgentXGetBulkPDU;
import java.util.List;
import org.snmp4j.agent.agentx.AgentXGetPDU;
import org.snmp4j.agent.agentx.AgentXPDU;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;

public class AgentXPendingGet extends AbstractAgentXPending {

  private static final LogAdapter logger =
      LogFactory.getLogger(AgentXPendingGet.class);

  private AgentXPDU agentXRequestPDU;
  private List<AgentXSearchRange> searchRanges = new LinkedList<AgentXSearchRange>();

  private short nonRepeater = 0;

  public AgentXPendingGet(AgentXRegEntry registration,
                          SnmpRequest request,
                          AgentXSearchRange searchRange) {
    super(registration, request);
    this.searchRanges.add(searchRange);
  }

  public synchronized void addSearchRange(AgentXSearchRange searchRange) {
    this.searchRanges.add(searchRange);
  }

  public void setNonRepeater(short nonRepeater) {
    this.nonRepeater = nonRepeater;
  }

  public synchronized void incNonRepeater() {
    nonRepeater++;
  }

  public AgentXPDU getAgentXPDU() {
    if (agentXRequestPDU == null) {
      createRequestPDU();
    }
    return agentXRequestPDU;
  }

  public Collection<AgentXSearchRange> getSearchRanges() {
    return searchRanges;
  }

  public short getNonRepeater() {
    return nonRepeater;
  }

  /**
   * Creates the AgentX request PDU from the search ranges.
   */
  private void createRequestPDU() {
    SnmpRequest request = (SnmpRequest)
        searchRanges.get(0).getReferenceSubRequest().getRequest();
    PDU requestPDU = request.getSource().getPDU();
    switch (requestPDU.getType()) {
      case PDU.GETBULK: {
        short maxRep = getMaxRepetitions(request, requestPDU);
        agentXRequestPDU =
            new AgentXGetBulkPDU(request.getContext(),
                                 maxRep, nonRepeater,
                                 searchRanges.toArray(new MOScope[searchRanges.size()]));
        break;
      }
      case PDU.GET: {
        OID[] oids = new OID[searchRanges.size()];
        for (int i = 0; i < oids.length; i++) {
          AgentXSearchRange sr = searchRanges.get(i);
          oids[i] = sr.getLowerBound();
        }
        agentXRequestPDU = new AgentXGetPDU(request.getContext(), oids);
        break;
      }
      case PDU.GETNEXT: {
        agentXRequestPDU =
            new AgentXGetNextPDU(request.getContext(),
                                 (MOScope[]) searchRanges.toArray(
                                     new MOScope[searchRanges.size()]));
        break;
      }
      default: {
        logger.error("Failed to create AgentX PDU for PDU type " +
                     requestPDU.getType());
      }
    }
  }

  private static short getMaxRepetitions(SnmpRequest request, PDU requestPDU) {
    int maxRep =
        requestPDU.getMaxRepetitions() - request.getCompleteRepetitions();
    maxRep = Math.max(1, maxRep);
    maxRep =
        Math.min(maxRep, AgentXMasterAgent.getMaxGetBulkRepetitions());
    return (short) maxRep;
  }

  public AgentXMasterSession getSession() {
    return registration.getSession();
  }

  public SubRequestIterator<SnmpSubRequest> getReferences() {
    return new GetSubRequestIterator();
  }

  public String toString() {
    return getClass().getName()+"["+super.toStringMembers()+",searchRanges="+
        searchRanges+"]";
  }

  class GetSubRequestIterator extends SubRequestIteratorSupport<SnmpSubRequest> {

    protected GetSubRequestIterator() {
      super(searchRanges.iterator());
    }

    protected SnmpSubRequest mapToSubRequest(Object element) {
      return ((AgentXSearchRange)element).getReferenceSubRequest();
    }
  }
}
