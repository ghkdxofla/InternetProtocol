/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanAttributeListMOTableInfo.java  
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

/**
 * The <code>MBeanAttributeListMOTableInfo</code> provides a mapping between
 * a multi valued MBean attribute an a single column SNMP table.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanAttributeListMOTableInfo extends MBeanAttributeMOTableInfo {

  /**
   * Creates a single column SNMP table to multi valued MBean attribute mapping.
   * @param name
   *    the name of the MBean.
   * @param listProvider
   *    the key provider that provides the keys and values for the SNMP table.
   */
  public MBeanAttributeListMOTableInfo(ObjectName name,
                                       MBeanAttributeKeyProvider listProvider) {
    super(name, listProvider, null, null, new JMXSimpleArrayIndexSupport());
  }

}
