/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanActionInfo.java  
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

/**
 * The <code>MBeanActionInfo</code> defines a MBean action. The information
 * needed to call a MBean action includes method name, parameters, and
 * optionally the concrete signature of the method implementing the action.
 * The action ID identifies the action
 *
 * @author Frank Fock
 * @version 2.0
 */
public class MBeanActionInfo {

  private int actionID;
  private String method;
  private Object[] parameters = new Object[0];
  private String[] signature = new String[0];

  public MBeanActionInfo(int actionID, String actionMethod) {
    this.actionID = actionID;
    this.method = actionMethod;
  }

  public MBeanActionInfo(int actionID, String actionMethod,
                         Object[] actionParameters) {
    this(actionID, actionMethod);
    if (actionParameters != null) {
      this.parameters = actionParameters;
      String[] signature = new String[actionParameters.length];
      for (int i = 0; i < actionParameters.length; i++) {
        signature[i] = actionParameters[i].getClass().getName();
      }
      this.signature = signature;
    }
  }

  public MBeanActionInfo(int actionID, String actionMethod,
                         Object[] actionParameters,
                         String[] signature) {
    this(actionID, actionMethod);
    this.parameters = actionParameters;
    this.signature = signature;
  }

  public int getActionID() {
    return actionID;
  }

  public String getMethod() {
    return method;
  }

  public Object[] getParameters() {
    return parameters;
  }

  public String[] getSignature() {
    return signature;
  }

}
