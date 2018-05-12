/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXRegisterPDU.java  
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

public class AgentXRegisterPDU extends AgentXContextPDU {

  private byte timeout = 0;
  private byte priority;
  private byte rangeSubID;
  private OID subtree;
  private int upperBound;

  public AgentXRegisterPDU(byte flags,
                           int sessionID, int transactionID, int packetID) {
    super(AGENTX_REGISTER_PDU, flags, sessionID, transactionID, packetID);
  }

  public AgentXRegisterPDU(AgentXMessageHeader header) {
    super(header);
  }

  public AgentXRegisterPDU(OctetString context, OID subtree,
                           byte priority,
                           byte rangeSubID,
                           int upperBound) {
    this(AGENTX_REGISTER_PDU,
         context, subtree, priority, rangeSubID, upperBound);
  }

  protected AgentXRegisterPDU(byte type, OctetString context, OID subtree,
                              byte priority,
                              byte rangeSubID,
                              int upperBound) {
    super(type, context);
    this.priority = priority;
    this.subtree = subtree;
    this.rangeSubID = rangeSubID;
    this.upperBound = upperBound;
  }


  public void decodeAfterContext(ByteBuffer buf, int length) throws IOException {
    timeout = buf.get();
    priority = buf.get();
    rangeSubID = buf.get();
    buf.get(); // reserved
    subtree = new OID();
    AgentXProtocol.decodeOID(buf, subtree);
    if (rangeSubID != 0) {
      upperBound = buf.getInt();
      if ((rangeSubID < 0) || (rangeSubID > subtree.size())) {
        throw new IOException("Range sub-identifier "+rangeSubID+
                              " is out of range of "+subtree);
      }
    }
  }

  public OctetString getContext() {
    return context;
  }

  public byte getPriority() {
    return priority;
  }

  public byte getRangeSubID() {
    return rangeSubID;
  }

  public OID getSubtree() {
    return subtree;
  }

  public byte getTimeout() {
    return timeout;
  }

  public int getUpperBound() {
    return upperBound;
  }

  public void setContext(OctetString context) {
    this.context = context;
  }

  public void setPriority(byte priority) {
    this.priority = priority;
  }

  public void setRangeSubID(byte rangeSubID) {
    this.rangeSubID = rangeSubID;
  }

  public void setSubtree(OID subtree) {
    this.subtree = subtree;
  }

  public void setTimeout(byte timeout) {
    this.timeout = timeout;
  }

  public void setUpperBound(int upperBound) {
    this.upperBound = upperBound;
  }

  public int getAfterContextLength() {
    return  AgentXProtocol.AGENTX_INT_SIZE +
        AgentXProtocol.getOIDLength(subtree) +
        ((rangeSubID != 0) ? AgentXProtocol.AGENTX_INT_SIZE : 0);
  }

  public AgentXRegion getRegion() {
    OID lower = new OID(subtree);
    OID upper = new OID(subtree);
    AgentXRegion region = new AgentXRegion(lower, upper);
    if (rangeSubID > 0) {
      if (upper.get(rangeSubID-1) == upperBound) {
        region.setSingleOID(true);
        region.setUpperIncluded(true);
      }
      else {
        upper.set(rangeSubID - 1, upperBound);
        region.setRangeSubID(rangeSubID);
      }
    }
    else if (isFlagSet(AgentXProtocol.FLAG_INSTANCE_REGISTRATION)) {
      region.setSingleOID(true);
      region.setUpperIncluded(true);
    }
    else {
      region.setUpperBound(upper.nextPeer());
    }
    return region;
  }

  protected void encodeAfterContext(ByteBuffer buf) {
    buf.put(timeout);
    buf.put(priority);
    buf.put(rangeSubID);
    buf.put((byte)0); // reserved
    AgentXProtocol.encodeOID(buf, subtree, false);
    if (rangeSubID != 0) {
      buf.putInt(upperBound);
    }
  }

  protected String toStringExtMembers() {
    return super.toStringExtMembers()+",timeout="+timeout+",priority="+priority+
        ",rangeSubID="+rangeSubID+",subtree="+subtree+",upperBound="+upperBound
        +",single="+isFlagSet(AgentXProtocol.FLAG_INSTANCE_REGISTRATION);
  }
}
