/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanProxyType.java  
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
package org.snmp4j.agent.mo.jmx.types;

import javax.management.ObjectName;
import javax.management.MBeanServerConnection;

public class MBeanProxyType extends TypedAttribute {

  private ObjectName objectName;
  private MBeanServerConnection server;
  private String operationName;
  private TypedAttribute proxy;

  public MBeanProxyType(MBeanServerConnection server,
                        ObjectName name,
                        Class<Long> type,
                        String operationName, TypedAttribute proxy) {
    super(proxy.getName(), type);
    this.server = server;
    this.objectName = name;
    this.operationName = operationName;
    this.proxy = proxy;
  }

  public Object transformFromNative(Object nativeValue, ObjectName objectName) {
    Object param = proxy.transformFromNative(nativeValue, null);
    try {
      Object result = server.invoke(this.objectName, operationName,
                                    new Object[] {param},
                                    new String[] {proxy.getType().getName()});
      return result;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public Object transformToNative(Object transformedValue,
                                  Object oldNativeValue, ObjectName objectName) {
    throw new UnsupportedOperationException();
  }

}
