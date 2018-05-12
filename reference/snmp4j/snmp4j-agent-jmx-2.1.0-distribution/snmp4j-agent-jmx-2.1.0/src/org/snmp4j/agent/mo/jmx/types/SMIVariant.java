/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - SMIVariant.java  
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
package org.snmp4j.agent.mo.jmx.types;

import org.snmp4j.smi.Variable;
import org.snmp4j.smi.AssignableFromLong;
import org.snmp4j.smi.AssignableFromString;
import org.snmp4j.smi.AssignableFromByteArray;
import org.snmp4j.PDU;
import org.snmp4j.smi.AssignableFromInteger;
import java.lang.reflect.Method;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.smi.OID;

public class SMIVariant {

  private static final LogAdapter logger =
      LogFactory.getLogger(SMIVariant.class);

  private Variable variable;

  public SMIVariant(Variable variable) {
    this.variable = variable;
  }

  public final static void set(AssignableFromLong variable, Number value) {
    variable.setValue(value.longValue());
  }

  public final static void set(AssignableFromLong variable, long value) {
    variable.setValue(value);
  }

  public final static void set(AssignableFromLong variable, Object[] value) {
    variable.setValue(value.length);
  }

  public final static void set(AssignableFromInteger variable, Number value) {
    variable.setValue(value.intValue());
  }

  public final static void set(AssignableFromInteger variable, int value) {
    variable.setValue(value);
  }

  public final static void set(AssignableFromInteger variable, Object[] value) {
    variable.setValue(value.length);
  }

  public final static void set(AssignableFromInteger variable, Boolean value) {
    variable.setValue((value.booleanValue() ? 1 : 2));
  }

  public final static void set(AssignableFromString variable, String value) {
    variable.setValue(value);
  }

  public final static void set(OID variable, String value) {
    variable.setValue(value);
  }

  public final static void set(OID variable, int[] value) {
    variable.setValue(value);
  }

  public final static void set(AssignableFromByteArray variable, Byte[] value) {
    byte[] b = new byte[value.length];
    for (int i=0; i<value.length; i++) {
      b[i] = value[i].byteValue();
    }
    variable.setValue(b);
  }

  public final static void set(AssignableFromByteArray variable, byte[] value) {
    variable.setValue(value);
  }

  public final static Object toInteger(AssignableFromInteger variable) {
    return new Integer(variable.toInt());
  }

  public final static Object toBoolean(AssignableFromInteger variable) {
    return new Boolean((variable.toInt() == 1));
  }

  public final static Object toLong(AssignableFromLong variable) {
    return new Long(variable.toLong());
  }

  public final static Object toString(AssignableFromString variable) {
    return variable.toString();
  }

  public final static Object toByteArray(AssignableFromByteArray variable) {
    return variable.toByteArray();
  }

  public final static Object toString(OID variable) {
    return variable.toString();
  }

  public final static Object toIntegerArray(OID variable) {
    return variable.getValue();
  }

  public int setValue(Object object) {
    if (object == null) {
      logger.debug("Cannot transform null value for "+
                   variable.getClass().getName());
      return PDU.noError;
    }
    Class[] params = new Class[] { variable.getClass(), object.getClass() };
    try {
      Method m = getMethod("set", params);
      m.invoke(this, variable, object);
      logger.debug("Set new value to '"+variable+"' from '"+object+"'");
      return PDU.noError;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      logger.error("Failed to convert '"+object+"' of class '"+
                   ((object==null)?"?":object.getClass())+"' to '"+variable+
                   "' of type '"+variable.getSyntaxString()+"'");
      return PDU.genErr;
    }
  }

  public Object getValue(Class returnType) {
    Class[] params = new Class[] { variable.getClass() };
    try {
      String methodName = "to"+returnType.getSimpleName();
      methodName = methodName.replaceAll("\\[\\]", "Array");
      Method m = getMethod(methodName, params);
      try {
        Object returnValue = m.invoke(this, variable);
        return returnValue;
      }
      catch (UnsupportedOperationException uoex) {
        uoex.printStackTrace();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      logger.error("Failed to convert '"+variable+"' of type '"+
                   variable.getSyntaxString()+"' to '"+
                   returnType.getSimpleName()+"'");
      return null;
    }
    logger.error("Failed to convert '"+variable+"' of type '"+
                 variable.getSyntaxString()+"' to '"+returnType.getName()+
                 "'");
    return null;
  }

  public Variable getVariable() {
    return variable;
  }

  @SuppressWarnings("unchecked")
  private Method getMethod(String name, Class[] params) {
    Method[] methods = getClass().getMethods();
    for (Method method : methods) {
      if (method.getName().equals(name) &&
          (params.length == method.getParameterTypes().length)) {
        Class[] declParams = method.getParameterTypes();
        boolean usable = true;
        for (int i=0; i<declParams.length; i++) {
          usable &= (declParams[i].isAssignableFrom(params[i]));
        }
        if (usable) {
          return method;
        }
      }
    }
    return null;
  }
}
