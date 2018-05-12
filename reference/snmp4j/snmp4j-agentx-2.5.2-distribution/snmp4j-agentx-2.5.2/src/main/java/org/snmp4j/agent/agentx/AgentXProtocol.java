/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXProtocol.java  
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

import java.net.*;
import java.nio.*;
import java.util.*;

import org.snmp4j.agent.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.MessageLengthDecoder;
import org.snmp4j.transport.MessageLength;
import java.io.IOException;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;

public class AgentXProtocol implements MessageLengthDecoder {

  private static final LogAdapter logger =
      LogFactory.getLogger(AgentXProtocol.class);

  public static final byte VERSION_1_0 = 1;

  public static final byte REASON_OTHER		= 1;
  public static final byte REASON_PARSE_ERROR     = 2;
  public static final byte REASON_PROTOCOL_ERROR  = 3;
  public static final byte REASON_TIMEOUTS   	= 4;
  public static final byte REASON_SHUTDOWN	= 5;
  public static final byte REASON_BY_MANAGER      = 6;

  public static final int AGENTX_OPEN_FAILED		= 256;
  public static final int AGENTX_NOT_OPEN		= 257;
  public static final int AGENTX_INDEX_WRONG_TYPE	= 258;
  public static final int AGENTX_INDEX_ALREADY_ALLOCATED= 259;
  public static final int AGENTX_INDEX_NONE_AVAILABLE	= 260;
  public static final int AGENTX_INDEX_NOT_ALLOCATED	= 261;
  public static final int AGENTX_UNSUPPORTED_CONTEXT	= 262;
  public static final int AGENTX_DUPLICATE_REGISTRATION	= 263;
  public static final int AGENTX_UNKNOWN_REGISTRATION	= 264;
  public static final int AGENTX_UNKNOWN_AGENTCAPS	= 265;
  public static final int AGENTX_PARSE_ERROR		= 266;
  public static final int AGENTX_REQUEST_DENIED		= 267;
  public static final int AGENTX_PROCESSING_ERROR	= 268;

  /** The maximum OID length for AgentX transported OIDs according to RFC2741 §5.1.*/
  public static final int AGENTX_MAX_OID_LENGTH = 128;

  /*  General errors  */

  public static final int AGENTX_SUCCESS		= 0;
  public static final int AGENTX_ERROR			= -1;
  public static final int AGENTX_DISCONNECT		= -5;
  public static final int AGENTX_BADVER			=-10;
  public static final int AGENTX_TIMEOUT		=-11;

  /*  User errors  */
  public static final int AGENTX_NOREG         =  -40;
  public static final int AGENTX_DUPMAP        =  -41;

  public static final byte FLAG_INSTANCE_REGISTRATION = 0x01;
  public static final byte FLAG_NEW_INDEX	       	= 0x02;
  public static final byte FLAG_ANY_INDEX	       	= 0x04;
  public static final byte FLAG_NON_DEFAULT_CONTEXT   = 0x08;
  public static final byte FLAG_NETWORK_BYTE_ORDER    = 0x10;

  private static final OID INTERNET = new OID(new int[]{ 1,3,6,1 });

  private static final int IPADDRESS_OCTETS = 4;

  protected static final int AGENTX_INT_SIZE = 4;

  public static final int HEADER_LENGTH = 5 * AGENTX_INT_SIZE;

  public static final int DEFAULT_TIMEOUT_SECONDS = 5;
  public static final int DEFAULT_MAX_CONSECUTIVE_TIMEOUTS = 3;
  public static final int DEFAULT_MAX_PARSE_ERRORS = -1;
  public static final int MAX_TIMEOUT_SECONDS = 255;


  private static boolean nonDefaultContextEnabled = true;


  public static void encodeOID(ByteBuffer buf,
                               OID oid,
                               boolean include) {
    if (oid == null) {
      buf.put(new byte[] { 0,0,0,0 });
    }
    else {
      int startPos = 0;
      int size =oid.size();
      if (size > AGENTX_MAX_OID_LENGTH || size < 0) {
        size = AGENTX_MAX_OID_LENGTH;
        logger.warn("Too long OID is trimmed to "+AGENTX_MAX_OID_LENGTH+" sub-identifiers allowed by AgentX: "+oid);
      }
      if ((size > INTERNET.size()) && (oid.startsWith(INTERNET))) {
        buf.put((byte) (size - (INTERNET.size() + 1)));
        buf.put((byte) oid.get(INTERNET.size()));
        startPos = INTERNET.size() + 1;
      }
      else {
        buf.put((byte) size);
        buf.put((byte) 0);
      }
      if ((include) && (size > 0)) {
        buf.put((byte) 1);
        buf.put((byte) 0);
      }
      else {
        buf.put(new byte[] {0, 0});
      }
      for (int i = startPos; i < size; i++) {
        buf.putInt(oid.get(i));
      }
    }
  }

