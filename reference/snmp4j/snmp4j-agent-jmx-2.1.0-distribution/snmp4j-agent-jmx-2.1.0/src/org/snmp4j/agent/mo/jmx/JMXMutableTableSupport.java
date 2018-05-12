/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXMutableTableSupport.java  
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

/**
 * The <code>JMXMutableTableSupport</code> defines the necessary interface to
 * map tabular JMX data to a SNMP4J-Agent mutable table model. The interface
 * takes care of mapping JMX row identifiers to SNMP table indexes and vice
 * versa.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface JMXMutableTableSupport extends JMXTableSupport {

  /**
   * Sets the values of a SNMP row to the corresponding JMX structure.
   * @param tableOID
   *    the OID of the SNMP table.
   * @param row
   *    the SNMP table row.
   * @return
   *    a SNMP error status that indicates whether to operation succeeded or
   *    not.
   */
  int setRow(OID tableOID, MOTableRow row);

  /**
   * Creates a JMX structure that corresponds to the specified SNMP row.
   * @param tableOID
   *    the OID of the SNMP table.
   * @param row
   *    the SNMP table row to be created.
   * @return
   *    a SNMP error status that indicates whether to operation succeeded or
   *    not.
   */
  int createRow(OID tableOID, MOTableRow row);

  /**
   * Removes a JMX structure that corresponds to the specified SNMP row.
   * @param tableOID
   *    the OID of the SNMP table.
   * @param rowIndex
   *    the SNMP table row index of the row to be deleted.
   * @return
   *    a SNMP error status that indicates whether to operation succeeded or
   *    not.
   */
  int removeRow(OID tableOID, OID rowIndex);

  /**
   * Remove all rows (thus all JMX structures) corresponding to the specified
   * SNMP table.
   * @param tableOID
   *    the OID of the SNMP table.
   */
  void clear(OID tableOID);
}
