/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanActionMOScalarSupport.java  
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

import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import javax.management.ObjectName;
import javax.management.MBeanServerConnection;
import org.snmp4j.PDU;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.SMIConstants;

/**
 * A MBean action is basically a method call on an MBean. An action in SNMP
 * is normally modeled as an enumeration where each value specifies a possible
 * action or a parameter set for an action. On the other hand, SNMP enumerations
 * are also used to indicate the states of a managed object.
 * <p>
 * The <code>MBeanActionMOScalarSupport</code> class provides a mapping between
 * these two action models for an arbitrary number of scalar instances and
 * corresponding actions.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanActionMOScalarSupport extends AbstractMBeanSupport
    implements JMXScalarSupport
{

  private static LogAdapter LOGGER = LogFactory.getLogger(MBeanActionMOScalarSupport.class);
  
  public MBeanActionMOScalarSupport(MBeanServerConnection server) {
    super(server);
  }

  /**
   * Adds an action mapping to the supported mappings.
   * @param oid
   *    the instance OID of a scalar SNMP variable.
   * @param mBean
   *    the action mapping information of the MBean actions/states.
   */
  public synchronized void add(OID oid, MBeanActionMOInfo mBean) {
    oid2MBeanMap.put(oid, mBean);
  }

  /**
   * Adds a list of action mappings related to a single MBean.
   * @param mBeanName
   *    the <code>ObjectName</code> of the MBean providing the actions.
   * @param mBeanScalarAttributeDescriptions
   *    an two dimensional array of action descriptions. Each description
   *    contains three elements:
   * <ol>
   * <li>the OID of the scalar SNMP instance that manages the action,</li>
   * <li>an array of MBeanStateInfo instances, and</li>
   * <li>an array of MBeanActionInfo instances.</li>
   * </ol>
   */
  public synchronized void addAll(ObjectName mBeanName,
                                  Object[][] mBeanScalarAttributeDescriptions) {
    for (Object[] attrDescr : mBeanScalarAttributeDescriptions) {
      MBeanActionMOInfo mBeanInfo;
      mBeanInfo = new MBeanActionMOInfo(mBeanName,
                                        (MBeanStateInfo[]) attrDescr[1],
                                        (MBeanActionInfo[]) attrDescr[2]);
      oid2MBeanMap.put((OID)attrDescr[0], mBeanInfo);
    }
  }

  public int checkScalarValue(OID scalarInstanceOID, Variable value) {
    MBeanActionMOInfo mBeanActionMOInfo = getActionInfo(scalarInstanceOID);
    if (mBeanActionMOInfo != null) {
      if (value.getSyntax() != SMIConstants.SYNTAX_INTEGER32) {
        return PDU.wrongType;
      }
      int actionID = ((Integer32)value).getValue();
      for (MBeanActionInfo action : mBeanActionMOInfo.getActions()) {
        if (actionID == action.getActionID()) {
          return PDU.noError;
        }
      }
      return PDU.wrongValue;
    }
    return PDU.resourceUnavailable;
  }

  public int getScalarValue(OID scalarInstanceOID, Variable value) {
    MBeanActionMOInfo mBeanActionMOInfo = getActionInfo(scalarInstanceOID);
    if (mBeanActionMOInfo != null) {
      // get state
      Integer32 v = (Integer32)value;
      for (MBeanStateInfo stateInfo : mBeanActionMOInfo.getStates()) {
        // check for default state
        if (stateInfo.getStateAttribute() == null) {
          v.setValue(stateInfo.getStateID());
          return PDU.noError;
        }
        try {
          Object attr;
          if (stateInfo.getStateAttribute().getName() == null) {
            attr = mBeanActionMOInfo.getLastActionResult();
          }
          else {
            attr = MBeanAttributeMOInfo.getAttribute(server,
                mBeanActionMOInfo.getObjectName(),
                stateInfo.getStateAttribute());
          }
          if (((attr == null) && (stateInfo.getStateIndication() == null)) ||
              ((attr != null) && attr.equals(stateInfo.getStateIndication()))) {
            v.setValue(stateInfo.getStateID());
            return PDU.noError;
          }
        }
        catch (Exception ex) {
          // ignore
          LOGGER.error("Exception while getting scalar value from "+mBeanActionMOInfo+": "+ex.getMessage(), ex);
        }
      }
      return PDU.genErr;
    }
    return PDU.noSuchName;
  }

  public int setScalarValue(OID scalarInstanceOID, Variable value) {
    MBeanActionMOInfo mBeanActionMOInfo = getActionInfo(scalarInstanceOID);
    if (mBeanActionMOInfo != null) {
      int actionID = ((Integer32)value).getValue();
      for (MBeanActionInfo action : mBeanActionMOInfo.getActions()) {
        if (actionID == action.getActionID()) {
          try {
            Object result = server.invoke(mBeanActionMOInfo.getObjectName(),
                                          action.getMethod(),
                                          action.getParameters(),
                                          action.getSignature());
            mBeanActionMOInfo.setLastActionResult(result);
          }
          catch (Exception ex) {
            ex.printStackTrace();
            return PDU.genErr;
          }
          return PDU.noError;
        }
      }
    }
    return PDU.genErr;
  }

  private MBeanActionMOInfo getActionInfo(OID scalarInstanceOID) {
    return (MBeanActionMOInfo) getMBeanMOInfo(scalarInstanceOID);
  }
}