  public static int getOIDLength(OID oid) {
    if (oid == null) {
      return AGENTX_INT_SIZE;
    }
    int startPos = 0;
    if ((oid.size() > INTERNET.size()) && (oid.startsWith(INTERNET))) {
      startPos = INTERNET.size()+1;
    }
    return AGENTX_INT_SIZE + (AGENTX_INT_SIZE * (oid.size() - startPos));
  }

  public static int getOIDLength(int[] oid) {
    if (oid == null) {
      return AGENTX_INT_SIZE;
    }
    int startPos = 0;
    if (oid.length > INTERNET.size()) {
      boolean ok = true;
      for (int i=0; ok && i<INTERNET.size(); i++) {
        ok = (oid[i] == INTERNET.get(i));
      }
      if (ok) {
        startPos = INTERNET.size() + 1;
      }
    }
    return AGENTX_INT_SIZE + (AGENTX_INT_SIZE * (Math.min(oid.length,AGENTX_MAX_OID_LENGTH) - startPos));
  }

  public static boolean decodeOID(ByteBuffer buf, OID oid) {
    int size = Math.min(0xFF & ((int)buf.get()), AGENTX_MAX_OID_LENGTH);
    int first = buf.get();
    int[] value = new int[size+((first != 0) ? INTERNET.size()+1 : 0)];
    int startPos = 0;
    if (first != 0) {
      System.arraycopy(INTERNET.getValue(), 0, value, 0, INTERNET.size());
      value[INTERNET.size()] = first;
      startPos = INTERNET.size()+1;
    }
    boolean include = (buf.get() != 0);
    buf.get(); // reserved
    for (int i=0; i<size; i++) {
      value[startPos+i] = buf.getInt();
    }
    oid.setValue(value);
    return include;
  }

  public static void encodeVariableData(ByteBuffer buf, Variable v) {
    if (v == null) {
      return;
    }
    switch (v.getSyntax()) {
      //case sNMP_SYNTAX_INT:
      case SMIConstants.SYNTAX_GAUGE32:
      case SMIConstants.SYNTAX_TIMETICKS:
      case SMIConstants.SYNTAX_COUNTER32: {
        buf.putInt((int)(((AssignableFromLong)v).toLong() & 0xFFFFFFFFL));
        break;
      }
      case SMIConstants.SYNTAX_INTEGER32: {
        buf.putInt(((AssignableFromInteger)v).toInt());
        break;
      }
      case SMIConstants.SYNTAX_COUNTER64: {
        buf.putLong(((AssignableFromLong)v).toLong());
        break;
      }
      case SMIConstants.SYNTAX_OCTET_STRING:
      case SMIConstants.SYNTAX_OPAQUE: {
        encodeOctetString(buf, (OctetString)v);
        break;
      }
      case SMIConstants.SYNTAX_IPADDRESS: {
        encodeOctetString(buf,
               new OctetString(((IpAddress)v).getInetAddress().getAddress()));
        //buf.put(((IpAddress)v).getInetAddress().getAddress());
        break;
      }
      case SMIConstants.SYNTAX_OBJECT_IDENTIFIER: {
        encodeOID(buf, (OID)v, false);
        break;
      }
      default:
        break;
    }
  }

  public static int getVariableDataLength(Variable v) {
    if (v == null) {
      return 0;
    }
    switch (v.getSyntax()) {
      //case sNMP_SYNTAX_INT:
      case SMIConstants.SYNTAX_GAUGE32:
      case SMIConstants.SYNTAX_TIMETICKS:
      case SMIConstants.SYNTAX_COUNTER32:
      case SMIConstants.SYNTAX_INTEGER32: {
        return AGENTX_INT_SIZE;
      }
      case SMIConstants.SYNTAX_COUNTER64: {
        return 2 * AGENTX_INT_SIZE;
      }
      case SMIConstants.SYNTAX_OCTET_STRING:
      case SMIConstants.SYNTAX_OPAQUE: {
        if (v instanceof OctetString) {
          return getOctetStringLength(((OctetString) v).length());
        }
        else if (v instanceof AssignableFromByteArray) {
          return getOctetStringLength(
              ((AssignableFromByteArray)v).toByteArray().length);
        }
      }
      case SMIConstants.SYNTAX_IPADDRESS: {
        return 2 * AGENTX_INT_SIZE;
      }
      case SMIConstants.SYNTAX_OBJECT_IDENTIFIER: {
        return getOIDLength(((AssignableFromIntArray)v).toIntArray());
      }
      default:
        break;
    }
    return 0;
  }

