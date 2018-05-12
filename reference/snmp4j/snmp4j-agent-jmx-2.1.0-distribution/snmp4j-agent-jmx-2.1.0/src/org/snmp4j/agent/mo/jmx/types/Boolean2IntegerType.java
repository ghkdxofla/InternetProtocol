/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - Boolean2IntegerType.java  
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

public class Boolean2IntegerType extends TypedAttribute {

  public static final int TRUTH_VALUE_FOR_TRUE = 1;
  public static final int TRUTH_VALUE_FOR_FALSE = 2;

  private Integer trueValue = new Integer(TRUTH_VALUE_FOR_TRUE);
  private Integer falseValue = new Integer(TRUTH_VALUE_FOR_FALSE);

  public Boolean2IntegerType(String name) {
    super(name, Integer.class);
  }

  public Boolean2IntegerType(String name,
                             Integer trueValue, Integer falseValue) {
    super(name, Integer.class);
    this.trueValue = trueValue;
    this.falseValue = falseValue;
  }

  public Object transformFromNative(Object nativeValue, ObjectName objectName) {
    boolean b = ((Boolean)nativeValue).booleanValue();
    return b ? trueValue : falseValue;
  }

  public Object transformToNative(Object transformedValue,
                                  Object oldNativeValue, ObjectName objectName) {
    if (transformedValue instanceof Integer) {
      if (transformedValue.equals(trueValue)) {
        return true;
      }
      else if (transformedValue.equals(falseValue)) {
        return false;
      }
    }
    return null;
  }
}
