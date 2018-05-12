/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanMultiAttributeMOInfo.java  
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

import javax.management.*;
import java.io.IOException;
import org.snmp4j.agent.mo.jmx.types.*;

/**
 * There are cases where a single SNMP variable is mapped to several
 * MBean attributes. <code>MBeanMultiAttributeMOInfo</code> provides means
 * for such a mapping.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanMultiAttributeMOInfo extends MBeanAttributeMOInfo {

  public MBeanMultiAttributeMOInfo(ObjectName name,
                                   CombinedTypedAttribute attributes) {
    super(name, attributes);
  }

  public CombinedTypedAttribute getAttributes() {
    return (CombinedTypedAttribute) super.getAttribute();
  }

  public Object getAttribute(MBeanServerConnection server) throws ReflectionException,
      InstanceNotFoundException, AttributeNotFoundException, MBeanException,
      IOException {
    return MBeanMultiAttributeMOInfo.getAttribute(server, getObjectName(),
                                                  getAttributes());
  }

  public static Object getAttribute(MBeanServerConnection server,
                                    ObjectName name,
                                    CombinedTypedAttribute attributes) throws
      ReflectionException,
      InstanceNotFoundException, AttributeNotFoundException, MBeanException,
      IOException
  {
    TypedAttribute[] attr = attributes.getAttributes();
    Object[] values = new Object[attr.length];
    for (int i=0; i<values.length; i++) {
      values[i] = server.getAttribute(name, attr[i].getName());
      values[i] = attr[i].transformFromNative(values[i], name);
    }
    return attributes.transformFromNative(values, name);
  }


  public void setAttribute(MBeanServerConnection server, Object value) throws
      ReflectionException, MBeanException, InvalidAttributeValueException,
      AttributeNotFoundException, InstanceNotFoundException, IOException {
    MBeanMultiAttributeMOInfo.setAttribute(server, getObjectName(),
                                           getAttributes(), value);
  }

  public static void setAttribute(MBeanServerConnection server,
                                  ObjectName name,
                                  CombinedTypedAttribute attributes,
                                  Object value) throws
      ReflectionException, MBeanException, InvalidAttributeValueException,
      AttributeNotFoundException, InstanceNotFoundException, IOException
  {
    TypedAttribute[] attr = attributes.getAttributes();
    Object[] values = new Object[attr.length];
    Object[] nativeValue;
    if (attributes.isNativeValueAlwaysNeeded()) {
      for (int i=0; i<values.length; i++) {
        values[i] = server.getAttribute(name, attr[i].getName());
      }
      nativeValue = (Object[]) attributes.transformToNative(value, values, null);
    }
    else {
      nativeValue = (Object[]) attributes.transformToNative(value, null, null);
    }
    for (int i=0; (i<nativeValue.length) && (i<attr.length); i++) {
      Object v = attr[i].transformToNative(nativeValue[i], values[i], null);
      if (v != null) {
        server.setAttribute(name, new Attribute(attr[i].getName(), v));
      }
    }
  }
}
