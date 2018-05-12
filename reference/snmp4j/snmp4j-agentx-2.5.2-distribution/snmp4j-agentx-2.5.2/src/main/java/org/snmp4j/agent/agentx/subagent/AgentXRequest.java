/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXRequest.java  
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

import java.util.*;

import org.snmp4j.*;
import org.snmp4j.mp.*;
import org.snmp4j.smi.*;
import org.snmp4j.agent.DefaultMOContextScope;
import org.snmp4j.agent.MOScope;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.agent.request.AbstractRequest;
import org.snmp4j.agent.request.SubRequest;
import org.snmp4j.agent.request.SubRequestIterator;
import org.snmp4j.agent.request.RequestStatusListener;
import org.snmp4j.agent.request.RequestStatus;
import org.snmp4j.agent.request.RequestStatusEvent;
import org.snmp4j.agent.agentx.AgentXResponsePDU;
import org.snmp4j.agent.agentx.AgentXGetBulkPDU;
import org.snmp4j.agent.agentx.AgentXRequestPDU;
import org.snmp4j.agent.agentx.AgentXVariableBindingPDU;
import org.snmp4j.agent.agentx.AgentXPDU;
import org.snmp4j.agent.agentx.AgentXContextPDU;
import org.snmp4j.agent.agentx.AgentXCommandEvent;
import org.snmp4j.agent.MOQuery;
import org.snmp4j.agent.request.SubRequestIteratorSupport;
import org.snmp4j.agent.request.Request;

