/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanMOInfo.java  
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
import java.util.Set;
import javax.management.MBeanServerConnection;
import java.io.IOException;
import javax.management.ObjectInstance;

/**
 * The <code>MBeanMOInfo</code> provides information associated with a MBean
 * through its <code>ObjectName</code>.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanMOInfo {

  private ObjectName name;

  public MBeanMOInfo(ObjectName name) {
    this.name = name;
  }

  /**
   * Returns the name of the MBean.
   * @return
   *    the MBean's <code>ObjectName</code>.
   */
  public ObjectName getObjectName() {
    return name;
  }

  /**
   * Returns the MBean object instances associated with this
   * {@link #getObjectName()} at the supplied MBean server.
   *
   * @param server
   *    a <code>MBeanServerConnection</code>.
   * @return Set
   *    a set of {@link ObjectInstance}s.
   * @throws IOException
   */
  public Set<ObjectInstance> getMBeanNames(MBeanServerConnection server)
      throws IOException
  {
    return server.queryMBeans(getObjectName(), null);
  }

}
