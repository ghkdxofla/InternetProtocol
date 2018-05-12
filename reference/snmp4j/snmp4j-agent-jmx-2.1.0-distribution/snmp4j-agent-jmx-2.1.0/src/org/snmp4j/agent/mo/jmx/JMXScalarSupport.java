/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXScalarSupport.java  
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
import org.snmp4j.smi.Variable;

/**
 * The <code>JMXScalarSupport</code> provides the interface between SNMP values
 * and MBean based instrumentation. A <code>JMXScalarSupport</code> instance
 * usually supports more than one scalar object.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface JMXScalarSupport {

  /**
   * Gets the actual value for the specified object instance and type.
   * @param scalarInstanceOID
   *    the instance OID of the target object.
   * @param value
   *    the instance to hold the return value.
   * @return
   *    zero on success or a SNMP error status value if fetching the value
   *    fails.
   */
  int getScalarValue(OID scalarInstanceOID, Variable value);

  /**
   * Sets the value of the specified object instance and type.
   * @param scalarInstanceOID
   *    the instance OID of the target object.
   * @param value
   *    the instance's new value.
   * @return
   *    zero on success or a SNMP error status value if setting the value
   *    fails.
   */
  int setScalarValue(OID scalarInstanceOID, Variable value);

  /**
   * Checks the value of the specified object instance and type.
   * @param scalarInstanceOID
   *    the instance OID of the target object.
   * @param value
   *    the instance's new value.
   * @return
   *    zero on success or a SNMP error status value if setting the value
   *    fails.
   */
  int checkScalarValue(OID scalarInstanceOID, Variable value);
}
