/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXIndexSupport.java  
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
import javax.management.ObjectName;

/**
 * The <code>JMXIndexSupport</code> provides a mapping between an Object and
 * a SNMP index OID. Optionally, the Object can also be mapped directly to
 * a MBean instance identified by an {@link ObjectName}.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface JMXIndexSupport {

  /**
   * Maps a native object identifying a row or alternatively the row's index
   * into an internal array (e.g., an array returned by a MBean method).
   *
   * @param nativeRowId
   *    an Object identifying a row.
   * @param nativeIndex
   *    optionally the index of the row into an internal array.
   * @return
   *    a key Object that can be directly mapped to a row index OID.
   */
  Object getRowIdentifier(Object nativeRowId, int nativeIndex);

  /**
   * Maps a row identifier (i.e., an Object returned by
   * {@link #getRowIdentifier}) to a row index.
   * @param rowIdentifier
   *    an Object describing a row index.
   * @return
   *    a row index OID.
   */
  OID mapToIndex(Object rowIdentifier);

  /**
   * Maps a row index OID to an object describing a row index internally.
   * @param rowIndex
   *    a row index OID.
   * @return
   *    an Object describing a row index.
   */
  Object mapToRowIdentifier(OID rowIndex);

  /**
   * Maps a row identifier to a MBean object name. If a row cannot be accessed
   * directly via an index, for example, because the rows are mapped from
   * a list or array, then <code>null</code> is returned.
   *
   * @param rowIdentifier
   *    the row identifier which may be also a native index value into an
   *    array or list of MBean attribute values.
   * @return ObjectName
   *    the object name of the MBean representing the row identified by
   *    <code>rowIdentifier</code>. If <code>rowIdentifier</code> is a
   *    <code>Integer</code> and <code>null</code> is returned, the caller
   *    should use the rowIdentifier value as index into the value list.
   */
  ObjectName mapToRowMBean(Object rowIdentifier);
}
