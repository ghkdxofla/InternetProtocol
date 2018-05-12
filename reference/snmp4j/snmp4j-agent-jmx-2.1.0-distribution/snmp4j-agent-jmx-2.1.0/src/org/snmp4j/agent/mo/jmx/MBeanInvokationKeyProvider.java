/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanInvokationKeyProvider.java  
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

import javax.management.ObjectName;
import org.snmp4j.agent.mo.jmx.types.TypedAttribute;
import javax.management.MBeanServerConnection;
import java.io.IOException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.ReflectionException;

/**
 * If a MBean provides the keys for a SNMP conceptual table by an attribute
 * and the objects that represent rows of that table can be accessed through
 * a call to an operation of that MBean then this class can be used to provide
 * the row objects for the SNMP conceptual table.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanInvokationKeyProvider extends MBeanAttributeKeyProvider {

  private String rowProvidingOperation;

  public MBeanInvokationKeyProvider(ObjectName mBeanName,
                                    TypedAttribute keyAttribute,
                                    String rowProvidingOperation) {
    super(mBeanName, keyAttribute);
    this.rowProvidingOperation = rowProvidingOperation;
  }

  public MBeanInvokationKeyProvider(ObjectName mBeanName,
                                    TypedAttribute keyAttribute,
                                    String rowProvidingOperation,
                                    boolean keysNeedSorting) {
    super(mBeanName, keyAttribute, keysNeedSorting);
    this.rowProvidingOperation = rowProvidingOperation;
  }

  public Object getRowValues(MBeanServerConnection server, Object indexObject)
      throws IOException, MBeanException, AttributeNotFoundException,
      InstanceNotFoundException, ReflectionException
  {
    Object row =
        server.invoke(getObjectName(), rowProvidingOperation,
                      new Object[] { indexObject },
                      new String[] { getAttribute().getType().getName() });
    return row;
  }

}
