/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXPDU.java  
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
import java.nio.ByteOrder;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.smi.OctetString;

public abstract class AgentXPDU {

  private static final LogAdapter logger =
      LogFactory.getLogger(AgentXPDU.class);

  public static final byte RESERVED = (byte)0;

  public static final byte AGENTX_OPEN_PDU		= 1;
  public static final byte AGENTX_CLOSE_PDU		= 2;
  public static final byte AGENTX_REGISTER_PDU		= 3;
  public static final byte AGENTX_UNREGISTER_PDU	= 4;
  public static final byte AGENTX_GET_PDU		= 5;
  public static final byte AGENTX_GETNEXT_PDU		= 6;
  public static final byte AGENTX_GETBULK_PDU		= 7;
  public static final byte AGENTX_TESTSET_PDU		= 8;
  public static final byte AGENTX_COMMITSET_PDU		= 9;
  public static final byte AGENTX_UNDOSET_PDU		=10;
  public static final byte AGENTX_CLEANUPSET_PDU	=11;
  public static final byte AGENTX_NOTIFY_PDU		=12;
  public static final byte AGENTX_PING_PDU		=13;
  public static final byte AGENTX_INDEXALLOCATE_PDU	=14;
  public static final byte AGENTX_INDEXDEALLOCATE_PDU	=15;
  public static final byte AGENTX_ADDAGENTCAPS_PDU	=16;
  public static final byte AGENTX_REMOVEAGENTCAPS_PDU   =17;
  public static final byte AGENTX_RESPONSE_PDU     	=18;

  protected byte type;
  protected byte version = AgentXProtocol.VERSION_1_0;
  protected int sessionID;
  protected int transactionID;
  protected int packetID;
  protected byte flags;
  protected ByteOrder byteOrder;

  protected AgentXPDU(byte type) {
    this.type = type;
  }

  protected AgentXPDU(byte type, byte flags,
                      int sessionID, int transactionID, int packetID) {
    this.type = type;
    this.flags = flags;
    this.sessionID = sessionID;
    this.transactionID = transactionID;
    this.packetID = packetID;
    byteOrder = isFlagSet(AgentXProtocol.FLAG_NETWORK_BYTE_ORDER) ?
        ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
  }

  protected AgentXPDU(AgentXMessageHeader header) {
    this(header.getType(), header.getFlags(), header.getSessionID(),
         header.getTransactionID(), header.getPacketID());
  }

  public final void addFlag(byte flag) {
    this.flags |= flag;
  }

  public final boolean isFlagSet(int flag) {
    return ((this.flags & flag) != 0);
  }

  public final byte getFlags() {
    return flags;
  }

  public final int getPacketID() {
    return packetID;
  }

  public final int getSessionID() {
    return sessionID;
  }

  public final byte getType() {
    return type;
  }

  public final byte getVersion() {
    return version;
  }

  public final ByteOrder getByteOrder() {
    return byteOrder;
  }

  public final int getTransactionID() {
    return transactionID;
  }

  public void setFlags(byte flags) {
    this.flags = flags;
  }

  public void setPacketID(int packetID) {
    this.packetID = packetID;
  }

  public void setSessionID(int sessionID) {
    this.sessionID = sessionID;
  }

  public void setType(byte type) {
    this.type = type;
  }

  public void setVersion(byte version) {
    this.version = version;
  }

  public void setByteOrder(ByteOrder byteOrder) {
    this.byteOrder = byteOrder;
  }

  public void setTransactionID(int transactionID) {
    this.transactionID = transactionID;
  }

  public void setSessionAttributes(AgentXSession session) {
    setSessionID(session.getSessionID());
    setByteOrder(session.getByteOrder());
  }

  protected abstract void encodePayload(ByteBuffer buf);

  public abstract int getPayloadLength();

