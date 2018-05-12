/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - MBeanAttributeMOTableSupport.java  
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

import java.util.Iterator;
import javax.management.MBeanServerConnection;

import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.smi.OID;
import java.util.Set;
import javax.management.ObjectName;
import java.util.Collection;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.List;
import org.snmp4j.PDU;
import org.snmp4j.agent.mo.jmx.types.SMIVariant;
import org.snmp4j.agent.mo.jmx.types.*;
import javax.management.ObjectInstance;
import javax.management.Attribute;
// For JavaDoc:
import org.snmp4j.agent.mo.MOTable;

/**
 * The <code>MBeanAttributeMOTableSupport</code> maps SNMP conceptual tables
 * ({@link MOTable}) to MBean attributes and vice versa.
 *
 * @author Frank Fock
 * @version 2.1
 */
public class MBeanAttributeMOTableSupport extends AbstractMBeanSupport
    implements JMXTableSupport {

  public MBeanAttributeMOTableSupport(MBeanServerConnection server) {
    super(server);
  }

  /**
   * Adds a table to MBean attributes mapping.
   * @param tableOID
   *    the entry OID of the table (including the .1).
   * @param mBeanInfo
   *    a <code>MBeanAttributeMOTableInfo</code> instance describing the
   *    actual mapping.
   */
  public synchronized void add(OID tableOID,
                               MBeanAttributeMOTableInfo mBeanInfo) {
    oid2MBeanMap.put(tableOID, mBeanInfo);
  }

  /**
   * Adds a list of table to MBean attributes mappings.
   * @param tableDescriptions
   *    an two dimensional array of table descriptions. Each description
   *    contains two elements:
   * <ol>
   * <li>the <code>OID</code> of the table entry (thus including the .1),</li>
   * <li>a {@link MBeanAttributeMOTableInfo} instance.</li>
   * </ol>
   */
  public synchronized void addAll(Object[][] tableDescriptions) {
    for (Object[] tableDescr : tableDescriptions) {
      MBeanAttributeMOTableInfo mBeanInfo =
          (MBeanAttributeMOTableInfo)tableDescr[1];
      oid2MBeanMap.put((OID)tableDescr[0], mBeanInfo);
    }
  }

  public OID getLastIndex(OID tableOID) {
    MBeanAttributeMOTableInfo mBeanInfo =
        (MBeanAttributeMOTableInfo) getMBeanMOInfo(tableOID);
    if (mBeanInfo != null) {
      try {
        if (mBeanInfo.getKeyProvider() == null) {
          OID maxIndex = new OID();
          Set<ObjectInstance> mBeans = mBeanInfo.getMBeanNames(server);
          for (ObjectInstance mBean : mBeans) {
            Object key = mBeanInfo.getKey(server, mBean.getObjectName());
            OID index = mBeanInfo.getIndexSupport().mapToIndex(key);
            if (index.compareTo(maxIndex) > 0) {
              maxIndex = index;
            }
          }
          return maxIndex;
        }
        else {
          MBeanAttributeMOInfo keyProvider = mBeanInfo.getKeyProvider();
          Object keys = keyProvider.getAttribute(server);
          if (keys instanceof Object[]) {
            Object[] k = (Object[])keys;
            if (k.length == 0) {
              return null;
            }
            return mapToIndex(tableOID, k[k.length-1], k.length-1);
          }
          else if (keys instanceof List) {
            List k = (List)keys;
            if (k.isEmpty()) {
              return null;
            }
            int lastID = k.size()-1;
            return mapToIndex(tableOID, k.get(lastID), lastID);
          }
          else if (keys instanceof Collection) {
            int i=0;
            for (Iterator it = ((Collection)keys).iterator(); it.hasNext(); i++) {
              Object k = it.next();
              if (!it.hasNext()) {
                return mapToIndex(tableOID, k, i);
              }
            }
          }
          else {
            throw new ClassCastException(keys.getClass()+
                                         " is not a supported list");
          }
        }
      }
      catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
    return null;
  }

  public int getRow(OID tableOID, MOTableRow row) {
    MBeanAttributeMOTableInfo mBeanInfo =
        (MBeanAttributeMOTableInfo) getMBeanMOInfo(tableOID);
    if (mBeanInfo != null) {
      Object key =
          mBeanInfo.getIndexSupport().mapToRowIdentifier(row.getIndex());
      ObjectName oname = mBeanInfo.getIndexSupport().mapToRowMBean(key);
      if (oname == null) {
        try {
          TypedAttribute[] columns = mBeanInfo.getColumns();
          Object value =
              mBeanInfo.getKeyProvider().getRowValues(server, key);
          if (value instanceof Object[]) {
            for (int i=0; i<((Object[])value).length && (i<row.size()); i++) {
              TypedAttribute col = columns[i];
              Object v = ((Object[])value)[i];
              SMIVariant smiValue = new SMIVariant(row.getValue(i));
              v = col.transformFromNative(v, null);
              smiValue.setValue(v);
            }
          }
          else {
            for (int i=0; ((columns == null) || (i<columns.length)) &&
                 (i<row.size()); i++) {
              SMIVariant smiValue = new SMIVariant(row.getValue(i));
              Object v;
              if (columns == null) {
                v = value;
              }
              else {
                v = columns[i].transformFromNative(value, null);
              }
              smiValue.setValue(v);
            }
          }
          return PDU.noError;
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        return PDU.resourceUnavailable;
      }
      TypedAttribute[] columns = mBeanInfo.getColumns();
      for (int i=0; (i<columns.length) && (i<row.size()); i++) {
        TypedAttribute col = columns[i];
        SMIVariant colVariant = new SMIVariant(row.getValue(i));
        try {
          Object colValue = server.getAttribute(oname, col.getName());
          colValue = col.transformFromNative(colValue, oname);
          int status = colVariant.setValue(colValue);
          if (status != PDU.noError) {
            return status;
          }
        }
        catch (Exception ex) {
//          ex.printStackTrace();
        }
      }
      return PDU.noError;
    }
    return PDU.resourceUnavailable;
  }

  public int setRow(OID tableOID, MOTableRow row, int column) {
    MBeanAttributeMOTableInfo mBeanInfo =
        (MBeanAttributeMOTableInfo) getMBeanMOInfo(tableOID);
    if (mBeanInfo != null) {
      Object key =
          mBeanInfo.getIndexSupport().mapToRowIdentifier(row.getIndex());
      if ((mBeanInfo.getKeyProvider() == null) && (key == null)) {
        try {
          Set<ObjectInstance> mBeans = mBeanInfo.getMBeanNames(server);
          for (ObjectInstance mBean : mBeans) {
            Object k = mBeanInfo.getKey(server, mBean.getObjectName());
            OID i = mBeanInfo.getIndexSupport().mapToIndex(k);
            if (row.getIndex().equals(i)) {
              key = k;
              break;
            }
          }
        }
        catch (Exception ex1) {
          ex1.printStackTrace();
        }
      }
      if (key == null) {
        return PDU.noSuchName;
      }
      ObjectName oname = mBeanInfo.getIndexSupport().mapToRowMBean(key);
      TypedAttribute[] columns = mBeanInfo.getColumns();
      if (column<columns.length) {
        TypedAttribute col = columns[column];
        SMIVariant colVariant = new SMIVariant(row.getValue(column));
        Object colValue = colVariant.getValue(col.getType());
        try {
          colValue = col.transformToNative(colValue,
                                           (col.isNativeValueAlwaysNeeded() ?
                                            server.getAttribute(oname, col.getName()):
                                            null),
                                           oname);
          if (colValue != null) {
            server.setAttribute(oname, new Attribute(col.getName(), colValue));
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      return PDU.noError;
    }
    return PDU.resourceUnavailable;
  }

  @Override
  public boolean isEmpty(OID tableOID) {
    MBeanAttributeMOTableInfo mBeanInfo =
        (MBeanAttributeMOTableInfo) getMBeanMOInfo(tableOID);
    if (mBeanInfo != null) {
      try {
        if (mBeanInfo.getKeyProvider() == null) {
          Set<ObjectInstance> mBeans = mBeanInfo.getMBeanNames(server);
          return mBeans.isEmpty();
        }
        else {
          MBeanAttributeKeyProvider keyProvider = mBeanInfo.getKeyProvider();
          return keyProvider.getKeyCount(server) > 0;
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return true;
  }


  public int getRowCount(OID tableOID) {
    MBeanAttributeMOTableInfo mBeanInfo =
        (MBeanAttributeMOTableInfo) getMBeanMOInfo(tableOID);
    if (mBeanInfo != null) {
      try {
        if (mBeanInfo.getKeyProvider() == null) {
          Set<ObjectInstance> mBeans = mBeanInfo.getMBeanNames(server);
          return mBeans.size();
        }
        else {
          MBeanAttributeKeyProvider keyProvider = mBeanInfo.getKeyProvider();
          return keyProvider.getKeyCount(server);
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return 0;
  }

  public Iterator rowIdIterator(OID tableOID) {
    MBeanAttributeMOTableInfo mBeanInfo =
        (MBeanAttributeMOTableInfo) getMBeanMOInfo(tableOID);
    if (mBeanInfo != null) {
      try {
        if (mBeanInfo.getKeyProvider() == null) {
          Set<ObjectInstance> mBeans = mBeanInfo.getMBeanNames(server);
          SortedMap<OID,Object> indexes = new TreeMap<OID,Object>();
          for (ObjectInstance mBean : mBeans) {
            Object key = mBeanInfo.getKey(server, mBean.getObjectName());
            OID index = mBeanInfo.getIndexSupport().mapToIndex(key);
            indexes.put(index, key);
          }
          return indexes.values().iterator();
        }
        else {
          MBeanAttributeKeyProvider keyProvider = mBeanInfo.getKeyProvider();
          return keyProvider.keyIterator(server);
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return null;
  }

  public OID mapToIndex(OID tableOID, Object nativeRowId, int nativeIndex) {
    MBeanAttributeMOTableInfo mBeanInfo =
        (MBeanAttributeMOTableInfo) getMBeanMOInfo(tableOID);
    if (mBeanInfo != null) {
      Object rowId =
          mBeanInfo.getIndexSupport().getRowIdentifier(nativeRowId, nativeIndex);
      return mBeanInfo.getIndexSupport().mapToIndex(rowId);
    }
    return null;
  }

  public Object mapToRowId(OID tableOID, OID rowIndex) {
    MBeanAttributeMOTableInfo mBeanInfo =
        (MBeanAttributeMOTableInfo) getMBeanMOInfo(tableOID);
    if (mBeanInfo != null) {
      return mBeanInfo.getIndexSupport().mapToRowIdentifier(rowIndex);
    }
    return null;
  }

  public Iterator rowIdTailIterator(OID tableOID, Object firstRowId) {
    MBeanAttributeMOTableInfo mBeanInfo =
        (MBeanAttributeMOTableInfo) getMBeanMOInfo(tableOID);
    if (mBeanInfo != null) {
      try {
        if (mBeanInfo.getKeyProvider() == null) {
          Set<ObjectInstance> mBeans = mBeanInfo.getMBeanNames(server);
          TreeMap<OID,Object> indexes = new TreeMap<OID,Object>();
          OID firstIndex = null;
          for (ObjectInstance mBean : mBeans) {
            Object key = mBeanInfo.getKey(server, mBean.getObjectName());
            OID index = mBeanInfo.getIndexSupport().mapToIndex(key);
            if (key.equals(firstRowId)) {
              firstIndex = index;
            }
            indexes.put(index, key);
          }
          if (firstIndex == null) {
            return indexes.values().iterator();
          }
          return indexes.tailMap(firstIndex).values().iterator();
        }
        else {
          MBeanAttributeKeyProvider keyProvider = mBeanInfo.getKeyProvider();
          return keyProvider.keyTailIterator(server, firstRowId);
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return null;
  }
}
