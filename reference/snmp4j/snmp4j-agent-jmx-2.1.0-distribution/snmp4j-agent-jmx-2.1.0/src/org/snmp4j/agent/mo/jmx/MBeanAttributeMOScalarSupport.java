/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanAttributeMOScalarSupport.java  
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

import org.snmp4j.smi.*;
import java.util.Map;
import javax.management.MBeanServerConnection;
import org.snmp4j.PDU;
import org.snmp4j.agent.mo.jmx.types.SMIVariant;
import org.snmp4j.agent.mo.jmx.types.TypedAttribute;
import org.snmp4j.agent.mo.jmx.types.CombinedTypedAttribute;
import javax.management.ObjectName;
// For JavaDoc
import org.snmp4j.agent.mo.MOScalar;

/**
 * <code>MBeanAttributeMOScalarSupport</code> objects map zero or more MBean
 * attributes to their corresponding {@link MOScalar} instance.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanAttributeMOScalarSupport extends AbstractMBeanSupport
    implements JMXScalarSupport {

  public MBeanAttributeMOScalarSupport(MBeanServerConnection server) {
    super(server);
  }

  /**
   * Adds a scalar to MBean attribute mapping.
   * @param oid
   *    the instance OID (including the .0) of the SNMP scalar object.
   * @param mBean
   *    an attribute description of a MBean.
   */
  public synchronized void add(OID oid, MBeanAttributeMOInfo mBean) {
    oid2MBeanMap.put(oid, mBean);
  }

  /**
   * Adds a list of scalar to attribute mappings for the specified MBean.
   * @param mBeanName
   *    the name of the MBean providing the attributes.
   * @param mBeanScalarAttributeDescriptions
   *    an two dimensional array of scalar descriptions. Each description
   *    contains three elements:
   * <ol>
   * <li>the <code>OID</code> of the scalar SNMP instance,</li>
   * <li>the name of the attribute as <code>String</code>, and</li>
   * <li>the <code>Class</code> of the attributes value.</li>
   * </ol>
   */
  public synchronized void addAll(ObjectName mBeanName,
                                  Object[][] mBeanScalarAttributeDescriptions) {
    for (Object[] attrDescr : mBeanScalarAttributeDescriptions) {
      MBeanAttributeMOInfo mBeanInfo;
      if (attrDescr[1] instanceof CombinedTypedAttribute) {
        mBeanInfo = new MBeanMultiAttributeMOInfo(mBeanName,
                                                  (CombinedTypedAttribute)
                                                  attrDescr[1]);
      }
      else if (attrDescr[1] instanceof TypedAttribute) {
        mBeanInfo = new MBeanAttributeMOInfo(mBeanName,
                                             (TypedAttribute) attrDescr[1]);
      }
      else {
        mBeanInfo = new MBeanAttributeMOInfo(mBeanName,
                                             (String) attrDescr[1],
                                             (Class) attrDescr[2]);
      }
      oid2MBeanMap.put((OID)attrDescr[0], mBeanInfo);
    }
  }

  /**
   * Checks the value of the specified object instance and type.
   *
   * @param scalarInstanceOID the instance OID of the target object.
   * @param value the instance's new value.
   * @return zero on success or a SNMP error status value if setting the value
   *   would fail.
   */
  public int checkScalarValue(OID scalarInstanceOID, Variable value) {
    MBeanAttributeMOInfo mBeanAttrMOInfo =
        (MBeanAttributeMOInfo) getMBeanMOInfo(scalarInstanceOID);
    if (mBeanAttrMOInfo != null) {
      Object attr = mBeanAttrMOInfo.getAttribute().transformSMI2Object(value);
      if (attr == null) {
        return PDU.wrongValue;
      }
      return PDU.noError;
    }
    return PDU.resourceUnavailable;
  }

  /**
   * Gets the actual value for the specified object instance and type.
   *
   * @param scalarInstanceOID the instance OID of the target object.
   * @param value the instance to hold the return value.
   * @return zero on success or a SNMP error status value if fetching the
   *   value fails.
   */
  public int getScalarValue(OID scalarInstanceOID, Variable value) {
    MBeanAttributeMOInfo mBeanAttrMOInfo =
        (MBeanAttributeMOInfo) getMBeanMOInfo(scalarInstanceOID);
    if (mBeanAttrMOInfo != null) {
      try {
        Object attr = mBeanAttrMOInfo.getAttribute(server);
        if (attr == null) {
          return PDU.noError;
        }
        SMIVariant v = new SMIVariant(value);
        return v.setValue(attr);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      return PDU.genErr;
    }
    return PDU.noSuchName;
  }

  /**
   * Sets the value of the specified object instance and type.
   *
   * @param scalarInstanceOID the instance OID of the target object.
   * @param value the instance's new value.
   * @return zero on success or a SNMP error status value if setting the value
   *   fails.
   */
  public int setScalarValue(OID scalarInstanceOID, Variable value) {
    MBeanAttributeMOInfo mBeanAttrMOInfo =
        (MBeanAttributeMOInfo) getMBeanMOInfo(scalarInstanceOID);
    if (mBeanAttrMOInfo != null) {
      try {
        Object attr = mBeanAttrMOInfo.getAttribute().transformSMI2Object(value);
        mBeanAttrMOInfo.setAttribute(server, attr);
        return PDU.noError;
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return PDU.genErr;
  }
}
