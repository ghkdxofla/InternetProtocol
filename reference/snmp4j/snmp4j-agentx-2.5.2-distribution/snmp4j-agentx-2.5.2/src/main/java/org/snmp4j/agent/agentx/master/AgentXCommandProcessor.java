/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXCommandProcessor.java  
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

import java.io.IOException;
import java.util.*;

import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.*;
import org.snmp4j.agent.agentx.*;
import org.snmp4j.agent.agentx.master.AgentXQueue.AgentXQueueEntry;
import org.snmp4j.agent.agentx.master.index.AgentXIndexRegistry;
import org.snmp4j.agent.agentx.subagent.AgentXRequest;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.AgentCapabilityList;
import org.snmp4j.agent.mo.snmp.SNMPv2MIB.SysUpTimeImpl;
import org.snmp4j.agent.mo.snmp.SysUpTime;
import org.snmp4j.agent.request.*;
import org.snmp4j.agent.security.VACM;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.ConnectionOrientedTransportMapping;
import org.snmp4j.transport.TransportStateEvent;
import org.snmp4j.transport.TransportStateListener;

/**
 * The <code>AgentXCommandProcessor</code> implements the {@link CommandProcessor}
 * interface for AgentX command processing of an AgentX master agent.
 * It holds the AgentX sessions and region registrations internally.
 * The context engine ID of the master agent, the {@link AgentXQueue},
 * the {@link AgentX} protocol adapter, and the {@link MOServer}s have
 * to be provided for instance creation.
 *
 * @author Frank Fock
 */
