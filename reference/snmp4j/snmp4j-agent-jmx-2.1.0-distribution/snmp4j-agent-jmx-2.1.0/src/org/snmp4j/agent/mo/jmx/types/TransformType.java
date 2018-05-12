/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - TransformType.java  
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

import javax.management.*;

public interface TransformType {

  Object transformFromNative(Object nativeValue, ObjectName objectName);

  Object transformToNative(Object transformedValue, Object oldNativeValue,
                           ObjectName objectName);

  /**
   * Indicates whether a caller of a "toNative" transformation needs to provide
   * the old native value in order to get a successful transformation. Otherwise
   * the caller may provide a <code>null</code> to save CPU cycles.
   *
   * @return
   *    <code>true</code> if the caller must always provide a none
   *    <code>null</code> value for the <code>nativeValue</code> parameter of
   *    the transformation methods.
   */
  boolean isNativeValueAlwaysNeeded();
}
