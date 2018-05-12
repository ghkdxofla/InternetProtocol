/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXMessageDispatcher.java  
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

import java.util.Collection;
import org.snmp4j.TransportMapping;
import org.snmp4j.transport.TransportListener;
import org.snmp4j.smi.Address;
import java.io.IOException;
import org.snmp4j.mp.PduHandle;
import org.snmp4j.mp.PduHandleCallback;

public interface AgentXMessageDispatcher extends TransportListener {

  /**
   * Adds a {@link TransportMapping} to the dispatcher. The transport mapping
   * is used to send and receive messages to/from the network.
   * @param transport
   *   a <code>TransportMapping</code> instance.
   */
  void addTransportMapping(TransportMapping transport);

  /**
   * Removes a previously added {@link TransportMapping} from
   * the dispatcher.
   * @param transport
   *    a <code>TransportMapping</code> instance.
   * @return
   *    the <code>TransportMapping</code> instance supplied if it
   * could be successfully removed, <code>null</code> otherwise.
   */
  TransportMapping removeTransportMapping(TransportMapping transport);

  /**
   * Gets the <code>Collection</code> of transport mappings in this message
   * dispatcher.
   * @return Collection
   */
  Collection<TransportMapping> getTransportMappings();

  void addCommandListener(AgentXCommandListener listener);

  void removeCommandListener(AgentXCommandListener listener);

  /**
   * Sends a AgentX PDU to the specified address using the specified transport
   * mapping.
   *
   * @param transport
   *    a TransportMapping supported by the AgentX protocol.
   * @param address
   *    the target Address.
   * @param message
   *    the AgentXPDU to send.
   * @param pduHandleCallback
   *    an optional callback reference. If not <code>null</code> then the
   *    callback will be informed about the assigned PduHandle just before the
   *    request is sent out.
   * @return
   *    the PduHandle associated with the PDU.
   * @throws IOException
   *    if the IO operation fails.
   */
  PduHandle send(TransportMapping transport,
                 Address address, AgentXPDU message,
                 PduHandleCallback<AgentXPDU> pduHandleCallback) throws IOException;

}
