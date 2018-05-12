/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXSimpleArrayIndexSupport.java  
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

import javax.management.ObjectName;

import org.snmp4j.smi.OID;
import org.snmp4j.agent.mo.jmx.util.JMXArrayIndexKey;

/**
 * The <code>JMXSimpleArrayIndexSupport</code> provides index support
 * for SNMP indexes that are directly related to index values of an array
 * provided through a MBean.
 * <p>
 * This class returns instances of {@link JMXArrayIndexKey} as row identifiers
 * and expect instances of the same as row identifiers to be mapped to a SNMP
 * index value.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class JMXSimpleArrayIndexSupport implements JMXIndexSupport {

  public JMXSimpleArrayIndexSupport() {
  }

  public Object getRowIdentifier(Object nativeRowId, int nativeIndex) {
    return new JMXArrayIndexKey(nativeIndex);
  }

  public OID mapToIndex(Object rowIdentifier) {
    return new OID(new int[] { ((JMXArrayIndexKey)rowIdentifier).getIndex() });
  }

  public ObjectName mapToRowMBean(Object rowIdentifier) {
    return null;
  }

  public Object mapToRowIdentifier(OID rowIndex) {
    if (rowIndex == null) {
      return new JMXArrayIndexKey(0);
    }
    return new JMXArrayIndexKey(rowIndex.get(0));
  }

}
