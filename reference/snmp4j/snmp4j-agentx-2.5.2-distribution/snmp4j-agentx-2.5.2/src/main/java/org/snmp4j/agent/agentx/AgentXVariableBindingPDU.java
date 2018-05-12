/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXVariableBindingPDU.java  
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

import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AgentXVariableBindingPDU extends AgentXContextPDU {

  protected VariableBinding[] variableBindings;

  protected AgentXVariableBindingPDU(byte type, OctetString context,
                                     VariableBinding[] vbs) {
    super(type, context);
    this.variableBindings = vbs;
  }

  protected AgentXVariableBindingPDU(AgentXMessageHeader header) {
    super(header);
  }

  protected AgentXVariableBindingPDU(byte type, byte flags, int sessionID,
                                     int transactionID, int packetID) {
    super(type, flags, sessionID, transactionID, packetID);
  }

  public void decodeAfterContext(ByteBuffer buf, int length) throws IOException {
    variableBindings = AgentXProtocol.decodeVariableBindings(buf);
  }

  public void encodeAfterContext(ByteBuffer buf) {
    AgentXProtocol.encodeVaribleBindings(buf, variableBindings);
  }

  public int getAfterContextLength() {
    return AgentXProtocol.getVariableBindingsLength(variableBindings);
  }

  public VariableBinding[] getVariableBindings() {
    return variableBindings;
  }

  public void setVariableBindings(VariableBinding[] variableBindings) {
    this.variableBindings = variableBindings;
  }

  public int size() {
    return variableBindings.length;
  }

  public String toStringExtMembers() {
    return super.toStringExtMembers()+",variableBindings="+
        ((variableBindings == null) ? null : Arrays.asList(variableBindings));
  }
}
