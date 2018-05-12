/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXOpenPDU.java  
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

public class AgentXOpenPDU extends AgentXPDU {

  protected byte timeout;
  protected OID subagentID;
  protected OctetString subagentDescr;

  AgentXOpenPDU(AgentXMessageHeader header) {
    super(header);
    if (header.getType() != AGENTX_OPEN_PDU) {
      throw new IllegalArgumentException("Incompatible PDU type");
    }
  }

  public AgentXOpenPDU(int sessionID, int transactionID, int packetID,
                       byte timeout,
                       OID subagentID, OctetString subagentDescr) {
    super(AGENTX_OPEN_PDU, (byte)0, sessionID, transactionID, packetID);
    this.timeout = timeout;
    this.subagentID = subagentID;
    this.subagentDescr = subagentDescr;
  }

  public void decodePayload(ByteBuffer buf, int length) throws IOException {
    timeout = buf.get();
    // reserved
    buf.get();
    buf.get();
    buf.get();

    subagentID = new OID();
    AgentXProtocol.decodeOID(buf, subagentID);
    subagentDescr = AgentXProtocol.decodeOctetString(buf);
  }

  public OctetString getSubagentDescr() {
    return subagentDescr;
  }

  public OID getSubagentID() {
    return subagentID;
  }

  public byte getTimeout() {
    return timeout;
  }

  public void setSubagentDescr(OctetString subagentDescr) {
    this.subagentDescr = subagentDescr;
  }

  public void setSubagentID(OID subagentID) {
    this.subagentID = subagentID;
  }

  public void setTimeout(byte timeout) {
    this.timeout = timeout;
  }

  public int getPayloadLength() {
    return AgentXProtocol.AGENTX_INT_SIZE +
        AgentXProtocol.getOIDLength(subagentID) +
        AgentXProtocol.getOctetStringLength(subagentDescr.length());
  }

  public void encodePayload(ByteBuffer buf) {
    buf.put(timeout);
    buf.put(new byte[] { 0,0,0 }); // reserved
    AgentXProtocol.encodeOID(buf, subagentID, false);
    AgentXProtocol.encodeOctetString(buf, subagentDescr);
  }

  protected void beforeEncode() {
  }
}
