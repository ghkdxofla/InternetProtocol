/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXQueue.java  
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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.snmp4j.PDU;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.agentx.AgentXProtocol;
import org.snmp4j.agent.request.SnmpRequest;
import org.snmp4j.agent.request.SnmpSubRequest;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.agent.DefaultMOScope;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.MOScope;
import org.snmp4j.smi.OctetString;

public class AgentXQueue {

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(AgentXQueue.class);

  private LinkedList<AgentXQueueEntry> queue = new LinkedList<AgentXQueueEntry>();
  private MOServer[] servers;

  public AgentXQueue() {
  }

  public void setServer4BulkOptimization(MOServer[] servers) {
    this.servers = servers;
  }

  public MOServer[] getServer4BulkOptimization() {
    return this.servers;
  }

  public synchronized boolean add(VariableBinding vb,
                                  SnmpSubRequest subRequest,
                                  AgentXRegEntry entry) {
    SnmpRequest request = (SnmpRequest) subRequest.getRequest();
    AgentXPendingSet pending =
        (AgentXPendingSet) get(entry.getSession().getSessionID(),
                               request.getTransactionID());
    if (pending == null) {
      pending = new AgentXPendingSet(entry, subRequest.getSnmpRequest());
      insertIntoQueue(request.getTransactionID(), pending);
    }
    if (!pending.isPending()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Variable binding " + vb +
                     " not added because AgentX request " + pending +
                     " is waiting for response");
      }
      return false;
    }
    pending.add(subRequest, vb);
    return true;
  }

  private synchronized void insertIntoQueue(int transactionID,
                                            AgentXPending pending) {
    AgentXRegEntry reg = pending.getRegistration();
    int timeout = reg.getTimeout();
    if (timeout == 0) {
      timeout = reg.getSession().getTimeout() & 0xFF;
    }
    pending.setTimeout(timeout);

    AgentXQueueEntry entry = getQueueEntry(transactionID, false);
    if (entry == null) {
      entry = new AgentXQueueEntry(transactionID);
      queue.add(entry);
    }
    entry.addEntry(pending);
  }

  public synchronized boolean add(AgentXSearchRange searchRange,
                                  AgentXRegEntry entry, boolean repeater) {
    SnmpRequest request =
        (SnmpRequest) searchRange.getReferenceSubRequest().getRequest();
    AgentXPendingGet pending =
        (AgentXPendingGet) get(entry.getSession().getSessionID(),
                               request.getTransactionID());
    if (pending == null) {
      // optimize upper bound if server is set
      if ((servers != null) &&
          (request.getSource().getPDU().getType() == PDU.GETBULK)) {
        optimizeSearchRange(searchRange, entry);
      }
      pending = new AgentXPendingGet(entry, request, searchRange);
      insertIntoQueue(request.getTransactionID(), pending);
    }
    else if (pending.isPending()) {
      switch (request.getSource().getPDU().getType()) {
        case PDU.GETBULK: {
          for (AgentXSearchRange psr : pending.getSearchRanges()) {
            int repCount = request.getRepeaterCount();
            if ((repCount > 0) &&
                (((searchRange.getReferenceSubRequest().getIndex()-pending.getNonRepeater()) -
                    (psr.getReferenceSubRequest().getIndex() - request.getNonRepeaters())) % repCount == 0)) {
              // this is a new repetition -> ignore it this time and send out
              // AgentX request
              if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Repetition not added because of pending AgentX" +
                    " processing of " + pending + " and repetition " + psr);
              }
              return false;
            }
          }
          // optimize upper bound if server is set
          if (servers != null) {
            optimizeSearchRange(searchRange, entry);
          }
          break;
        }
        default: {
          // do nothing special
        }
      }
      pending.addSearchRange(searchRange);
    }
    else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Search range " + searchRange +
                     " not added because AgentX request " + pending +
                     " is not pending");
      }
      return false;
    }
    if (!repeater) {
      pending.incNonRepeater();
    }
    return true;
  }

  private MOServer getServer(OctetString context) {
    MOServer[] sc = servers;
    for (MOServer s : sc) {
      if (s.isContextSupported(context)) {
        return s;
      }
    }
    return null;
  }

  protected void optimizeSearchRange(AgentXSearchRange searchRange,
                                     AgentXRegEntry entry) {
    DefaultMOScope scope = new DefaultMOScope(searchRange.getUpperBound(),
                                              !searchRange.isUpperIncluded(),
                                              null, false);
    AgentXNodeQuery query =
        new AgentXNodeQuery(entry.getContext(), scope,
                            AgentXNodeQuery.QUERY_ALL);
    MOScope requestScope = searchRange.getReferenceSubRequest().getScope();
    MOServer server = getServer(entry.getContext());
    for (ManagedObject node = server.lookup(query);
         (node instanceof AgentXNode);
         node = server.lookup(nextQuery(query, (AgentXNode)node)))
    {
      AgentXRegEntry activeReg = ((AgentXNode)node).getActiveRegistration();
      MOScope region = node.getScope();
      if ((activeReg != null) &&
          (activeReg.getSession().equals(entry.getSession()))) {
        if ((requestScope.getUpperBound() != null) &&
            (requestScope.getUpperBound().
             compareTo(region.getUpperBound()) <= 0)) {
          searchRange.setUpperBound(requestScope.getUpperBound());
          searchRange.setUpperIncluded(requestScope.isUpperIncluded());
          break;
        }
        searchRange.setUpperBound(region.getUpperBound());
        searchRange.setUpperIncluded(region.isUpperIncluded());
      }
      else {
        if ((searchRange.getUpperBound() == null) ||
            (searchRange.getUpperBound().compareTo(region.getLowerBound()) >= 0)) {
          searchRange.setUpperBound(region.getLowerBound());
          searchRange.setUpperIncluded(!region.isLowerIncluded());
        }
        break;
      }
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Optimized upper bound for bulk AgentX request to "+
                   searchRange);
    }
  }

  private static AgentXNodeQuery nextQuery(AgentXNodeQuery lastQuery,
                                           AgentXNode lastNode) {
    if (lastNode != null) {
      lastQuery.getMutableScope().setLowerBound(
          lastNode.getScope().getUpperBound());
      lastQuery.getMutableScope().setLowerIncluded(false);
    }
    return lastQuery;
  }

  /**
   * Returns the AgentX request in the queue identified by an AgentX session ID
   * and a transaction ID.
   * @param sessionID
   *    the session ID.
   * @param transactionID
   *    the transaction ID.
   * @return
   *    the associated <code>AgentXPending</code> instance or <code>null</code>
   *    if no such request exists.
   */
  public synchronized AgentXPending get(int sessionID, int transactionID) {
    AgentXQueueEntry entry = getQueueEntry(transactionID, false);
    if (entry != null) {
      return entry.get(sessionID, false);
    }
    return null;
  }

  /**
   * Returns the AgentX request in the queue identified by an AgentX session ID
   * and a transaction ID and removes that request from the queue.
   *
   * @param sessionID
   *    the session ID.
   * @param transactionID
   *    the transaction ID.
   * @return
   *    the associated <code>AgentXPending</code> instance or <code>null</code>
   *    if no such request exists.
   */
  public synchronized AgentXPending remove(int sessionID, int transactionID) {
    AgentXQueueEntry entry = getQueueEntry(transactionID, false);
    if (entry != null) {
      return entry.get(sessionID, true);
    }
    return null;
  }


  /**
   * Return all pending AgentX requests for the specified transaction ID.
   *
   * @param transactionID
   *   a transcation ID.
   * @return
   *   a possibly empty List of pending requests.
   */
  public synchronized AgentXQueueEntry get(int transactionID) {
    return getQueueEntry(transactionID, false);
  }

  /**
   * Remove all AgentX request entries for the supplied transaction ID.
   * @param transactionID
   *    a transaction ID.
   */
  public synchronized void removeAll(int transactionID) {
    getQueueEntry(transactionID, true);
  }

  private AgentXQueueEntry getQueueEntry(int transactionID, boolean remove) {
    for (Iterator<AgentXQueueEntry> it = queue.iterator(); it.hasNext(); ) {
      AgentXQueueEntry entry = it.next();
      if (entry.transactionID == transactionID) {
        if (remove) {
          it.remove();
        }
        return entry;
      }
    }
    return null;
  }

  public class AgentXQueueEntry implements Comparable<AgentXQueueEntry> {

    private int transactionID;
    private LinkedList<AgentXPending> requests;
    private int minTimeout = AgentXProtocol.MAX_TIMEOUT_SECONDS;
    private long timestamp = 0;

    public AgentXQueueEntry(int transactionID) {
      this.transactionID = transactionID;
      this.requests = new LinkedList<AgentXPending>();
    }

    public synchronized final void addEntry(AgentXPending pendingRequest) {
      this.requests.add(pendingRequest);
      if (minTimeout > pendingRequest.getTimeout()) {
        minTimeout = pendingRequest.getTimeout();
      }
    }

    public final void updateTimestamp() {
      this.timestamp = System.currentTimeMillis();
    }

    public final long getTimestamp() {
      return timestamp;
    }

    public final int getMinTimeout() {
      return minTimeout;
    }

    public boolean equals(Object obj) {
      if (obj instanceof AgentXQueueEntry) {
        AgentXQueueEntry other = (AgentXQueueEntry)obj;
        return ((transactionID == other.transactionID));
      }
      return false;
    }

    public int hashCode() {
      return transactionID;
    }

    public String toString() {
      return "AgentXQueueEntry[transactionID="+transactionID+",requests="+
          requests+"]";
    }

    public int compareTo(AgentXQueueEntry other) {
      return transactionID - other.transactionID;
    }

    public final synchronized AgentXPending get(int sessionID, boolean remove) {
      for (Iterator<AgentXPending> it = requests.iterator(); it.hasNext(); ) {
        AgentXPending p = it.next();
        if (p.getSession().getSessionID() == sessionID) {
          if (remove) {
            it.remove();
            if (requests.isEmpty()) {
              queue.remove(this);
            }
          }
          return p;
        }
      }
      return null;
    }

    public synchronized final boolean isEmpty() {
      return requests.isEmpty();
    }

    public synchronized final Collection<AgentXPending> getPending() {
      LinkedList<AgentXPending> pending = new LinkedList<AgentXPending>();
      for (AgentXPending item : requests) {
        if (item.isPending()) {
          pending.add(item);
        }
      }
      return pending;
    }
  }
}
