/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - KeyIndexRelation.java  
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
package org.snmp4j.agent.mo.jmx.util;

import java.util.TreeMap;
import org.snmp4j.smi.OID;
import java.util.Map;
import java.util.SortedMap;
import java.util.HashMap;

public class KeyIndexRelation {

  private Map<Object,OID> keys;
  private SortedMap<OID,Object> indexes;

  KeyIndexRelation(int initialSize) {
    this.keys = new HashMap<Object,OID>(initialSize);
    this.indexes = new TreeMap<OID,Object>();
  }

  public Map<Object,OID> getKeys() {
    return keys;
  }

  public SortedMap<OID,Object> getIndexes() {
    return indexes;
  }
}
