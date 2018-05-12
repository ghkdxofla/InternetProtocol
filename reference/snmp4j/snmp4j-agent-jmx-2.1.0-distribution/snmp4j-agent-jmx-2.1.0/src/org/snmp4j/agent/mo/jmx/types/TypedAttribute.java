/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - TypedAttribute.java  
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

import java.io.*;

import org.snmp4j.smi.Variable;
import javax.management.*;

/**
 * A <code>TypedAttribute</code> is the combination of a attribute name
 * and a <code>Class</code> instance denoting the name and type of a MBean
 * attribute value. Various transformation methods provide mappings to and from
 * SNMP values.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class TypedAttribute
    implements Serializable, SMITransformType, TransformType
{

  private String name;
  private Class type;

  public TypedAttribute(String name, String type) throws ClassNotFoundException {
    this.name = name;
    this.type = Class.forName(type);
  }

  public TypedAttribute(String name, Class type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public Class getType() {
    return type;
  }

  public void transformObject2SMI(Object object, Variable value) {
    SMIVariant colVariant = new SMIVariant(value);
    colVariant.setValue(object);
  }

  public Object transformSMI2Object(Variable value) {
    SMIVariant smiVariant = new SMIVariant(value);
    return smiVariant.getValue(type);
  }

  public Object transformFromNative(Object nativeValue, ObjectName objectName) {
    return nativeValue;
  }

  public Object transformToNative(Object transformedValue,
                                  Object oldNativeValue, ObjectName objectName) {
    return transformedValue;
  }

  public boolean isNativeValueAlwaysNeeded() {
    return false;
  }

}
