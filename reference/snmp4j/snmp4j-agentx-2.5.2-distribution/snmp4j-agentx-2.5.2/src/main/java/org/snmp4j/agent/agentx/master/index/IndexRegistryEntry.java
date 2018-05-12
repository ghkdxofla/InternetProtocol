/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - IndexRegistryEntry.java  
  _## 
  _##  Copyright (C) 2005-2014  Frank Fock (SNMP4J.org)
  _##  
  _##  This program is free software; you can redistribute it and/or modify
  _##  it under the terms of the GNU General Public License version 2 as 
  _##  published by the Free Software Foundation.
  _##
  _##  This program is distributed in the hope that it will be useful,
  _##  but WITHOUT ANY WARRANTY; without even the implied warranty of
  _##  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  _##  GNU General Public License for more details.
  _##
  _##  You should have received a copy of the GNU General Public License
  _##  along with this program; if not, write to the Free Software
  _##  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
  _##  MA  02110-1301  USA
  _##  
  _##########################################################################*/


package org.snmp4j.agent.agentx.master.index;

import org.snmp4j.agent.agentx.AgentXProtocol;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public class IndexRegistryEntry implements Comparable {

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(IndexRegistryEntry.class);


  private OctetString context;
  private VariableBinding indexType;
  protected SortedMap<IndexEntry, IndexEntry> indexValues = new TreeMap<IndexEntry, IndexEntry>(new IndexComparator());
  protected SortedMap<IndexEntry, IndexEntry> usedValues = new TreeMap<IndexEntry, IndexEntry>(new IndexComparator());

  public IndexRegistryEntry(OctetString context, VariableBinding indexType) {
    this.context = (context == null) ? new OctetString() : context;
    this.indexType = indexType;
  }

  protected IndexEntry newIndexEntry(int sessionID, Variable indexValue) {
    return new IndexEntry(sessionID, indexValue);
  }

  protected void duplicateAllocation(IndexEntry entry) {

  }
  
  public synchronized int allocate(int sessionID,
                                   Variable indexValue, boolean testOnly) {
    IndexEntry newEntry = newIndexEntry(sessionID, indexValue);
    if (indexValue.getSyntax() != indexType.getSyntax()) {
      return AgentXProtocol.AGENTX_INDEX_WRONG_TYPE;
    }
    else {
      IndexEntry oldEntry = indexValues.get(newEntry);
      if (oldEntry != null) {
        if (!testOnly) {
          duplicateAllocation(oldEntry);
        }
        return AgentXProtocol.AGENTX_INDEX_ALREADY_ALLOCATED;
      }
      else {
        if (!testOnly) {
          indexValues.put(newEntry, newEntry);
        }
        return AgentXProtocol.AGENTX_SUCCESS;
      }
    }
  }

  public synchronized int release(int sessionID,
                                  Variable indexValue, boolean testOnly) {
    IndexEntry removeKey = newIndexEntry(sessionID, indexValue);
    IndexEntry contained = indexValues.get(removeKey);
    if ((contained != null) && (contained.getSessionID() == sessionID)) {
      if (!testOnly) {
        if (!removeEntry(contained)) {
          return AgentXProtocol.AGENTX_INDEX_ALREADY_ALLOCATED;
        }
      }
      return AgentXProtocol.AGENTX_SUCCESS;
    }
    return AgentXProtocol.AGENTX_INDEX_NOT_ALLOCATED;
  }

  protected boolean removeEntry(IndexEntry entryKey) {
    IndexEntry r = indexValues.remove(entryKey);
    addUsed(r);
    return true;
  }

  protected void addUsed(IndexEntry entry) {
    usedValues.put(entry, entry);
  }
  
  public synchronized void release(int sessionID) {
    for (Iterator<IndexEntry> it = indexValues.values().iterator(); it.hasNext();) {
      IndexEntry entry = it.next();
      if (entry.getSessionID() == sessionID) {
        it.remove();
        addUsed(entry);
      }
    }
  }

  public int compareTo(Object o) {
    IndexRegistryEntry other = (IndexRegistryEntry)o;
    int c = context.compareTo(other.context);
    if (c == 0) {
      c = indexType.getOid().compareTo(other.indexType.getOid());
    }
    return c;
  }

  public int hashCode() {
    return indexType.getOid().hashCode()+
        ((context == null)?0:context.hashCode());
  }

  public boolean equals(Object o) {
    if (o instanceof IndexRegistryEntry) {
      return indexType.getOid().equals(
          ((IndexRegistryEntry)o).indexType.getOid());
    }
    return false;
  }

  public VariableBinding getIndexType() {
    return indexType;
  }

  public OctetString getContext() {
    return context;
  }

  public synchronized Variable newIndex(int sessionID, boolean testOnly) {
    try {
      IndexEntry last = null;
      if (!usedValues.isEmpty()) {
        last = usedValues.lastKey();
      }
      if (!indexValues.isEmpty()) {
        IndexEntry lastAllocated = indexValues.lastKey();
        if (last == null) {
          last = lastAllocated;
        }
        else if (lastAllocated.compareTo(last) > 0) {
          last = lastAllocated;
        }
      }
      else if (last == null) {
        return anyIndex(sessionID, testOnly);
      }
      OID nextIndex = last.getIndexValue().toSubIndex(true);
      nextIndex = nextIndex.nextPeer();
      Variable nextIndexValue = (Variable)last.getIndexValue().clone();
      nextIndexValue.fromSubIndex(nextIndex, true);
      int status = allocate(sessionID, nextIndexValue, testOnly);
      if (status != AgentXProtocol.AGENTX_SUCCESS) {
        return null;
      }
      return nextIndexValue;
    }
    catch (Exception ex) {
      LOGGER.error("Exception occurred while creating/allocating new index:"+
                   ex.getMessage(), ex);
      return null;
    }
  }

  public synchronized Variable anyIndex(int sessionID, boolean testOnly) {
    try {
      OID nextIndex;
      if (usedValues.isEmpty()) {
        if (indexValues.isEmpty()) {
          nextIndex = indexType.getVariable().toSubIndex(true);
        }
        else {
          nextIndex = (indexValues.lastKey()).
              getIndexValue().toSubIndex(true);
        }
      }
      else {
        IndexEntry last = usedValues.firstKey();
        nextIndex = last.getIndexValue().toSubIndex(true);
      }
      nextIndex = nextIndex.nextPeer();
      Variable nextIndexValue = (Variable) indexType.getVariable().clone();
      nextIndexValue.fromSubIndex(nextIndex, true);
      int status = allocate(sessionID, nextIndexValue, testOnly);
      if (status != AgentXProtocol.AGENTX_SUCCESS) {
        return null;
      }
      return nextIndexValue;
    }
    catch (Exception ex) {
      LOGGER.error("Exception occurred while creating/allocating"+
                   " any new index:"+ex.getMessage(), ex);
      return null;
    }
  }

  private class IndexComparator implements Comparator<IndexEntry> {

    public int compare(IndexEntry o1, IndexEntry o2) {
      Variable c1,c2;
      c1 = o1.getIndexValue();
      c2 = o2.getIndexValue();
      return c1.compareTo(c2);
    }

  }
}
