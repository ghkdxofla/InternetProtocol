/*_############################################################################
  _##
  _##  SNMP4J-AgentX - BufferedMOMutableTableModel.java
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

package org.snmp4j.agent.mo;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The BufferedMOMutableTableModel is a read/write/create table model that dynamically loads data from an
 * backend as needed. The internal buffer holds up to {@link #getMaxBufferSize()} rows fetched from the backend.
 * Rows are removed from the buffer either when they are older than {@link #getBufferTimeoutNanoSeconds()} or
 * if the maximum number of buffered rows is reached.
 * <p>
 * Updates, row creations and removals are directly processed by the backend. Thus, updates are not buffered
 * by this table model although the internal buffer reflects the changes.
 * </p>
 * <p>
 * IMPORTANT: The table model needs to be informed about row changes by calling the {@link #rowChanged(MOTableRowEvent)}
 * method of the implemented {@link org.snmp4j.agent.mo.MOTableRowListener} interface. This is typically done by
 * adding this model as row listener with
 * {@link org.snmp4j.agent.mo.DefaultMOTable#addMOTableRowListener(MOTableRowListener)} but it can also be called
 * manually.
 * </p>
 * <p>
 * If you need to be sure that every row change is automatically processed by the backend through this
 * BufferedMOMutableTableModel then you will have to implement and use your own {@link org.snmp4j.agent.mo.MOTableRow}
 * implementation created by your own {@link org.snmp4j.agent.mo.MOTableRowFactory}. That row implementation has then
 * to call the {@link #rowChanged(MOTableRowEvent)} of this table model after each change.
 * </p>
 * @author Frank Fock
 * @since 2.2
 */
public abstract class BufferedMOMutableTableModel<R extends MOTableRow>
    extends BufferedMOTableModel<R>
    implements MOMutableTableModel<R>, MOTableRowListener<R> {

  /**
   * Creates a BufferedMOMutableTableModel with the specified {@link org.snmp4j.agent.mo.MOTableRowFactory}.
   *
   * @param rowFactory the row factory to be used to create rows from backend data.
   */
  protected BufferedMOMutableTableModel(MOTableRowFactory<R> rowFactory) {
    super(rowFactory);
  }

  @Override
  public R addRow(R row) {
    Variable[] values = new Variable[row.size()];
    for (int i=0; i<values.length; i++) {
      values[i] = row.getValue(i);
    }
    R previous = getRow(row.getIndex());
    if (previous != null) {
      writeRow(row.getIndex(), values);
    }
    else {
      insertRow(row.getIndex(), values);
    }
    updateBuffer(Collections.singletonList(row), null);
    return previous;
  }

  @Override
  public synchronized void clear() {
    firstRow = null;
    lastRow = null;
    bufferedChunksList.clear();
    bufferedRows.clear();
    deleteAllRows();
  }

  @Override
  public synchronized void clear(MOTableRowFilter<R> filter) {
    ArrayList<OID> deleted = new ArrayList<OID>();
    for (Iterator<R> rowIterator = iterator(); rowIterator.hasNext();) {
      R row = rowIterator.next();
      if (!filter.passesFilter(row)) {
        deleted.add(row.getIndex());
      }
    }
    bulkDeleteRows(deleted);
    BufferedMOTableRow<R> firstRowCopy = firstRow;
    if (firstRowCopy != null && deleted.contains(firstRowCopy.getIndex())) {
      firstRow = null;
    }
    BufferedMOTableRow<R> lastRowCopy = lastRow;
    if (lastRowCopy != null && deleted.contains(lastRowCopy.getIndex())) {
      lastRow = null;
    }
    for (OID index : deleted) {
      BufferedMOTableRow<R> bufferedMOMutableTableRow = bufferedRows.remove(index);
      if (bufferedMOMutableTableRow != null) {
        bufferedChunksList.remove(bufferedMOMutableTableRow);
      }
    }
  }

  /**
   * Removes all rows in the table.
   */
  protected abstract void deleteAllRows();

  /**
   * Removes the rows with a row index that matches an entry in the supplied index list.
   * @param indexList
   *    the row indexes of the rows to remove.
   */
  protected abstract void bulkDeleteRows(List<OID> indexList);

  /**
   * Inserts a non-existing row.
   * @param index
   *   the row index of the new row.
   * @param values
   *   the values for the columns of the row.
   */
  protected abstract void insertRow(OID index, Variable[] values);

  /**
   * Updates/writes the content of a row.
   * @param index
   *   the row index of the existing row.
   * @param values
   *   the new values for the columns in that row.
   */
  protected abstract void writeRow(OID index, Variable[] values);

  /**
   * Deletes a single row.
   * @param index
   *    the row index of the deleting row.
   */
  protected abstract void deleteRow(OID index);

  @Override
  public void rowChanged(MOTableRowEvent event) {
    if (event.getType() == MOTableRowEvent.UPDATED) {
      Variable[] values = new Variable[event.getRow().size()];
      for (int i=0; i<values.length; i++) {
        values[i] = event.getRow().getValue(i);
      }
      writeRow(event.getRow().getIndex(), values);
      updateBuffer(Collections.singletonList(rowFactory.createRow(event.getRow().getIndex(), values)), null);
    }
  }

  @Override
  public R removeRow(OID index) {
    BufferedMOTableRow<R> row = getRowFromBuffer(index);
    if (row == null) {
      bufferMisses++;
      Variable[] rowValues = fetchRow(index);
      if (rowValues != null) {
        R r = rowFactory.createRow(index, rowValues);
        row = new BufferedMOTableRow<R>(null);
        deleteRow(index);
        bufferedRows.put(index, row);
        return r;
      }
    }
    else {
      bufferHits++;
      row.setBufferedRow(null);
      row.setLastRefresh(System.nanoTime());
      deleteRow(index);
      return row.getBufferedRow();
    }
    return null;
  }
}
