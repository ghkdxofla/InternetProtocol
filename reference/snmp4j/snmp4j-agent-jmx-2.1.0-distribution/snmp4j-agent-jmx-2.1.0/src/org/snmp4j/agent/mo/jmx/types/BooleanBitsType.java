/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - BooleanBitsType.java  
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

import org.snmp4j.smi.OctetString;
import javax.management.*;

public class BooleanBitsType extends TypedAttribute {

  private int offset = 0;

  public BooleanBitsType(String name, int offset) {
    super(name, byte[].class);
    this.offset = offset;
  }

  public int getOffset() {
    return offset;
  }

  public Object transformFromNative(Object nativeValue, ObjectName objectName) {
    StringBuffer buf =
        new StringBuffer(((Boolean)nativeValue).booleanValue() ? "1" : "0");
    for (int i=0; i<offset; i++) {
      buf.insert(0, "0");
    }
    while (buf.length() % 8 > 0) {
      buf.append("0");
    }
    return OctetString.fromString(buf.toString(), 2).toByteArray();
  }

  public Object transformToNative(Object transformedValue,
                                  Object oldNativeValue, ObjectName objectName) {
    OctetString os = new OctetString((byte[])transformedValue);
    String s = os.toString(2);
    if (s.length() <= offset) {
      return false;
    }
    return (s.charAt(offset) == '1');
  }

}
