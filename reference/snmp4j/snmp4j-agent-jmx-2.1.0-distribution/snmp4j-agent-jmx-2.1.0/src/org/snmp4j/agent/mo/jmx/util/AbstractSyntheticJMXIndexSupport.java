/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - AbstractSyntheticJMXIndexSupport.java  
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

package org.snmp4j.agent.mo.jmx.util;

import javax.management.ObjectName;

import org.snmp4j.agent.mo.jmx.JMXIndexSupport;
import org.snmp4j.smi.OID;

/**
 * The <code>AbstractSyntheticJMXIndexSupport</code> maps a synthtically
 * generated index to a JMX row identifier and vice versa. The synthetic
 * index is generated by using the hash code of the JMX row identifier
 * converted to a String.
 *
 * @author Frank Fock
 */
public abstract class AbstractSyntheticJMXIndexSupport
    implements JMXIndexSupport
{

  protected KeyIndexRelation keyIndexRelation;

  public AbstractSyntheticJMXIndexSupport() {
    this(50);
  }

  public AbstractSyntheticJMXIndexSupport(int initialSize) {
    keyIndexRelation = new KeyIndexRelation(initialSize);
  }

  public OID mapToIndex(Object rowIdentifier) {
    OID index = keyIndexRelation.getKeys().get(rowIdentifier);
    if (index == null) {
      return allocateNewIndex(rowIdentifier);
    }
    return index;
  }

  protected OID allocateNewIndex(Object rowIdentifier) {
    int hashCode = rowIdentifier.hashCode();
    OID index = new OID(new int[] { hashCode });
    while (keyIndexRelation.getIndexes().containsKey(index)) {
      hashCode++;
      index.set(0, hashCode);
    }
    keyIndexRelation.getKeys().put(rowIdentifier, index);
    keyIndexRelation.getIndexes().put(index, rowIdentifier);
    return index;
  }

  public abstract ObjectName mapToRowMBean(Object rowIdentifier);

  public Object mapToRowIdentifier(OID rowIndex) {
    if (rowIndex == null) {
      return null;
    }
    return keyIndexRelation.getIndexes().get(rowIndex);
  }

  public Object getRowIdentifier(Object nativeRowId, int nativeRowIndex) {
    return nativeRowId;
  }
}
