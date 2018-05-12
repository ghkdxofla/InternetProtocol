/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXNotificationSupport.java  
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

import javax.management.Notification;
import javax.management.NotificationListener;
import java.util.HashMap;
import java.util.Map;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.agent.NotificationOriginator;

/**
 * The <code>JMXNotificationSupport</code> receives JMX notifications and
 * forwards them to a {@link NotificationOriginator}.
 *
 * @author Frank Fock
 * @version 2.0
 */
public class JMXNotificationSupport implements NotificationListener {

  private Map<OID,MBeanNotificationInfo> notificationInfos;
  private NotificationOriginator notificationOriginator;

  /**
   * Creates a <code>JMXNotificationSupport</code> to will use the supplied
   * <code>NotificationOriginator</code> to send SNMP notifications on behalf
   * of JMX notifications.
   *
   * @param notificationOriginator
   *   a <code>NotificationOriginator</code> instance
   */
  public JMXNotificationSupport(NotificationOriginator notificationOriginator) {
    this.notificationOriginator = notificationOriginator;
    notificationInfos = new HashMap<OID,MBeanNotificationInfo>();
  }

  /**
   * Adds a notification type to this support instance.
   * @param notificationID
   *   a notification or trap OID.
   * @param notificationInfo
   *   a <code>MBeanNotificationInfo</code> instance describing the mapping
   *   between JMX and SNMP notification.
   */
  public void add(OID notificationID, MBeanNotificationInfo notificationInfo) {
    notificationInfos.put(notificationID, notificationInfo);
  }

  /**
   * Adds all supplied notification types to this support instance.
   * @param notificationDefinitions
   *    an array containing zero or more arrays with two elements where the
   *    first is a notification or trap OID and the second element is
   *    a <code>MBeanNotificationInfo</code> instance describing the mapping
   *    between JMX and SNMP notification.
   */
  public void addAll(Object[][] notificationDefinitions) {
    for (Object[] tableDescr : notificationDefinitions) {
      MBeanNotificationInfo mBeanInfo =
          (MBeanNotificationInfo)tableDescr[1];
      notificationInfos.put((OID)tableDescr[0], mBeanInfo);
    }
  }

  /**
   * Invoked when a JMX notification occurs, this method sends a SNMP
   * notification
   *
   * @param notification
   *   The notification.
   * @param notificationID
   *   An OID instance which associates the listener with the MBean emitter.
   *   This object is passed to the MBean during the addListener call and
   *   resent, without modification, to the listener.
   *   The MBean object should not use or modify the object.
   */
  public void handleNotification(Notification notification, Object notificationID) {
    MBeanNotificationInfo info = notificationInfos.get((OID)notificationID);
    if (info != null) {
      VariableBinding[] vbs =
          info.getNotificationPayload(notification.getUserData());
      notificationOriginator.notify(info.getContext(), (OID)notificationID, vbs);
    }
  }
}
