/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXTableSupport.java  
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

import org.snmp4j.smi.OID;
import org.snmp4j.agent.mo.MOTableRow;
import java.util.Iterator;

/**
 * The <code>JMXTableSupport</code> defines the necessary interface to
 * map tabular JMX data to a SNMP4J-Agent table model. The interface
 * takes care of mapping JMX row identifiers to SNMP table indexes and vice
 * versa.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface JMXTableSupport {

  /**
   * Maps a native MBean row identifier object or index to a SNMP table index
   * OID.
   * @param tableOID
   *    the OID of the table ManagedObject (including the .1 of the entry
   *    object) for which indexes are mapped.
   * @param nativeRowID
   *    the native row ID object which identifies the row. If <code>null</code>
   *    then <code>nativeIndex</code> identifies the row.
   * @param nativeIndex
   *    the native enumerating index if rows are not identified by an object.
   * @return
   *    the SNMP row index OID (suffix).
   */
  OID mapToIndex(OID tableOID, Object nativeRowID, int nativeIndex);

  /**
   * Maps a SNMP row index to a native row identifier object.
   * @param tableOID
   *    the OID of the table ManagedObject (including the .1 of the entry
   *    object) for which indexes are mapped.
   * @param rowIndex
   *    a SNMP row index of the table specified by <code>tableOID</code>.
   * @return
   *    a native row identifier.
   */
  Object mapToRowId(OID tableOID, OID rowIndex);

  /**
   * Returns an Iterator over the row identifiers of the table.
   * @param tableOID
   *    the OID of the table ManagedObject (including the .1 of the entry
   *    object) for which indexes are mapped.
   * @return
   *   an Iterator of row identifiers.
   */
  Iterator rowIdIterator(OID tableOID);

  /**
   * Returns a tail iterator over the row identifiers of the table.
   * @param tableOID
   *    the OID of the table ManagedObject (including the .1 of the entry
   *    object) for which indexes are mapped.
   * @param firstRowId
   *    the lower bound (including) of the row identifiers to return.
   * @return
   *   an Iterator of row identifiers.
   */
  Iterator rowIdTailIterator(OID tableOID, Object firstRowId);

  /**
   * Gets the last SNMP index currently supported by the specified table.
   * @param tableOID
   *    the OID of the table ManagedObject (including the .1 of the entry
   *    object) for which indexes are mapped.
   * @return
   *    the last SNMP row index of the specified table.
   */
  OID getLastIndex(OID tableOID);

  /**
   * Returns the number of rows of the specified table.
   * @param tableOID
   *    the OID of the table ManagedObject (including the .1 of the entry
   *    object) for which indexes are mapped.
   * @return
   *    the number of rows in the table.
   */
  int getRowCount(OID tableOID);

  /**
   * Gets the row values for the specified row.
   * @param tableOID
   *    the OID of the table ManagedObject (including the .1 of the entry
   *    object) for which indexes are mapped.
   * @param row
   *    a <code>MOTableRow</code> instance which will be modified to hold the
   *    SNMP values of the row specified by <code>row</code>'s index value.
   * @return
   *    a SNMP error status that indicates whether to operation succeeded or
   *    not.
   */
  int getRow(OID tableOID, MOTableRow row);

  /**
   * Sets the value of a column (cell) of a row from the corresponding SNMP
   * conceptual row.
   * @param tableOID
   *    the OID of the table ManagedObject (including the .1 of the entry
   *    object) for which indexes are mapped.
   * @param row
   *    a <code>MOTableRow</code> instance that identifies the target row by its
   *    row index (indirectly) and that contains the SNMP values that replace
   *    the corresponding native values.
   * @param column
   *    the zero-based column index to set.
   * @return
   *    a SNMP error status that indicates whether to operation succeeded or
   *    not.
   */
  int setRow(OID tableOID, MOTableRow row, int column);

  /**
   * Checks if the table is empty or not.
   * @return
   *    <code>true</code> if the table is empty (has no rows), <code>false/code> otherwise.
   * @since 2.1
   * @param tableOID
   */
  boolean isEmpty(OID tableOID);

}