  public static Variable decodeVariableData(ByteBuffer buf, int syntax) {
    switch (syntax) {
      //case sNMP_SYNTAX_INT:
      case SMIConstants.SYNTAX_GAUGE32:
        return new Gauge32((buf.getInt() & 0xFFFFFFFFL));
      case SMIConstants.SYNTAX_TIMETICKS:
        return new TimeTicks((buf.getInt() & 0xFFFFFFFFL));
      case SMIConstants.SYNTAX_COUNTER32:
        return new Counter32((buf.getInt() & 0xFFFFFFFFL));
      case SMIConstants.SYNTAX_INTEGER32:
        return new Integer32(buf.getInt());
      case SMIConstants.SYNTAX_COUNTER64:
        return new Counter64(buf.getLong());
      case SMIConstants.SYNTAX_OCTET_STRING:
        return decodeOctetString(buf);
      case SMIConstants.SYNTAX_OPAQUE:
        return new Opaque(decodeOctetString(buf).getValue());
      case SMIConstants.SYNTAX_IPADDRESS: {
        byte[] addrBytes = decodeOctetString(buf).getValue();
        // Workaround for incorrectly implemented sub-agents like
        // NET-SNMP 5.4, that return addresses with more than 4 bytes
        if (addrBytes.length > IPADDRESS_OCTETS) {
          logger.warn("Subagent returned IpAddress with length "+
                      addrBytes.length+
                      " > "+IPADDRESS_OCTETS+
                      " which violates AgentX protocol specification");
          byte[] fourBytes = new byte[IPADDRESS_OCTETS];
          System.arraycopy(addrBytes, 0, fourBytes, 0, IPADDRESS_OCTETS);
          addrBytes = fourBytes;
        }
        InetAddress addr = null;
        try {
          addr = InetAddress.getByAddress(addrBytes);
        }
        catch (UnknownHostException ex) {
          logger.error("Failed to create IpAddress from address bytes "+
                       " with length "+addrBytes.length+
                       ", using default IpAddress instead", ex);
          return new IpAddress();
        }
        return new IpAddress(addr);
      }
      case SMIConstants.SYNTAX_OBJECT_IDENTIFIER: {
        OID oid = new OID();
        decodeOID(buf, oid);
        return oid;
      }
      case SMIConstants.EXCEPTION_END_OF_MIB_VIEW:
      case SMIConstants.EXCEPTION_NO_SUCH_INSTANCE:
      case SMIConstants.EXCEPTION_NO_SUCH_OBJECT: {
        return new Null(syntax);
      }
      case SMIConstants.SYNTAX_NULL: {
        return new Null();
      }
      default: {
        logger.error("Unknown AgentX variable syntax '"+syntax+
                     "', using Null instead");
        return new Null();
      }
    }
  }

  public static VariableBinding[] decodeVariableBindings(ByteBuffer buf) {
    ArrayList<VariableBinding> vbs = new ArrayList<VariableBinding>();
    while (buf.remaining() > 0) {
      int type = buf.getShort() & 0xFFFF;
      buf.getShort();
      OID oid = new OID();
      decodeOID(buf, oid);
      Variable v = decodeVariableData(buf, type);
      vbs.add(new VariableBinding(oid, v));
    }
    return vbs.toArray(new VariableBinding[vbs.size()]);
  }

  public static void encodeVaribleBindings(ByteBuffer buf, VariableBinding[] vbs) {
    for (VariableBinding vb : vbs) {
      buf.putShort((short) vb.getSyntax());
      buf.put(new byte[]{0, 0}); // reserved
      encodeOID(buf, vb.getOid(), false);
      encodeVariableData(buf, vb.getVariable());
    }
  }

