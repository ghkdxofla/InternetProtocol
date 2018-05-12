/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - TypedCompositeDataAttribute.java  
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

import javax.management.openmbean.CompositeDataSupport;
import javax.management.*;

public class TypedCompositeDataAttribute extends TypedAttribute {

  private String compositeDataAttrName;
  private TypedAttribute proxyAttribute;

  public TypedCompositeDataAttribute(String beanName,
                                     String attrName,
                                     String type) throws ClassNotFoundException {
    super(beanName, type);
    this.compositeDataAttrName = attrName;
  }

  public TypedCompositeDataAttribute(String beanName,
                                     String attrName,
                                     Class<Long> type) {
    super(beanName, type);
    this.compositeDataAttrName = attrName;
  }

  public TypedCompositeDataAttribute(TypedAttribute proxyAttribute) {
    super(proxyAttribute.getName(), proxyAttribute.getType());
    this.compositeDataAttrName = proxyAttribute.getName();
    this.proxyAttribute = proxyAttribute;
  }

  public Object transformFromNative(Object nativeValue, ObjectName objectName) {
    if (nativeValue == null) {
      return null;
    }
    Object n = ((CompositeDataSupport)nativeValue).get(compositeDataAttrName);
    if (proxyAttribute != null) {
      n = proxyAttribute.transformFromNative(n, null);
    }
    return n;
  }

  public Object transformToNative(Object transformedValue,
                                  Object oldNativeValue, ObjectName objectName) {
    throw new UnsupportedOperationException();
  }
}
