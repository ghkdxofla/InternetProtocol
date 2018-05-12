/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXClosePDU.java  
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

public class AgentXClosePDU extends AgentXPDU {

  private byte reason;

  public AgentXClosePDU(AgentXMessageHeader header) {
    super(header);
    if (header.getType() != AGENTX_CLOSE_PDU) {
      throw new IllegalArgumentException("Incompatible PDU type");
    }
  }

  public AgentXClosePDU(byte reason) {
    super(AGENTX_CLOSE_PDU);
    this.reason = reason;
  }

  public void decodePayload(ByteBuffer buf, int length) throws IOException {
    reason = buf.get();
  }

  public byte getReason() {
    return reason;
  }

  public void setReason(byte reason) {
    this.reason = reason;
  }

  public int getPayloadLength() {
    return AgentXProtocol.AGENTX_INT_SIZE;
  }

  public void encodePayload(ByteBuffer buf) {
    buf.put(reason);
    buf.put(new byte[] { 0,0,0 }); // reserved
  }

  protected void beforeEncode() {
  }
}
