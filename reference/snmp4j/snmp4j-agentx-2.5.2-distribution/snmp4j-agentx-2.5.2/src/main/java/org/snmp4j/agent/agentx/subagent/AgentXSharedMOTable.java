/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXSharedMOTable.java  
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

package org.snmp4j.agent.agentx.subagent;

import org.snmp4j.agent.mo.*;
// For JavaDoc:


/**
 * The <code>AgentXSharedMOTable</code> extends the <code>MOTable</code>
 * interface. It serves as a marker interface to be able to differentiate
 * between regular tables and AgentX shared index tables. The latter
 * require an index definition where all sub-indexes have an OID specified
 * ({@link MOTableSubIndex#getOid()}). AgentX shared index tables provide
 * services needed for sharing rows of the same conceptual table
 * across multiple AgentX sub-agents.
 *
 * @author Frank Fock
 * @version 1.0
 */
public interface AgentXSharedMOTable
    <R extends MOTableRow, C extends MOColumn, M extends MOTableModel<R>>
    extends MOTable<R,C,M> {

}
