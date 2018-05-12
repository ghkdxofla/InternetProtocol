/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXRequestPDU.java  
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

import java.io.IOException;
import java.nio.ByteBuffer;

import org.snmp4j.agent.MOScope;
import org.snmp4j.smi.OctetString;
import java.util.Arrays;

public abstract class AgentXRequestPDU extends AgentXContextPDU {

  protected MOScope[] ranges;

  protected AgentXRequestPDU(byte type, OctetString context) {
    super(type, context);
  }

  protected AgentXRequestPDU(byte type, OctetString context, MOScope[] ranges) {
    super(type, context);
    this.ranges = ranges;
  }

  protected AgentXRequestPDU(AgentXMessageHeader header) {
    super(header);
  }

  protected void decodeAfterContext(ByteBuffer buf, int length)
      throws IOException
  {
    ranges = AgentXProtocol.decodeRanges(buf);
  }

  protected void encodeAfterContext(ByteBuffer buf) {
    AgentXProtocol.encodeRanges(buf, ranges);
  }

  protected int getAfterContextLength() {
    return AgentXProtocol.getRangesLength(ranges);
  }

  public int size() {
    if (ranges != null) {
      return ranges.length;
    }
    return 0;
  }

  public MOScope[] getRanges() {
    return ranges;
  }

  protected String toStringExtMembers() {
    return super.toStringExtMembers()+",ranges="+Arrays.asList(ranges);
  }

}
