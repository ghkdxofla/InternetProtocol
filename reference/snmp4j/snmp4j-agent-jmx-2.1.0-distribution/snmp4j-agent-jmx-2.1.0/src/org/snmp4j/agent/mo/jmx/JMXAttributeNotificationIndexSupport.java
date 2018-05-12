/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXAttributeNotificationIndexSupport.java  
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
import org.snmp4j.agent.mo.jmx.types.TypedAttribute;
import javax.management.openmbean.CompositeDataSupport;

/**
 * Notification objects can refer to scalar and tabular data. Whereas for scalar
 * data the OID of the instance is constant, the index portion of the instance
 * identifier of tabular data may vary from notification to notification.
 * <p>
 * The <code>JMXAttributeNotificationIndexSupport</code> provides the index
 * portion for a notification object by using a <code>JMXIndexSupport</code>
 * instance. That instance can be shared with the table holding the target
 * object.
 *
 * @author Frank Fock
 * @version 2.0
 */
public class JMXAttributeNotificationIndexSupport
    implements JMXNotificationIndexSupport
{
  private TypedAttribute attribute;
  private JMXIndexSupport indexSupport;
  private OID index;

  public JMXAttributeNotificationIndexSupport(TypedAttribute attribute,
                                              JMXIndexSupport indexSupport) {
    this.attribute = attribute;
    this.indexSupport = indexSupport;
  }

  public void initialize(Object notificationUserObject) {
    Object key = notificationUserObject;
    if (notificationUserObject instanceof CompositeDataSupport) {
      key = ((CompositeDataSupport)
             notificationUserObject).get(attribute.getName());
    }
    index = indexSupport.mapToIndex(key);
  }

  public OID getIndex(int objectIndex) {
    return index;
  }
}