  public static void encodeRanges(ByteBuffer buf, MOScope[] searchRanges) {
    for (MOScope searchRange : searchRanges) {
      encodeOID(buf, searchRange.getLowerBound(),
          searchRange.isLowerIncluded());
      if (searchRange.isUpperIncluded()) {
        encodeOID(buf, searchRange.getUpperBound().successor(), false);
      }
      else {
        encodeOID(buf, searchRange.getUpperBound(), false);
      }
    }
  }

  public static int getOctetStringLength(int length) {
    int padding = 0;
    if ((length % AGENTX_INT_SIZE) > 0) {
      padding = AGENTX_INT_SIZE - (length % AGENTX_INT_SIZE);
    }
    return AGENTX_INT_SIZE + length + padding;
  }

  public static void encodeOctetString(ByteBuffer buf, OctetString os) {
    buf.putInt(os.length());
    buf.put(os.getValue());
    if ((os.length() % AGENTX_INT_SIZE) > 0) {
      for (int i=0; i < AGENTX_INT_SIZE - (os.length() % AGENTX_INT_SIZE); i++) {
        buf.put((byte)0);
      }
    }
  }

  public static OctetString decodeOctetString(ByteBuffer buf) {
    int size = buf.getInt();
    byte[] value = new byte[size];
    buf.get(value);
    if ((size % AGENTX_INT_SIZE) > 0) {
      for (int i=0; i < AGENTX_INT_SIZE - (size % AGENTX_INT_SIZE); i++) {
        buf.get(); // skip 0 bytes
      }
    }
    return new OctetString(value);
  }

  public static MOScope[] decodeRanges(ByteBuffer buf) {
    return decodeRanges(buf, false);
  }

  public static MOScope[] decodeRanges(ByteBuffer buf,
                                       boolean lowerAlwaysIncluded) {
    ArrayList<MOScope> ranges = new ArrayList<MOScope>();
    while (buf.hasRemaining()) {
      OID lowerBound = new OID();
      boolean isLowerIncluded= lowerAlwaysIncluded | decodeOID(buf, lowerBound);
      OID upperBound = new OID();
      decodeOID(buf, upperBound);
      if (upperBound.size() == 0) {
        upperBound = null;
      }
      ranges.add(new DefaultMOScope(lowerBound, isLowerIncluded,
                                    upperBound, false));
    }
    return ranges.toArray(new MOScope[ranges.size()]);
  }

  public static int getRangesLength(MOScope[] ranges) {
    int length = 0;
    for (MOScope range : ranges) {
      length += AgentXProtocol.getOIDLength(range.getLowerBound());
      if (range.isUpperIncluded()) {
        length +=
            AgentXProtocol.getOIDLength(range.getUpperBound().successor());
      }
      else {
        length += AgentXProtocol.getOIDLength(range.getUpperBound());
      }
    }
    return length;
  }

  public static int getVariableBindingsLength(VariableBinding[] vbs) {
    int length = 0;
    for (VariableBinding vb : vbs) {
      length += AGENTX_INT_SIZE + getOIDLength(vb.getOid()) +
          getVariableDataLength(vb.getVariable());
    }
    return length;
  }

  public int getMinHeaderLength() {
    return HEADER_LENGTH;
  }

  public MessageLength getMessageLength(ByteBuffer buf) throws IOException {
    return decodeHeader(buf);
  }

  public static AgentXMessageHeader decodeHeader(ByteBuffer buf)
      throws IOException
  {
    byte version = buf.get();
    if (version != AgentXProtocol.VERSION_1_0) {
      throw new IOException("Unknown AgentX version: "+version);
    }
    byte type = buf.get();
    byte flags = buf.get();
    buf.get();
    ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
    if ((flags & AgentXProtocol.FLAG_NETWORK_BYTE_ORDER) != 0) {
      byteOrder = ByteOrder.BIG_ENDIAN;
    }
    buf.order(byteOrder);
    int sessionID = buf.getInt();
    int transactionID = buf.getInt();
    int packetID = buf.getInt();
    int length = buf.getInt();
    return new AgentXMessageHeader(type, flags, sessionID, transactionID,
                                   packetID, length);
  }

  public static void setNonDefaultContextsEnabled(boolean enabled) {
    nonDefaultContextEnabled = enabled;
  }

  public static boolean isNonDefaultContextsEnabled() {
    return nonDefaultContextEnabled;
  }

  public static final byte DEFAULT_PRIORITY = 127;
  public static final int FLAG_ALLOCATE_INDEX = 0;
}