/**
 * The <code>AgentXRequest</code> class represents AgentX sub-agent requests.
 * AgentX sub-agent requests are similar to SNMP requests but especially for
 * SET request processing and query scopes differences apply.
 *
 * @author Frank Fock
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class AgentXRequest 
    extends AbstractRequest<AgentXRequest.AgentXSubRequest, AgentXCommandEvent, AgentXResponsePDU> {

  private static final LogAdapter logger =
      LogFactory.getLogger(AgentXRequest.class);

  public static final OctetString DEFAULT_CONTEXT = new OctetString();

  protected Map<Object, Object> processingUserObjects;

  private static int nextTransactionID = 0;

  public AgentXRequest(AgentXCommandEvent request) {
    super(request);
    this.source = request;
    correctRequestValues();
    this.transactionID = nextTransactionID();
  }

  public static int nextTransactionID() {
    return nextTransactionID++;
  }

  public int size() {
    if (source.getCommand() instanceof AgentXRequestPDU) {
      return ((AgentXRequestPDU)source.getCommand()).size();
    }
    else if (source.getCommand() instanceof AgentXVariableBindingPDU) {
      return ((AgentXVariableBindingPDU)source.getCommand()).size();
    }
    return 0;
  }

  public boolean isBulkRequest() {
    return source.getCommand().getType() == AgentXPDU.AGENTX_GETBULK_PDU;
  }

  private void correctRequestValues() {
    AgentXPDU request = source.getCommand();
    if (request instanceof AgentXGetBulkPDU) {
      repeaterStartIndex = getNonRepeaters();
      repeaterRowSize =
          Math.max(size() - repeaterStartIndex, 0);
    }
    else {
      repeaterStartIndex = 0;
      repeaterRowSize = size();
    }
  }

  protected void setupSubRequests() {
    int capacity = size();
    int totalRepetitions = 0;
    if (source.getCommand() instanceof AgentXGetBulkPDU) {
      totalRepetitions = repeaterRowSize * getMaxRepetitions();
    }
    subrequests = new ArrayList<AgentXSubRequest>(capacity + totalRepetitions);
    if (response == null) {
      response = createResponse();
    }
    if (source.getCommand() instanceof AgentXRequestPDU) {
      AgentXRequestPDU rangeRequest =
          (AgentXRequestPDU)source.getCommand();
      MOScope[] ranges = rangeRequest.getRanges();
      for (int i = 0; i < ranges.length; i++) {
        AgentXSubRequest subReq =
            new AgentXSubRequest(
                new DefaultMOContextScope(getContext(), ranges[i]), i);
        addSubRequest(subReq);
      }
    }
    else if (source.getCommand() instanceof AgentXVariableBindingPDU) {
      AgentXVariableBindingPDU vbRequest =
          (AgentXVariableBindingPDU)source.getCommand();
      VariableBinding[] vbs = vbRequest.getVariableBindings();
      for (int i = 0; i < vbs.length; i++) {
        AgentXSubRequest subReq = new AgentXSubRequest(vbs[i], i);
        addSubRequest(subReq);
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("AgentXSubRequests initialized: "+subrequests);
    }
  }

  public int getMaxRepetitions() {
    if (source.getCommand() instanceof AgentXGetBulkPDU) {
      return ((AgentXGetBulkPDU)source.getCommand()).getMaxRepetitions()
          & 0xFFFF;
    }
    return 0;
  }

  public int getNonRepeaters() {
    if (source.getCommand() instanceof AgentXGetBulkPDU) {
      return ((AgentXGetBulkPDU)source.getCommand()).getNonRepeaters()
          & 0xFFFF;
    }
    return 0;
  }

  private void addSubRequest(AgentXSubRequest subReq) {
    subrequests.add(subReq);
    response.add(subReq.getVariableBinding());
  }

  protected int getMaxPhase() {
    return (is2PC()) ? PHASE_2PC_CLEANUP : PHASE_1PC;
  }

  public void setRequestEvent(AgentXCommandEvent source) {
    this.source = source;
  }

  protected void assignErrorStatus2Response() {
    int errStatus = getErrorStatus();
    response.setErrorStatus((short)errStatus);
    response.setErrorIndex((short)getErrorIndex());
  }

  private AgentXResponsePDU createResponse() {
    AgentXResponsePDU resp =
        new AgentXResponsePDU(0, (short)0, (short)0);
    resp.setTransactionID(transactionID);
    return resp;
  }

  public AgentXResponsePDU getResponsePDU() {
    return getResponse();
  }

  public AgentXResponsePDU getResponse() {
    if (response == null) {
      response = createResponse();
      assignErrorStatus2Response();
    }
    else {
      assignErrorStatus2Response();
    }
    if (is2PC()) {
      response.clear();
      if ((source.getCommand().getType() == AgentXPDU.AGENTX_CLEANUPSET_PDU) ||
          (source.getCommand().getType() == AgentXPDU.AGENTX_UNDOSET_PDU)) {
        return null;
      }
    }
    return response;
  }

  public Iterator<AgentXRequest.AgentXSubRequest> iterator() {
    initSubRequests();
    return new AgentXSubRequestIterator();
  }


  protected boolean is2PC() {
    return ((source.getCommand().getType() >= AgentXPDU.AGENTX_TESTSET_PDU) &&
            (source.getCommand().getType() <= AgentXPDU.AGENTX_CLEANUPSET_PDU));
  }

  public OctetString getContext() {
    if (source.getCommand() instanceof AgentXContextPDU) {
      return ((AgentXContextPDU)source.getCommand()).getContext();
    }
    return DEFAULT_CONTEXT;
  }

  public OctetString getViewName() {
    throw new UnsupportedOperationException();
  }

  public void setViewName(OctetString viewName) {
    throw new UnsupportedOperationException();
  }

  public int getSecurityLevel() {
    throw new UnsupportedOperationException();
  }

  public int getSecurityModel() {
    throw new UnsupportedOperationException();
  }

  public OctetString getSecurityName() {
    throw new UnsupportedOperationException();
  }

  public int getViewType() {
    throw new UnsupportedOperationException();
  }

  protected synchronized void addRepeaterSubRequest() {
    int predecessorIndex = subrequests.size() - repeaterRowSize;
    AgentXSubRequest sreq =
        new AgentXSubRequest(subrequests.get(predecessorIndex),
                             subrequests.size());
    addSubRequest(sreq);
  }

  /**
   * Returns the last repetition row that is complete (regarding the number
   * of elements in the row).
   * @return
   *    a sub list of the sub-requests list that contains the row's elements.
   *    If no such row exists <code>null</code> is returned.
   */
  private List<AgentXSubRequest> lastRow() {
    if (repeaterRowSize == 0) {
      return null;
    }
    int rows = (subrequests.size() - repeaterStartIndex) / repeaterRowSize;
    int startIndex = repeaterStartIndex + (repeaterRowSize*(rows-1));
    int endIndex = repeaterStartIndex + (repeaterRowSize*rows);
    return subrequests.subList(startIndex, endIndex);
  }

  public int getMessageProcessingModel() {
    throw new UnsupportedOperationException();
  }

  public String toString() {
    return getClass().getName()+"[subrequests="+subrequests+",phase="+phase+
        ",source="+source+"]";
  }

  public boolean isPhaseComplete() {
    if (errorStatus == SnmpConstants.SNMP_ERROR_SUCCESS) {
      initSubRequests();
      for (SubRequest subrequest : subrequests) {
        RequestStatus status = subrequest.getStatus();
        if (status.getErrorStatus() != SnmpConstants.SNMP_ERROR_SUCCESS) {
          return true;
        } else if (!status.isPhaseComplete()) {
          return false;
        }
      }
    }
    return true;
  }

  public synchronized Object getProcessingUserObject(Object key) {
    if (processingUserObjects != null) {
      return processingUserObjects.get(key);
    }
    return null;
  }

  public synchronized Object setProcessingUserObject(Object key, Object value) {
    if (processingUserObjects == null) {
      processingUserObjects = new HashMap<Object, Object>(5);
    }
    return processingUserObjects.put(key, value);
  }

  /**
   * The AgentXSubRequestIterator iterates over the subrequests in a AgentX
   * request. In case of bulk operations, it also may physically append
   * new sub-request instances while iterating, until the bulk operations
   * limits are reached.
   *
   * @author Frank Fock
   * @version 1.0
   */
  public class AgentXSubRequestIterator implements SubRequestIterator {

    private int cursor = 0;
    private int increment = 1;

    protected AgentXSubRequestIterator() {
    }

    protected AgentXSubRequestIterator(int offset, int increment) {
      this.cursor = offset;
      this.increment = increment;
    }

    private int getRepeaterCount() {
      AgentXPDU pdu = source.getCommand();
      if (pdu instanceof AgentXGetBulkPDU) {
        AgentXGetBulkPDU bulkPDU = (AgentXGetBulkPDU)pdu;
        return Math.max(bulkPDU.size() - bulkPDU.getNonRepeaters(), 0);
      }
      return 0;
    }

    public boolean hasNext() {
      AgentXPDU reqPDU = source.getCommand();
      if (reqPDU.getType() == AgentXPDU.AGENTX_GETBULK_PDU) {
        AgentXGetBulkPDU bulkPDU = (AgentXGetBulkPDU)reqPDU;
        if (cursor < Math.min(bulkPDU.size(), bulkPDU.getNonRepeaters())) {
          return true;
        }
        else {
          if (cursor < bulkPDU.getNonRepeaters() +
              bulkPDU.getMaxRepetitions() * getRepeaterCount()) {
            List<AgentXSubRequest> lastRow = lastRow();
            if (lastRow != null) {
              boolean allEndOfMibView = true;
              for (SubRequest sreq : lastRow) {
                if (sreq.getVariableBinding().getSyntax() !=
                    SMIConstants.EXCEPTION_END_OF_MIB_VIEW) {
                  allEndOfMibView = false;
                  break;
                }
              }
              if (allEndOfMibView) {
                return false;
              }
            }
            return true;
          }
        }
        return false;
      }
      return (cursor < size());
    }

    public SubRequest next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      if ((isBulkRequest()) &&
          (cursor >= subrequests.size())) {
        while (cursor >= subrequests.size()) {
          addRepeaterSubRequest();
        }
      }
      SubRequest sreq = subrequests.get(cursor);
      cursor += increment;
      return sreq;
    }

    public void remove() {
      throw new UnsupportedOperationException("Remove is not supported on sub-requests");
    }

    public boolean equals(Object other) {
      if (other instanceof AgentXRequest) {
        return ((AgentXRequest)other).getTransactionID() == getTransactionID();
      }
      return false;
    }

    public int hashCode() {
      return getTransactionID();
    }
  }


  /**
   * The <code>AgentXSubRequest</code> implements the <code>SubRequest</code>
   * interface for AgentX sub-requests.
   *
   * @author Frank Fock
   * @version 1.0
   */
  public class AgentXSubRequest implements SubRequest, RequestStatusListener {

    private RequestStatus status;
    private VariableBinding vb;
    private Object undoValue;
    private MOScope scope;
    private MOQuery query;
    private ManagedObject targetMO;
    private int index;

    private volatile Object userObject;

    private AgentXSubRequest(int index) {
      this.index = index;
      status = new RequestStatus();
      status.addRequestStatusListener(this);
    }

    protected AgentXSubRequest(MOScope searchRange, int index) {
      this(index);
      this.scope = searchRange;
      this.vb = new VariableBinding(searchRange.getLowerBound());
    }

    protected AgentXSubRequest(VariableBinding subrequest, int index) {
      this(index);
      this.vb = subrequest;
      OID oid = this.vb.getOid();
      this.scope = new DefaultMOContextScope(getContext(),
                                             oid, true, oid, true);
    }

    protected AgentXSubRequest(AgentXSubRequest predecessor, int index) {
      this(index);
      this.vb = new VariableBinding(predecessor.getVariableBinding().getOid());
      switch (source.getCommand().getType()) {
        case AgentXPDU.AGENTX_GETBULK_PDU:
        case AgentXPDU.AGENTX_GETNEXT_PDU: {
          this.scope =
              new DefaultMOContextScope(getContext(),
                                        predecessor.getVariableBinding().getOid(),
                                        false,
                                        predecessor.getScope().getUpperBound(),
                                        predecessor.getScope().isUpperIncluded());
          break;
        }
        default: {
          this.scope = new DefaultMOContextScope(getContext(),
                                                 predecessor.getScope());
        }
      }
//    Do not copy queries because they need to be updated externally only!
//    this.query = predecessor.getQuery();
    }

    public Request getRequest() {
      return AgentXRequest.this;
    }

    public RequestStatus getStatus() {
      return status;
    }

    public VariableBinding getVariableBinding() {
      return vb;
    }

    public void setStatus(RequestStatus status) {
      this.status = status;
    }

    public Object getUndoValue() {
      return undoValue;
    }

    public void setUndoValue(Object undoInformation) {
      this.undoValue = undoInformation;
    }

    public void requestStatusChanged(RequestStatusEvent event) {
      int newStatus = event.getStatus().getErrorStatus();
      AgentXRequest.this.setErrorStatus(newStatus);
      if (logger.isDebugEnabled() &&
          (newStatus != SnmpConstants.SNMP_ERROR_SUCCESS)) {
        new Exception("Error "+event.getStatus().getErrorStatus()+
                      " generated at: "+vb).printStackTrace();
      }
    }

    public MOScope getScope() {
      return scope;
    }

    public void completed() {
      status.setPhaseComplete(true);
    }

    public boolean hasError() {
      return getStatus().getErrorStatus() != SnmpConstants.SNMP_ERROR_SUCCESS;
    }

    public boolean isComplete() {
      return status.isPhaseComplete();
    }

    public void setTargetMO(ManagedObject managedObject) {
      this.targetMO = managedObject;
    }

    public ManagedObject getTargetMO() {
      return targetMO;
    }

    public int getIndex() {
      return index;
    }

    public void setQuery(MOQuery query) {
      this.query = query;
    }

    public MOQuery getQuery() {
      return query;
    }

    public String toString() {
      return getClass().getName()+"[scope="+scope+
          ",vb="+vb+",status="+status+",query="+query+",index="+index+
          ",targetMO="+targetMO+"]";
    }

    public SubRequestIterator repetitions() {
      initSubRequests();
      if (source.getCommand().getType() == AgentXPDU.AGENTX_GETBULK_PDU) {
        AgentXGetBulkPDU getBulk = (AgentXGetBulkPDU) source.getCommand();
        int repeaters = getBulk.size() - getBulk.getNonRepeaters();
        if (repeaters > 0) {
          return new AgentXSubRequestIterator(Math.max(getIndex(),getBulk.getNonRepeaters()), repeaters);
        }
        // return empty iterator
      }
      return new SubRequestIteratorSupport(Collections.EMPTY_LIST.iterator());
    }

    public void updateNextRepetition() {
      SubRequestIterator repetitions = repetitions();
      // skip this one
      repetitions.next();
      if (repetitions.hasNext()) {
        if ((getStatus().getErrorStatus() == PDU.noError) &&
            (!this.vb.isException())) {
          AgentXSubRequest nsreq =
              (AgentXSubRequest) repetitions.next();
          nsreq.query = null;
          nsreq.scope =
              new DefaultMOContextScope(getContext(),
                                        this.getVariableBinding().getOid(),
                                        false,
                                        this.getScope().getUpperBound(),
                                        this.getScope().isUpperIncluded());
        }
        else if (this.vb.isException()) {
          while (repetitions.hasNext()) {
            AgentXSubRequest nsreq =
                (AgentXSubRequest) repetitions.next();
            nsreq.query = null;
            nsreq.getVariableBinding().setOid(this.vb.getOid());
            nsreq.getVariableBinding().setVariable(this.vb.getVariable());
            nsreq.getStatus().setPhaseComplete(true);
          }
        }
      }
    }

    public final void setErrorStatus(int errorStatus) {
      getStatus().setErrorStatus(errorStatus);
    }

    public final int getErrorStatus() {
      return getStatus().getErrorStatus();
    }

    public Object getUserObject() {
      return userObject;
    }

    public void setUserObject(Object userObject) {
      this.userObject = userObject;
    }
  }
}

