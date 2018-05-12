/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXRowSupport.java  
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
 * The <code>JMXRowSupport</code> maps an internal row identifier of a table
 * to a MBean <code>ObjectName</code>.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface JMXRowSupport {

  /**
   * Maps the supplied internal row key to a MBean <code>ObjectName</code>
   * @param tableOID
   *    a table entry OID (e.g., ifEntry).
   * @param rowKey
   *    an Object that identifies a MBean representing the corresponding row
   *    of the through <code>tableOID</code> identified table.
   * @return
   *    the <code>ObjectName</code> of the MBean associated with the specified
   *    row.
   */
  ObjectName getRowMBean(OID tableOID, Object rowKey);
}
