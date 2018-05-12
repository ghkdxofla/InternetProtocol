/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - AbstractMBeanSupport.java  
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

import java.util.Map;
import org.snmp4j.smi.OID;
import java.util.HashMap;
import javax.management.MBeanServerConnection;

public abstract class AbstractMBeanSupport {

  protected Map<OID,MBeanMOInfo> oid2MBeanMap;
  protected MBeanServerConnection server;

  public AbstractMBeanSupport(MBeanServerConnection server) {
    this.server = server;
    this.oid2MBeanMap = new HashMap<OID,MBeanMOInfo>();
  }

  protected synchronized MBeanMOInfo getMBeanMOInfo(OID snmpOID) {
    return oid2MBeanMap.get(snmpOID);
  }

  public void removeMBean(OID oid) {
    oid2MBeanMap.remove(oid);
  }

}