  public final void encode(ByteBuffer buf) {
    beforeEncode();
    buf.put(version);
    buf.put(type);
    if (byteOrder == null) {
      byteOrder = ByteOrder.nativeOrder();
    }
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      flags |= AgentXProtocol.FLAG_NETWORK_BYTE_ORDER;
    }
    else {
      flags &= ~AgentXProtocol.FLAG_NETWORK_BYTE_ORDER;
    }
    buf.order(byteOrder);
    buf.put(flags);
    buf.put(RESERVED);
    buf.putInt(sessionID);
    buf.putInt(transactionID);
    buf.putInt(packetID);
    buf.putInt(getPayloadLength());
    encodePayload(buf);
  }

  /**
   * Initialize flags and other things before a PDU is encoded.
   */
  protected abstract void beforeEncode();

  public abstract void decodePayload(ByteBuffer buf, int length)
      throws IOException;

  public static AgentXPDU decode(ByteBuffer buf) throws IOException {
    AgentXMessageHeader header = AgentXProtocol.decodeHeader(buf);
    if (buf.remaining() < header.getPayloadLength()) {
      throw new IOException("Short AgentX PDU with payload length="+
                            header.getPayloadLength()+"<"+
                            buf.remaining()+" remaining length");
    }
    try {
      AgentXPDU pdu = createAgentXPDU(header);
      pdu.decodePayload(buf, header.getPayloadLength());
      return pdu;
    }
    catch (IOException iox) {
      logger.warn("IO Exception while parsing AgentX PDU with header "+header+
                  ", exception is: "+iox.getMessage());
      throw new AgentXParseException(header, iox);
    }
  }

  private static AgentXPDU createAgentXPDU(AgentXMessageHeader header) {
    AgentXPDU pdu = null;
    switch (header.getType()) {
      case AGENTX_OPEN_PDU: {
        pdu = new AgentXOpenPDU(header);
        break;
      }
      case AGENTX_CLOSE_PDU: {
        pdu = new AgentXClosePDU(header);
        break;
      }
      case AGENTX_RESPONSE_PDU: {
        pdu = new AgentXResponsePDU(header);
        break;
      }
      case AGENTX_ADDAGENTCAPS_PDU: {
        pdu = new AgentXAddAgentCapsPDU(header);
        break;
      }
      case AGENTX_CLEANUPSET_PDU: {
        pdu = new AgentXCleanupSetPDU(header);
        break;
      }
      case AGENTX_COMMITSET_PDU: {
        pdu = new AgentXCommitSetPDU(header);
        break;
      }
      case AGENTX_GET_PDU: {
        pdu = new AgentXGetPDU(header);
        break;
      }
      case AGENTX_GETBULK_PDU: {
        pdu = new AgentXGetBulkPDU(header);
        break;
      }
      case AGENTX_GETNEXT_PDU: {
        pdu = new AgentXGetNextPDU(header);
        break;
      }
      case AGENTX_INDEXALLOCATE_PDU: {
        pdu = new AgentXIndexAllocatePDU(header);
        break;
      }
      case AGENTX_INDEXDEALLOCATE_PDU: {
        pdu = new AgentXIndexDeallocatePDU(header);
        break;
      }
      case AGENTX_NOTIFY_PDU: {
        pdu = new AgentXNotifyPDU(header);
        break;
      }
      case AGENTX_PING_PDU: {
        pdu = new AgentXPingPDU(header);
        break;
      }
      case AGENTX_REGISTER_PDU: {
        pdu = new AgentXRegisterPDU(header);
        break;
      }
      case AGENTX_REMOVEAGENTCAPS_PDU: {
        pdu = new AgentXRemoveAgentCapsPDU(header);
        break;
      }
      case AGENTX_TESTSET_PDU: {
        pdu = new AgentXTestSetPDU(header);
        break;
      }
      case AGENTX_UNDOSET_PDU: {
        pdu = new AgentXUndoSetPDU(header);
        break;
      }
      case AGENTX_UNREGISTER_PDU: {
        pdu = new AgentXUnregisterPDU(header);
        break;
      }
      default:
        break;
    }
    return pdu;
  }

  public final boolean isConfirmedPDU() {
    if (getType() == AGENTX_RESPONSE_PDU) {
        return false;
    }
    return true;
  }

  protected String toStringExtMembers() {
    return "";
  }

  public String toString() {
    return getClass().getName()+"[type="+type+",version="+version+
        ",sessionID="+sessionID+",transactionID="+transactionID+
        ",packetID="+packetID+",byteOrder="+byteOrder+toStringExtMembers()+"]";
  }

  public static void main(String[] args) {
    OctetString s = OctetString.fromHexString(args[0]);
    ByteBuffer buf = ByteBuffer.wrap(s.toByteArray());
    try {
      AgentXPDU pdu = AgentXPDU.decode(buf);
      System.out.println(pdu.toString());
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
