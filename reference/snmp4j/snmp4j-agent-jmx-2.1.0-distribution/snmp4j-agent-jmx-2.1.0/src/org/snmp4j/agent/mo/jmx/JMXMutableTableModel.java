/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXMutableTableModel.java  
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

import org.snmp4j.agent.mo.*;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.PDU;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>JMXMutableTableModel</code> extends the {@link JMXTableModel} by
 * adding support for row creation and deletion.
 *
 * @author Frank Fock
 * @version 2.0
 */
public class JMXMutableTableModel<R extends MOTableRow> extends JMXTableModel<R>
    implements MOMutableTableModel<R> {

  private static LogAdapter logger =
      LogFactory.getLogger(JMXMutableTableModel.class);

  /**
   * Creates a JMX based table model for a table with the specified OID and
   * columns using the supplied {@link JMXMutableTableSupport} to map
   * between SNMP and JMX.
   *
   * @param tableOID
   *    the OID of the table (e.g., ifEntry).
   * @param tableSupport
   *    the JMXMutableTableSupport instance that maps between SNMP values and
   *    value organisation to JMX MBean(s).
   * @param columns
   *    the columns defined for the table.
   * @param rowFactory
   *    the factory instance that creates the rows for this table. By default
   *    use the
   */
  public JMXMutableTableModel(OID tableOID,
                              JMXMutableTableSupport tableSupport,
                              MOColumn[] columns,
                              MOTableRowFactory<R> rowFactory) {
    super(tableOID, tableSupport, columns, rowFactory);
  }

  public R createRow(OID index, Variable[] values) throws
      UnsupportedOperationException {
    return rowFactory.createRow(index, values);
  }

  public void freeRow(MOTableRow row) {
    //nothing to do by default
  }

  public R addRow(MOTableRow row) {
    R oldRow =
        rowFactory.createRow(row.getIndex(), getInitialRowValues());
    if (table.getRow(tableOID, oldRow) != PDU.noError) {
      oldRow = null;
    }
    else {
      removeRow(row.getIndex());
    }
    int status = ((JMXMutableTableSupport)table).createRow(tableOID, row);
    if (status != PDU.noError) {
      throw new UnsupportedOperationException(PDU.toErrorStatusText(status));
    }
    return oldRow;
  }

  public R removeRow(OID index) {
    R row = rowFactory.createRow(index, getInitialRowValues());
    if (row != null) {
      table.getRow(tableOID, row);
      int status = ((JMXMutableTableSupport)table).removeRow(tableOID, index);
      if (status != PDU.noError) {
        logger.debug("Row removal failed for index="+index+" and table "+
                     tableOID+" with "+PDU.toErrorStatusText(status));
        return null;
      }
    }
    return row;
  }

  public void clear() {
    ((JMXMutableTableSupport)table).clear(tableOID);
  }

  public void clear(MOTableRowFilter<R> filter) {
    Iterator it = table.rowIdIterator(tableOID);
    List<OID> toRemove = new ArrayList<OID>();
    for (int i=0; it != null && it.hasNext(); i++) {
      Object rowID = it.next();
      OID index = table.mapToIndex(tableOID, rowID, i);
      R row = getRow(index);
      if (filter.passesFilter(row)) {
        toRemove.add(index);
      }
    }
    for (OID removedOID : toRemove) {
      ((JMXMutableTableSupport) table).removeRow(tableOID, removedOID);
    }
  }

  @Override
  public <F extends MOTableRowFactory<R>> void setRowFactory(F rowFactory) {
    this.rowFactory = rowFactory;
  }

}
