/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXSubagent.java  
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

package org.snmp4j.agent.agentx.subagent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import org.snmp4j.PDU;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.*;
import org.snmp4j.agent.agentx.*;
import org.snmp4j.agent.agentx.event.PingEvent;
import org.snmp4j.agent.agentx.event.PingListener;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.snmp.CoexistenceInfo;
import org.snmp4j.agent.request.*;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.ConnectionOrientedTransportMapping;
import org.snmp4j.transport.TransportMappings;
import org.snmp4j.util.ThreadPool;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.agentx.subagent.index.AnyNewIndexOID;
import org.snmp4j.agent.agentx.subagent.index.NewIndexOID;

import java.util.Map.Entry;
import org.snmp4j.util.WorkerPool;
import org.snmp4j.util.WorkerTask;
import org.snmp4j.agent.mo.MOTable;

/**
 * The <code>AgentXSubagent</code> class implements the AgentX communication
 * for an AgentX subagent implementation.
 *
 * @author Frank Fock
 * @version 1.1
 */
public class AgentXSubagent
    implements AgentXCommandListener, NotificationOriginator {

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(AgentXSubagent.class);

  private ArrayList<MOServer> moServers = new ArrayList<MOServer>();
  private WorkerPool threadPool;
  private RequestFactory<AgentXCommandEvent,AgentXResponsePDU,AgentXRequest> factory;
  private AgentX agentX;
  protected Map<RequestID, AgentXRequest> requestList;

  protected Map<Address, AgentXPeer> peers = new LinkedHashMap<Address, AgentXPeer>(2);
  protected Map<Integer, AgentXSession> sessions = new Hashtable<Integer, AgentXSession>(2);


  protected RequestHandler<AgentXRequest> requestHandlerGet;
  protected RequestHandler<AgentXRequest> requestHandlerGetNext;
  protected RequestHandler<AgentXRequest> requestHandlerGetBulk;
  protected RequestHandler<AgentXRequest> requestHandlerTestSet;
  protected RequestHandler<AgentXRequest> requestHandlerCommitSet;
  protected RequestHandler<AgentXRequest> requestHandlerUndoSet;
  protected RequestHandler<AgentXRequest> requestHandlerCleanupSet;

  protected int nextTransactionID = 0;
  
  protected Map<String, AgentXSharedMOTableSupport> sharedMOTableSupport = 
      Collections.synchronizedMap(new HashMap<String, AgentXSharedMOTableSupport>(5));

  private OID subagentID;
  private OctetString subagentDescr;

  private long timeout = AgentXProtocol.DEFAULT_TIMEOUT_SECONDS * 1000;
  private byte defaultPriority = AgentXProtocol.DEFAULT_PRIORITY;

  private Timer pingTimer;
  private transient Vector<PingListener> pingListeners;


  public AgentXSubagent(AgentX agentX,
                        OID subagentID, OctetString subagentDescr) {
    this.requestList = Collections.synchronizedMap(new HashMap<RequestID, AgentXRequest>(10));
    this.agentX = agentX;
    this.subagentID = subagentID;
    this.subagentDescr = subagentDescr;
    this.factory = new DefaultAgentXRequestFactory();
    requestHandlerGet = new GetRequestHandler();
    requestHandlerCleanupSet = new CleanupSetHandler();
    requestHandlerCommitSet = new CommitSetHandler();
    requestHandlerTestSet = new TestSetHandler();
    requestHandlerUndoSet = new UndoSetHandler();
    requestHandlerGetNext = new GetNextHandler();
    requestHandlerGetBulk = new GetBulkHandler();
    agentX.addCommandResponder(this);
  }

  /**
   * Sets the ping delay in seconds. If greater than zero, for each session
   * a ping PDU is sent to the master to validate the session regularly with
   * the specified delay. To monitor the ping requests, it is necessary to
   * add a {@link PingListener} with {@link #addPingListener}.
   *
   * @param seconds
   *    the delay. If zero or a negative value is supplied, no pings are sent
   */
  public void setPingDelay(int seconds) {
    if (pingTimer != null) {
      pingTimer.cancel();
      pingTimer = null;
    }
    if (seconds > 0) {
      pingTimer = new Timer();
      pingTimer.schedule(new PingTask(), seconds * 1000, seconds * 1000);
    }
  }

  public void processCommand(AgentXCommandEvent event) {
    if (event.getCommand() != null) {
      event.setProcessed(true);
      Command command = new Command(event);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Processing AgentX PDU: "+event.getCommand());
      }
      if (threadPool != null) {
        threadPool.execute(command);
      }
      else {
        command.run();
      }
    }
  }

  protected synchronized int getNextTransactionID() {
    return nextTransactionID++;
  }

  protected int closeSession(int sessionID, byte reason) throws
      IOException {
    AgentXSession session;
    synchronized (this) {
      session = removeSession(sessionID);
      if ((session == null) || (session.isClosed())) {
        return AgentXProtocol.AGENTX_NOT_OPEN;
      }
      session.setClosed(true);
    }
    AgentXClosePDU closePDU =
        new AgentXClosePDU(AgentXProtocol.REASON_SHUTDOWN);
    closePDU.setSessionID(sessionID);
    AgentXTarget target = session.createAgentXTarget();
    AgentXResponseEvent resp =
        agentX.send(closePDU, target, session.getPeer().getTransport());
    if ((resp == null) || (resp.getResponse() == null)) {
      return AgentXProtocol.AGENTX_TIMEOUT;
    }
    return resp.getResponse().getErrorStatus();
  }

  protected int openSession(TransportMapping transport,
                            Address masterAddress,
                            AgentXSession session) throws IOException {
    AgentXOpenPDU openPDU = new AgentXOpenPDU(0, getNextTransactionID(),
                                              0, session.getTimeout(),
                                              subagentID, subagentDescr);
    AgentXResponseEvent responseEvent =
        agentX.send(openPDU, session.createAgentXTarget(), transport);
    if (responseEvent.getResponse() == null) {
      LOGGER.error("Timeout on connection to master "+masterAddress);
    }
    else if (responseEvent.getResponse() != null) {
      AgentXResponsePDU response = responseEvent.getResponse();
      if (response.getErrorStatus() == AgentXProtocol.AGENTX_SUCCESS) {
        session.setSessionID(response.getSessionID());
      }
      return response.getErrorStatus();
    }
    else {
      LOGGER.error("Received packet on open PDU is not a response AgentX PDU: "+
                   responseEvent);
    }
    return AgentXProtocol.AGENTX_TIMEOUT;
  }

  private static int getResponseStatus(AgentXResponseEvent responseEvent) {
    if (responseEvent.getResponse() == null) {
      LOGGER.error("Timeout on connection to master "+
                   responseEvent.getTarget());
      return AgentXProtocol.AGENTX_TIMEOUT;
    }
    else if (responseEvent.getResponse() != null) {
      AgentXResponsePDU response = responseEvent.getResponse();
      return response.getErrorStatus();
    }
    else {
      LOGGER.error("Received packet on open PDU is not a response AgentX PDU: "+
                   responseEvent);
    }
    return AgentXProtocol.AGENTX_ERROR;
  }

  @SuppressWarnings("unchecked")
  public void disconnect(Address masterAddress) throws IOException {
    AgentXPeer peer = peers.remove(masterAddress);
    if (peer != null) {
      TransportMapping transport = peer.getTransport();
      if (transport instanceof ConnectionOrientedTransportMapping) {
        ((ConnectionOrientedTransportMapping)transport).close(masterAddress);
      }
    }
  }

  public int connect(Address masterAddress, Address localAddress,
                     AgentXSession session) throws IOException {
    AgentXPeer peer = peers.get(masterAddress);
    TransportMapping transport;
    if (peer == null) {
      transport = addMaster(localAddress);
      peer = new AgentXPeer(transport, masterAddress);
    }
    else {
      transport = peer.getTransport();
      agentX.removeTransportMapping(transport);
      agentX.addTransportMapping(transport);
      if (!transport.isListening()) {
        transport.listen();
      }
    }
    peer.setTimeout(session.getTimeout());
    session.setPeer(peer);
    int status = AgentXProtocol.AGENTX_TIMEOUT;
    try {
      status = openSession(transport, masterAddress, session);
      if (status != AgentXProtocol.AGENTX_TIMEOUT) {
        peers.put(masterAddress, peer);
        LOGGER.info("Added new peer address="+masterAddress+",peer="+peer);
      }
    }
    catch (IOException ex) {
      LOGGER.error(ex);
      removeMaster(transport);
      return AgentXProtocol.AGENTX_ERROR;
    }
    if (status == AgentXProtocol.AGENTX_SUCCESS) {
      sessions.put(session.getSessionID(), session);
      LOGGER.info("Opened subagent session successfully: "+session);
    }
    else {
      removeMaster(transport);
    }
    return status;
  }

  public int close(AgentXSession session, byte reason) throws IOException {
    return closeSession(session.getSessionID(), reason);
  }

  private synchronized AgentXSession getSession(int sessionID) {
    return sessions.get(sessionID);
  }

  private synchronized AgentXSession removeSession(int sessionID) {
    return sessions.remove(sessionID);
  }

  public void setDefaultPriority(byte priority) {
    this.defaultPriority = priority;
  }

  public byte getDefaultPriority() {
    return defaultPriority;
  }

  /**
   * Gets the priority with which the supplied managed object and
   * region should be registered at the master agent. Overwrite
   * this method to use individual priorities depending on the registered
   * region/managed object. The default implementation returns
   * {@link #getDefaultPriority()}.
   *
   * @param mo ManagedObject
   *    a managed object instance that manages <code>region</code>.
   * @param region
   *    the region to be registered.
   * @return
   *    the priority between 0 and 255 (lower value results in higher priority).
   */
  protected byte getPriority(ManagedObject mo, AgentXRegion region) {
    return defaultPriority;
  }

  /**
   * Registers the subagent regions at the master agent. It uses the
   * {@link AgentXSharedMOTableSupport} instances of {@link AgentXSharedMutableMOTable}
   * instances. For any other instances a support object instance will
   * be created for each session and context.
   * @param session
   *    the session on whose behalf regions are registered.
   * @param context
   *    the context to use for registration.
   * @param sysUpTime
   *    if not <code>null</code>, the master agent's notion of the sysUpTime
   *    for the registered context is returned. The input value is always
   *    ignored!
   * @param registrationCallback
   *    a possibly <code>null</code> reference to a
   *    <code>RegistrationCallback</code> instance to handle registration
   *    events.
   */
  public void registerRegions(AgentXSession session, OctetString context,
                              TimeTicks sysUpTime,
                              RegistrationCallback registrationCallback) {
    MOServer server = getServer(context);
    if (server == null) {
      LOGGER.warn("No MOServer found for context '"+context+"'");
      return;
    }
    for (Iterator it = server.iterator(); it.hasNext();) {
      Entry e = (Entry) it.next();
      ManagedObject mo = (ManagedObject)e.getValue();
      MOScope scope = (MOScope) e.getKey();
      if (context != null) {
        if ((scope instanceof MOContextScope) &&
            (!context.equals(((MOContextScope) scope).getContext()))) {
          continue;
        }
      }
      if (mo instanceof AgentXSharedMOTable) {
        AgentXSharedMOTableSupport sharedTableSupport = null;
        if (mo instanceof AgentXSharedMutableMOTable) {
          sharedTableSupport = ((AgentXSharedMutableMOTable)mo).getAgentXSharedMOTableSupport();
        }
        String sharedTableSupportKey = session.getSessionID()+"#"+context;
        if (sharedTableSupport == null) {
          sharedTableSupport = sharedMOTableSupport.get(sharedTableSupportKey);
        }
        if (sharedTableSupport == null) {
          sharedTableSupport = createSharedTableSupport(session, context);
          sharedMOTableSupport.put(sharedTableSupportKey, sharedTableSupport);
        }
        registerSharedTableRows(session, context,
                                (AgentXSharedMOTable)mo,
                                registrationCallback, sharedTableSupport);
      }
      else {
        AgentXRegion region =
            new AgentXRegion(scope.getLowerBound(), scope.getUpperBound());
        if (mo instanceof MOScalar) {
          region.setSingleOID(true);
        }
        region.setUpperIncluded(scope.isUpperIncluded());
        try {
          int status = registerRegion(session, context, region,
                                      getPriority(mo, region), sysUpTime);
          if (status != AgentXProtocol.AGENTX_SUCCESS) {
            if (LOGGER.isWarnEnabled()) {
              LOGGER.warn("Failed to registered MO " + scope +
                          " with status = " +
                          status);
            }
          }
          else {
            if (LOGGER.isInfoEnabled()) {
              LOGGER.info("Registered MO " + scope + " successfully");
            }
          }
          if (registrationCallback != null) {
            registrationCallback.registrationEvent(context, mo, status);
          }
        }
        catch (IOException ex) {
          LOGGER.warn("Failed to register " + mo + " in context '" + context +
                      "' of session " + session);
          if (registrationCallback != null) {
            registrationCallback.registrationEvent(context, mo,
                AgentXProtocol.AGENTX_ERROR);
          }
        }
      }
    }
  }

  /**
   * Create a new {@link AgentXSharedMOTableSupport} instance for the given AgentX session and context.
   * @param session
   *    an AgentXSession instance.
   * @param context
   *    a AgentX context.
   * @return
   *    a (new) AgentXSharedMOTableSupport instance.
   */
  protected AgentXSharedMOTableSupport createSharedTableSupport(AgentXSession session, OctetString context) {
    return new AgentXSharedMOTableSupport(agentX, session, context);
  }

  /**
   * Registers the indexes and (row) regions of a shared table. This method
   * is called on behalf of {@link #registerRegions(org.snmp4j.agent.agentx.AgentXSession,
   * org.snmp4j.smi.OctetString, org.snmp4j.smi.TimeTicks, RegistrationCallback)}.
   *
   * @param session
   *    the session on whose behalf regions are registered.
   * @param context
   *    the context to use for registration.
   * @param mo
   *    the <code>AgentXSharedMOTable</code> instance to register.
   * @param registrationCallback
   *    if not <code>null</code> the callback is informed when registration
   *    of a row succeeded or failed.
   * @deprecated
   *    Use {@link #registerSharedTableRows(org.snmp4j.agent.agentx.AgentXSession, org.snmp4j.smi.OctetString,
   *    AgentXSharedMOTable, RegistrationCallback, AgentXSharedMOTableSupport)} instead. This version creates
   *    a new table support object for each call (shared table) which is not recommended.
   */
  public void registerSharedTableRows(AgentXSession session,
                                      OctetString context,
                                      final AgentXSharedMOTable mo,
                                      RegistrationCallback registrationCallback) {
    registerSharedTableRows(session, context, mo, registrationCallback,
        new AgentXSharedMOTableSupport(agentX, session, context));
  }

  /**
   * Registers the indexes and (row) regions of a shared table. This method
   * is called on behalf of {@link #registerRegions(org.snmp4j.agent.agentx.AgentXSession,
   * org.snmp4j.smi.OctetString, org.snmp4j.smi.TimeTicks, RegistrationCallback)}.
   *
   * @param session
   *    the session on whose behalf regions are registered.
   * @param context
   *    the context to use for registration.
   * @param mo
   *    the <code>AgentXSharedMOTable</code> instance to register.
   * @param registrationCallback
   *    if not <code>null</code> the callback is informed when registration
   *    of a row succeeded or failed.
   * @param sharedTableSupport
   *    the shared table support to be used for row registration. If <code>mo</code> has
   *    no table support instance and is a {@link AgentXSharedMutableMOTable} then its
   *    sharedTableSupport will be set to <code>sharedTableSupport</code>.
   * @since 2.1
   */
  @SuppressWarnings("unchecked")
  public void registerSharedTableRows(AgentXSession session,
                                      OctetString context,
                                      final AgentXSharedMOTable mo,
                                      RegistrationCallback registrationCallback,
                                      AgentXSharedMOTableSupport sharedTableSupport) {
    synchronized (mo) {
      if ((mo instanceof AgentXSharedMutableMOTable) &&
          (((AgentXSharedMutableMOTable)mo).getAgentXSharedMOTableSupport() == null)) {
        ((AgentXSharedMutableMOTable)
         mo).setAgentXSharedMOTableSupport(sharedTableSupport);
      }
      // decouple iterator from table modifications (may still fail if table
      // is being modified while row list is copied - if such a concurrency is
      // needed a table model must be used that returns an table independent
      // iterator.
      ArrayList<MOTableRow> rows = new ArrayList<MOTableRow>(mo.getModel().getRowCount());
      for (Iterator<MOTableRow> it = mo.getModel().iterator(); it.hasNext();) {
        rows.add(it.next());
      }
      for (MOTableRow row : rows) {
        int retries = 0;
        int status;
        OID newIndex;
        do {
          newIndex = (OID) row.getIndex().clone();
          status = sharedTableSupport.allocateIndex(context, mo.getIndexDef(),
              (byte) AgentXSharedMOTableSupport.INDEX_MODE_ALLOCATE,
              newIndex);
        }
        while ((registrationCallback != null) &&
            registrationCallback.tableRegistrationEvent(context,
                mo, row, true, status, retries++));
        if (status == AgentXProtocol.AGENTX_SUCCESS) {
          if ((newIndex instanceof AnyNewIndexOID) ||
              (newIndex instanceof NewIndexOID)) {
            if (mo instanceof AgentXSharedMutableMOTable) {
              ((AgentXSharedMutableMOTable) mo).
                  changeRowIndex(row.getIndex(), newIndex);
            }
            break;
          }
          status = sharedTableSupport.registerRow(mo, row);
          if (status != AgentXProtocol.AGENTX_SUCCESS) {
            sharedTableSupport.deallocateIndex(context, mo.getIndexDef(),
                row.getIndex());
            LOGGER.warn("Failed to register row with " + status + " for " +
                row);
          }
          if (registrationCallback != null) {
            registrationCallback.tableRegistrationEvent(context,
                mo, row, false, status, retries);
          }
        } else {
          LOGGER.warn("Failed to allocate index with " + status + " for row " +
              row);
        }
      }
    }
  }

  protected int registerRegion(AgentXSession session,
                               OctetString context, AgentXRegion region,
                               byte priority,
                               TimeTicks sysUpTime) throws IOException {
    if ((session == null) || (session.isClosed())) {
      return AgentXProtocol.AGENTX_NOT_OPEN;
    }
    long t = (this.timeout == 0) ? session.getTimeout()*1000 : this.timeout;
    AgentXRegisterPDU pdu =
        new AgentXRegisterPDU(context, region.getLowerBound(), priority,
                              region.getRangeSubID(),
                              region.getUpperBoundSubID());
    pdu.setSessionAttributes(session);
    AgentXResponseEvent event =
        agentX.send(pdu, new AgentXTarget(session.getPeer().getAddress(), t),
                    session.getPeer().getTransport());
    if ((sysUpTime != null) && (event.getResponse() != null)) {
      sysUpTime.setValue(event.getResponse().getSysUpTime() & 0xFFFFFFFFL);
    }
    return getResponseStatus(event);
  }

  protected int unregisterRegion(AgentXSession session,
                                 OctetString context, AgentXRegion region,
                                 byte timeout) throws IOException {
    if ((session == null) || (session.isClosed())) {
      return AgentXProtocol.AGENTX_NOT_OPEN;
    }
    byte t = (timeout == 0) ? session.getTimeout() : timeout;
    AgentXUnregisterPDU pdu =
        new AgentXUnregisterPDU(context, region.getLowerBound(), t,
                                region.getRangeSubID(),
                                region.getUpperBoundSubID());
    pdu.setSessionAttributes(session);
    AgentXResponseEvent event =
        agentX.send(pdu, new AgentXTarget(session.getPeer().getAddress(),
                                          this.timeout),
                    session.getPeer().getTransport());
    return getResponseStatus(event);
  }



  protected TransportMapping addMaster(Address localAddress)
      throws IOException
  {
    TransportMapping transport =
          TransportMappings.getInstance().createTransportMapping(localAddress);
    if (transport instanceof ConnectionOrientedTransportMapping) {
      ConnectionOrientedTransportMapping tcpTransport =
          (ConnectionOrientedTransportMapping)transport;
      tcpTransport.setConnectionTimeout(0);
      tcpTransport.setMessageLengthDecoder(new AgentXProtocol());
    }
    agentX.addTransportMapping(transport);
    transport.listen();
    return transport;
  }

  protected void removeMaster(TransportMapping transport) {
    agentX.removeTransportMapping(transport);
    try {
      transport.close();
    }
    catch (IOException ex) {
      LOGGER.warn("Closing transport mapping "+transport+" failed with: "+
                  ex.getMessage());
    }
  }

  public synchronized void addMOServer(MOServer server) {
    moServers.add(server);
  }

  public synchronized void removeMOServer(MOServer server) {
    moServers.remove(server);
  }

  public synchronized MOServer getServer(OctetString context) {
    for (MOServer s : moServers) {
      if (s.isContextSupported(context)) {
        return s;
      }
    }
    return null;
  }

  public synchronized Collection<OctetString> getContexts() {
    LinkedList<OctetString> allContexts = new LinkedList<OctetString>();
    for (MOServer s : moServers) {
      OctetString[] contexts = s.getContexts();
      allContexts.addAll(Arrays.asList(contexts));
    }
    return allContexts;
  }

  public WorkerPool getWorkerPool() {
    return threadPool;
  }

  public void setThreadPool(ThreadPool threadPool) {
    this.threadPool = threadPool;
  }

  public void dispatchCommand(AgentXCommandEvent cmd) {
    boolean pendingSessionClose = false;
    if (cmd.getCommand().isConfirmedPDU()) {
      AgentXRequest request = null;
      MOServer server = null;
      int type = cmd.getCommand().getType();
      switch (type) {
        case AgentXPDU.AGENTX_GET_PDU: {
          request = (AgentXRequest) factory.createRequest(cmd, null);
          server = getServer(request.getContext());
          requestHandlerGet.processPdu(request, server);
          break;
        }
        case AgentXPDU.AGENTX_GETNEXT_PDU: {
          request = (AgentXRequest) factory.createRequest(cmd, null);
          server = getServer(request.getContext());
          requestHandlerGetNext.processPdu(request, server);
          break;
        }
        case AgentXPDU.AGENTX_GETBULK_PDU: {
          request = (AgentXRequest) factory.createRequest(cmd, null);
          server = getServer(request.getContext());
          requestHandlerGetBulk.processPdu(request, server);
          break;
        }
        case AgentXPDU.AGENTX_TESTSET_PDU: {
          request = (AgentXRequest) factory.createRequest(cmd, null);
          request.setPhase(Request.PHASE_2PC_PREPARE);
          server = getServer(request.getContext());
          requestHandlerTestSet.processPdu(request, server);
          requestList.put(createRequestID(cmd), request);
          break;
        }
        case AgentXPDU.AGENTX_COMMITSET_PDU:
        case AgentXPDU.AGENTX_UNDOSET_PDU:
        case AgentXPDU.AGENTX_CLEANUPSET_PDU: {
          RequestID reqID = createRequestID(cmd);
          request = requestList.get(reqID);
          if (request == null) {
            LOGGER.error("Request with ID "+reqID+" not found in request list");
            request = new AgentXRequest(cmd);
            request.setErrorStatus(AgentXProtocol.AGENTX_PROCESSING_ERROR);
            break;
          }
          server = getServer(request.getContext());
          switch (type) {
            case AgentXPDU.AGENTX_COMMITSET_PDU:
              request.setPhase(Request.PHASE_2PC_COMMIT);
              requestHandlerCommitSet.processPdu(request, server);
              break;
            case AgentXPDU.AGENTX_UNDOSET_PDU:
              request.setPhase(Request.PHASE_2PC_UNDO);
              requestHandlerUndoSet.processPdu(request, server);
              break;
            case AgentXPDU.AGENTX_CLEANUPSET_PDU:
              request.setPhase(Request.PHASE_2PC_CLEANUP);
              requestHandlerCleanupSet.processPdu(request, server);
              break;
            default: {
              LOGGER.fatal("Internal error");
            }
          }
          if (cmd.getCommand().getType() != AgentXPDU.AGENTX_COMMITSET_PDU) {
            // remove request from request list
            requestList.remove(reqID);
          }
          break;
        }
        case AgentXPDU.AGENTX_CLOSE_PDU: {
          AgentXSession session =
              removeSession(cmd.getCommand().getSessionID());
          if (session != null) {
            session.setClosed(true);
            pendingSessionClose = true;
          }
          break;
        }
        default: {
          LOGGER.error("Unhandled PDU type: "+cmd.getCommand());
          request = new AgentXRequest(cmd);
          request.setErrorStatus(AgentXProtocol.AGENTX_PROCESSING_ERROR);
        }
      }
      if (request != null) {
        // Since this is an AgentX subagent it only processes a single phase at
        // once.
        if ((type != AgentXPDU.AGENTX_CLEANUPSET_PDU) &&
            request.isPhaseComplete()) {
          // send response
          sendResponse(cmd, request);
        }
        if (server != null) {
          release(server, request);
        }
      }
      if (pendingSessionClose) {
        try {
          disconnect(cmd.getPeerAddress());
        }
        catch (IOException ex) {
          LOGGER.error("Failed to disconnect from master at "+
                       cmd.getPeerAddress()+": "+ex.getMessage(), ex);
        }
      }
    }
    else {
      processResponse(cmd);
    }
  }

  protected void sendResponse(AgentXCommandEvent cmd, AgentXRequest request) {
    AgentXMessageDispatcher dispatcher = cmd.getDispatcher();
    AgentXResponsePDU response = request.getResponsePDU();
    if (response != null) {
      AgentXPDU rpdu = cmd.getCommand();
      response.setSessionID(rpdu.getSessionID());
      response.setTransactionID(rpdu.getTransactionID());
      response.setByteOrder(rpdu.getByteOrder());
      response.setPacketID(rpdu.getPacketID());
      // only send a response if required
      try {
        dispatcher.send(cmd.getPeerTransport(),
                        cmd.getPeerAddress(), response, null);
      }
      catch (IOException ex) {
        LOGGER.warn("Failed to send AgentX response to '"+
                    cmd.getPeerAddress()+"' with error: "+ex.getMessage());
      }
    }
  }

  protected void release(MOServer server, AgentXRequest req) {
    for (Iterator<AgentXRequest.AgentXSubRequest> it = req.iterator(); it.hasNext();) {
      SubRequest sreq = it.next();
      if (sreq.getTargetMO() != null) {
        server.unlock(req, sreq.getTargetMO());
      }
    }
  }

  private static RequestID createRequestID(AgentXCommandEvent cmd) {
    return new RequestID(cmd.getPeerAddress(),
                         cmd.getCommand().getSessionID(),
                         cmd.getCommand().getTransactionID());

  }

  protected void processResponse(AgentXCommandEvent cmd) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Received response "+cmd);
    }
  }

  protected void processNextSubRequest(Request request, MOServer server,
                                       OctetString context,
                                       SubRequest sreq)
      throws NoSuchElementException
  {
    // We can be sure to have a default context scope here because
    // the inner class AgentXSubRequest creates it!
    DefaultMOContextScope scope =
        (DefaultMOContextScope)sreq.getScope();
    MOQuery query = sreq.getQuery();
    if (query == null) {
      query = new MOQueryWithSource(scope, false, request);
    }
    while (!sreq.getStatus().isProcessed()) {
      ManagedObject mo = server.lookup(query);
      if (mo == null) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("EndOfMibView at scope="+query.getScope()+
                       " and query "+query);
        }
        sreq.getVariableBinding().setVariable(Null.endOfMibView);
        sreq.getStatus().setPhaseComplete(true);
        break;
      }
      try {
        if (!mo.next(sreq)) {
          // We can be sure to have a default context scope here because
          // the inner class SnmpSubRequest creates it!
          // don't forget to update query:
          sreq.getVariableBinding().setVariable(Null.instance);
          scope.substractScope(mo.getScope());
          // query is updated automatically because scope is updated.
          query.substractScope(mo.getScope());
        }
      }
      catch (Exception moex) {
        if (LOGGER.isDebugEnabled()) {
          moex.printStackTrace();
        }
        LOGGER.warn(moex);
        if (sreq.getStatus().getErrorStatus() == PDU.noError) {
          sreq.getStatus().setErrorStatus(PDU.genErr);
        }
      }
    }
  }

  /**
   * Sends notifications (traps) to all appropriate notification targets
   * through the master agent.
   *
   * @param context the context name of the context on whose behalf this
   *   notification has been generated.
   * @param notificationID the object ID that uniquely identifies this
   *   notification. For SNMPv1 traps, the notification ID has to be build
   *   using the rules provided by RFC 2576.
   * @param vbs an array of <code>VariableBinding</code> instances
   *   representing the payload of the notification.
   * @return
   *    an {@link AgentXResponseEvent} instance or <code>null</code> if the
   *    notification request timed out.
   */
  public Object notify(OctetString context,
                       OID notificationID,
                       VariableBinding[] vbs) {
    return notify(context, notificationID, null, vbs);
  }

  public Object notify(OctetString context, OID notificationID,
                       TimeTicks sysUpTime, VariableBinding[] vbs) {
    AgentXSession session = firstSession();
    AgentXResponseEvent agentXResponse;
    try {
      agentXResponse =
          notify(session, context, notificationID, sysUpTime, vbs);
      if ((agentXResponse == null) || (agentXResponse.getResponse() == null)) {
        LOGGER.warn("Timeout on sending notification in context '"+context+
                   "' with ID '"+notificationID+"' and payload "+
                   Arrays.asList(vbs));
        return null;
      }
      return agentXResponse;
    }
    catch (IOException ex) {
      LOGGER.error("Failed to send notification in context '"+context+
                   "' with ID '"+notificationID+"' and payload "+
                   Arrays.asList(vbs)+", reason is: "+ex.getMessage());
      return null;
    }
  }

  /**
   * Returns the first session that have been opened by this subagent and is
   * still open. If no open session exists, <code>null</code> is returned.
   *
   * @return
   *    an <code>AgentXSession</code>.
   */
  public synchronized final AgentXSession firstSession() {
    if (sessions.size() > 0) {
      return sessions.values().iterator().next();
    }
    return null;
  }

  public AgentXResponseEvent notify(AgentXSession session,
                                    OctetString context,
                                    OID notificationID,
                                    TimeTicks sysUpTime,
                                    VariableBinding[] vbs) throws IOException {
    int offset = 1;
    if (sysUpTime != null) {
      offset = 2;
    }
    VariableBinding[] notifyVBs = new VariableBinding[vbs.length+offset];
    if (sysUpTime != null) {
      notifyVBs[0] = new VariableBinding(SnmpConstants.sysUpTime, sysUpTime);
    }
    notifyVBs[offset-1] =
        new VariableBinding(SnmpConstants.snmpTrapOID, notificationID);
    System.arraycopy(vbs, 0, notifyVBs, offset, vbs.length);
    AgentXNotifyPDU notifyPDU = new AgentXNotifyPDU(context, notifyVBs);
    notifyPDU.setSessionAttributes(session);
    notifyPDU.setTransactionID(getNextTransactionID());
    return agentX.send(notifyPDU, session.createAgentXTarget(),
                session.getPeer().getTransport());
  }

  public int addAgentCaps(AgentXSession session,
                          OctetString context, OID id, OctetString descr) {
    AgentXAddAgentCapsPDU pdu = new AgentXAddAgentCapsPDU(context, id, descr);
    pdu.setSessionAttributes(session);
    try {
      AgentXResponseEvent resp = agentX.send(pdu, session.createAgentXTarget(),
                                             session.getPeer().getTransport());
      if (resp.getResponse() == null) {
        return AgentXProtocol.AGENTX_TIMEOUT;
      }
      return resp.getResponse().getErrorStatus();
    }
    catch (IOException ex) {
      LOGGER.error("Failed to send AgentX AddAgentCaps PDU "+pdu+
                   " because: "+ex.getMessage(), ex);
      return AgentXProtocol.AGENTX_NOT_OPEN;
    }
  }

  public int removeAgentCaps(AgentXSession session,
                             OctetString context, OID id) {
    AgentXRemoveAgentCapsPDU pdu = new AgentXRemoveAgentCapsPDU(context, id);
    pdu.setSessionAttributes(session);
    try {
      AgentXResponseEvent resp = agentX.send(pdu, session.createAgentXTarget(),
                                             session.getPeer().getTransport());
      return resp.getResponse().getErrorStatus();
    }
    catch (IOException ex) {
      LOGGER.error("Failed to send AgentX RemoveAgentCaps PDU "+pdu+
                   " because: "+ex.getMessage(), ex);
      return AgentXProtocol.AGENTX_NOT_OPEN;
    }
  }

  public void addPingListener(PingListener l) {
    if (pingListeners == null) {
      pingListeners = new Vector<PingListener>();
    }
    pingListeners.add(l);
  }

  public void removePingListener(PingListener l) {
    if (pingListeners != null) {
      synchronized (pingListeners) {
        pingListeners.remove(l);
      }
    }
  }

  protected void firePinged(PingEvent event) {
    final Vector<PingListener> listeners = pingListeners;
    if (listeners != null) {
      synchronized (listeners) {
        for (PingListener listener : listeners) {
          listener.pinged(event);
        }
      }
    }
  }

  private static void initRequestPhase(Request request) {
    if (request.getPhase() == Request.PHASE_INIT) {
      request.nextPhase();
    }
  }

  static class GetRequestHandler implements RequestHandler<AgentXRequest> {

    public boolean isSupported(int pduType) {
      return pduType == AgentXPDU.AGENTX_GET_PDU;
    }

    public void processPdu(AgentXRequest request, MOServer server) {
      initRequestPhase(request);
      try {
        Iterator<AgentXRequest.AgentXSubRequest> it = request.iterator();
        while (it.hasNext()) {
          SubRequest sreq =  it.next();
          DefaultMOQuery query =
              new MOQueryWithSource((MOContextScope)sreq.getScope(),
                                    false, request);
          ManagedObject mo = server.lookup(query);
          if (mo == null) {
            sreq.getVariableBinding().setVariable(Null.noSuchObject);
            sreq.getStatus().setPhaseComplete(true);
            continue;
          }
          try {
            mo.get(sreq);
          }
          catch (Exception moex) {
            if (LOGGER.isDebugEnabled()) {
              moex.printStackTrace();
            }
            LOGGER.warn(moex);
            if (sreq.getStatus().getErrorStatus() == PDU.noError) {
              sreq.getStatus().setErrorStatus(PDU.genErr);
            }
          }
        }
      }
      catch (NoSuchElementException nsex) {
        if (LOGGER.isDebugEnabled()) {
          nsex.printStackTrace();
        }
        LOGGER.error("SubRequest not found");
        request.setErrorStatus(PDU.genErr);
      }
    }
  }

  class GetNextHandler implements RequestHandler<AgentXRequest> {

    public void processPdu(AgentXRequest request, MOServer server) {
      initRequestPhase(request);
      OctetString context = request.getContext();
      try {
        Iterator<AgentXRequest.AgentXSubRequest> it = request.iterator();
        while (it.hasNext()) {
          SubRequest sreq =  it.next();
          processNextSubRequest(request, server, context, sreq);
        }
      }
      catch (NoSuchElementException nsex) {
        if (LOGGER.isDebugEnabled()) {
          nsex.printStackTrace();
        }
        LOGGER.error("SubRequest not found");
        request.setErrorStatus(PDU.genErr);
      }
    }


    public boolean isSupported(int pduType) {
      return (pduType == PDU.GETNEXT);
    }

  }

  class GetBulkHandler implements RequestHandler<AgentXRequest> {

    public void processPdu(AgentXRequest request, MOServer server) {
      initRequestPhase(request);
      OctetString context = request.getContext();
      AgentXRequest req = (AgentXRequest)request;
      int nonRep = req.getNonRepeaters();
      try {
        Iterator<AgentXRequest.AgentXSubRequest> it = request.iterator();
        int i = 0;
        // non repeaters
        for (; ((i < nonRep) && it.hasNext()); i++) {
          SubRequest sreq =  it.next();
          processNextSubRequest(request, server, context, sreq);
        }
        // repetitions
        for (; it.hasNext(); i++) {
          SubRequest sreq =  it.next();
          processNextSubRequest(request, server, context, sreq);
        }
      }
      catch (NoSuchElementException nsex) {
        if (LOGGER.isDebugEnabled()) {
          nsex.printStackTrace();
        }
        LOGGER.error("SubRequest not found");
        request.setErrorStatus(PDU.genErr);
      }

    }

    public boolean isSupported(int pduType) {
      return (pduType == PDU.GETBULK);
    }

  }


  static class TestSetHandler implements RequestHandler<AgentXRequest> {

    public void processPdu(AgentXRequest request, MOServer server) {
      try {
        Iterator<AgentXRequest.AgentXSubRequest> it = request.iterator();
        while ((!request.isPhaseComplete()) && (it.hasNext())) {
          SubRequest sreq =  it.next();
          if (sreq.isComplete()) {
            continue;
          }
          DefaultMOQuery query =
              new MOQueryWithSource((MOContextScope)sreq.getScope(), false,
                                    request);
          ManagedObject mo = server.lookup(query);
          if (mo == null) {
            sreq.getStatus().setErrorStatus(PDU.notWritable);
            break;
          }
          sreq.setTargetMO(mo);
          server.lock(sreq.getRequest(), mo);
          try {
            mo.prepare(sreq);
            sreq.getStatus().setPhaseComplete(true);
          }
          catch (Exception moex) {
            if (sreq.getStatus().getErrorStatus() == PDU.noError) {
              sreq.getStatus().setErrorStatus(PDU.genErr);
            }
            LOGGER.error("Exception occurred while preparing SET request, "+
                         "returning genErr: "+moex.getMessage(), moex);
          }
        }
      }
      catch (NoSuchElementException nsex) {
        if (LOGGER.isDebugEnabled()) {
          nsex.printStackTrace();
        }
        LOGGER.error("Cannot find sub-request: ", nsex);
        request.setErrorStatus(PDU.genErr);
      }
    }

    public boolean isSupported(int pduType) {
      return (pduType == AgentXPDU.AGENTX_TESTSET_PDU);
    }
  }

  class UndoSetHandler implements RequestHandler<AgentXRequest> {

    public void processPdu(AgentXRequest request, MOServer server) {
      try {
        Iterator<AgentXRequest.AgentXSubRequest> it = request.iterator();
        while (it.hasNext()) {
          SubRequest sreq =  it.next();
          if (sreq.isComplete()) {
            continue;
          }
          ManagedObject mo = sreq.getTargetMO();
          if (mo == null) {
            DefaultMOQuery query =
                new DefaultMOQuery((MOContextScope)sreq.getScope(), true);
            mo = server.lookup(query);
          }
          if (mo == null) {
            sreq.getStatus().setErrorStatus(PDU.undoFailed);
            continue;
          }
          try {
            mo.undo(sreq);
            sreq.getStatus().setPhaseComplete(true);
          }
          catch (Exception moex) {
            if (LOGGER.isDebugEnabled()) {
              moex.printStackTrace();
            }
            LOGGER.error(moex);
            if (sreq.getStatus().getErrorStatus() == PDU.noError) {
              sreq.getStatus().setErrorStatus(PDU.undoFailed);
            }
          }
        }
      }
      catch (NoSuchElementException nsex) {
        if (LOGGER.isDebugEnabled()) {
          nsex.printStackTrace();
        }
        LOGGER.error("Cannot find sub-request: ", nsex);
        request.setErrorStatus(PDU.genErr);
      }
    }

    public boolean isSupported(int pduType) {
      return (pduType == AgentXPDU.AGENTX_UNDOSET_PDU);
    }
  }

  class CommitSetHandler implements RequestHandler<AgentXRequest> {

    public void processPdu(AgentXRequest request, MOServer server) {
      try {
        Iterator<AgentXRequest.AgentXSubRequest> it = request.iterator();
        while ((!request.isPhaseComplete()) && (it.hasNext())) {
          SubRequest sreq =  it.next();
          if (sreq.isComplete()) {
            continue;
          }
          ManagedObject mo = sreq.getTargetMO();
          if (mo == null) {
            DefaultMOQuery query =
                new DefaultMOQuery((MOContextScope)sreq.getScope(), true);
            mo = server.lookup(query);
          }
          if (mo == null) {
            sreq.getStatus().setErrorStatus(PDU.commitFailed);
            continue;
          }
          try {
            mo.commit(sreq);
            sreq.getStatus().setPhaseComplete(true);
          }
          catch (Exception moex) {
            if (LOGGER.isDebugEnabled()) {
              moex.printStackTrace();
            }
            LOGGER.error(moex);
            if (sreq.getStatus().getErrorStatus() == PDU.noError) {
              sreq.getStatus().setErrorStatus(PDU.commitFailed);
            }
          }
        }
      }
      catch (NoSuchElementException nsex) {
        if (LOGGER.isDebugEnabled()) {
          nsex.printStackTrace();
        }
        LOGGER.error("Cannot find sub-request: ", nsex);
        request.setErrorStatus(PDU.genErr);
      }
    }

    public boolean isSupported(int pduType) {
      return (pduType == AgentXPDU.AGENTX_COMMITSET_PDU);
    }

  }

  class CleanupSetHandler implements RequestHandler<AgentXRequest> {

    public void processPdu(AgentXRequest request, MOServer server) {
      try {
        Iterator<AgentXRequest.AgentXSubRequest> it = request.iterator();
        while (it.hasNext()) {
          SubRequest sreq =  it.next();
          if (sreq.isComplete()) {
            continue;
          }
          ManagedObject mo = sreq.getTargetMO();
          if (mo == null) {
            DefaultMOQuery query =
                new DefaultMOQuery((MOContextScope)sreq.getScope(), false);
            mo = server.lookup(query);
          }
          if (mo == null) {
            sreq.completed();
            continue;
          }
          server.unlock(sreq.getRequest(), mo);
          try {
            mo.cleanup(sreq);
            sreq.getStatus().setPhaseComplete(true);
          }
          catch (Exception moex) {
            if (LOGGER.isDebugEnabled()) {
              moex.printStackTrace();
            }
            LOGGER.error(moex);
          }
        }
      }
      catch (NoSuchElementException nsex) {
        if (LOGGER.isDebugEnabled()) {
          nsex.printStackTrace();
        }
        LOGGER.warn("Cannot find sub-request: "+ nsex.getMessage());
      }
    }

    public boolean isSupported(int pduType) {
      return (pduType == AgentXPDU.AGENTX_CLEANUPSET_PDU);
    }

  }


  static class DefaultAgentXRequestFactory
      implements RequestFactory<AgentXCommandEvent,AgentXResponsePDU,AgentXRequest> {

    public AgentXRequest createRequest(AgentXCommandEvent initiatingEvent,
                                 CoexistenceInfo cinfo) {
      AgentXRequest request = new AgentXRequest(initiatingEvent);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Creating AgentX request "+request+
                     " from "+initiatingEvent);
      }
      return request;
    }

  }

  class Command implements WorkerTask {

    private AgentXCommandEvent request;

    public Command(AgentXCommandEvent event) {
      this.request = event;
    }

    public void run() {
      dispatchCommand(request);
    }

    public void terminate() {
    }

    public void join() throws InterruptedException {
    }

    public void interrupt() {
    }

  }


  static class RequestID implements Comparable<RequestID> {
    private Address masterAddress;
    private int sessionID;
    private int transactionID;

    public RequestID(Address masterAddress, int sessionID, int transactionID) {
      this.masterAddress = masterAddress;
      this.sessionID = sessionID;
      this.transactionID = transactionID;
    }

    public int compareTo(RequestID other) {
      ByteBuffer ma = ByteBuffer.wrap(masterAddress.toByteArray());
      ByteBuffer oa = ByteBuffer.wrap(other.masterAddress.toByteArray());
      int c = ma.compareTo(oa);
      if (c == 0) {
        c = sessionID - other.sessionID;
        if (c == 0) {
          c = transactionID - other.transactionID;
        }
      }
      return c;
    }

    public boolean equals(Object obj) {
      return obj instanceof RequestID && (compareTo((RequestID) obj) == 0);
    }

    public int hashCode() {
      return transactionID;
    }

  }

  class PingTask extends TimerTask {

    public void run() {
      List<AgentXSession> l;
      synchronized (sessions) {
        l = new LinkedList<AgentXSession>(sessions.values());
      }
      for (AgentXSession session : l) {
        if (!session.isClosed()) {
          for (OctetString context : getContexts()) {
            AgentXPingPDU ping = new AgentXPingPDU(context);
            ping.setSessionAttributes(session);
            ping.setTransactionID(getNextTransactionID());
            PingEvent pingEvent;
            try {
              AgentXResponseEvent resp =
                  agentX.send(ping, session.createAgentXTarget(),
                      session.getPeer().getTransport());
              pingEvent = new PingEvent(this, session,
                  resp.getResponse());
            } catch (IOException ex) {
              pingEvent = new PingEvent(this, session, ex);
            }
            firePinged(pingEvent);
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("Fired ping event " + pingEvent);
            }
            if (pingEvent.isCloseSession() || pingEvent.isResetSession()) {
              try {
                closeSession(session.getSessionID(),
                    AgentXProtocol.REASON_TIMEOUTS);
                if (pingEvent.isResetSession()) {
                  reopenSession(session);
                }
              } catch (IOException ex1) {
                LOGGER.warn("IOException while resetting AgentX session in PingTask:"+
                    ex1.getMessage());
              }
            }
          }
        }
      }
    }

    /**
     * Reopens a closed session.
     *
     * @param session
     *    a closed AgentXSession instance.
     * @return
     *    {@link AgentXProtocol#AGENTX_SUCCESS} if the session could be opened
     *    successfully. Otherwise the AgentX error status is returned.
     * @throws IOException
     *    if the session cannot be reopened due to an IO exception.
     */
    public int reopenSession(AgentXSession session) throws IOException {
      return openSession(session.getPeer().getTransport(),
                         session.getPeer().getAddress(),
                         session);
    }

  }

}
