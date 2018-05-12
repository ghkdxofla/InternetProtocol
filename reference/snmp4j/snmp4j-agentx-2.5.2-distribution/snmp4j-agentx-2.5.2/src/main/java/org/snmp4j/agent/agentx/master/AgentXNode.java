/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXNode.java  
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

import java.util.Iterator;
import java.util.TreeSet;

import org.snmp4j.agent.MOScope;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.agentx.AgentXRegion;
import org.snmp4j.agent.agentx.subagent.AgentXRequest;
import org.snmp4j.agent.request.SnmpRequest;
import org.snmp4j.agent.request.SnmpSubRequest;
import org.snmp4j.agent.request.SubRequest;
import org.snmp4j.smi.OID;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.smi.Null;
import org.snmp4j.PDU;
import org.snmp4j.agent.request.SubRequestIterator;

/**
 * A <code>AgentXNode</code> represents an atomic registration
 * region within the master agents {@link ManagedObject}s.
 * There can be several AgentXNodes for a single AgentX
 * region registration.
 * @author Frank Fock
 * @version 1.0
 */
public class AgentXNode implements ManagedObject {

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(AgentXNode.class);

  private TreeSet<AgentXRegEntry> registrations = new TreeSet<AgentXRegEntry>();
  private AgentXRegion region;

  public AgentXNode(AgentXRegion region, AgentXRegEntry registration) {
    this.region = new AgentXRegion(region);
    this.registrations.add(registration);
  }

  protected AgentXNode(AgentXRegion region, TreeSet<AgentXRegEntry> registrations) {
    this.region = new AgentXRegion(region);
    this.registrations = registrations;
  }

  @SuppressWarnings("unchecked")
  public AgentXNode getClone(AgentXRegion region) {
    return new AgentXNode(new AgentXRegion(region),
                          (TreeSet<AgentXRegEntry>)registrations.clone());
  }

  public int getRegistrationCount() {
    return registrations.size();
  }

  public synchronized boolean shrink(OID upper) {
    if (region.covers(upper)) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Shrinking node "+toString()+" to "+upper);
      }
      this.region.setUpperBound(upper);
      return true;
    }
    return false;
  }

  public synchronized boolean expand(OID upper, boolean inclusive) {
    /**@todo check regions*/
    if ((!region.covers(upper)) &&
        (region.getUpperBound().compareTo(upper) >= 0)) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Expanding node "+toString()+" to "+upper+
                     ", inclusive="+inclusive);
      }
      this.region.setUpperBound(upper);
      this.region.setUpperIncluded(inclusive);
      return true;
    }
    return false;
  }

  public synchronized void addRegistration(AgentXRegEntry entry) {
    registrations.add(entry);
  }

  public synchronized boolean removeRegistration(AgentXRegEntry entry) {
    boolean removed = registrations.remove(entry);
    if (LOGGER.isDebugEnabled()) {
      if (removed) {
        LOGGER.debug("Removed registration " + entry +
                     " from AgentX node " + toString());
      }
      else {
        LOGGER.debug("Removing registration failed for " + entry +
                     " from AgentX node " + toString());
      }
    }
    return removed;
  }

  public OID find(MOScope range) {
    OID next = OID.max(range.getLowerBound(), region.getLowerBound());
    if (region.covers(next) && (getActiveRegistration() != null)) {
      return next;
    }
    return null;
  }

  public final synchronized AgentXRegEntry getActiveRegistration() {
    AgentXRegEntry activeReg = null;
    while (!registrations.isEmpty() && (activeReg == null)) {
      activeReg = registrations.first();
      if (activeReg.getSession().isClosed()) {
        registrations.remove(activeReg);
        LOGGER.warn("Removed registration from already closed session: "+
                    activeReg);
        activeReg = null;
      }
    }
    return activeReg;
  }

  public void get(SubRequest request) {
    AgentXRegEntry activeReg = getActiveRegistration();
    if (activeReg == null) {
      request.getVariableBinding().setVariable(Null.noSuchObject);
      request.getStatus().setPhaseComplete(true);
      return;
    }
    AgentXQueue queue = activeReg.getSession().getQueue();
    AgentXSearchRange searchRange =
        new AgentXSearchRange(request.getScope().getLowerBound(),
                              request.getScope().isLowerIncluded(),
                              request.getScope().getUpperBound(),
                              request.getScope().isUpperIncluded(),
                              (SnmpSubRequest)request);
    queue.add(searchRange, activeReg, false);
    markAsProcessed(request);
  }

  public MOScope getScope() {
    return region;
  }

  public boolean next(SubRequest request) {
    AgentXRegEntry activeReg = getActiveRegistration();
    if (activeReg == null) {
      return false;
    }
    AgentXQueue queue = activeReg.getSession().getQueue();
    AgentXSearchRange searchRange =
        new AgentXSearchRange(request.getScope().getLowerBound(),
                              request.getScope().isLowerIncluded(),
                              region.getUpperBound(),
                              region.isUpperIncluded(),
                              (SnmpSubRequest)request);
    OID upperRequestBound = request.getScope().getUpperBound();
    if ((upperRequestBound != null) &&
        (upperRequestBound.compareTo(region.getUpperBound()) < 0)) {
      searchRange.setUpperBound(upperRequestBound);
      searchRange.setUpperIncluded(request.getScope().isUpperIncluded());
    }
    if (searchRange.isEmpty()) {
      return false;
    }
    int nonRepeaters = ((SnmpRequest)request.getRequest()).getNonRepeaters();
    if (queue.add(searchRange, activeReg, request.getIndex() >= nonRepeaters)) {
      if (((SnmpRequest)request.getRequest())
          .getSource().getPDU().getType() == PDU.GETBULK) {
        // need to set also repetitions to processed
        for (SubRequestIterator<? extends SubRequest> it = request.repetitions(); it.hasNext(); ) {
          SubRequest sreq = it.next();
          sreq.getStatus().setProcessed(true);
        }
      }
    }
    markAsProcessed(request);
    return true;
  }

  public void prepare(SubRequest request) {
    addAgentXSet2Queue(request);
    markAsProcessed(request);
  }

  public void undo(SubRequest request) {
    addAgentXSet2Queue(request);
    markAsProcessed(request);
  }

  public void cleanup(SubRequest request) {
    addAgentXSet2Queue(request);
    markAsProcessed(request);
  }

  public void commit(SubRequest request) {
    addAgentXSet2Queue(request);
    markAsProcessed(request);
  }

  private static void markAsProcessed(SubRequest request) {
    request.getStatus().setProcessed(true);
  }

  private void addAgentXSet2Queue(SubRequest request) {
    AgentXRegEntry activeReg = getActiveRegistration();
    if (activeReg != null) {
      AgentXMasterSession session = activeReg.getSession();
      if (session != null) {
        AgentXQueue queue = session.getQueue();
        if (queue != null) {
          queue.add(request.getVariableBinding(), (SnmpSubRequest) request,
                    activeReg);
        }
        else if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("No queue for session " + session);
        }
      }
      else if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("No session for registration entry " + activeReg);
      }
    }
    else if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("No active registration left for " + request);
    }
  }

  public String toString() {
    return getClass().getName()+"[region="+region+
        ",registrations="+registrations+"]";
  }

}
