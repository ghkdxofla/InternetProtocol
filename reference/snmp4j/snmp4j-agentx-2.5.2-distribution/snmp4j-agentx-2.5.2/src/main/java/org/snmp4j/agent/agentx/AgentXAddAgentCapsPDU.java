/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXAddAgentCapsPDU.java  
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

public class AgentXAddAgentCapsPDU extends AgentXContextPDU {

  private OID id;
  private OctetString descr;

  public AgentXAddAgentCapsPDU(AgentXMessageHeader header) {
    super(header);
    if (header.getType() != AGENTX_ADDAGENTCAPS_PDU) {
      throw new IllegalArgumentException();
    }
  }

  public AgentXAddAgentCapsPDU(OctetString context, OID id, OctetString descr) {
    super(AGENTX_ADDAGENTCAPS_PDU, context);
    this.id = id;
    this.descr = descr;
  }

  public void decodeAfterContext(ByteBuffer buf, int length) throws IOException {
    id = new OID();
    AgentXProtocol.decodeOID(buf, id);
    descr = AgentXProtocol.decodeOctetString(buf);
  }

  public void encodeAfterContext(ByteBuffer buf) {
    AgentXProtocol.encodeOID(buf, id, false);
    AgentXProtocol.encodeOctetString(buf, descr);
  }

  public int getAfterContextLength() {
    return AgentXProtocol.getOIDLength(id) +
        AgentXProtocol.getOctetStringLength(descr.length());
  }

  public OctetString getDescr() {
    return descr;
  }

  public OID getId() {
    return id;
  }

  public void setDescr(OctetString descr) {
    this.descr = descr;
  }

  public void setId(OID id) {
    this.id = id;
  }
}
