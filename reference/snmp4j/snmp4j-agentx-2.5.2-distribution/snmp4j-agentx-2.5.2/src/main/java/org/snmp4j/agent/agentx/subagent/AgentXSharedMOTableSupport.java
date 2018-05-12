/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXSharedMOTableSupport.java  
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
import java.util.ArrayList;

import org.snmp4j.agent.agentx.*;
import org.snmp4j.agent.agentx.subagent.index.SubAgentXIndexRegistry;
import org.snmp4j.agent.mo.*;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.smi.*;
import org.snmp4j.agent.agentx.subagent.index.AnyNewIndexOID;
import org.snmp4j.agent.agentx.subagent.index.NewIndexOID;

/**
 * The <code>AgentXSharedMOTableSupport</code> provides helper functions for
 * shared table implementations to register rows and indexes at a master agent.
 *
 * @author Frank Fock
 * @version 2.2
 */
public class AgentXSharedMOTableSupport<R extends MOTableRow> implements MOTableRowListener<R> {

  public enum IndexStrategy {
    /**
     * Do not allocate any indexes at the master agent.
     */
    noIndexAllocation,
    /**
     * Only allocate an index value for the first sub-index of a row at the
     * master agent. This is the recommended strategy for most use cases.
     * Index values already allocated are not allocated again. A reference
     * count is being hold to guarantee proper deallocation.
     */
    firstSubIndexOnly,
    /**
     * Allocate for all sub-indexes of a row index as long as the sub-index
     * value has not yet been allocated by this shared table support instance.
     */
    anyNonAllocatedSubIndex,
    /**
     * Always allocate the first sub-index only and do not hold a local reference
     * count for allocated sub-indexes.
     */
    alwaysFirstSubIndex,
    /**
     * Always allocate any sub-index values and do not hold a local reference
     * count for allocated sub-indexes.
     */
    alwaysAnySubIndex }

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(AgentXSharedMOTableSupport.class);

  public static final int INDEX_MODE_ALLOCATE = AgentXProtocol.FLAG_ALLOCATE_INDEX;
  public static final int INDEX_MODE_ANY_INDEX = AgentXProtocol.FLAG_ANY_INDEX;
  public static final int INDEX_MODE_NEW_INDEX = AgentXProtocol.FLAG_NEW_INDEX;

  private AgentX agentX;
  private AgentXSession session;
  private OctetString context;
  private byte priority = AgentXProtocol.DEFAULT_PRIORITY;
  private byte indexMode = INDEX_MODE_ALLOCATE;

  /**
   * The strategy how multi-sub-index indexes are allocated.
   */
  private IndexStrategy indexStrategy = IndexStrategy.firstSubIndexOnly;

  protected SubAgentXIndexRegistry indexRegistry = new SubAgentXIndexRegistry();

  /**
   * Creates a shared table support object for a AgentX connection, session,
   * and context.
   *
   * @param agentX
   *    an AgentX connection.
   * @param session
   *    an AgentXSession session (does not need to be open at creation time).
   * @param context
   *    a context ("" by default).
   */
  public AgentXSharedMOTableSupport(AgentX agentX, AgentXSession session,
                                    OctetString context) {
    this.agentX = agentX;
    this.session = session;
    this.context = context;
    LOGGER.debug("SharedMOTableSupport created for "+session.getSessionID()+"#"+context);
  }

  /**
   * Creates a shared table support object for a AgentX connection, session,
   * and context.
   *
   * @param agentX
   *    an AgentX connection.
   * @param session
   *    an AgentXSession session (does not need to be open at creation time).
   * @param context
   *    a context ("" by default).
   * @param priority
   *    the registration priority used for this shared table support.
   * @param indexAllocationMode
   *    the index allocation mode to be used as default for this shared table.
   */
  public AgentXSharedMOTableSupport(AgentX agentX, AgentXSession session,
                                    OctetString context,
                                    byte priority, byte indexAllocationMode) {
    this(agentX, session, context);
    this.priority = priority;
    this.indexMode = indexAllocationMode;
  }

