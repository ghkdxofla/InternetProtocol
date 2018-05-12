/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanAttributeMOTableInfo.java  
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

import java.io.IOException;
import javax.management.*;
import org.snmp4j.agent.mo.jmx.types.*;

/**
 * The <code>MBeanAttributeMOTableInfo</code> class describes the mapping from
 * the attributes of an MBean to a SNMP conceptual table and vice versa.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanAttributeMOTableInfo extends MBeanMOInfo {

  private MBeanAttributeKeyProvider keyProvider;
  private String[] indexAttributes;
  private TypedAttribute[] columns;
  private JMXIndexSupport indexSupport;

  /**
   * Creates a table mapping for the supplied MBean name.
   *
   * @param name
   *    the name of the MBean that provides the table data.
   * @param keyProvider
   *    a key provider that provides the keys that identify rows in the table.
   * @param columns
   *    the attributes that represent the columns of the table.
   * @param keyAttributes
   *    the name of the attributes in <code>columns</code> that represent the
   *    primary key of the row.
   * @param indexSupport
   *    provides the mapping between the row keys and their corresponding
   *    SNMP index values.
   */
  public MBeanAttributeMOTableInfo(ObjectName name,
                                   MBeanAttributeKeyProvider keyProvider,
                                   TypedAttribute[] columns,
                                   String[] keyAttributes,
                                   JMXIndexSupport indexSupport) {
    super(name);
    this.keyProvider = keyProvider;
    this.indexAttributes = keyAttributes;
    this.columns = columns;
    this.indexSupport = indexSupport;
  }

  public String[] getIndexAttributes() {
    return indexAttributes;
  }

  public TypedAttribute[] getColumns() {
    return columns;
  }

  public MBeanAttributeKeyProvider getKeyProvider() {
    return keyProvider;
  }

  public JMXIndexSupport getIndexSupport() {
    return indexSupport;
  }

  public Object getKey(MBeanServerConnection server, ObjectName row) throws
      IOException, ReflectionException, InstanceNotFoundException {
    Object[] key = new Object[indexAttributes.length];
    AttributeList keyObjects = server.getAttributes(row, indexAttributes);
    for (int i=0; i<keyObjects.size(); i++) {
      key[i] = ((Attribute)keyObjects.get(i)).getValue();
    }
    if (key.length == 1) {
      return key[0];
    }
    return key;
  }
}
