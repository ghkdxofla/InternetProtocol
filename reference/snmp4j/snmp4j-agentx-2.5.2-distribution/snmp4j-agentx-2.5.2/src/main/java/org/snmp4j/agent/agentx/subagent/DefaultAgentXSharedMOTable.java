/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - DefaultAgentXSharedMOTable.java  
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

import org.snmp4j.agent.mo.DefaultMOTable;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOTableIndex;
import org.snmp4j.smi.OID;
import org.snmp4j.agent.mo.MOTableModel;
import org.snmp4j.agent.mo.MOMutableTableModel;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.MOTableRowEvent;

/**
 * The <code>DefaultAgentXSharedMOTable</code> class is a default implementation
 * for a shared table. It supports dynamic (while AgentX session is up) row
 * creation and deletion.
 *
 * @author Frank Fock
 * @version 2.1
 */
public class DefaultAgentXSharedMOTable
    <R extends MOTableRow, C extends MOColumn, M extends MOTableModel<R>>
    extends DefaultMOTable<R,C,M>
    implements AgentXSharedMutableMOTable<R,C,M>
{
  private AgentXSharedMOTableSupport<R> sharedTableSupport;
  private byte overrideIndexAllocationMode = 0;
  private byte overridePriority = 0;

  public DefaultAgentXSharedMOTable(OID oid, MOTableIndex indexDef,
                                    C[] columns) {
    super(oid, indexDef, columns);
  }

  public DefaultAgentXSharedMOTable(OID oid, MOTableIndex indexDef,
                                    C[] columns, M model) {
    super(oid, indexDef, columns, model);
  }

  public AgentXSharedMOTableSupport<R> getAgentXSharedMOTableSupport() {
    return sharedTableSupport;
  }

  public void setAgentXSharedMOTableSupport(AgentXSharedMOTableSupport<R>
                                            sharedTableSupport) {
    if (this.sharedTableSupport != null) {
      removeMOTableRowListener(this.sharedTableSupport);
    }
    this.sharedTableSupport = sharedTableSupport;
    addMOTableRowListener(this.sharedTableSupport);
  }

  public byte getOverrideIndexAllocationMode() {
    return overrideIndexAllocationMode;
  }

  public byte getOverridePriority() {
    return overridePriority;
  }

  /**
   * Sets the index allocation mode that overrides the allocation mode
   * inherited from the shared table support for index allocation operations
   * for this shared table.
   *
   * @param overrideIndexAllocationMode
   *    an index allocation mode as defined by
   *    {@link AgentXSharedMOTableSupport} or zero to use the default priority.
   */
  public void setOverrideIndexAllocationMode(byte overrideIndexAllocationMode) {
    this.overrideIndexAllocationMode = overrideIndexAllocationMode;
  }

  /**
   * Sets the registration priority that overrides the priority inherited from
   * the shared table support object (if not equal zero).
   * @param overridePriority
   *    a value between 1 and 255 (-1 respectively - its a byte) or zero which
   *    indicates that the default priority should be used.
   */
  public void setOverridePriority(byte overridePriority) {
    this.overridePriority = overridePriority;
  }

  public boolean changeRowIndex(OID oldIndex, OID newIndex) {
    if (model instanceof MOMutableTableModel) {
      @SuppressWarnings("unchecked")
      MOMutableTableModel<R> mutableModel = (MOMutableTableModel<R>) model;
      R r = mutableModel.removeRow(oldIndex);
      if (r == null) {
        return false;
      }
      r.getIndex().setValue(newIndex.getValue());
      R existingRow = mutableModel.addRow(r);
      if (existingRow != null) {
        mutableModel.removeRow(newIndex);
        r.getIndex().setValue(oldIndex.getValue());
        mutableModel.addRow(r);
        mutableModel.addRow(existingRow);
        return false;
      }
      fireRowChanged(new MOTableRowEvent<R>(this, this, r, MOTableRowEvent.UPDATED, false));
      return true;
    }
    return false;
  }

}
