/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXDefaultMOFactory.java  
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

import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.*;
import org.snmp4j.smi.*;
import org.snmp4j.agent.mo.snmp.tc.TextualConvention;


/**
 * The <code>JMXDefaultMOFactory</code> extends the default SNMP4J-Agent
 * {@link ManagedObject} factory to create {@link MOScalarJMX} and
 * {@link MOTableJMX} instances instead of {@link MOScalar} and
 * {@link DefaultMOTable} instances respectively.
 * <p>
 * Scalars are created ready-to-use with the supplied or a default
 * JMXScalarSupport instance. For tables, a {@link DefaultMOMutableTableModel}
 * is created at initialization that need to be replaced externally by
 * a {@link JMXTableModel} to instrument a table with JMX.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class JMXDefaultMOFactory extends DefaultMOFactory {

  private MBeanServerConnection server;
  private JMXScalarSupport scalarSupport;

  /**
   * Creates a <code>JMXDefaultMOFactory</code> instance backed by the specified
   * MBean server. If the <code>JMXScalarSupport</code> member is not set before
   * the factory is used, a default <code>MBeanAttributeMOScalarSupport</code>
   * is used.
   * @param server
   *    the MBeanServerConnection to be used by this JMXDefaultMOFactory.
   */
  public JMXDefaultMOFactory(MBeanServerConnection server) {
    this(server, null);
  }

  /**
   * Creates a <code>JMXDefaultMOFactory</code> instance backed by the specified
   * MBean server and using the supplied JMXScalarSupport instance to create
   * scalars.
   *
   * @param server MBeanServerConnection
   * @param scalarSupport JMXScalarSupport
   */
  public JMXDefaultMOFactory(MBeanServerConnection server,
                             JMXScalarSupport scalarSupport) {
    this.server = server;
    this.scalarSupport = scalarSupport;
  }

  public synchronized JMXScalarSupport getScalarSupport() {
    if (scalarSupport == null) {
      scalarSupport = new MBeanAttributeMOScalarSupport(server);
    }
    return scalarSupport;
  }

  public MBeanServerConnection getServer() {
    return server;
  }

  public synchronized void setScalarSupport(JMXScalarSupport scalarSupport) {
    this.scalarSupport = scalarSupport;
  }

  public <V extends Variable> MOScalar<V> createScalar(OID id, MOAccess access, V value) {
    return new MOScalarJMX<V>(getScalarSupport(), id, access, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V extends Variable> MOScalar<V> createScalar(OID id, MOAccess access, V value,
                               String tcModuleName, String textualConvention) {
    TextualConvention<V> tc =
        getTextualConvention(tcModuleName, textualConvention);
    if (tc != null) {
      return tc.createScalar(id, access, value);
    }
    return createScalar(id, access, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R extends MOTableRow, M extends MOTableModel<R>> MOTable<R, MOColumn, M>
    createTable(OID oid, MOTableIndex indexDef, MOColumn[] columns, M model) {
    return (MOTable<R, MOColumn, M>) new MOTableJMX<R,MOColumn,M>(oid, indexDef, columns, model);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R extends MOTableRow, M extends MOTableModel<R>> MOTable<R, MOColumn, M>
    createTable(OID oid, MOTableIndex indexDef, MOColumn[] columns) {
    return new MOTableJMX(oid, indexDef, columns, createTableModel(oid, indexDef, columns));
  }
}
