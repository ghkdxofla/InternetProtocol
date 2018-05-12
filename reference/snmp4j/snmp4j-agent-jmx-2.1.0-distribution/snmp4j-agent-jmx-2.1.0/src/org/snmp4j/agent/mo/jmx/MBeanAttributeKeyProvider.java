/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanAttributeKeyProvider.java  
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

import java.io.IOException;
import java.util.*;
import javax.management.*;

import org.snmp4j.agent.mo.jmx.types.TypedAttribute;
import org.snmp4j.agent.mo.jmx.util.JMXArrayIndexKey;

/**
 * The <code>MBeanAttributeKeyProvider</code> provides the row keys of a
 * conceptual table from a MBean attribute. The keys are returned in the same
 * order as provided by the MBean attribute by default. If the
 * <code>keysNeedSorting</code> is set, keys are returned always in their
 * natural order.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class MBeanAttributeKeyProvider extends MBeanAttributeMOInfo {

  private boolean keysNeedSorting;
  private MBeanAttributeKeyProvider subKeyProvider;
  private String[] keyAttributes;

  /**
   * Creates a key provider with a MBean name and a attribute description.
   * @param name
   *    a MBean's <code>ObjectName</code>.
   * @param attribute
   *    the description of an attribute of the MBean identified by
   *    <code>name</code>.
   */
  public MBeanAttributeKeyProvider(ObjectName name, TypedAttribute attribute) {
    super(name, attribute);
  }

  /**
   * Creates a key provider with a MBean name and an attribute description.
   * @param name
   *    a MBean's <code>ObjectName</code>.
   * @param attribute
   *    the description of an attribute of the MBean identified by
   *    <code>name</code>.
   * @param keysNeedSorting
   *    if <code>true</code> keys will be sorted by their natural order.
   */
  public MBeanAttributeKeyProvider(ObjectName name, TypedAttribute attribute,
                                   boolean keysNeedSorting) {
    super(name, attribute);
    this.keysNeedSorting = keysNeedSorting;
  }

  /**
   * Creates a key provider with a MBean name and an attribute description.
   * @param name
   *    a MBean's <code>ObjectName</code>.
   * @param attribute
   *    the description of an attribute of the MBean identified by
   *    <code>name</code>.
   * @param keysNeedSorting
   *    if <code>true</code> keys will be sorted by their natural order.
   * @param subKeyProvider
   *    if not <code>null</code>, this key provider is called per key provided
   *    by this provider in order to form a combined key.
   * @param keyAttributes
   *    if <code>attribute</code> is <code>null</code> this parameter has to
   *    be specified to identify the key attributes that build the key object
   *    for the MBean instances under <code>name</code>.
   */
  public MBeanAttributeKeyProvider(ObjectName name, TypedAttribute attribute,
                                   boolean keysNeedSorting,
                                   MBeanAttributeKeyProvider subKeyProvider,
                                   String[] keyAttributes) {
    this(name, attribute, keysNeedSorting);
    this.subKeyProvider = subKeyProvider;
    this.keyAttributes = keyAttributes;
  }


  /**
   * Returns an iterator on the keys provided by this MBean attribute.
   * @param server
   *    the <code>MBeanServerConnection</code> to be used to access the MBean.
   * @return Iterator
   *    an iterator providing the keys returned by the key provider attribute.
   * @throws IOException
   * @throws MBeanException
   * @throws AttributeNotFoundException
   * @throws InstanceNotFoundException
   * @throws ReflectionException
   */
  public Iterator keyIterator(MBeanServerConnection server) throws IOException,
      MBeanException, AttributeNotFoundException, InstanceNotFoundException,
      ReflectionException
  {
    List keyList = getKeys(server, getObjectName());
    return keyList.iterator();
  }

  /**
   * Determine the object name for the MBean instance that provides sub key
   * elements through the subKeyProvider. The subKeyProvider's object name
   * will be
   * @param key Object
   * @return ObjectName
   * @throws MalformedObjectNameException
   */
  protected ObjectName getSubKeyProviderObjectName(Object key) throws
      MalformedObjectNameException {
    return new ObjectName(getObjectName()+",name="+key);
  }

  public static List<Integer> asList(final int[] a) {
    return new AbstractList<Integer>() {
      public Integer get(int i) { return a[i]; }
      // Throws NullPointerException if val == null
      public Integer set(int i, Integer val) {
        Integer oldVal = a[i];
        a[i] = val;
        return oldVal;
      }
      public int size() { return a.length; }
    };
  }

  public static List<Long> asList(final long[] a) {
    return new AbstractList<Long>() {
      public Long get(int i) { return a[i]; }
      // Throws NullPointerException if val == null
      public Long set(int i, Long val) {
        Long oldVal = a[i];
        a[i] = val;
        return oldVal;
      }
      public int size() { return a.length; }
    };
  }

  /**
   * Returns an iterator on the keys provided by this MBean attribute starting
   * from the supplied row key.
   *
   * @param server
   *    the <code>MBeanServerConnection</code> to be used to access the MBean.
   * @param firstRowId
   *    the lower bound (including) row key for the iterator.
   * @return Iterator
   *    an iterator providing the keys returned by the key provider attribute.
   * @throws IOException
   * @throws MBeanException
   * @throws AttributeNotFoundException
   * @throws InstanceNotFoundException
   * @throws ReflectionException
   */
  @SuppressWarnings("unchecked")
  public Iterator keyTailIterator(MBeanServerConnection server,
                                  Object firstRowId) throws IOException,
      MBeanException, AttributeNotFoundException, InstanceNotFoundException,
      ReflectionException
  {
    List keyList = getKeys(server, getObjectName());
    int pos = 0;
    if (firstRowId instanceof JMXArrayIndexKey) {
      pos = ((JMXArrayIndexKey)firstRowId).getIndex();
    }
    else {
      if (firstRowId instanceof Object[]) {
        Object[] keyPairList = keyList.toArray();
        pos = Arrays.binarySearch(keyPairList, firstRowId, new Comparator() {
          public int compare(Object o1, Object o2) {
            Object[] a1 = (Object[]) o1;
            Object[] a2 = (Object[]) o2;
            for (int i = 0; i < Math.min(a1.length, a2.length); i++) {
              int c = ((Comparable) a1[i]).compareTo(a2[i]);
              if (c != 0) {
                return c;
              }
            }
            return a1.length - a2.length;
          }

          public boolean equals(Object obj) {
            return false;
          }

        });
      }
      else {
        pos = Collections.binarySearch(keyList, firstRowId);
      }
    }
    if (Math.abs(pos) >= keyList.size()) {
      return Collections.emptyList().iterator();
    }
    return createTailIterator(keyList.listIterator(Math.abs(pos)), pos);
  }

  protected Iterator createTailIterator(Iterator it, int indexPos) {
    return it;
  }

  /**
   * Returns the number of row keys available.
   * @param server
   *    the <code>MBeanServerConnection</code> to be used to access the MBean.
   * @return
   *    the number of keys.
   * @throws IOException
   * @throws MBeanException
   * @throws AttributeNotFoundException
   * @throws InstanceNotFoundException
   * @throws ReflectionException
   */
  public int getKeyCount(MBeanServerConnection server) throws IOException,
      MBeanException, AttributeNotFoundException, InstanceNotFoundException,
      ReflectionException
  {
    List keyList = getKeys(server, getObjectName());
    return keyList != null ? keyList.size() : 0;
  }

  public Object getRowValues(MBeanServerConnection server,
                             Object indexObject) throws IOException,
      MBeanException, AttributeNotFoundException, InstanceNotFoundException,
      ReflectionException
  {
    return indexObject;
  }

  @SuppressWarnings("unchecked")
  protected List getKeys(MBeanServerConnection server,
                         ObjectName keyProviderInstance) throws IOException,
      MBeanException, AttributeNotFoundException, InstanceNotFoundException,
      ReflectionException {
    Object keys = null;
    List<Comparable> keyList = null;
    if (getAttribute() != null) {
      keys = getAttribute(server, keyProviderInstance, getAttribute());
    }
    else {
      keyList = new ArrayList<Comparable>();
      Set<ObjectInstance> mBeans = getMBeanNames(server);
      for (ObjectInstance mBean : mBeans) {
        Comparable key = (Comparable)getKey(server, mBean.getObjectName());
        keyList.add(key);
      }
    }
    if (keyList == null) {
      keyList = convertToKeyList(keys);
    }
    if (keysNeedSorting) {
      Collections.sort(keyList);
    }
    if (subKeyProvider != null) {
      List combinedKeyList = new ArrayList(keyList.size());
      for (Object key : keyList) {
        ObjectName oname;
        try {
          oname = getSubKeyProviderObjectName(key);
        } catch (MalformedObjectNameException ex) {
          throw new InstanceNotFoundException(ex.getMessage());
        }
        Object subKeys = subKeyProvider.getKeys(server, oname);
        List subKeyList = convertToKeyList(subKeys);
        for (Object subKey : subKeyList) {
          combinedKeyList.add(combineKeys(key, subKey));
        }
      }
      return combinedKeyList;
    }
    return keyList;
  }

  protected Object combineKeys(Object key, Object subKey) {
    return new Object[] { key, subKey };
  }

  protected Object getKey(MBeanServerConnection server, ObjectName row) throws
      IOException, ReflectionException, InstanceNotFoundException {
    Object[] key = new Object[keyAttributes.length];
    AttributeList keyObjects = server.getAttributes(row, keyAttributes);
    for (int i=0; i<keyObjects.size(); i++) {
      key[i] = ((Attribute)keyObjects.get(i)).getValue();
    }
    if (key.length == 1) {
      return key[0];
    }
    return key;
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> convertToKeyList(Object keys) throws ClassCastException {
    List<T> keyList = null;
    if (keys instanceof Collection) {
      keyList = new ArrayList<T>((Collection)keys);
    }
    else if (keys instanceof Object[]) {
      keyList = (List<T>) Arrays.asList((Object[])keys);
    }
    else if (keys instanceof long[]) {
      keyList = (List<T>) asList((long[])keys);
    }
    else if (keys instanceof int[]) {
      keyList = (List<T>) asList((int[])keys);
    }
    else {
      throw new ClassCastException(keys.getClass()+
                                   " is not a supported list");
    }
    return keyList;
  }
}
