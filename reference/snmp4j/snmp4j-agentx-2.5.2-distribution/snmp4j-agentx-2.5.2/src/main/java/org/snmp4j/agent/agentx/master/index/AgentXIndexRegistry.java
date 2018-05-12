/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXIndexRegistry.java  
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

import java.util.*;

import org.snmp4j.agent.agentx.AgentXProtocol;
import org.snmp4j.smi.*;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;


public class AgentXIndexRegistry {

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(AgentXIndexRegistry.class);

  private final Map<IndexRegistryEntry, IndexRegistryEntry> indexes =
      Collections.synchronizedMap(new HashMap<IndexRegistryEntry, IndexRegistryEntry>());

  public AgentXIndexRegistry() {
  }
  
  protected IndexRegistryEntry newIndexRegistryEntry(OctetString context, VariableBinding vb) {
    return new IndexRegistryEntry(context, vb);
  }

  public int allocate(int sessionID,
                      OctetString context, VariableBinding vb,
                      boolean testOnly) {
    try {
      vb.getVariable().toSubIndex(true);
    }
    catch (UnsupportedOperationException ex) {
      return AgentXProtocol.AGENTX_INDEX_WRONG_TYPE;
    }
    IndexRegistryEntry newEntry = newIndexRegistryEntry(context, vb);
    IndexRegistryEntry oldEntry = indexes.get(newEntry);
    if (oldEntry == null) {
      if (!testOnly) {
        int status = newEntry.allocate(sessionID, vb.getVariable(), testOnly);
        if (status == AgentXProtocol.AGENTX_SUCCESS) {
          indexes.put(newEntry, newEntry);
        }
        return status;
      }
      return AgentXProtocol.AGENTX_SUCCESS;
    }
    else {
      return oldEntry.allocate(sessionID, vb.getVariable(), testOnly);
    }
  }

  public int release(int sessionID,
                     OctetString context, VariableBinding vb,
                     boolean testOnly) {
    IndexRegistryEntry newEntry = newIndexRegistryEntry(context, vb);
    IndexRegistryEntry entry = indexes.get(newEntry);
    if (entry == null) {
      return AgentXProtocol.AGENTX_INDEX_NOT_ALLOCATED;
    }
    else {
      if (entry.getIndexType().getSyntax() != vb.getSyntax()) {
        return AgentXProtocol.AGENTX_INDEX_NOT_ALLOCATED;
      }
      return entry.release(sessionID, vb.getVariable(), testOnly);
    }
  }

  public void release(int sessionID) {
    synchronized (indexes) {
      for (IndexRegistryEntry entry : indexes.values()) {
        entry.release(sessionID);
      }
    }
  }

  public int newIndex(int sessionID,
                      OctetString context, VariableBinding vb,
                      boolean testOnly) {
    IndexRegistryEntry newEntry = newIndexRegistryEntry(context, vb);
    IndexRegistryEntry entry = indexes.get(newEntry);
    if (entry == null) {
      entry = newIndexRegistryEntry(context, vb);
    }
    Variable v = entry.newIndex(sessionID, testOnly);
    if (v == null) {
      return AgentXProtocol.AGENTX_INDEX_NONE_AVAILABLE;
    }
    else if (!testOnly) {
      vb.setVariable(v);
    }
    return AgentXProtocol.AGENTX_SUCCESS;
  }

  public int anyIndex(int sessionID,
                      OctetString context, VariableBinding vb,
                      boolean testOnly) {
    IndexRegistryEntry newEntry = newIndexRegistryEntry(context, vb);
    IndexRegistryEntry entry = indexes.get(newEntry);
    boolean newEntryCreated = false;
    if (entry == null) {
      entry = newIndexRegistryEntry(context, vb);
      newEntryCreated = true;
    }
    Variable v = entry.anyIndex(sessionID, testOnly);
    if (v == null) {
      return AgentXProtocol.AGENTX_INDEX_NONE_AVAILABLE;
    }
    else if (!testOnly) {
      vb.setVariable(v);
      if (newEntryCreated) {
        indexes.put(entry, entry);
      }
    }
    return AgentXProtocol.AGENTX_SUCCESS;
  }

}
