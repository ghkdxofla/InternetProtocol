/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXMasterEvent.java  
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

package org.snmp4j.agent.agentx.master;

import java.util.EventObject;
import org.snmp4j.agent.agentx.AgentXProtocol;

/**
 * The <code>AgentXMasterEvent</code> object describes an event that has been
 * triggered on behalf of a sub-agent to master agent connection/session.
 * <p>
 * When the type of the event is vetoable (i.e. the event can be used to
 * cancel an action), its type has to be an integer value less than zero.
 * If the type is greater than zero, the event is fired for information only
 * and changing the veto reason has no effect.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class AgentXMasterEvent extends EventObject {

  public static final int SESSION_TO_ADD = -3;
  public static final int REGISTRATION_TO_ADD = -5;

  public static final int PEER_ADDED = 1;
  public static final int PEER_REMOVED = 2;
  public static final int SESSION_ADDED = 3;
  public static final int SESSION_REMOVED = 4;
  public static final int REGISTRATION_ADDED = 5;
  public static final int REGISTRATION_REMOVED = 6;

  private int type;
  private Object changedObject;
  private int vetoReason = AgentXProtocol.AGENTX_SUCCESS;

  /**
   * Creates an new master agent event.
   * @param source
   *    the command processor that fired the event.
   * @param type
   *    the event type (less than zero if vetoable, greater than zero if not).
   * @param changedObject
   *    an optional reference to the changed object, which might be an
   *    AgentXPeer, AgentXSession, or AgentXRegistration instance for example.
   */
  public AgentXMasterEvent(Object source, int type, Object changedObject) {
    super(source);
    this.type = type;
    this.changedObject = changedObject;
  }

  /**
   * Returns the event type.
   * @return
   *    if less than zero, this event can be canceled by setting an appropriate
   *    veto reason. If greater than zero, this event is for information only.
   */
  public int getType() {
    return type;
  }

  /**
   * Returns the changed object (or the object to be changed).
   * @return
   *    an AgentXPeer, AgentXSession, or AgentXRegistration instance for
   *    example.
   */
  public Object getChangedObject() {
    return changedObject;
  }

  /**
   * Returns the veto reason. A value other than zero
   * (={@link AgentXProtocol#AGENTX_SUCCESS}) indicates that a vetoable event
   * should be canceled.
   * @return
   *    an AgentX REASON code as defined by {@link AgentXProtocol}.
   */
  public int getVetoReason() {
    return vetoReason;
  }

  public String toString() {
    return getClass().getName()+
        "[type="+type+",changedObject="+changedObject+
        ",vetoReason="+vetoReason+"]";
  }

  /**
   * Sets the AgentX reason (see {@link AgentXProtocol}) other than
   * {@link AgentXProtocol#AGENTX_SUCCESS} why the action caused this event
   * should be rejected and undone.
   *
   * @param vetoReason
   *    an AgentX reason code.
   */
  public void setVetoReason(int vetoReason) {
    this.vetoReason = vetoReason;
  }
}
