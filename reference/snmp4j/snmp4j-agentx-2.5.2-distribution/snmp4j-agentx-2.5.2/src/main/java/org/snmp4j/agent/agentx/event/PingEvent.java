/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - PingEvent.java  
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

package org.snmp4j.agent.agentx.event;

import java.util.EventObject;

import org.snmp4j.agent.agentx.AgentXResponsePDU;
import org.snmp4j.agent.agentx.AgentXSession;

/**
 * The <code>PingEvent</code> object describes an AgentX ping result.
 * @author Frank Fock
 * @version 1.0
 */
public class PingEvent extends EventObject {

  private AgentXSession session;
  private AgentXResponsePDU response;
  private boolean closeSession;
  private boolean resetSession;
  private Exception error;

  public PingEvent(Object source,
                   AgentXSession pingedSession,
                   AgentXResponsePDU pingResponse){
    super(source);
    this.session = pingedSession;
    this.response = pingResponse;
  }

  public PingEvent(Object source,
                   AgentXSession pingedSession,
                   Exception error){
    super(source);
    this.session = pingedSession;
    this.error = error;
  }

  public AgentXResponsePDU getResponse() {
    return response;
  }

  public AgentXSession getSession() {
    return session;
  }

  public boolean isCloseSession() {
    return closeSession;
  }

  public boolean isResetSession() {
    return resetSession;
  }

  public Exception getError() {
    return error;
  }

  public void setCloseSession(boolean closeSession) {
    this.closeSession = closeSession;
  }

  public void setResetSession(boolean resetSession) {
    this.resetSession = resetSession;
  }

  public String toString() {
    return getClass().getName()+"[session="+session+",response="+response+
        ",error="+error+
        ",closeSession="+closeSession+",resetSession="+resetSession+"]";
  }
}
