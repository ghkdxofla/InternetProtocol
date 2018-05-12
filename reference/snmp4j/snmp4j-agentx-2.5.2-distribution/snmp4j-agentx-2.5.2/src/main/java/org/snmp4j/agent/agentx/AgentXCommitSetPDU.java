/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXCommitSetPDU.java  
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

import java.nio.ByteBuffer;
import java.io.IOException;

public class AgentXCommitSetPDU extends AgentXPDU {

  public AgentXCommitSetPDU() {
    super(AGENTX_COMMITSET_PDU);
  }

  public AgentXCommitSetPDU(AgentXMessageHeader header) {
    super(header);
    if (header.getType() != AGENTX_COMMITSET_PDU) {
      throw new IllegalArgumentException();
    }
  }

  protected void encodePayload(ByteBuffer buf) {
  }

  public int getPayloadLength() {
    return 0;
  }

  public void decodePayload(ByteBuffer buf, int length) throws IOException {
  }

  protected void beforeEncode() {
  }

}