public class AgentXCommandProcessor extends CommandProcessor implements
    AgentXCommandListener, TransportStateListener,
    AgentXResponseListener {

  public static final int MAX_REPROCESSING_DEFAULT = 100;

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(AgentXCommandProcessor.class);

  private static final OctetString DEFAULT_CONTEXT = new OctetString();

  private AgentXQueue agentXQueue;
  private AgentX agentX;
  private Map<Integer, AgentXMasterSession> sessions = new HashMap<Integer, AgentXMasterSession>();
  private Map<Address, AgentXPeer> peers = new HashMap<Address, AgentXPeer>(10);
  private Set<AgentXRegEntry> registrations = new TreeSet<AgentXRegEntry>(new AgentXRegEntryComparator());
  private int nextSessionID = 1;
  private byte defaultTimeout = AgentXProtocol.DEFAULT_TIMEOUT_SECONDS;
  private int maxConsecutiveTimeouts =
      AgentXProtocol.DEFAULT_MAX_CONSECUTIVE_TIMEOUTS;
  private int maxParseErrors =
      AgentXProtocol.DEFAULT_MAX_PARSE_ERRORS;
  private Map<OctetString, MasterContextInfo> contextInfo =
      new HashMap<OctetString, MasterContextInfo>(10);
  private boolean acceptNewContexts = false;

  private int nextPacketID = 0;

  protected AgentXIndexRegistry indexRegistry = new AgentXIndexRegistry();

  private transient List<AgentXMasterListener> agentXMasterListeners;

  private int maxReprocessing = MAX_REPROCESSING_DEFAULT;

  public AgentXCommandProcessor(OctetString contextEngineID,
                                AgentXQueue queue,
                                AgentX agentX,
                                MOServer[] server) {
    super(contextEngineID);
    this.agentXQueue = queue;
    this.agentX = agentX;
    for (MOServer aServer : server) {
      super.addMOServer(aServer);
    }
    if (this.agentXQueue.getServer4BulkOptimization() == null) {
      this.agentXQueue.setServer4BulkOptimization(server);
    }
  }

  private synchronized int createNextPacketID() {
    return nextPacketID++;
  }

  public void setMaxReprocessing(int maxReprocessing) {
    this.maxReprocessing = maxReprocessing;
  }

  public int getMaxReprocessing() {
    return maxReprocessing;
  }

  /**
   * Sets the maximum number of parse errors allowed per peer. If this number
   * is exceeded then the peer will be closed with reason
   * {@link AgentXProtocol#REASON_PARSE_ERROR}.
   *
   * @param maxParseErrors
   *    a positive value (including zero) sets the upper limit of parse errors
   *    tolerated per peer. If the number of parse errors exceeds this limit,
   *    all sessions with that peer will be closed. A negative value deactivates
   *    any limit.
   * @since 1.0.1
   */
  public void setMaxParseErrors(int maxParseErrors) {
    this.maxParseErrors = maxParseErrors;
  }

  /**
   * Gets the upper limit for parse errors for an AgentX peer.
   * @return
   *    a positive value (including zero) indicates the upper limit of parse
   *    errors tolerated per peer. A negative value indicates that there is no
   *    limit.
   * @since 1.0.1
   */
  public int getMaxParseErrors() {
    return maxParseErrors;
  }

  protected void finalizeRequest(CommandResponderEvent command,
                                 SnmpRequest req,
                                 MOServer server) {
    boolean complete = req.isComplete();
    AgentXQueueEntry entry = agentXQueue.get(req.getTransactionID());
    boolean waitingForResponse = false;
    if (entry != null) {
      Collection<AgentXPending> pending = entry.getPending();
      entry.updateTimestamp();
      if (pending != null) {
        for (AgentXPending p : pending) {
          AgentXPDU agentXPDU = p.getAgentXPDU();
          AgentXMasterSession session = p.getSession();
          agentXPDU.setSessionID(session.getSessionID());
          agentXPDU.setTransactionID(req.getTransactionID());
          agentXPDU.setPacketID(createNextPacketID());
          p.updateTimestamp();
          boolean expectResponse = true;
          if (agentXPDU.getType() != AgentXPDU.AGENTX_CLEANUPSET_PDU) {
            waitingForResponse = true;
          } else {
            p.setPending(false);
            complete = req.isComplete();
            expectResponse = false;
          }
          try {
            agentX.send(agentXPDU,
                session.createAgentXTarget(),
                session.getPeer().getTransport(),
                (expectResponse) ? p : null,
                (expectResponse) ? this : null);
          } catch (IOException ex) {
            LOGGER.error("Failed to send AgentX subrequest: " +
                ex.getMessage());
            p.getReferences().next().getStatus().setErrorStatus(PDU.genErr);
            break;
          }
        }
      }
    }
    if ((entry == null) || (!waitingForResponse)) {
      if (complete) {
        agentXQueue.removeAll(req.getTransactionID());
      }
      else {
        // there are still incomplete sub-requests -> reprocess them
        if (req.getReprocessCounter() < this.maxReprocessing) {
          reprocessRequest(server, (SnmpRequest)req);
        }
        else {
          req.setErrorStatus(PDU.genErr);
          LOGGER.warn("The following request has been reprocessed "+
                      req.getReprocessCounter()+" which exceeds the agent's "+
                      "upper limit of "+this.maxReprocessing+": "+
                      req);
        }
      }
      super.finalizeRequest(command, req, server);
    }
  }

  protected synchronized int getNextSessionID() {
    return nextSessionID++;
  }

  /**
   * Gets the default server (for the <code>null</code> context).
   * @return
   *    the default server instance.
   * @deprecated
   *    Use {@link #getServer(OctetString context)} instead.
   */
  public MOServer getServer() {
    return super.getServer(null);
  }

  public byte getDefaultTimeout() {
    return defaultTimeout;
  }

  /**
   * Gets the maximum number of consecutive timeouts allowed per session.
   * @return
   *    the maximum number of consecutive timeouts allowed per session
   */
  public int getMaxConsecutiveTimeouts() {
    return maxConsecutiveTimeouts;
  }

  /**
   * Indicates whether subagents can register contexts that are not yet
   * supported by this master agent.
   * @return
   *    <code>true</code> if subagents can register objects for new contexts.
   */
  public boolean isAcceptNewContexts() {
    return acceptNewContexts;
  }

  public void setDefaultTimeout(byte defaultTimeout) {
    this.defaultTimeout = defaultTimeout;
  }

  /**
   * Sets the maximum number of timeouts allowed per session. If the number
   * is exceeded then the session will be closed with reason
   * {@link AgentXProtocol#REASON_TIMEOUTS}.
   * @param maxConsecutiveTimeouts
   *    the maximum number of timeouts (should be greater than zero).
   */
  public void setMaxConsecutiveTimeouts(int maxConsecutiveTimeouts) {
    this.maxConsecutiveTimeouts = maxConsecutiveTimeouts;
  }

  /**
   * Enables or disables accepting new contexts from subagents.
   * @param acceptNewContexts
   *    <code>true</code> if subagents are allowed to register objects for new
   *    contexts, <code>false</code> otherwise. Default is <code>false</code>.
   */
  public void setAcceptNewContexts(boolean acceptNewContexts) {
    this.acceptNewContexts = acceptNewContexts;
  }

  public void processCommand(AgentXCommandEvent event) {
    boolean pendingClose = false;
    if (event.isException()) {
      AgentXPeer peer = getPeer(event.getPeerAddress());
      if (peer != null) {
        peer.incParseErrors();
        LOGGER.warn("AgentX parse exception from peer '"+peer+
                    "' : " + event.getException());
        if ((maxParseErrors >= 0) && (peer.getParseErrors() > maxParseErrors)) {
          LOGGER.warn("Removing peer due to excessive parse errors: " +peer);
          closePeer(peer.getAddress(), AgentXProtocol.REASON_PARSE_ERROR);
        }
      }
      else {
        LOGGER.error("AgentX parse exception from unknown peer '"+
                     event.getPeerAddress()+
                     "' : " + event.getException());
      }
    }
    else {
      AgentXPDU pdu = event.getCommand();
      AgentXMasterSession session = getSession(pdu);
      AgentXResponsePDU response = null;
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Processing AgentX PDU "+pdu+" for session "+session);
      }
      switch (pdu.getType()) {
        case AgentXPDU.AGENTX_RESPONSE_PDU: {
          LOGGER.error(
              "Internal error: received AgentX response without request");
          return;
        }
        case AgentXPDU.AGENTX_OPEN_PDU: {
          response = openSession((AgentXOpenPDU) pdu, event);
          session = getSession(response.getSessionID());
          break;
        }
        case AgentXPDU.AGENTX_CLOSE_PDU: {
          response = closeSession((AgentXClosePDU)pdu, session);
          pendingClose = true;
          break;
        }
        case AgentXPDU.AGENTX_REGISTER_PDU: {
          response = register((AgentXRegisterPDU)pdu, event, session);
          break;
        }
        case AgentXPDU.AGENTX_UNREGISTER_PDU: {
          response = unregister((AgentXUnregisterPDU)pdu, event, session);
          break;
        }
        case AgentXPDU.AGENTX_ADDAGENTCAPS_PDU: {
          response = addAgentCaps((AgentXAddAgentCapsPDU)pdu, session);
          break;
        }
        case AgentXPDU.AGENTX_REMOVEAGENTCAPS_PDU: {
          response = removeAgentCaps((AgentXRemoveAgentCapsPDU)pdu, session);
          break;
        }
        case AgentXPDU.AGENTX_NOTIFY_PDU: {
          response = notify((AgentXNotifyPDU)pdu, session);
          break;
        }
        case AgentXPDU.AGENTX_PING_PDU: {
          response = ping((AgentXPingPDU)pdu, session);
          break;
        }
        case AgentXPDU.AGENTX_INDEXALLOCATE_PDU: {
          response = indexAllocate((AgentXIndexAllocatePDU)pdu, session);
          break;
        }
        case AgentXPDU.AGENTX_INDEXDEALLOCATE_PDU: {
          response = indexDeallocate((AgentXIndexDeallocatePDU)pdu, session);
          break;
        }
        default:
          LOGGER.warn("Unknown AgentX PDU type received: " + pdu);
      }
      if ((response != null) && (session != null)) {
        sendResponse(response, session);
      }
      if (pendingClose) {
        if (session != null) {
          closePeer(session.getPeer());
        }
      }
    }
    event.setProcessed(true);
  }

  @SuppressWarnings("unchecked")
  private void closePeer(AgentXPeer peer) {
    TransportMapping transport = peer.getTransport();
    if (transport instanceof ConnectionOrientedTransportMapping) {
      try {
        if (((ConnectionOrientedTransportMapping)transport).close(peer.getAddress())) {
          if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Closed sub-agent connection to " + peer.getAddress());
          }
        }
        else {
          LOGGER.warn("Failed to close sub-agent connection to " + peer.getAddress());
        }
      }
      catch (IOException ex) {
        LOGGER.error("Failed to close transport mapping "+
                     peer.getTransport()+" because: "+
                     ex.getMessage(), ex);
      }
    }
  }

  public AgentXResponsePDU indexDeallocate(AgentXIndexDeallocatePDU pdu,
                                           AgentXMasterSession session) {
    AgentXResponsePDU response = createResponse(pdu, session);
    boolean contextSupported = isContextSupported(pdu.getContext());
    if (contextSupported) {
      VariableBinding[] vbs = pdu.getVariableBindings();
      // test index allocation
      deallocateIndexes(response, pdu, session, vbs, true);
      if (response.getErrorStatus() == AgentXProtocol.AGENTX_SUCCESS) {
        // do it on success
        deallocateIndexes(response, pdu, session, vbs, false);
        response.setVariableBindings(vbs);
      }
    }
    else {
      response.setErrorStatus(AgentXProtocol.AGENTX_UNSUPPORTED_CONTEXT);
    }
    return response;
  }

  private boolean isContextSupported(OctetString context) {
    MOServer s = getServer(context);
    return (s != null) && (s.isContextSupported(context));
  }

  private boolean checkIfContextIsSupported(OctetString context) {
    boolean contextSupported = isContextSupported(context);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Checking context '"+context+"' is supported");
    }
    if (isAcceptNewContexts() && !contextSupported) {
      MOServer server = getServer(null);
      if (server != null) {
        server.addContext(context);
        contextSupported = server.isContextSupported(context);
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info("Adding new context '" + context +
                      "' on subagent request returned: " + contextSupported);
        }
      }
      else {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Adding new context '" + context +
                      "' on subagent request failed "+
                      "because no default server found");
        }
      }
    }
    return contextSupported;
  }

  public AgentXResponsePDU indexAllocate(AgentXIndexAllocatePDU pdu,
                                         AgentXMasterSession session) {
    AgentXResponsePDU response = createResponse(pdu, session);
    response.setVariableBindings(pdu.getVariableBindings());
    boolean contextSupported = checkIfContextIsSupported(pdu.getContext());
    if (contextSupported) {
      VariableBinding[] vbs = pdu.getVariableBindings();
      // test index allocation
      allocateIndexes(response, pdu, session, vbs, true);
      if (response.getErrorStatus() == AgentXProtocol.AGENTX_SUCCESS) {
        // do it on success
        allocateIndexes(response, pdu, session, vbs, false);
        response.setVariableBindings(vbs);
      }
    }
    else {
      response.setErrorStatus(AgentXProtocol.AGENTX_UNSUPPORTED_CONTEXT);
    }
    return response;
  }

  private int allocateIndexes(AgentXResponsePDU response,
                              AgentXIndexAllocatePDU pdu,
                              AgentXMasterSession session,
                              VariableBinding[] vbs,
                              boolean testOnly) {
    int status = AgentXProtocol.AGENTX_SUCCESS;
    int i=0;
    for (; (i<vbs.length) && (status == AgentXProtocol.AGENTX_SUCCESS); i++) {
      VariableBinding vb = vbs[i];
      if (pdu.isFlagSet(AgentXProtocol.FLAG_ANY_INDEX)) {
        status = indexRegistry.anyIndex(session.getSessionID(),
                                        pdu.getContext(), vb, testOnly);
      }
      else if (pdu.isFlagSet(AgentXProtocol.FLAG_NEW_INDEX)) {
        status = indexRegistry.newIndex(session.getSessionID(),
                                        pdu.getContext(), vb, testOnly);
      }
      else {
        status = indexRegistry.allocate(session.getSessionID(),
                                        pdu.getContext(), vb, testOnly);
      }
    }
    response.setErrorStatus(status);
    if (status != AgentXProtocol.AGENTX_SUCCESS) {
      response.setErrorIndex(i);
    }
    return status;
  }

  private int deallocateIndexes(AgentXResponsePDU response,
                                AgentXIndexDeallocatePDU pdu,
                                AgentXMasterSession session,
                                VariableBinding[] vbs,
                                boolean testOnly) {
    int status = AgentXProtocol.AGENTX_SUCCESS;
    int i=0;
    for (; (i<vbs.length) && (status == AgentXProtocol.AGENTX_SUCCESS); i++) {
      VariableBinding vb = vbs[i];
      status = indexRegistry.release(session.getSessionID(),
                                     pdu.getContext(), vb, testOnly);
    }
    response.setErrorStatus(status);
    if (status != AgentXProtocol.AGENTX_SUCCESS) {
      response.setErrorIndex(i);
    }
    return status;
  }

  protected void processAgentXSearchResponse(AgentXPending pending,
                                             AgentXResponsePDU pdu) {
    if (pdu.getErrorStatus() != PDU.noError) {
      processsErrorResponse(pending, pdu);
    }
    else {
      // no error -> normal processing
      if (pending.getAgentXPDU().getType() == AgentXPDU.AGENTX_GETBULK_PDU) {
        processAgentXNextResponse(pending, pdu, Integer.MAX_VALUE);
      }
      else {
        processAgentXNextResponse(pending, pdu,
                                  ((AgentXRequestPDU)pending.getAgentXPDU()).
                                  getRanges().length);
      }
    }
  }

  private SubRequestIterator
      processAgentXNextResponse(AgentXPending pending,
                                AgentXResponsePDU pdu,
                                int subRequestIndexUpperBound) throws
      NoSuchElementException
  {
    VariableBinding[] vbs = pdu.getVariableBindings();
    AgentXRequestPDU axReqPDU = (AgentXRequestPDU) pending.getAgentXPDU();
    SubRequestIterator subRequests = pending.getReferences();
    for (int i=0; (i<subRequestIndexUpperBound) && subRequests.hasNext(); i++) {
      SnmpSubRequest sreq = (SnmpSubRequest) subRequests.next();
      processNextSubRequest(vbs, axReqPDU, i, i, sreq);
    }
    return subRequests;
  }

  private void processNextSubRequest(VariableBinding[] vbs,
                                     AgentXRequestPDU axReqPDU,
                                     int vbIndex,
                                     int rangeIndex,
                                     SnmpSubRequest sreq) {
    MOScope srange = axReqPDU.getRanges()[rangeIndex];
    if (vbIndex < vbs.length) {
      VariableBinding vb = vbs[vbIndex];
      if (vb.getSyntax() == SMIConstants.EXCEPTION_END_OF_MIB_VIEW) {
        processEndOfMibView(sreq, srange, vb.getOid());
      }
      else if (!srange.covers(vb.getOid())) {
        processEndOfMibView(sreq, srange, null);
      }
      else if ((vb.isException()) ||
               (super.vacm.isAccessAllowed(sreq.getSnmpRequest().
                                           getViewName(),
                                           vb.getOid()) != VACM.VACM_OK)) {
        DefaultMOContextScope nscope = (DefaultMOContextScope) sreq.getScope();
        nscope.substractScope(srange);
        nscope.setUpperBound(null);
        nscope.setUpperIncluded(true);
        // reset query because scope changed!
        sreq.setQuery(null);
        sreq.getStatus().setProcessed(false);
      }
      else {
        sreq.getVariableBinding().setOid(vb.getOid());
        sreq.getVariableBinding().setVariable(vb.getVariable());
        sreq.getStatus().setPhaseComplete(true);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Assigned next subrequest "+sreq);
        }
        // Not needed here because bulk processing does it anyway:
        sreq.updateNextRepetition();
      }
    }
    else {
      // less VBs than expected
      processEndOfMibView(sreq, srange, null);
    }
  }

  private static void processEndOfMibView(SnmpSubRequest sreq, MOScope srange,
                                          OID oid) {
    if (srange.getUpperBound() == null) {
      // unbounded
      // set also all following repetitions to endOfMibView
      SubRequestIterator<? extends SubRequest> tail = sreq.repetitions();
      while (tail.hasNext()) {
        SubRequest sr = tail.next();
        if (oid == null) {
          sr.getVariableBinding().setOid(srange.getLowerBound());
        }
        else {
          sreq.getVariableBinding().setOid(oid);
        }
        sr.getVariableBinding().setVariable(Null.endOfMibView);
        sr.getStatus().setPhaseComplete(true);
      }
      return;
    }
    else {
      sreq.getStatus().setProcessed(false);
    }
    DefaultMOContextScope nscope = (DefaultMOContextScope) sreq.getScope();
    nscope.substractScope(srange);
    nscope.setUpperBound(null);
    nscope.setUpperIncluded(true);
    // reset query because scope changed!
    sreq.setQuery(null);
  }

  protected void processAgentXBulkResponse(AgentXPending pending,
                                           AgentXResponsePDU pdu) {
    if (pdu.getErrorStatus() != PDU.noError) {
      processsErrorResponse(pending, pdu);
    }
    else {
      AgentXGetBulkPDU requestPDU = (AgentXGetBulkPDU) pending.getAgentXPDU();
      VariableBinding[] vbs = pdu.getVariableBindings();
      int numBindings = vbs.length;
      int repeaters =
          requestPDU.getRanges().length - requestPDU.getNonRepeaters();
      if (numBindings - requestPDU.getNonRepeaters() >
          requestPDU.getMaxRepetitions() * repeaters) {
        LOGGER.warn("Bulk response with more repetitions ("+
                    ((numBindings - requestPDU.getNonRepeaters())/ repeaters)+
                    ") than max rep. "+requestPDU.getMaxRepetitions());
        numBindings = requestPDU.getMaxRepetitions() * repeaters
            + requestPDU.getNonRepeaters();
      }
      if (numBindings == 0) {
        // this is IMHO outside the AgentX/SNMP spec but it is in fact
        // needed to be interoperable with NET-SNMP sub-agent
        AgentXRequestPDU axReqPDU = (AgentXRequestPDU) pending.getAgentXPDU();
        SubRequestIterator subRequests = pending.getReferences();
        for (int i=0; subRequests.hasNext(); i++) {
          SnmpSubRequest sreq = (SnmpSubRequest) subRequests.next();
          MOScope srange = axReqPDU.getRanges()[i];
          processEndOfMibView(sreq, srange, null);
        }
      }
      else {
        // process non repeaters first
        SubRequestIterator it =
            processAgentXNextResponse(pending, pdu, requestPDU.getNonRepeaters());
        int nonRep = requestPDU.getNonRepeaters();
        for (int c = 0;
             (c+nonRep < requestPDU.getRanges().length) && it.hasNext(); c++) {
          int rangeIndex = c + nonRep;
          SnmpSubRequest sreq = (SnmpSubRequest) it.next();
          SubRequestIterator rsreq = sreq.repetitions();
          for (int r = 0; (nonRep + (r * repeaters) + c < numBindings) &&
               rsreq.hasNext(); r++) {
            SnmpSubRequest repetition = (SnmpSubRequest) rsreq.next();
/*
            System.err.println("nr="+nonRep+",r="+r+",repeaters="+repeaters+
                ",c="+c+",rangeIndex="+rangeIndex+",rep="+repetition);
*/
            processNextSubRequest(vbs, requestPDU, nonRep + (r * repeaters) + c,
                                  rangeIndex, repetition);
          }
        }
      }
    }
  }

  protected static void processsErrorResponse(AgentXPending pending,
                                              AgentXResponsePDU pdu) throws
      NoSuchElementException
  {
    SubRequestIterator<SnmpSubRequest> subRequests = pending.getReferences();
    for (int i=1; i<pdu.getErrorIndex(); i++) {
      if (subRequests.hasNext()) {
        subRequests.next();
      }
      else {
        pending.getRequest().setErrorStatus(PDU.genErr);
        return;
      }
    }
    if (subRequests.hasNext()) {
      SubRequest sreq = subRequests.next();
      RequestStatus status = sreq.getStatus();
      // handle AgentX specific error status values and map them to SNMP
      switch (pdu.getErrorStatus()) {
        case AgentXProtocol.AGENTX_INDEX_ALREADY_ALLOCATED:
        case AgentXProtocol.AGENTX_INDEX_NONE_AVAILABLE:
        case AgentXProtocol.AGENTX_INDEX_NOT_ALLOCATED:
        case AgentXProtocol.AGENTX_INDEX_WRONG_TYPE:
          status.setErrorStatus(PDU.resourceUnavailable);
          break;
        case AgentXProtocol.AGENTX_NOREG:
        case AgentXProtocol.AGENTX_NOT_OPEN:
        default:
          status.setErrorStatus(pdu.getErrorStatus());
          break;
      }
    }
    else {
      pending.getRequest().setErrorStatus(PDU.genErr);
    }
  }


  private static boolean checkAgentXResponse(AgentXResponsePDU pdu,
                                             AgentXPending pending) {
    switch (pending.getAgentXPDU().getType()) {
      case AgentXPDU.AGENTX_GET_PDU:
      case AgentXPDU.AGENTX_GETNEXT_PDU: {
        if (((AgentXRequestPDU) pending.getAgentXPDU()).getRanges().length !=
            pdu.size()) {
          pending.getRequest().setErrorStatus(PDU.genErr);
          return false;
        }
        break;
      }
      default: {
        // no check?
      }
    }
    return true;
  }

  protected AgentXResponsePDU ping(AgentXPingPDU pdu,
                                   AgentXMasterSession session) {
    AgentXResponsePDU response = createResponse(pdu, session);
    if (!checkIfContextIsSupported(pdu.getContext())) {
      response.setErrorStatus(AgentXProtocol.AGENTX_UNSUPPORTED_CONTEXT);
      return response;
    }
    return response;
  }

  protected AgentXResponsePDU notify(AgentXNotifyPDU pdu,
                                     AgentXMasterSession session) {
    AgentXResponsePDU response = createResponse(pdu, session);
    if (session != null) {
      if (!checkIfContextIsSupported(pdu.getContext())) {
        response.setErrorStatus(AgentXProtocol.AGENTX_UNSUPPORTED_CONTEXT);
        return response;
      }
      VariableBinding[] vbs = pdu.getVariableBindings();
      response.setVariableBindings(vbs);
      int payloadIndex = 1;
      OID trapoid = null;
      TimeTicks timestamp = new TimeTicks(getContextSysUpTime(DEFAULT_CONTEXT));

      if (vbs.length >= 1) {
        if (SnmpConstants.sysUpTime.equals(vbs[0].getOid())) {
          payloadIndex++;
          if ((vbs.length < 2) ||
              (!SnmpConstants.snmpTrapOID.equals(vbs[1].getOid()))) {
            response.setErrorStatus(AgentXProtocol.AGENTX_PROCESSING_ERROR);
            response.setErrorIndex(2);
          }
          else {
            timestamp = (TimeTicks) vbs[0].getVariable();
            trapoid = (OID) vbs[1].getVariable();
          }
        }
        else if (SnmpConstants.snmpTrapOID.equals(vbs[0].getOid())) {
          trapoid = (OID) vbs[0].getVariable();
        }
        else {
          response.setErrorStatus(AgentXProtocol.AGENTX_PROCESSING_ERROR);
          response.setErrorIndex(1);
        }
      }
      if (trapoid != null) {
        VariableBinding[] pvbs = new VariableBinding[vbs.length - payloadIndex];
        System.arraycopy(vbs, payloadIndex, pvbs, 0, pvbs.length);
        notify(pdu.getContext(), trapoid, timestamp, pvbs);
      }
    }
    return response;
  }

  protected TimeTicks getContextSysUpTime(OctetString context) {
    MasterContextInfo info = contextInfo.get(context);
    SysUpTime contextSysUpTime;
    if (info == null) {
      MOContextScope scope =
          new DefaultMOContextScope(context,
                                    SnmpConstants.sysUpTime, true,
                                    SnmpConstants.sysUpTime, true);
      ManagedObject mo = getManagedObject(context, new DefaultMOQuery(scope));
      if (mo instanceof SysUpTime) {
        contextSysUpTime = (SysUpTime) mo;
      }
      else {
        if (mo instanceof AssignableFromLong) {
          LOGGER.info("SysUpTime could not be found in '"+context+
              "' context but managed object "+mo+" found which has a long value. Using this to init a new SysUpTime.");
          contextSysUpTime = new SysUpTimeImpl();
          ((SysUpTimeImpl)contextSysUpTime).setValue(new TimeTicks(((AssignableFromLong)mo).toLong()));
        }
        else {
          LOGGER.warn("SysUpTime could not be found in '"+context+
              "' context, using a new instance instead");
          contextSysUpTime = new SysUpTimeImpl();
        }
      }
      contextInfo.put(context,
                      new MasterContextInfo(context, contextSysUpTime));
    }
    else {
      contextSysUpTime = info.getUpTime();
    }
    if (contextSysUpTime != null) {
      return contextSysUpTime.get();
    }
    return null;
  }

  public AgentXResponsePDU addAgentCaps(AgentXAddAgentCapsPDU pdu,
                                        AgentXMasterSession session) {
    AgentXResponsePDU response = createResponse(pdu, session);
    if (session != null) {
      if (!checkIfContextIsSupported(pdu.getContext())) {
        response.setErrorStatus(AgentXProtocol.AGENTX_UNSUPPORTED_CONTEXT);
        return response;
      }
      AgentCapabilityList agentCaps = getAgentCaps(pdu.getContext());
      if (agentCaps != null) {
        OID index = agentCaps.addSysOREntry(pdu.getId(), pdu.getDescr());
        session.addAgentCaps(pdu.getId(), index);
      }
    }
    return response;
  }

  protected AgentCapabilityList getAgentCaps(OctetString contextName) {
    MOContextScope scope =
        new DefaultMOContextScope(contextName,
                                  SnmpConstants.sysOREntry, true,
                                  SnmpConstants.sysOREntry.nextPeer(), false);
    ManagedObject mo = getManagedObject(contextName, new DefaultMOQuery(scope));
    if (mo instanceof AgentCapabilityList) {
      return (AgentCapabilityList)mo;
    }
    else {
      LOGGER.warn("SysOREntry managed object for context "+contextName+
                  " not found, instead found: "+mo);
    }
    return null;
  }

  private ManagedObject getManagedObject(OctetString contextName,
                                         MOQuery query) {
    ManagedObject mo = null;
    MOServer server = getServer(contextName);
    if (server != null) {
      mo = server.lookup(query);
    }
    return mo;
  }

  public AgentXResponsePDU removeAgentCaps(AgentXRemoveAgentCapsPDU pdu,
                                           AgentXMasterSession session) {
    AgentXResponsePDU response = createResponse(pdu, session);
    if (session != null) {
      OID index = session.removeAgentCaps(pdu.getId());
      AgentCapabilityList agentCaps = getAgentCaps(pdu.getContext());
      if (agentCaps != null) {
        MOTableRow ac = agentCaps.removeSysOREntry(index);
        if (ac == null) {
          response.setErrorStatus(AgentXProtocol.AGENTX_UNKNOWN_AGENTCAPS);
        }
      }
      else {
        response.setErrorStatus(AgentXProtocol.AGENTX_UNKNOWN_AGENTCAPS);
      }
    }
    return response;
  }

  public AgentXResponsePDU closeSession(AgentXClosePDU pdu,
                                        AgentXMasterSession session) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Subagent is closing session "+session+
                  " because "+pdu.getReason());
    }
    AgentXResponsePDU response = createResponse(pdu, session);
    if (session != null) {
      removeSession(session.getSessionID());
      removeAllRegistrations(session);
      session.setClosed(true);
    }
    return response;
  }

  public void closeSession(AgentXMasterSession session,
                           byte reason) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Closing sub-agent session "+session+" because "+reason);
    }
    AgentXClosePDU closePDU = new AgentXClosePDU(reason);
    try {
      agentX.send(closePDU,
                  session.createAgentXTarget(),
                  session.getPeer().getTransport(),
                  new AgentXPendingClose(session, closePDU), this);
    }
    catch (IOException ex) {
      LOGGER.error("Failed to send CloseSessionPDU to close session "+session+
                   ": "+ex.getMessage(), ex);
    }
    removeSession(session.getSessionID());
    removeAllRegistrations(session);
    session.setClosed(true);
  }


  protected synchronized void removeAllRegistrations(AgentXMasterSession session) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Removing all registrations (out of "+registrations.size()+
                   ") of session "+session);
    }
    for (Iterator<AgentXRegEntry> it = registrations.iterator(); it.hasNext(); ) {
      AgentXRegEntry r = it.next();
      if (r.getSession().equals(session)) {
        removeRegistration(r, it);
      }
    }
  }

  protected AgentXMasterSession getSession(int sessionID) {
    return sessions.get(sessionID);
  }

  protected synchronized AgentXMasterSession getSession(AgentXPDU pdu) {
    int sessionID = pdu.getSessionID();
    return getSession(sessionID);
  }

  protected AgentXResponsePDU register(AgentXRegisterPDU pdu,
                                       AgentXCommandEvent command,
                                       AgentXMasterSession session) {
    AgentXResponsePDU response = createResponse(pdu, session);
    if (session != null) {
      if (!checkIfContextIsSupported(pdu.getContext())) {
        response.setErrorStatus(AgentXProtocol.AGENTX_UNSUPPORTED_CONTEXT);
        return response;
      }
      AgentXRegEntry regEntry =
          new AgentXRegEntry(session,
                             pdu.getRegion(),
                             pdu.getPriority() & 0xFF,
                             pdu.getContext(),
                             pdu.getTimeout());
      if (isDuplicate(regEntry)) {
        response.setErrorStatus(AgentXProtocol.AGENTX_DUPLICATE_REGISTRATION);
        return response;
      }
      AgentXMasterEvent event =
          new AgentXMasterEvent(this, AgentXMasterEvent.REGISTRATION_TO_ADD,
                                regEntry);
      fireMasterChanged(event);
      if (event.getVetoReason() == AgentXProtocol.AGENTX_SUCCESS) {
        try {
          addRegistration(regEntry);
        }
        catch (DuplicateRegistrationException drex) {
          if (LOGGER.isDebugEnabled()) {
            drex.printStackTrace();
          }
          response.setErrorStatus(AgentXProtocol.AGENTX_DUPLICATE_REGISTRATION);
          return response;
        }
      }
      else {
        response.setErrorStatus(event.getVetoReason());
      }
    }
    return response;
  }

  protected AgentXResponsePDU unregister(AgentXUnregisterPDU pdu,
                                         AgentXCommandEvent event,
                                         AgentXMasterSession session) {
    AgentXResponsePDU response = createResponse(pdu, session);
    if (session != null) {
      AgentXRegEntry regEntry =
          new AgentXRegEntry(session,
                             pdu.getRegion(),
                             pdu.getPriority() & 0xFF,
                             pdu.getContext(),
                             pdu.getTimeout());
      boolean found = false;
      for (Iterator<AgentXRegEntry> it = registrations.iterator(); it.hasNext(); ) {
        AgentXRegEntry r = it.next();
        if (r.equals(regEntry)) {
          found = true;
          if (!removeRegistration(r, it)) {
            response.setErrorStatus(AgentXProtocol.AGENTX_UNKNOWN_REGISTRATION);
          }
          break;
        }
      }
      if (!found) {
        response.setErrorStatus(AgentXProtocol.AGENTX_UNKNOWN_REGISTRATION);
      }
    }
    return response;
  }

  protected synchronized boolean isDuplicate(AgentXRegEntry registration) {
    if (registrations.contains(registration)) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Identical registration attempt for "+registration+" in "+registrations);
      }
      return true;
    }
    AgentXNodeQuery query =
        new AgentXNodeQuery(registration.getContext(),
                            registration.getRegion(),
                            AgentXNodeQuery.QUERY_NON_AGENTX_NODES);
    ManagedObject mo = getManagedObject(registration.getContext(), query);
    if (mo != null) {
      // overlaps non AgentX --> return duplicate region error
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("New registration is rejected as duplicate because it "+
                     "overlaps with non AgentX managed object: "+mo);
      }
      return true;
    }
    return false;
  }

  protected synchronized void addRegistration(AgentXRegEntry registration)
      throws DuplicateRegistrationException
  {
    registrations.add(registration);
    if (registration.getRegion().isRange()) {
      AgentXRegion r = registration.getRegion();
      long start = r.getLowerBoundSubID() & 0xFFFFFFFFL;
      long stop = r.getUpperBoundSubID() & 0xFFFFFFFFL;
      if (start > stop) {
        LOGGER.warn("Empty range registration "+registration);
      }
      else {
        for (long s = start; s <= stop; s++) {
          OID root = new OID(r.getLowerBound());
          root.set(r.getRangeSubID()-1, (int)s);
          AgentXRegion sr = new AgentXRegion(root, root.nextPeer());
          addRegion(registration, sr);
        }
      }
    }
    else {
      addRegion(registration, registration.getRegion());
    }
    AgentXMasterEvent e =
        new AgentXMasterEvent(this, AgentXMasterEvent.REGISTRATION_ADDED,
                              registration);
    fireMasterChanged(e);
  }

  private static AgentXNodeQuery nextQuery(AgentXNodeQuery lastQuery,
                                           AgentXNode lastNode) {
    if (lastNode != null) {
      lastQuery.getMutableScope().setLowerBound(
          lastNode.getScope().getUpperBound());
      lastQuery.getMutableScope().setLowerIncluded(
          !lastNode.getScope().isUpperIncluded());
    }
    return lastQuery;
  }

  protected synchronized void addRegion(AgentXRegEntry registration,
                                        AgentXRegion region) throws
      DuplicateRegistrationException
  {
    if (region.isRange()) {
      String errText = "Regions with range cannot be added";
      LOGGER.error(errText);
      throw new IllegalArgumentException(errText);
    }
    AgentXNodeQuery query =
        new AgentXNodeQuery(registration.getContext(),
                            region,
                            AgentXNodeQuery.QUERY_AGENTX_NODES);
    AgentXNode lastNode = null;
    MOServer server = getServer(registration.getContext());
    AgentXNode node = (AgentXNode)server.lookup(query);
    if (node != null) {
      LinkedList<AgentXNode> splitted = new LinkedList<AgentXNode>();
      AgentXRegion r1 = new AgentXRegion(region);
      for (; ((node != null) && (r1 != null));
           node = (AgentXNode) server.lookup(nextQuery(query, lastNode))) {
        AgentXRegion r2 = new AgentXRegion(node.getScope().getLowerBound(),
                                           node.getScope().getUpperBound());
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Affected region r2="+r2+
                       " from registered region r1="+r1);
        }
        if (r2.covers(r1)) {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Region r2 covers r1 (r1="+r1+",r2="+r2+")");
          }
          oldRegionCoversNew(registration, node, splitted, r1, r2);
          r1 = null;
        }
        else if (r1.covers(r2)) {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Region r1 covers r2 (r1="+r1+",r2="+r2+")");
          }
          r1 = newRegionCoversOld(registration, lastNode,
                                  node, splitted, r1, r2);
        }
        else if ((r1.isOverlapping(r2)) &&
                 (r2.getLowerBound().compareTo(r1.getLowerBound()) < 0)) {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Region r1 ovelaps r2 and r2 < r1 (r1="+r1+
                         ",r2="+r2+")");
          }
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Shrinking node "+node+
                         " to "+r1.getLowerBound());
          }
          node.shrink(r1.getLowerBound());
          AgentXNode r2b =
              node.getClone(new AgentXRegion(r1.getLowerBound(),
                                             r2.getUpperBound()));
          r2b.addRegistration(registration);
          splitted.add(r2b);
          r1 = new AgentXRegion(r2.getUpperBound(), r1.getLowerBound());
        }
        // r1.overlaps(r2) and (r1.get_lower() < r2.get_lower())
        else {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Region r1 overlaps r2 and r1 < r2 (r1="+
                         r1+",r2="+r2+")");
          }
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Shrinking node "+node+
                        " to "+r1.getUpperBound());
          }
          node.shrink(r1.getUpperBound());
          AgentXNode r2b =
              node.getClone(new AgentXRegion(r1.getUpperBound(),
                                             r2.getUpperBound()));
          node.addRegistration(registration);
          splitted.add(r2b);
          AgentXNode r1a =
            new AgentXNode(new AgentXRegion(r1.getLowerBound(),
                                            r2.getLowerBound()), registration);
          splitted.add(r1a);
          r1 = null;
        }
        if (r1 != null) {
          if (r1.isEmpty()) {
            splitted.add(new AgentXNode(region, registration));
          }
          else {
            splitted.add(new AgentXNode(r1, registration));
          }
        }
        lastNode = node;
      }
      for (AgentXNode n : splitted) {
        server.register(n, registration.getContext());
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Registered splitted AgentX node: " + n);
        }
      }
    }
    else {
      node = new AgentXNode(region, registration);
      server.register(node, registration.getContext());
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Registered AgentX node: "+node);
      }
    }
  }

  protected boolean removeRegistration(AgentXRegEntry registration,
                                       Iterator<AgentXRegEntry> regIterator) {
    LinkedList<AgentXNode> remove = new LinkedList<AgentXNode>();
    AgentXRegion queryRegion = new AgentXRegion(registration.getRegion());
    queryRegion.setUpperIncluded(true);
    AgentXNodeQuery query =
        new AgentXNodeQuery(registration.getContext(),
                            queryRegion,
                            AgentXNodeQuery.QUERY_AGENTX_NODES);
    AgentXNode lastNode = null;
    MOServer server = getServer(registration.getContext());
    AgentXNode node = (AgentXNode)server.lookup(query);
    if (node != null) {
      for (; (node != null);
           node = (AgentXNode) server.lookup(nextQuery(query, lastNode))) {
        if (node == lastNode) {
          break;
        }
        if ((node.removeRegistration(registration)) &&
            (node.getRegistrationCount() == 0)) {
          remove.add(node);
        }
        else {
          if ((lastNode != null) &&
              (lastNode.getRegistrationCount() == 1) &&
              (node.getRegistrationCount() == 1) &&
              (lastNode.getScope().getUpperBound().equals(
                  node.getScope().getLowerBound())) &&
              (node.getActiveRegistration().equals(
                   lastNode.getActiveRegistration()))) {
            AgentXRegion r =
                new AgentXRegion(node.getScope().getLowerBound(),
                                 lastNode.getScope().getUpperBound());
            if (node.getActiveRegistration().getRegion().covers(r)) {
              remove.add(node);
              lastNode.expand(node.getScope().getUpperBound(), false);
            }
          }
        }
        lastNode = node;
      }
    }
    else {
      LOGGER.warn("A registration is removed with not associated subtree: "+
                  registration);
    }
    for (AgentXNode rnode : remove) {
      server.unregister(rnode, registration.getContext());
    }
    if (regIterator != null) {
      regIterator.remove();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Removed registration "+registration+
                     " by session close, "+registrations.size()+" left.");
      }
      fireMasterChanged(new AgentXMasterEvent(this,
                                              AgentXMasterEvent.REGISTRATION_REMOVED,
                                              registration));
      return true;
    }
    else if (registrations.remove(registration)) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Removed registration "+registration+
                     ", "+registrations.size()+" left.");
      }
      fireMasterChanged(new AgentXMasterEvent(this,
                                              AgentXMasterEvent.REGISTRATION_REMOVED,
                                              registration));
      return true;
    }
    return false;
  }

  private static AgentXRegion newRegionCoversOld(AgentXRegEntry registration,
                                                 AgentXNode lastNode,
                                                 AgentXNode node,
                                                 LinkedList<AgentXNode> splitted,
                                                 AgentXRegion r1,
                                                 AgentXRegion r2) {
    AgentXNode r1a;
    if (lastNode != null) {
      AgentXRegion r =
          new AgentXRegion(lastNode.getScope().getUpperBound(),
                           r2.getLowerBound());
      r1a = new AgentXNode(r, registration);
    }
    else {
      AgentXRegion r =
          new AgentXRegion(r1.getLowerBound(), r2.getLowerBound());
      r1a = new AgentXNode(r, registration);
    }
    if (!splitted.isEmpty()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Shrinking node "+splitted.getLast()+
                     " to "+r2.getLowerBound());
      }
      (splitted.getLast()).shrink(r2.getLowerBound());
    }
    node.addRegistration(registration);
    if ((r1.getLowerBound().equals(r2.getLowerBound())) ||
        ((!splitted.isEmpty()) &&
         ((splitted.getLast()).
          getScope().equals(r1a.getScope())))) {
      // release unused region:
      r1a = null;
    }
    else {
      splitted.add(r1a);
    }
    return new AgentXRegion(r2.getUpperBound(), r1.getUpperBound());
  }

  private static void oldRegionCoversNew(AgentXRegEntry registration,
                                         AgentXNode node,
                                         List<AgentXNode> splitted,
                                         AgentXRegion r1,
                                         AgentXRegion r2) {
    AgentXRegion r = new AgentXRegion(r1.getUpperBound(),
                                      node.getScope().getUpperBound());
    AgentXNode r2c = node.getClone(r);
    if (r2.getLowerBound().equals(r1.getLowerBound())) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Shrinking node "+node+" to "+r1.getUpperBound());
      }
      node.shrink(r1.getUpperBound());
      node.addRegistration(registration);
    }
    else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Shrinking node "+node+" to "+r1.getLowerBound());
      }
      node.shrink(r1.getLowerBound());
      AgentXNode r2b = node.getClone(r1);
      r2b.addRegistration(registration);
      splitted.add(r2b);
    }
    splitted.add(r2c);
  }

  public AgentXResponsePDU openSession(AgentXOpenPDU pdu,
                                       AgentXCommandEvent event) {
    AgentXMasterSession session =
        new AgentXMasterSession(getNextSessionID(), agentXQueue,
                                pdu.getSubagentID(), pdu.getSubagentDescr());
    AgentXPeer peer = getPeer(event.getPeerAddress());
    if (peer == null) {
      peer = new AgentXPeer(event.getPeerTransport(), event.getPeerAddress());
      addPeer(peer);
      LOGGER.warn("Added peer during session opening: "+peer+
                  " (peer should have been there already due "+
                  "to connection setup)");
    }
    session.setPeer(peer);
    session.setAgentXVersion(pdu.getVersion() & 0xFF);
    if (pdu.getTimeout() != 0) {
      session.setTimeout(pdu.getTimeout());
    }
    else {
      session.setTimeout(defaultTimeout);
    }
    int sessionAccepted = acceptSession(session);
    if (sessionAccepted == AgentXProtocol.AGENTX_SUCCESS) {
      addSession(session);
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Session " + session + " opened from "+peer.getAddress());
      }
    }
    else {
      LOGGER.warn("Session open rejected because "+sessionAccepted+" for "+
                  session+" from "+event.getPeerAddress());
    }
    AgentXResponsePDU response = createResponse(pdu, session);
    response.setErrorStatus((short)sessionAccepted);
    return response;
  }

  protected synchronized void addPeer(AgentXPeer peer) {
    peers.put(peer.getAddress(), peer);
    fireMasterChanged(new AgentXMasterEvent(this,
                                            AgentXMasterEvent.PEER_ADDED,
                                            peer));
  }

  protected synchronized AgentXPeer getPeer(Address address) {
    return peers.get(address);
  }

  protected int acceptSession(AgentXMasterSession session) {
    AgentXMasterEvent event =
        new AgentXMasterEvent(this, AgentXMasterEvent.SESSION_ADDED, session);
    fireMasterChanged(event);
    return event.getVetoReason();
  }

  protected synchronized void addSession(AgentXMasterSession session) {
    sessions.put(session.getSessionID(), session);
    fireMasterChanged(new AgentXMasterEvent(this,
                                            AgentXMasterEvent.SESSION_ADDED,
                                            session));
  }

  protected synchronized AgentXMasterSession removeSession(int sessionID) {
    AgentXMasterSession session = sessions.remove(sessionID);
    if (session != null) {
      fireMasterChanged(new AgentXMasterEvent(this,
                                              AgentXMasterEvent.SESSION_REMOVED,
                                              session));
    }
    return session;
  }

  protected AgentXResponsePDU createResponse(AgentXPDU request,
                                             AgentXSession session) {
    OctetString context = DEFAULT_CONTEXT;
    if (request instanceof AgentXContextPDU) {
      OctetString reqContext = ((AgentXContextPDU) request).getContext();
      MOServer server = getServer(reqContext);
      if ((server != null) && server.isContextSupported(reqContext)) {
        context = reqContext;
      }
    }
    AgentXResponsePDU response =
        new AgentXResponsePDU(getContextSysUpTime(context).toInt(),
                              (short)0, (short)0);
    if (session == null) {
      response.setSessionID(request.getSessionID());
      response.setErrorStatus(AgentXProtocol.AGENTX_NOT_OPEN);
    }
    else {
      response.setSessionID(session.getSessionID());
    }
    response.setPacketID(request.getPacketID());
    response.setTransactionID(request.getTransactionID());
    response.setByteOrder(request.getByteOrder());
    return response;
  }

  protected void sendResponse(AgentXPDU response, AgentXSession session) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending AgentX response "+response+" to session "+session);
    }
    try {
      agentX.send(response,
                  session.createAgentXTarget(), session.getPeer().getTransport());
    }
    catch (IOException ex) {
      if (LOGGER.isDebugEnabled()) {
        ex.printStackTrace();
      }
      LOGGER.error("Failed to send AgentX response "+response+" to session "+
                   session+" because: "+ex.getMessage(), ex);
    }
  }

  public synchronized void connectionStateChanged(TransportStateEvent change) {
    Address peerAddress = change.getPeerAddress();
    switch (change.getNewState()) {
      case TransportStateEvent.STATE_CLOSED:
      case TransportStateEvent.STATE_DISCONNECTED_REMOTELY:
      case TransportStateEvent.STATE_DISCONNECTED_TIMEOUT: {
        AgentXPeer removedPeer = removePeer(peerAddress);
        fireMasterChanged(new AgentXMasterEvent(this,
                                                AgentXMasterEvent.PEER_REMOVED,
                                                removedPeer));
        break;
      }
      default: {
        AgentXPeer newPeer =
            new AgentXPeer((TransportMapping)change.getSource(), peerAddress);
        addPeer(newPeer);
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected synchronized AgentXPeer removePeer(Address peerAddress) {
    AgentXPeer peer = peers.remove(peerAddress);
    if (peer != null) {
      peer.setClosing(true);
      for (Iterator<AgentXMasterSession> it = sessions.values().iterator(); it.hasNext(); ) {
        AgentXMasterSession session = it.next();
        if (session.getPeer().equals(peer)) {
          it.remove();
          fireMasterChanged(new AgentXMasterEvent(this,
                                                  AgentXMasterEvent.SESSION_REMOVED,
                                                  session));
          indexRegistry.release(session.getSessionID());
          removeAllRegistrations(session);
          session.setClosed(true);
          if (peer.getTransport() instanceof ConnectionOrientedTransportMapping) {
            try {
              ((ConnectionOrientedTransportMapping)peer.getTransport()).
                  close(peer.getAddress());
            }
            catch (IOException iox) {
              LOGGER.warn("Caught exception while closing transport: " +
                          iox.getMessage());
            }
          }
        }
      }
/* Optional code for debugging of registry issues:
      if (LOGGER.isDebugEnabled()) {
        if (server instanceof DefaultMOServer) {
          SortedMap registry = ((DefaultMOServer) server).getRegistry();
          System.err.println(registry.toString());
        }
      }
*/
    }
    else {
      LOGGER.warn("Tried to remove peer with address "+peerAddress+
                  " which is not part of peer list: "+peers);
    }
    return peer;
  }

  @SuppressWarnings("unchecked")
  protected synchronized AgentXPeer closePeer(Address peerAddress, byte reason) {
    AgentXPeer peer = peers.remove(peerAddress);
    if (peer != null) {
      peer.setClosing(true);
      Map<Integer, AgentXMasterSession> s = new HashMap<Integer, AgentXMasterSession>(sessions);
      for (AgentXMasterSession session : s.values()) {
        if (session.getPeer().equals(peer)) {
          closeSession(session, reason);
          if (peer.getTransport() instanceof ConnectionOrientedTransportMapping) {
            try {
              ((ConnectionOrientedTransportMapping) peer.getTransport()).
                  close(peer.getAddress());
            } catch (IOException iox) {
              LOGGER.warn("Caught exception while closing transport: " +
                  iox.getMessage());
            }
          }
        }
      }
    }
    else {
      LOGGER.warn("Tried to remove peer with address "+peerAddress+
                  " which is not part of peer list: "+peers);
    }
    return peer;
  }

  public byte getAgentXVersion() {
    return AgentXProtocol.VERSION_1_0;
  }

  public synchronized void addAgentXMasterListener(AgentXMasterListener l) {
    if (agentXMasterListeners == null) {
      agentXMasterListeners = new Vector<AgentXMasterListener>(2);
    }
    agentXMasterListeners.add(l);
  }

  public synchronized void removeAgentXMasterListener(AgentXMasterListener l) {
    if (agentXMasterListeners != null) {
      agentXMasterListeners.remove(l);
    }
  }

  protected void fireMasterChanged(AgentXMasterEvent event) {
    final List<AgentXMasterListener> listenersFinalRef = agentXMasterListeners;
    if (listenersFinalRef != null) {
      List<AgentXMasterListener> listeners;
      synchronized (listenersFinalRef) {
        listeners = new ArrayList<AgentXMasterListener>(listenersFinalRef);
      }
      for (AgentXMasterListener listener : listeners) {
        try {
          listener.masterChanged(event);
        } catch (RuntimeException ex) {
          LOGGER.error("AgentXMasterListener " + listener +
              " threw exception on " + event + ": " + ex.getMessage(), ex);
        }
      }
    }
  }

  protected static class AgentXRegEntryComparator implements Comparator<AgentXRegEntry> {

    public int compare(AgentXRegEntry a, AgentXRegEntry b) {
      int c = a.getRegion().compareTo(b.getRegion());
      if (c == 0) {
        c = a.getContext().compareTo(b.getContext());
      }
      return c;
    }
  }

  public void onResponse(AgentXResponseEvent event) {
    AgentXResponsePDU pdu = event.getResponse();
    AgentXPending pending = (AgentXPending) event.getUserObject();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Processing AgentX response "+pdu+" for request "+pending);
    }
    if (pending.getRequest() != null) {
      AgentXPending p =
          agentXQueue.remove(pending.getAgentXPDU().getSessionID(),
                             pending.getRequest().getTransactionID());
      if (p == null) {
        LOGGER.warn("Pending AgentX request not found (may be timed out already): " +
                    "Received AgentX response from " + event.getPeerAddress() +
                    " for request " + event.getUserObject() +
                    " does not match any pending request:" + pdu);
        return;
      }
    }
    if ((pdu == null) &&
        (pending.getAgentXPDU().getType() != AgentXPDU.AGENTX_CLOSE_PDU)) {
      pending.getSession().incConsecutiveTimeouts();
      pending.getReferences().
          next().getStatus().setErrorStatus(PDU.genErr);
      if (pending.getSession().getConsecutiveTimeouts() >
          maxConsecutiveTimeouts) {
        closeSession(pending.getSession(), AgentXProtocol.REASON_TIMEOUTS);
      }
    }
    if (pdu != null) {
      pending.getSession().clearConsecutiveTimeouts();
    }
    if (pending.getRequest() != null) {
      MOServer server = getServer(pending.getRequest().getContext());
      if (requestList.contains(pending.getRequest())) {
        if (pdu != null) {
          if (checkAgentXResponse(pdu, pending)) {
            switch (pending.getAgentXPDU().getType()) {
              case AgentXPDU.AGENTX_GET_PDU: {
                processAgentXGetResponse(pending, pdu);
                break;
              }
              case AgentXPDU.AGENTX_GETNEXT_PDU: {
                processAgentXGetNextResponse(pending, pdu);
                break;
              }
              case AgentXPDU.AGENTX_GETBULK_PDU: {
                processAgentXBulkResponse(pending, pdu);
                break;
              }
              case AgentXPDU.AGENTX_CLEANUPSET_PDU:
              case AgentXPDU.AGENTX_UNDOSET_PDU:
              case AgentXPDU.AGENTX_COMMITSET_PDU:
              case AgentXPDU.AGENTX_TESTSET_PDU: {
                processAgentXSetResponse(pending, pdu);
                break;
              }
              default: {
                LOGGER.warn("Unhandled AgentX response " + pdu);
              }
            }
          }
          else {
            LOGGER.warn("Invalid AgentX response " + pdu +
                        " on request " + pending);
          }
        }
        // reprocess SNMP request
        if (!pending.getRequest().isComplete()) {
          reprocessRequest(server, pending.getRequest());
        }
        finalizeRequest(pending.getRequest().getSource(), pending.getRequest(),
                        server);
      }
      else {
        if (pending.getAgentXPDU().getType() == AgentXPDU.AGENTX_CLOSE_PDU) {
          if (pdu != null) {
            LOGGER.info("Subagent " + event.getPeerAddress() +
                        " confirmed close, disconnection transport now");
          }
          else {
            LOGGER.info("Subagent " + event.getPeerAddress() +
                        " did not answered on session close, " +
                        "disconnection now");
          }
          AgentXPeer peer = pending.getSession().getPeer();
          if (peer != null) {
            closePeer(peer);
          }
        }
        else {
          LOGGER.info("Received late response " + pdu + " on AgentX request: " +
                      pending);
          super.release(server, pending.getRequest());
        }
      }
    }
  }

  protected void processAgentXGetResponse(AgentXPending pending,
                                          AgentXResponsePDU pdu) {
    if (pdu.getErrorStatus() != PDU.noError) {
      processsErrorResponse(pending, pdu);
    }
    else {
      VariableBinding[] vbs = pdu.getVariableBindings();
      SubRequestIterator subRequests = pending.getReferences();
      for (int i=0; (i<pending.getRequest().size()) &&
           subRequests.hasNext(); i++) {
        SnmpSubRequest sreq = (SnmpSubRequest) subRequests.next();
        sreq.getVariableBinding().setVariable(vbs[i].getVariable());
        sreq.getStatus().setPhaseComplete(true);
      }
    }
  }

  protected void processAgentXGetNextResponse(AgentXPending pending,
                                              AgentXResponsePDU pdu) {
    if (pdu.getErrorStatus() != PDU.noError) {
      processsErrorResponse(pending, pdu);
    }
    else {
      processAgentXNextResponse(pending, pdu, pending.getRequest().size());
    }
  }

  protected void processAgentXSetResponse(AgentXPending pending,
                                          AgentXResponsePDU pdu) {
    if (pdu.getErrorStatus() != PDU.noError) {
      processsErrorResponse(pending, pdu);
    }
    else {
      SubRequestIterator<SnmpSubRequest> it = pending.getReferences();
      while (it.hasNext()) {
        SubRequest sreq = it.next();
        sreq.getStatus().setPhaseComplete(true);
      }
    }
  }

}
