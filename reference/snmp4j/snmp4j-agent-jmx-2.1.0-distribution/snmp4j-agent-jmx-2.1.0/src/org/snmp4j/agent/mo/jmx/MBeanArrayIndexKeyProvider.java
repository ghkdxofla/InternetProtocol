/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanArrayIndexKeyProvider.java  
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
import java.util.Iterator;
import javax.management.MBeanServerConnection;
import java.io.IOException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.ReflectionException;
import org.snmp4j.agent.mo.jmx.util.JMXArrayIndexKeyIterator;
import org.snmp4j.agent.mo.jmx.types.*;
import org.snmp4j.agent.mo.jmx.util.JMXArrayIndexKey;
import java.util.Collection;
import java.util.List;

/**
 * To map the array indexes of an array provided by a MBean to SNMP table row
 * indexes, the <code>MBeanArrayIndexKeyProvider</code> can be used.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanArrayIndexKeyProvider extends MBeanAttributeKeyProvider {

  /**
   * Creates an index mapping based on a MBean attribute that provides the
   * keys of the table rows. The attribute may return an instance of one of
   * the following supported classes:
   * <ul>
   * <li>Object[]</li>
   * <li>{@link List}</li>
   * <li>{@link Collection}</li>
   * </ul>
   * @param name
   *    the MBean's <code>ObjectName</code>.
   * @param attribute
   *    the description of the attribute that provides the row keys.
   */
  public MBeanArrayIndexKeyProvider(ObjectName name, TypedAttribute attribute) {
    super(name, attribute);
  }

  @SuppressWarnings("unchecked")
  public Iterator keyIterator(MBeanServerConnection server) throws IOException,
      MBeanException, AttributeNotFoundException, InstanceNotFoundException,
      ReflectionException {
    return new JMXArrayIndexKeyIterator<Object>(super.keyIterator(server));
  }

  @SuppressWarnings("unchecked")
  protected Iterator createTailIterator(Iterator it, int pos) {
    return new JMXArrayIndexKeyIterator<Object>(it, pos);
  }

  /**
   * Gets the row value(s) for the specified index object. If the index object
   * is not an instance of {@link JMXArrayIndexKey} the index object itself is
   * returned. Otherwise, the n-th object of the objects provided by the MBean
   * attribute supplied at creation time is returned where <code>n</code>
   * corresponds to the index object's index value.
   *
   * @param server
   *    a <code>MBeanServerConnection</code> to use to access the MBean.
   * @param indexObject
   *    a <code>JMXArrayIndexKey</code> denoting the row index/value(s) to
   *    return.
   * @return
   *    <code>indexObject</code> if it is not an instance of
   *    <code>JMXArrayIndexKey</code> or otherwise the n-th key (or row value).
   * @throws IOException
   * @throws MBeanException
   * @throws AttributeNotFoundException
   * @throws InstanceNotFoundException
   * @throws ReflectionException
   */
  public Object getRowValues(MBeanServerConnection server,
                             Object indexObject) throws IOException,
      MBeanException, AttributeNotFoundException, InstanceNotFoundException,
      ReflectionException
  {
    if (indexObject instanceof JMXArrayIndexKey) {
      int index = ((JMXArrayIndexKey) indexObject).getIndex();
      Object keys = getAttribute(server);
      if (keys instanceof List) {
        return ((List) keys).get(index);
      }
      if (keys instanceof Object[]) {
        return ((Object[]) keys)[index];
      }
      else if (keys instanceof Collection) {
        Iterator it = ((Collection) keys).iterator();
        for (int i = 0; (i < index) && (it.hasNext()); i++) {
          it.next();
        }
        if (it.hasNext()) {
          return it.next();
        }
      }
    }
    return indexObject;
  }

}
