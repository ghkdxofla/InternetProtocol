/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanAttributeMOInfo.java  
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
 * The <code>MBeanAttributeMOInfo</code> describes an attribute of a MBean.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanAttributeMOInfo extends MBeanMOInfo {

  protected TypedAttribute attribute;

  /**
   * Creates an attribute description with the attributes name and type.
   * @param name
   *    the MBean's <code>ObjectName</code>.
   * @param attributeName
   *    the name of the attribute.
   * @param attributeType
   *    the class of the attributes value objects.
   */
  public MBeanAttributeMOInfo(ObjectName name, String attributeName,
                              Class attributeType) {
    super(name);
    this.attribute = new TypedAttribute(attributeName, attributeType);
  }

  /**
   * Creates an attribute description with a <code>TypedAttribute</code>.
   * @param name
   *    the MBean's <code>ObjectName</code>.
   * @param attribute
   *    a <code>TypedAttribute</code> describing the attribute. The value is
   *    by reference.
   */
  public MBeanAttributeMOInfo(ObjectName name, TypedAttribute attribute) {
    super(name);
    this.attribute = attribute;
  }

  public String getAttributeName() {
    return attribute.getName();
  }

  public Class getAttributeType() {
    return attribute.getType();
  }

  public TypedAttribute getAttribute() {
    return attribute;
  }

  public Object getAttribute(MBeanServerConnection server) throws ReflectionException,
      InstanceNotFoundException, AttributeNotFoundException, MBeanException,
      IOException {
    return getAttribute(server, getObjectName(), getAttribute());
  }

  public static Object getAttribute(MBeanServerConnection server,
                                    ObjectName name,
                                    TypedAttribute attribute) throws
      ReflectionException,
      InstanceNotFoundException, AttributeNotFoundException, MBeanException,
      IOException
  {
    Object nativeValue = server.getAttribute(name, attribute.getName());
    return attribute.transformFromNative(nativeValue, name);
  }


  public void setAttribute(MBeanServerConnection server, Object value) throws
      ReflectionException, MBeanException, InvalidAttributeValueException,
      AttributeNotFoundException, InstanceNotFoundException, IOException {
    setAttribute(server, getObjectName(), getAttribute(), value);
  }

  public static void setAttribute(MBeanServerConnection server,
                                  ObjectName name,
                                  TypedAttribute attribute,
                                  Object value) throws
      ReflectionException, MBeanException, InvalidAttributeValueException,
      AttributeNotFoundException, InstanceNotFoundException, IOException
  {
    Object nativeValue;
    if (attribute.isNativeValueAlwaysNeeded()) {
      nativeValue =
          server.getAttribute(name, attribute.getName());
      nativeValue = attribute.transformToNative(value, nativeValue, name);
    }
    else {
      nativeValue = attribute.transformToNative(value, null, name);
    }
    server.setAttribute(name, new Attribute(attribute.getName(), nativeValue));
  }

}
