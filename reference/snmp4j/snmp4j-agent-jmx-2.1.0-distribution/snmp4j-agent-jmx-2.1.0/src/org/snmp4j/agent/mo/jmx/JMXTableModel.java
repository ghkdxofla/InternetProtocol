/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXTableModel.java  
  _## 
  _##  Copyright (C) 2005-2012  Frank Fock (SNMP4J.org)
  _##  
  _##  Licensed under the Apache License, Version 2.0 (the "License");
  _##  you may not use this file except in compliance with the License.
  _##  You may obtain a copy of the License at
  _##  
  _##      http://www.apache.org/licenses/LICENSE-2.0
  _##  
  _##  Unless required by applicable law or agreed to in writing, software
  _##  distributed under the License is distributed on an "AS IS" BASIS,
  _##  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  _##  See the License for the specific language governing permissions and
  _##  limitations under the License.
  _##  
  _##########################################################################*/

package org.snmp4j.agent.mo.jmx;

import java.util.Iterator;

import org.snmp4j.agent.mo.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.AbstractVariable;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.agent.mo.jmx.util.JMXArrayIndexKey;

/**
 * The <code>JMXTableModel</code> implements the {@link MOTableModel} interface
 * with the support of a {@link JMXTableSupport} instance.
 *
 * @author Frank Fock
 * @version 2.0
 */
public class JMXTableModel<R extends MOTableRow> implements MOTableModel<R> {

  protected OID tableOID;
  protected JMXTableSupport table;
  protected MOColumn[] columns;
  protected MOTableRowFactory<R> rowFactory;

  public JMXTableModel(OID tableEntryOID,
                       JMXTableSupport table,
                       MOColumn[] columns,
                       MOTableRowFactory<R> rowFactory) {
    this.table = table;
    this.tableOID = tableEntryOID;
    this.columns = columns;
    this.rowFactory = rowFactory;
  }

  public static JMXTableModel<DefaultMOMutableRow2PC> getDefaultInstance(OID tableEntryOID,
                                                                         JMXTableSupport table,
                                                                         MOColumn[] columns) {
    JMXTableModel<DefaultMOMutableRow2PC> tableModel =
        new JMXTableModel<DefaultMOMutableRow2PC>(tableEntryOID, table, columns, null);
    tableModel.setRowFactory(tableModel.getDefaultRowFactory());
    return tableModel;
  }

  protected MOTableRowFactory<DefaultMOMutableRow2PC> getDefaultRowFactory() {
    return new JMXMutableTableRowFactory();
  }

  protected Variable[] getInitialRowValues() {
    Variable[] values = new Variable[getColumnCount()];
    for (int i = 0; (i < values.length); i++) {
      if (columns[i] instanceof MOMutableColumn) {
        values[i] = ((MOMutableColumn) columns[i]).getDefaultValue();
        if (values[i] == null) {
          values[i] = AbstractVariable.createFromSyntax(columns[i].getSyntax());
        }
      }
      else {
        values[i] = AbstractVariable.createFromSyntax(columns[i].getSyntax());
      }
    }
    return values;
  }


  public boolean containsRow(OID index) {
    int i=0;
    for (Iterator it = table.rowIdIterator(tableOID); it.hasNext(); i++) {
      Object rowKey = it.next();
      OID rowIndex = table.mapToIndex(tableOID, rowKey, i);
      if (index.equals(rowIndex)) {
        return true;
      }
    }
    return false;
  }

  public OID firstIndex() {
    Iterator it = table.rowIdIterator(tableOID);
    if (it.hasNext()) {
      return table.mapToIndex(tableOID, it.next(), 0);
    }
    return null;
  }

  public R firstRow() {
    OID firstIndex = firstIndex();
    if (firstIndex != null) {
      R row = rowFactory.createRow(firstIndex, getInitialRowValues());
      table.getRow(tableOID, row);
      return row;
    }
    return null;
  }

  public int getColumnCount() {
    return columns.length;
  }

  public R getRow(OID index) {
    R row = rowFactory.createRow(index, getInitialRowValues());
    int status = table.getRow(tableOID, row);
    if (status == SnmpConstants.SNMP_ERROR_SUCCESS) {
      return row;
    }
    return null;
  }

