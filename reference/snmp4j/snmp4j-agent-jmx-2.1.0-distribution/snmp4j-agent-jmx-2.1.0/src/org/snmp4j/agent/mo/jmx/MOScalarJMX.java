/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MOScalarJMX.java  
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

import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.smi.OID;
import org.snmp4j.agent.MOAccess;
import org.snmp4j.smi.Variable;
import org.snmp4j.agent.request.SubRequest;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.PDU;

/**
 * The <code>MOScalarJMX</code> actually implements a {@link MOScalar} that
 * gets and sets its value through a {@link JMXScalarSupport} proxy instance.
 * This proxy maps through several configuration and support objects one or more
 * scalar SNMP values to MBean attributes and vice versa.
 *
 * @author Frank Fock
 * @version 2.0
 */
public class MOScalarJMX<V extends Variable> extends MOScalar<V> {

  private JMXScalarSupport valueProxy;

  public MOScalarJMX(JMXScalarSupport valueProxy,
                     OID oid, MOAccess access, V initialValue) {
    super(oid, access, initialValue);
    this.valueProxy = valueProxy;
  }

  public int isValueOK(SubRequest request) {
    int status = super.isValueOK(request);
    if (status == SnmpConstants.SNMP_ERROR_SUCCESS) {
      Variable newValue = request.getVariableBinding().getVariable();
      status = valueProxy.checkScalarValue(getOid(), newValue);
    }
    return status;
  }

  public void commit(SubRequest request) {
    Variable newValue = request.getVariableBinding().getVariable();
    int status = valueProxy.setScalarValue(getOid(), newValue);
    if (status != PDU.noError) {
      request.getStatus().setErrorStatus(status);
    }
    else {
      super.commit(request);
    }
  }

  public void get(SubRequest request) {
    int status = valueProxy.getScalarValue(getOid(), super.getValue());
    if (status != PDU.noError) {
      request.getStatus().setErrorStatus(status);
    }
    super.get(request);
  }

  public void undo(SubRequest request) {
    Variable newValue = (Variable) request.getUndoValue();
    int status = valueProxy.setScalarValue(getOid(), newValue);
    request.getStatus().setErrorStatus(status);
  }

  public boolean next(SubRequest request) {
    int status = valueProxy.getScalarValue(getOid(), super.getValue());
    return status == PDU.noError && super.next(request);
  }

  protected String toStringDetails() {
    return ",valueProxy="+valueProxy;
  }

}