  /**
   * Process shared table row events. If index mode is
   * {@link #INDEX_MODE_ALLOCATE} this method will do nothing if the associated
   * AgentX session is closed. For other index modes, the event's veto status
   * will be set to the AgentX error {@link AgentXProtocol#AGENTX_NOT_OPEN}.
   * <p>
   * If the index OID of a created row has zero length then, depending on the
   * current index mode, a new or any new index is allocated at the master
   * agent.
   *
   * @param event
   *    a <code>MOTableRowEvent</code> indicating a row change in an AgentX
   *    shared table.
   */
  public void rowChanged(MOTableRowEvent<R> event) {
    if ((indexMode == INDEX_MODE_ALLOCATE) && getSession().isClosed()) {
      // ignore closed session for allocation mode
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Row event " + event +
                    " ignored, because session to master agent is closed: " +
                    getSession());
      }
      return;
    }
    switch (event.getType()) {
      case MOTableRowEvent.CREATE: {
        byte indexMode = getEffectiveIndexMode(event);
        OID index2Allocate = event.getRow().getIndex();
        int status =
            allocateIndex(context, event.getTable().getIndexDef(),
                          (event.getRow().getIndex().size() == 0) ?
                          indexMode : 0,
                          index2Allocate);
        if (status != AgentXProtocol.AGENTX_SUCCESS) {
          event.setVetoStatus(status);
        }
        break;
      }
      case MOTableRowEvent.ADD: {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Registering row for table event: "+event);
        }
        byte priority = getEffectivePriority(event);
        int status = registerRow(event.getTable(), event.getRow(), priority);
        if (status != AgentXProtocol.AGENTX_SUCCESS) {
          event.setVetoStatus(status);
        }
        break;
      }
      case MOTableRowEvent.DELETE: {
        byte priority = getEffectivePriority(event);
        int status = unregisterRow(event.getTable(), event.getRow(), priority);
        if ((status != AgentXProtocol.AGENTX_SUCCESS) &&
            (status != AgentXProtocol.AGENTX_UNKNOWN_REGISTRATION)) {
          event.setVetoStatus(status);
        }
        else {
          OID index2Deallocate = event.getRow().getIndex();
          deallocateIndex(context, event.getTable().getIndexDef(), index2Deallocate);
        }
        break;
      }
      default: {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Ignored AgentX shared table event "+event);
        }
      }
    }
  }

  protected byte getEffectiveIndexMode(MOTableRowEvent<R> event) {
    byte indexMode = this.indexMode;
    if (event.getTable() instanceof DefaultAgentXSharedMOTable) {
      byte om = ((DefaultAgentXSharedMOTable)event.getTable()).getOverrideIndexAllocationMode();
      if (om != 0) {
        indexMode = om;
      }
    }
    return indexMode;
  }

  protected byte getEffectivePriority(MOTableRowEvent<R> event) {
    byte priority = this.priority;
    if (event.getTable() instanceof DefaultAgentXSharedMOTable) {
      byte op = ((DefaultAgentXSharedMOTable)event.getTable()).getOverridePriority();
      if (op != 0) {
        priority = op;
      }
    }
    return priority;
  }

  /**
   * Allocate a new or any index at the master agent and return its value in
   * <code>allocateIndex</code>.
   * For the index strategies {@link IndexStrategy#firstSubIndexOnly} and
   * {@link IndexStrategy#anyNonAllocatedSubIndex} a local index registry
   * maintains a reference to all allocated index values. If an allocation
   * fails, the index value is removed from the registry.
   *
   * @param context
   *    the context for which to allocate the index. Specify an empty
   *    <code>OctetString</code> for the default context.
   * @param indexDef
   *    the index definition with OID values for sub-index definitions.
   * @param indexAllocationMode
   *    one of {@link AgentXProtocol#FLAG_ANY_INDEX},
   *    {@link AgentXProtocol#FLAG_NEW_INDEX}, or 0 (if index value is supplied
   *    by <code>allocateIndex</code>).
   * @param allocatedIndex
   *    the index value to allocate or if <code>indexAllocationMode</code> is
   *    not zero then an (arbitrary non-null OID) which returns the allocated
   *    new index value. If <code>allocateIndex</code> is an instance of
   *    {@link AnyNewIndexOID} or {@link NewIndexOID} the index value of the
   *    row will be replaced by a globally unique index value allocated by the
   *    master agent. The caller is responsible for changing the row's index
   *    in the table model of the shared table.
   * @return
   *    {@link AgentXProtocol#AGENTX_SUCCESS} if the index could be allocated
   *     or an AgentX protocol error code if allocation failed and
   *    <code>allocateIndex</code> is not altered.
   */
  public int allocateIndex(OctetString context,
                           MOTableIndex indexDef,
                           byte indexAllocationMode,
                           OID allocatedIndex) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Allocating index "+allocatedIndex+
                   " with strategy "+indexStrategy+" for index definition "+indexDef+ "with mode "+indexAllocationMode);
    }
    if (indexStrategy == IndexStrategy.noIndexAllocation) {
      return AgentXProtocol.AGENTX_SUCCESS;
    }
    VariableBinding[] vbs = new VariableBinding[indexDef.size()];
    Variable[] indexValues;
    if (allocatedIndex instanceof AnyNewIndexOID) {
      indexAllocationMode = INDEX_MODE_ANY_INDEX;
    }
    else if (allocatedIndex instanceof NewIndexOID) {
      indexAllocationMode = INDEX_MODE_NEW_INDEX;
    }
    if (indexAllocationMode == 0) {
      indexValues = indexDef.getIndexValues(allocatedIndex);
    }
    else {
      indexValues = new Variable[indexDef.size()];
      for (int i=0; i<indexDef.size(); i++) {
        MOTableSubIndex subIndex = indexDef.getIndex(i);
        indexValues[i] =
            AbstractVariable.createFromSyntax(subIndex.getSmiSyntax());
      }
    }
    for (int i=0; i<indexDef.size(); i++) {
      MOTableSubIndex subIndex = indexDef.getIndex(i);
      OID oid = subIndex.getOid();
      if (oid == null) {
        throw new IllegalArgumentException("Sub-index "+i+" has no OID");
      }
      vbs[i] = new VariableBinding();
      vbs[i].setOid(oid);
      vbs[i].setVariable(indexValues[i]);
    }
    switch (indexStrategy) {
      case alwaysFirstSubIndex: {
        VariableBinding[] vb = new VariableBinding[1];
        vb[0] = vbs[0];
        vbs = vb;
        break;
      }
      case firstSubIndexOnly: {
        VariableBinding[] vb = new VariableBinding[1];
        vb[0] = vbs[0];
        vbs = vb;
        if (indexRegistry.allocate(session.getSessionID(), context, vb[0], false) != AgentXProtocol.AGENTX_SUCCESS) {
          // already allocated
          return AgentXProtocol.AGENTX_SUCCESS;
        }
        break;
      }
      case anyNonAllocatedSubIndex:
        for (VariableBinding vb : vbs) {
          if (indexRegistry.allocate(session.getSessionID(), context, vb, false) == AgentXProtocol.AGENTX_SUCCESS) {
            VariableBinding[] vbSingle = new VariableBinding[1];
            vbSingle[0] = vb;
            vbs = vbSingle;
            break;
          }
        }
        break;
    }
    AgentXIndexAllocatePDU pdu = new AgentXIndexAllocatePDU(context, vbs);
    if (indexAllocationMode != 0) {
      pdu.addFlag(indexAllocationMode);
    }
    pdu.setSessionAttributes(session);
    try {
      AgentXResponseEvent response =
          agentX.send(pdu, session.createAgentXTarget(),
                      session.getPeer().getTransport());
      if (response.getResponse() != null) {
        AgentXResponsePDU resp = response.getResponse();
        if (resp.getErrorStatus() == AgentXProtocol.AGENTX_SUCCESS) {
          switch (indexStrategy) {
            case firstSubIndexOnly:
              indexRegistry.allocate(session.getSessionID(), context, vbs[0], false);
              break;
            case anyNonAllocatedSubIndex:
              for (VariableBinding vb : vbs) {
                indexRegistry.allocate(session.getSessionID(), context, vb, false);
              }
              break;
          }
          OID index =
              indexDef.getIndexOID(getVariables(resp.getVariableBindings()));
          allocatedIndex.setValue(index.getValue());
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Allocated index "+allocatedIndex+" for context "+
                         context+" and index definition "+indexDef);
          }
        }
        else {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Index allocation failed for context "+
                         context+" and index definition "+indexDef+
                         " with value "+allocatedIndex);
          }
          switch (indexStrategy) {
            case anyNonAllocatedSubIndex:
            case firstSubIndexOnly: {
              indexRegistry.release(session.getSessionID(), context, vbs[0], false);
              break;
            }
          }
          return handleIndexAllocationError(indexDef, context, allocatedIndex, resp);
        }
        return AgentXProtocol.AGENTX_SUCCESS;
      }
      else {
        return AgentXProtocol.AGENTX_TIMEOUT;
      }
    }
    catch (IOException ex) {
      LOGGER.error("Failed to allocate index "+indexDef+" at "+session, ex);
    }
    return AgentXProtocol.AGENTX_DISCONNECT;
  }

  /**
   * Handle an index allocation error. By default, this method simply returns the AgentX error status
   * from the master agent response.
   * By overwriting this method, sub-classes may ignore such errors
   * (for example, if using some of the "always..." index strategies.
   *
   * @param indexDef
   *    the index definition for the index whose sub-index allocation failed.
   * @param context
   *    the context of the allocation.
   * @param allocatedIndex
   *    the index OID of the sub-index that failed to allocate.
   * @param resp
   *    the AgentX reponse from the master agent.
   * @return
   *    an AgentX error status or {@link AgentXProtocol#AGENTX_SUCCESS} to ignore the error.
   * @since 2.1
   */
  protected int handleIndexAllocationError(MOTableIndex indexDef, OctetString context,
                                           OID allocatedIndex, AgentXResponsePDU resp) {
    return resp.getErrorStatus();
  }

  /**
   * Deallocate an index at the master agent.
   *
   * @param context
   *    the context for which to allocate the index. Specify an empty
   *    <code>OctetString</code> for the default context.
   * @param indexDef
   *    the index definition with OID values for sub-index definitions.
   * @param allocatedIndex
   *    the index value of the previously allocated index.
   * @return
   *    {@link AgentXProtocol#AGENTX_SUCCESS} if the index could be deallocated
   *    or an AgentX protocol error code if deallocation failed.
   */
  public int deallocateIndex(OctetString context,
                             MOTableIndex indexDef,
                             OID allocatedIndex) {
    if (indexStrategy == IndexStrategy.noIndexAllocation) {
      return AgentXProtocol.AGENTX_SUCCESS;
    }
    VariableBinding[] vbs = new VariableBinding[indexDef.size()];
    Variable[] indexValues = indexDef.getIndexValues(allocatedIndex);
    for (int i=0; i<indexDef.size(); i++) {
      vbs[i] = new VariableBinding();
      MOTableSubIndex subIndex = indexDef.getIndex(i);
      OID oid = subIndex.getOid();
      if (oid == null) {
        throw new IllegalArgumentException("Sub-index "+i+" has no OID");
      }
      vbs[i].setOid(oid);
      vbs[i].setVariable(indexValues[i]);
    }
    switch (indexStrategy) {
      case firstSubIndexOnly:
        if (indexRegistry.release(session.getSessionID(), context, vbs[0], false) ==
            AgentXProtocol.AGENTX_INDEX_ALREADY_ALLOCATED) {
          return AgentXProtocol.AGENTX_SUCCESS;
        }
        break;
      case anyNonAllocatedSubIndex:
        ArrayList<VariableBinding> dvbs = new ArrayList<VariableBinding>(vbs.length);
        for (VariableBinding vb : vbs) {
          if (indexRegistry.release(session.getSessionID(), context, vb, false) == AgentXProtocol.AGENTX_SUCCESS) {
            dvbs.add(vb);
            break;
          }
        }
        vbs = dvbs.toArray(new VariableBinding[dvbs.size()]);
        break;
    }
    AgentXIndexDeallocatePDU pdu = new AgentXIndexDeallocatePDU(context, vbs);
    pdu.setSessionAttributes(session);
    try {
      AgentXResponseEvent response =
          agentX.send(pdu, session.createAgentXTarget(),
                      session.getPeer().getTransport());
      if (response.getResponse() != null) {
        AgentXResponsePDU resp = response.getResponse();
        return resp.getErrorStatus();
      }
      else {
        return AgentXProtocol.AGENTX_TIMEOUT;
      }
    }
    catch (IOException ex) {
      LOGGER.error("Failed to deallocate index "+indexDef+" at "+session, ex);
    }
    return AgentXProtocol.AGENTX_DISCONNECT;
  }

  public int registerRow(MOTable table, R row2Register) {
    return registerRow(table, row2Register, this.priority);
  }

  /**
   * Register the necessary regions for a table row.
   * @param table
   *    the MOTable to register rows for.
   * @param row2Register
   *    the row to register.
   * @param priority
   *    the priority (default is 127). A smaller value takes precedence over
   *    larger values.
   * @return
   *    the AgentX status of the registration.
   * @since 2.1
   */
  public int registerRow(MOTable table, R row2Register, byte priority) {
    OID subtree = new OID(table.getOID());
    subtree.append(table.getColumn(0).getColumnID());
    subtree.append(row2Register.getIndex());
    AgentXRegisterPDU pdu =
        new AgentXRegisterPDU(context, subtree, priority,
                              (byte)(table.getOID().size()+1),
                              table.getColumn(table.getColumnCount()-1).
                              getColumnID());
    if (table.getColumnCount() == 1) {
      pdu.addFlag(AgentXProtocol.FLAG_INSTANCE_REGISTRATION);
    }
    pdu.setSessionAttributes(session);
    try {
      AgentXResponseEvent resp =
          agentX.send(pdu, session.createAgentXTarget(),
                      session.getPeer().getTransport());
      if (resp.getResponse() == null) {
        return AgentXProtocol.AGENTX_TIMEOUT;
      }
      else if (resp.getResponse().getErrorStatus() !=
               AgentXProtocol.AGENTX_SUCCESS) {
        return resp.getResponse().getErrorStatus();
      }
    }
    catch (IOException ex) {
      LOGGER.error("Failed to send AgentXRegister pdu "+pdu+" to "+session+
                   " because: "+ex.getMessage(), ex);
      return AgentXProtocol.AGENTX_DISCONNECT;
    }
    return AgentXProtocol.AGENTX_SUCCESS;
  }

  public int unregisterRow(MOTable table, R row2Unregister) {
    return unregisterRow(table, row2Unregister, this.priority);
  }

  /**
   * Removes the region registration of a table row.
   * @param table
   *    the MOTable to register rows for.
   * @param row2Unregister
   *    the row to unregister.
   * @param priority
   *    the priority (default is 127). A smaller value takes precedence over
   *    larger values.
   * @return
   *    the AgentX status of the registration.
   * @since 2.1
   */
  public int unregisterRow(MOTable table, R row2Unregister, byte priority) {
    OID subtree = new OID(table.getOID());
    subtree.append(table.getColumn(0).getColumnID());
    subtree.append(row2Unregister.getIndex());
    AgentXUnregisterPDU pdu =
        new AgentXUnregisterPDU(context, subtree, priority,
                                (byte)(table.getOID().size()+1),
                                table.getColumn(table.getColumnCount()-1).
                                getColumnID());
    pdu.setSessionAttributes(session);
    try {
      AgentXResponseEvent resp =
          agentX.send(pdu, session.createAgentXTarget(),
                      session.getPeer().getTransport());
      if (resp.getResponse() == null) {
        return AgentXProtocol.AGENTX_TIMEOUT;
      }
      else if (resp.getResponse().getErrorStatus() !=
               AgentXProtocol.AGENTX_SUCCESS) {
        return resp.getResponse().getErrorStatus();
      }
    }
    catch (IOException ex) {
      LOGGER.error("Failed to send AgentXRegister pdu "+pdu+" to "+session+
                   " because: "+ex.getMessage(), ex);
      return AgentXProtocol.AGENTX_DISCONNECT;
    }
    return AgentXProtocol.AGENTX_SUCCESS;
  }

  private static Variable[] getVariables(VariableBinding[] vbs) {
    Variable[] variables = new Variable[vbs.length];
    for (int i=0; i<vbs.length; i++) {
      variables[i] = vbs[i].getVariable();
    }
    return variables;
  }

  /**
   * Sets the priority for the region registrations made by this table support.
   *
   * @param priority
   *    the priority (default is 127). A smaller value takes precedence over
   *    larger values.
   */
  public void setPriority(byte priority) {
    this.priority = priority;
  }

  /**
   * Sets the AgentX session to be used for this shared table support.
   * @param session
   *   an <code>AgentXSession</code> instance.
   */
  public void setSession(AgentXSession session) {
    this.session = session;
  }

  /**
   * Sets the index mode to be used by this shared table support object.
   * {@link #INDEX_MODE_ALLOCATE} simply allocates index values at the master
   * agent, whereas {@link #INDEX_MODE_ANY_INDEX} fetches any currently
   * unique index value from the master agent for a new row and
   * {@link #INDEX_MODE_NEW_INDEX} fetches a new index (never used before by
   * the master).
   * @param indexMode
   *    an index mode to be used for shared tables supported by this object.
   */
  public void setIndexMode(byte indexMode) {
    this.indexMode = indexMode;
  }

  public void setContext(OctetString context) {
    this.context = context;
  }

  public byte getPriority() {
    return priority;
  }

  /**
   * Gets the AgentX session used by this shared table support object.
   * @return
   *   an <code>AgentXSession</code> instance or <code>null</code> if there
   *   is no connection/session established with the master agent.
   */
  public AgentXSession getSession() {
    return session;
  }

  public byte getIndexMode() {
    return indexMode;
  }

  public OctetString getContext() {
    return context;
  }

  public AgentX getAgentX() {
    return agentX;
  }

  /**
   * Gets the index strategy of this shared table support.
   * @return
   *    an {@link IndexStrategy} instance.
   * @since 2.1
   */
  public IndexStrategy getIndexStrategy() {
    return indexStrategy;
  }


  /**
   * Sets the index strategy for this shared table support. For most use cases the
   * {@link IndexStrategy#firstSubIndexOnly} strategy is recommended. With that strategy,
   * only the first sub-index value will be allocated at the master agent if it has not
   * yet been allocated by this table support (for any table).
   * Unless you need to allocated sub-indexes by a multi-sub-index table, because there
   * does not exists a shared table for the same session with that sub-index as first
   * sub-index value, you should always use the {@link IndexStrategy#firstSubIndexOnly}
   * or {@link IndexStrategy#anyNonAllocatedSubIndex}.
   * The {@link IndexStrategy#anyNonAllocatedSubIndex} should be used for a master and
   * dependent table combination where the dependent table(s) extend the index of the
   * master table by additional sub-indexes.
   * @param indexStrategy
   *    the new index allocation/deallocation strategy.
   * @since 2.1
   */
  public void setIndexStrategy(IndexStrategy indexStrategy) {
    this.indexStrategy = indexStrategy;
  }

}