  public int getRowCount() {
    return table.getRowCount(tableOID);
  }

  @Override
  public boolean isEmpty() {
    return table.isEmpty(tableOID);
  }

  public Iterator<R> iterator() {
    return new JMXTableRowIterator(table.rowIdIterator(tableOID));
  }

  /**
   * Returns the last row index in this model.
   *
   * @return the last index OID of this model.
   */
  public OID lastIndex() {
    return table.getLastIndex(tableOID);
  }

  /**
   * Returns the last row contained in this model.
   *
   * @return the <code>MOTableRow</code> with the greatest index or
   *   <code>null</code> if the model is empty.
   */
  public R lastRow() {
    OID lastIndex = lastIndex();
    if (lastIndex != null) {
      R row = rowFactory.createRow(lastIndex, getInitialRowValues());
      table.getRow(tableOID, row);
      return row;
    }
    return null;
  }

  /**
   * Returns an iterator on a view of the rows of this table model whose index
   * values are greater or equal <code>lowerBound</code>.
   *
   * @param lowerBound the lower bound index (inclusive). If
   *   <code>lowerBound</code> is <code>null</code> the returned iterator is
   *   the same as returned by {@link #iterator}.
   * @return an <code>Iterator</code> over the
   */
  public Iterator<R> tailIterator(OID lowerBound) {
    int i=0;
    Object rowId = table.mapToRowId(tableOID, lowerBound);
    if (rowId != null) {
      return new JMXTableRowIterator(table.rowIdTailIterator(tableOID, rowId));
    }
    else {
      for (Iterator it = table.rowIdIterator(tableOID); it.hasNext(); i++) {
        Object key = it.next();
        OID index = table.mapToIndex(tableOID, key, i+1);
        if ((lowerBound == null) || (index.compareTo(lowerBound) >= 0)) {
          return new JMXTableRowIterator(it, key, i+1);
        }
      }
    }
    return null;
  }

  public class JMXTableRowIterator implements Iterator<R> {

    private Object nextKey;
    private Iterator keys;
    private int nativeIndex = 0;

    private JMXTableRowIterator(Iterator keys) {
      this.keys = keys;
    }

    private JMXTableRowIterator(Iterator keys, Object firstKey,
                                int firstNativeIndex) {
      this(keys);
      this.nextKey = firstKey;
      this.nativeIndex = firstNativeIndex;
    }

    public boolean hasNext() {
      return (nextKey != null) || keys.hasNext();
    }

    public R next() {
      Object key = nextKey;
      if (key == null) {
        key = keys.next();
      }
      else {
        nextKey = null;
      }
      if (key instanceof JMXArrayIndexKey) {
        nativeIndex = ((JMXArrayIndexKey)key).getIndex();
      }
      OID index = table.mapToIndex(tableOID, key, nativeIndex++);
      R row = rowFactory.createRow(index, getInitialRowValues());
      int status = table.getRow(tableOID, row);
      if (status == SnmpConstants.SNMP_ERROR_SUCCESS) {
        return row;
      }
      return null;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  public MOTableRowFactory<R> getRowFactory() {
    return rowFactory;
  }

  public <F extends MOTableRowFactory<R>> void setRowFactory(F rowFactory) {
    this.rowFactory = rowFactory;
  }

  protected class JMXMutableTableRowFactory implements MOTableRowFactory<DefaultMOMutableRow2PC> {

    public JMXMutableRow2PC createRow(OID index, Variable[] values) throws
        UnsupportedOperationException {
      return new JMXMutableRow2PC(index, values);
    }

    @Override
    public void freeRow(DefaultMOMutableRow2PC defaultMOMutableRow2PC) {

    }

  }

  protected class JMXMutableRow2PC extends DefaultMOMutableRow2PC {
    public JMXMutableRow2PC(OID index, Variable[] values) {
      super(index, values);
    }

    public void setValue(int column, Variable value) {
      super.setValue(column, value);
      table.setRow(tableOID, this, column);
    }

  }
}
