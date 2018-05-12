/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MOTableJMX.java  
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

import org.snmp4j.agent.mo.*;
import org.snmp4j.smi.OID;

/**
 * The <code>MOTableJMX</code> implements a {@link DefaultMOTable} for MBean
 * instrumented SNMP tables. Currently this class' implementation is the same
 * as {@link DefaultMOTable}. The real mapping between the SNMP data and the
 * MBean data is done by the table's model.
 *
 * @author Frank Fock
 */
public class MOTableJMX<R extends MOTableRow, C extends MOColumn, M extends MOTableModel<R>>
    extends DefaultMOTable<R, C, MOTableModel<R>> {

  public MOTableJMX(OID oid, MOTableIndex indexDef, C[] columns, M model) {
    super(oid, indexDef, columns, model);
  }

  public void setModel(JMXTableModel<R> model) {
    super.model = model;
  }

}
