/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanNotificationInfo.java  
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

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

/**
 * The <code>MBeanNotificationInfo</code> class represents information for
 * a MBean notification to SNMP notification type mapping. Since MBean
 * notifications contain payload information that does not include information
 * about the payload's structural source. As a consequence, the payload cannot
 * always be mapped to a SNMP notification without additional information about
 * table indexing. This information is provided by a
 * {@link JMXNotificationIndexSupport} instance then.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanNotificationInfo {

  private JMXNotificationIndexSupport indexSupport;
  private MBeanNotificationObjectInfo[] objects;
  private OctetString context = new OctetString();

  /**
   * Creates a notification mapping between a MBean notification to a SNMP
   * notification. The notification mapping uses the default context.
   * @param objects
   *    the mappings of the SNMP notification payload to MBean attributes.
   * @param indexSupport
   *    the index support instance that provides indexes for the notification
   *    payload object OIDs where necessary.
   */
  public MBeanNotificationInfo(MBeanNotificationObjectInfo[] objects,
                               JMXNotificationIndexSupport indexSupport) {
    this.indexSupport = indexSupport;
    this.objects = objects;
  }

  /**
   * Creates a notification mapping between a MBean notification to a SNMP
   * notification.
   * @param objects
   *    the mappings of the SNMP notification payload to MBean attributes.
   * @param indexSupport
   *    the index support instance that provides indexes for the notification
   *    payload object OIDs where necessary.
   * @param context
   *    the context of the notification. Default is a zero length string.
   */
  public MBeanNotificationInfo(MBeanNotificationObjectInfo[] objects,
                               JMXNotificationIndexSupport indexSupport,
                               OctetString context) {
    this(objects, indexSupport);
    this.context = context;
  }

  public VariableBinding[] getNotificationPayload(Object notificationObject) {
    VariableBinding[] vbs = new VariableBinding[objects.length];
    if (indexSupport != null) {
      synchronized (indexSupport) {
        indexSupport.initialize(notificationObject);
        for (int i=0; i<objects.length; i++) {
          MBeanNotificationObjectInfo oinfo = objects[i];
          OID index = indexSupport.getIndex(i);
          vbs[i] = oinfo.getVariableBinding(notificationObject, index);
        }
      }
    }
    else {
      for (int i=0; i<objects.length; i++) {
        MBeanNotificationObjectInfo oinfo = objects[i];
        vbs[i] = oinfo.getVariableBinding(notificationObject, null);
      }
    }
    return vbs;
  }

  public OctetString getContext() {
    return context;
  }

}
