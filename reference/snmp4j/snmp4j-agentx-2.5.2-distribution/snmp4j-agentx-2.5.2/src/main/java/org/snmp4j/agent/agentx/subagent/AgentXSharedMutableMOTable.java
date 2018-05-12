/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXSharedMutableMOTable.java  
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

import org.snmp4j.agent.mo.*;
import org.snmp4j.smi.OID;
// For JavaDoc:
import org.snmp4j.agent.agentx.AgentXSession;

/**
 * The <code>AgentXSharedMutableMOTable</code> extends the
 * <code>AgentXSharedMOTable</code> and <code>MutableMOTable</code> interface.
 * It serves as a marker interface to be able to differentiate
 * between regular tables, AgentX shared index tables, and AgentX shared index
 * tables with varying number of rows (this kind). The latter two
 * require an index definition where all sub-indexes have an OID specified
 * ({@link MOTableSubIndex#getOid()}). AgentX shared index tables provide
 * services needed for sharing rows of the same conceptual table
 * across multiple AgentX sub-agents.
 * <p>
 * This mutable variant is able to add (allocate and register) and remove
 * (deallocate and deregister) rows after initial registration of the subagent.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface AgentXSharedMutableMOTable
    <R extends MOTableRow, C extends MOColumn, M extends MOTableModel<R>>
    extends AgentXSharedMOTable<R,C,M> {

  /**
   * Returns the shared table support object this shared table uses.
   * @return
   *    an <code>AgentXSharedMOTableSupport</code> instance or <code>null</code>
   *    if this table has not been initialized for AgentX yet.
   */
  AgentXSharedMOTableSupport<R> getAgentXSharedMOTableSupport();

  /**
   * Sets the shared table support that this shared table should be use to
   * (de)register rows and (de)allocate indexes while this shared table is
   * part of an connected AgentX session. The shared table determines whether
   * the AgentX session is established by inspecting the {@link AgentXSession}
   * instance returned by this shared tabe support. If either the shared table
   * support instance is <code>null</code> or {@link AgentXSession#isClosed()}
   * returns <code>true</code> the AgentX session is considered to be
   * disconnected (not established).
   *
   * @param sharedTableSupport
   *    an <code>AgentXSharedMOTableSupport</code> instance to be used to
   *    (de)allocate indexes and (de)register rows at the AgentX master agent.
   */
  void setAgentXSharedMOTableSupport(AgentXSharedMOTableSupport<R>
                                     sharedTableSupport);

  /**
   * Changes the index of a row without firing a {@link MOTableRowEvent}
   * about removing and adding of the row. Only a
   * {@link MOTableRowEvent#UPDATED} event will be fired.
   *
   * @param oldIndex
   *    the old index of the row to change.
   * @param newIndex
   *    the new index of the row.
   * @return
   *    <code>true</code> if the row index could be changed and
   *    <code>false</code> if either the row does not exists or the table model
   *    does not allow to add/remove rows.
   */
  boolean changeRowIndex(OID oldIndex, OID newIndex);
}
