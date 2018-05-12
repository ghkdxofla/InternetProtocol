/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - AbstractJMXTableSupport.java  
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

import java.util.*;

import org.snmp4j.agent.mo.*;
import org.snmp4j.smi.*;
import org.snmp4j.PDU;
import javax.management.ObjectName;

public abstract class AbstractJMXTableSupport implements JMXTableSupport {

  protected Map<OID,JMXTableDetailSupport> columnValueMappings =
      new HashMap<OID,JMXTableDetailSupport>(10);

  public AbstractJMXTableSupport() {
  }

  public void add(OID tableOID,
                  JMXRowSupport rowSupport,
                  JMXColumnSupport columnSupport) {
    this.columnValueMappings.put(tableOID,
                                 new JMXTableDetailSupport(rowSupport,
        columnSupport));
  }

  public JMXTableDetailSupport removeDetailSupport(OID tableOID) {
    return this.removeDetailSupport(tableOID);
  }

  public abstract OID getLastIndex(OID tableOID);

  public int getRow(OID tableOID, MOTableRow row) {
    Object rowKey = mapToRowId(tableOID, row.getIndex());
    if (rowKey != null) {
      JMXTableDetailSupport detailSupport = getTableDetailSupport(tableOID);
      if (detailSupport != null) {
        ObjectName mBean =
            detailSupport.getRowSupport().getRowMBean(tableOID, rowKey);
        for (int i = 0; i < row.size(); i++) {
          Variable v = row.getValue(i);
          detailSupport.getColumnSupport().getColumnValue(mBean, i, v);
        }
      }
      return PDU.noError;
    }
    return PDU.resourceUnavailable;
  }

  protected synchronized JMXTableDetailSupport getTableDetailSupport(OID tableOID) {
    return columnValueMappings.get(tableOID);
  }

  public JMXRowSupport getRowSupport(OID tableOID) {
    JMXTableDetailSupport detailSupport = getTableDetailSupport(tableOID);
    if (detailSupport != null) {
      return detailSupport.getRowSupport();
    }
    return null;
  }

  public JMXColumnSupport getColumnSupport(OID tableOID) {
    JMXTableDetailSupport detailSupport = getTableDetailSupport(tableOID);
    if (detailSupport != null) {
      return detailSupport.getColumnSupport();
    }
    return null;
  }

  public abstract int getRowCount(OID tableOID);

  public abstract Iterator rowIdIterator(OID tableOID);

  public abstract OID mapToIndex(OID tableOID, Object rowIdentifier);

  public abstract Object mapToRowId(OID tableOID, OID rowIndex);

  class JMXTableDetailSupport {
    private JMXColumnSupport columnSupport;
    private JMXRowSupport rowSupport;

    public JMXTableDetailSupport(JMXRowSupport rowSupport,
                                 JMXColumnSupport columnSupport) {
      this.rowSupport = rowSupport;
      this.columnSupport = columnSupport;
    }

    public JMXRowSupport getRowSupport() {
      return rowSupport;
    }

    public JMXColumnSupport getColumnSupport() {
      return columnSupport;
    }

  }

}
