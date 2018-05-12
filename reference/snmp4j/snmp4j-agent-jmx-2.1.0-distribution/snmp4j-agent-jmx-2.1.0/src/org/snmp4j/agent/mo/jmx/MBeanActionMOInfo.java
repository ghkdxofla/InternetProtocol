/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanActionMOInfo.java  
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
 * The action information for an MBean ManagedObject association.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanActionMOInfo extends MBeanMOInfo {

  private MBeanActionInfo[] actions;
  private MBeanStateInfo[] states;
  private Object lastActionResult;

  public MBeanActionMOInfo(ObjectName name,
                           MBeanStateInfo[] states, MBeanActionInfo[] actions) {
    super(name);
    this.actions = actions;
    this.states = states;
  }

  public MBeanActionInfo[] getActions() {
    return actions;
  }

  public MBeanStateInfo[] getStates() {
    return states;
  }

  public Object getLastActionResult() {
    return lastActionResult;
  }

  public void setLastActionResult(Object lastActionResult) {
    this.lastActionResult = lastActionResult;
  }

}
