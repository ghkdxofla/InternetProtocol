/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXContextPDU.java  
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

import org.snmp4j.smi.OctetString;

public abstract class AgentXContextPDU extends AgentXPDU {

  protected OctetString context;

  protected AgentXContextPDU(byte type, OctetString context) {
    super(type);
    this.context = context;
  }

  protected AgentXContextPDU(AgentXMessageHeader header) {
    super(header);
  }

  protected AgentXContextPDU(byte type, byte flags, int sessionID,
                             int transactionID, int packetID) {
    super(type, flags, sessionID, transactionID, packetID);
  }

  protected abstract void decodeAfterContext(ByteBuffer buf, int length)
      throws IOException;

  protected abstract void encodeAfterContext(ByteBuffer buf);

  protected abstract int getAfterContextLength();

  public final void decodePayload(ByteBuffer buf, int length)
      throws IOException
  {
    if (isFlagSet(AgentXProtocol.FLAG_NON_DEFAULT_CONTEXT)) {
      context = AgentXProtocol.decodeOctetString(buf);
    }
    else {
      context = new OctetString();
    }
    decodeAfterContext(buf, length);
  }

  public final void encodePayload(ByteBuffer buf) {
    if (isFlagSet(AgentXProtocol.FLAG_NON_DEFAULT_CONTEXT)) {
      AgentXProtocol.encodeOctetString(buf, context);
    }
    encodeAfterContext(buf);
  }

  public final int getPayloadLength() {
    int length = 0;
    if ((context != null) && (context.length() > 0) &&
        (AgentXProtocol.isNonDefaultContextsEnabled())) {
      length = AgentXProtocol.getOctetStringLength(context.length());
    }
    length += getAfterContextLength();
    return length;
  }

  public OctetString getContext() {
    return context;
  }

  public void setContext(OctetString context) {
    this.context = context;
  }

  protected String toStringExtMembers() {
    return super.toStringExtMembers()+",context="+context;
  }

  /**
   * Initialize flags and other things before a PDU is encoded.
   */
  protected void beforeEncode() {
    if ((context != null) && (context.length() > 0) &&
        (AgentXProtocol.isNonDefaultContextsEnabled())) {
      addFlag(AgentXProtocol.FLAG_NON_DEFAULT_CONTEXT);
    }
  }

}
