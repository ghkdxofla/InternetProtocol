/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXRemoveAgentCapsPDU.java  
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

package org.snmp4j.agent.agentx;

import java.io.*;
import java.nio.*;

import org.snmp4j.smi.*;

public class AgentXRemoveAgentCapsPDU extends AgentXContextPDU {

  private OID id;

  public AgentXRemoveAgentCapsPDU(OctetString context, OID id) {
    super(AGENTX_REMOVEAGENTCAPS_PDU, context);
    this.id = id;
  }

  public AgentXRemoveAgentCapsPDU(AgentXMessageHeader header) {
    super(header);
    if (header.getType() != AGENTX_REMOVEAGENTCAPS_PDU) {
      throw new IllegalArgumentException();
    }
  }


  protected void decodeAfterContext(ByteBuffer buf, int length) throws
      IOException {
    id = new OID();
    AgentXProtocol.decodeOID(buf, id);
  }

  protected void encodeAfterContext(ByteBuffer buf) {
    AgentXProtocol.encodeOID(buf, id, false);
  }

  protected int getAfterContextLength() {
    return AgentXProtocol.getOIDLength(id);
  }

  public OID getId() {
    return id;
  }

  public void setId(OID id) {
    this.id = id;
  }
}
