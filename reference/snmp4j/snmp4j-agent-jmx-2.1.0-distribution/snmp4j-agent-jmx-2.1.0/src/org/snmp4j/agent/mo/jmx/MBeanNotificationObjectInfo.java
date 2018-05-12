/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanNotificationObjectInfo.java  
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

import org.snmp4j.agent.mo.jmx.types.TypedAttribute;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import javax.management.openmbean.CompositeDataSupport;
import org.snmp4j.agent.mo.jmx.types.SMIVariant;
import org.snmp4j.smi.Variable;

/**
 * The <code>MBeanNotificationObjectInfo</code> maps a SNMP object class OID
 * and value type to a MBean attribute.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanNotificationObjectInfo {

  private OID classID;
  private Variable valueType;
  private TypedAttribute attribute;

  public MBeanNotificationObjectInfo(OID classID,
                                     Variable valueType,
                                     TypedAttribute attribute) {
    this.classID = classID;
    this.valueType = valueType;
    this.attribute = attribute;
  }

  public VariableBinding getVariableBinding(Object mBeanNotifyUserObject,
                                            OID index) {
    Object value;
    if (mBeanNotifyUserObject instanceof CompositeDataSupport) {
      CompositeDataSupport data = (CompositeDataSupport)mBeanNotifyUserObject;
      value = data.get(attribute.getName());
    }
    else {
      value = mBeanNotifyUserObject;
    }
    Variable smiValue = (Variable) valueType.clone();
    SMIVariant smiVariant = new SMIVariant(smiValue);
    value = attribute.transformFromNative(value, null);
    smiVariant.setValue(value);
    OID oid = new OID(classID);
    if (index != null) {
      oid.append(index);
    }
    return new VariableBinding(oid, smiValue);
  }

}
