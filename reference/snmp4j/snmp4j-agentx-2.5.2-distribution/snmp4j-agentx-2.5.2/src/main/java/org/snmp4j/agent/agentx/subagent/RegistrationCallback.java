/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - RegistrationCallback.java  
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

package org.snmp4j.agent.agentx.subagent;

import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.MOTable;
import org.snmp4j.smi.OctetString;

/**
 * The <code>RegistrationCallback</code> informs about the state of a
 * AgentX registration or index allocation request.
 *
 * @author Frank Fock
 * @version 1.1
 */
public interface RegistrationCallback {

  /**
   * The registration attempt of a <code>ManagedObject</code> succeeded or
   * failed.
   * @param context
   *    the registration context.
   * @param mo
   *    the <code>ManagedObject</code> that was subject to the registration
   *    process.
   * @param status
   *    the AgentX status of the registration response.
   */
  public void registrationEvent(OctetString context,
                                ManagedObject mo, int status);

  /**
   * The registration attempt of a <code>ManagedObject</code> succeeded or
   * failed.
   * @param context
   *    the registration context.
   * @param mo
   *    the <code>MOTable</code> that was subject to the registration
   *    process.
   * @param row
   *    the row whose index or region was subject to the
   *    allocation/registration.
   * @param indexAllocation
   *    <code>true</code> if this event relates to an index allocation
   *    request and <code>false</code> if it relates to a row region
   *    registration.
   * @param status
   *    the AgentX status of the registration response.
   * @param retryCount
   *    the number of retries already processed. This value can be used to
   *    avoid endless loops.
   * @return
   *    <code>true</code> if the caller should retry the registration operation,
   *    <code>false</code> otherwise.
   */
  public boolean tableRegistrationEvent(OctetString context,
                                        MOTable mo, MOTableRow row,
                                        boolean indexAllocation, int status,
                                        int retryCount);

  /**
   * The unregistration attempt of a <code>ManagedObject</code> succeeded or
   * failed.
   * @param context
   *    the registration context.
   * @param mo
   *    the <code>ManagedObject</code> that was subject to the registration
   *    process.
   * @param status
   *    the AgentX status of the registration response.
   */
  public void unregistrationEvent(OctetString context,
                                  ManagedObject mo, int status);

  /**
   * The unregistration attempt of a <code>ManagedObject</code> succeeded or
   * failed.
   * @param context
   *    the registration context.
   * @param mo
   *    the <code>MOTable</code> that was subject to the registration
   *    process.
   * @param row
   *    the row whose index or region was subject to the
   *    allocation/registration.
   * @param indexAllocation
   *    <code>true</code> if this event relates to an index allocation
   *    request and <code>false</code> if it relates to a row region
   *    registration.
   * @param status
   *    the AgentX status of the registration response.
   */
  public void tableUnregistrationEvent(OctetString context,
                                       MOTable mo, MOTableRow row,
                                       boolean indexAllocation, int status);


}
