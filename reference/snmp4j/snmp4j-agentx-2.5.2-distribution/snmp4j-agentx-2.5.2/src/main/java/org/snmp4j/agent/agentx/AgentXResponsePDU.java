/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXResponsePDU.java  
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
import java.util.Arrays;
import java.util.List;

import org.snmp4j.smi.VariableBinding;
import java.util.ArrayList;

public class AgentXResponsePDU extends AgentXPDU {

  private int sysUpTime;
  private short errorStatus;
  private short errorIndex;

  private List<VariableBinding> variableBindings = new ArrayList<VariableBinding>();

  public AgentXResponsePDU(int sysUpTime, short errorStatus, short errorIndex) {
    super(AGENTX_RESPONSE_PDU);
    this.sysUpTime = sysUpTime;
    this.errorIndex = errorIndex;
    this.errorStatus = errorStatus;
  }

  public AgentXResponsePDU(AgentXMessageHeader header) {
    super(header);
  }

  public void decodePayload(ByteBuffer buf, int length) throws IOException {
    sysUpTime = buf.getInt();
    errorStatus = buf.getShort();
    errorIndex = buf.getShort();
    variableBindings = Arrays.asList(AgentXProtocol.decodeVariableBindings(buf));
  }

  protected void encodePayload(ByteBuffer buf) {
    buf.putInt(sysUpTime);
    buf.putShort(errorStatus);
    buf.putShort(errorIndex);
    AgentXProtocol.encodeVaribleBindings(buf, getVariableBindings());
  }

  public int getPayloadLength() {
    return 2 * AgentXProtocol.AGENTX_INT_SIZE +
        AgentXProtocol.getVariableBindingsLength(getVariableBindings());
  }

  public int getSysUpTime() {
    return sysUpTime;
  }

  public VariableBinding[] getVariableBindings() {
    return variableBindings.toArray(new VariableBinding[variableBindings.size()]);
  }

  public int size() {
    return variableBindings.size();
  }

  public short getErrorIndex() {
    return errorIndex;
  }

  public short getErrorStatus() {
    return errorStatus;
  }

  public void setVariableBindings(VariableBinding[] variableBindings) {
    this.variableBindings = Arrays.asList(variableBindings);
  }

  public void setSysUpTime(int sysUpTime) {
    this.sysUpTime = sysUpTime;
  }

  public void setErrorStatus(short errorStatus) {
    this.errorStatus = errorStatus;
  }

  public void setErrorIndex(short errorIndex) {
    this.errorIndex = errorIndex;
  }

  public void setErrorStatus(int errorStatus) {
    this.errorStatus = (short)errorStatus;
  }

  public void setErrorIndex(int errorIndex) {
    this.errorIndex = (short)errorIndex;
  }

  public void add(VariableBinding vb) {
    variableBindings.add(vb);
  }

  public void clear() {
    variableBindings.clear();
  }

  protected void beforeEncode() {
  }

  protected String toStringExtMembers() {
    return super.toStringExtMembers()+",sysUpTime="+sysUpTime+",errorStatus="+
        errorStatus+",errorIndex="+errorIndex+",vbs="+variableBindings;
  }
}
