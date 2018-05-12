/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXNotificationIndexSupport.java  
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

/**
 * Notification objects can refer to scalar and tabular data. Whereas for scalar
 * data the OID of the instance is constant, the index portion of the instance
 * identifier of tabular data may vary from notification to notification.
 * <p>
 * This interface provides the means needed by a {@link MBeanNotificationInfo}
 * instance to send a notification referring to tabular data.
 *
 * @author Frank Fock
 * @version 2.0
 */
public interface JMXNotificationIndexSupport {

  /**
   * Initialize the index support instance with the MBean notification object.
   * @param notificationUserObject
   *    an Object that contains or refers to the payload data of the
   *    notification.
   */
  void initialize(Object notificationUserObject);

  /**
   * Gets the index portion for the object with the specified index in the
   * notification.
   * @param objectIndex
   *    a zero based index into the SNMP notification objects.
   * @return OID
   *    a row index for the n-th notification object.
   */
  OID getIndex(int objectIndex);

}
