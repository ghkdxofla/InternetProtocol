/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXGetBulkPDU.java  
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
import org.snmp4j.agent.MOScope;
import org.snmp4j.smi.OctetString;

public class AgentXGetBulkPDU extends AgentXRequestPDU {

  protected short maxRepetitions;
  protected short nonRepeaters;

  public AgentXGetBulkPDU(OctetString context,
                          short maxRepetitions,
                          short nonRepeaters,
                          MOScope[] ranges) {
    super(AGENTX_GETBULK_PDU, context, ranges);
    this.maxRepetitions = maxRepetitions;
    this.nonRepeaters = nonRepeaters;
  }

  public AgentXGetBulkPDU(AgentXMessageHeader header) {
    super(header);
    if (header.getType() != AGENTX_GETBULK_PDU) {
      throw new IllegalArgumentException();
    }
  }

  public short getMaxRepetitions() {
    return maxRepetitions;
  }

  public short getNonRepeaters() {
    return nonRepeaters;
  }

  public void setMaxRepetitions(short maxRepetitions) {
    this.maxRepetitions = maxRepetitions;
  }

  public void setNonRepeaters(short nonRepeaters) {
    this.nonRepeaters = nonRepeaters;
  }

  protected void decodeAfterContext(ByteBuffer buf, int length) throws IOException {
    nonRepeaters = buf.getShort();
    maxRepetitions = buf.getShort();
    super.decodeAfterContext(buf, length);
  }

  protected void encodeAfterContext(ByteBuffer buf) {
    buf.putShort(nonRepeaters);
    buf.putShort(maxRepetitions);
    super.encodeAfterContext(buf);
  }

  protected int getAfterContextLength() {
    return AgentXProtocol.AGENTX_INT_SIZE + super.getAfterContextLength();
  }

  protected String toStringExtMembers() {
    return super.toStringExtMembers()+",nonRepeaters="+nonRepeaters+
        ",maxRepetitions="+maxRepetitions;
  }

}
