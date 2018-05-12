/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXResponseEvent.java  
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

import java.util.EventObject;

import org.snmp4j.smi.Address;

public class AgentXResponseEvent extends EventObject {
  private Address peerAddress;
  private AgentXTarget target;
  private AgentXPDU request;
  private AgentXResponsePDU response;
  private Object userObject;
  private Exception error;

  /**
   * Creates an <code>AgentXResponseEvent</code> instance.
   * @param source
   *    the event source.
   * @param target
   *    the target where the request has been sent to.
   * @param peerAddress
   *    the transport address of the entity that send the response.
   * @param request
   *    the request AgentXPDU (must not be <code>null</code>).
   * @param response
   *    the AgentXResponsePDU or <code>null</code> if the request timed out.
   * @param userObject
   *    an optional user object.
   */
  public AgentXResponseEvent(Object source,
                             AgentXTarget target,
                             Address peerAddress,
                             AgentXPDU request,
                             AgentXResponsePDU response,
                             Object userObject) {
    super(source);
    setPeerAddress(peerAddress);
    this.target = target;
    setRequest(request);
    setResponse(response);
    setUserObject(userObject);
  }

  /**
   * Creates an <code>ResponseEvent</code> instance with an exception object
   * indicating a message processing error.
   * @param source
   *    the event source.
   * @param target
   *    the target where the request has been sent to.
   * @param peerAddress
   *    the transport address of the entity that send the response.
   * @param request
   *    the request PDU (must not be <code>null</code>).
   * @param response
   *    the response PDU or <code>null</code> if the request timed out.
   * @param userObject
   *    an optional user object.
   * @param error
   *    an <code>Exception</code>.
   */
  public AgentXResponseEvent(Object source,
                             AgentXTarget target,
                             Address peerAddress,
                             AgentXPDU request,
                             AgentXResponsePDU response,
                             Object userObject,
                             Exception error) {
    this(source, target, peerAddress, request, response, userObject);
    this.error = error;
  }

  /**
   * Gets the request PDU.
   * @return
   *    a <code>PDU</code>.
   */
  public AgentXPDU getRequest() {
    return request;
  }

  protected final void setPeerAddress(Address peerAddress) {
    this.peerAddress = peerAddress;
  }

  protected final void setRequest(AgentXPDU request) {
    this.request = request;
  }

  protected final void setResponse(AgentXResponsePDU response) {
    this.response = response;
  }

  /**
   * Gets the response PDU.
   * @return
   *    a PDU instance if a response has been received. If the request
   *    timed out then <code>null</code> will be returned.
   */
  public AgentXResponsePDU getResponse() {
    return response;
  }

  protected final void setUserObject(Object userObject) {
    this.userObject = userObject;
  }

  /**
   * Gets the user object that has been supplied to the asynchronous request
   * {@link AgentX#send(AgentXPDU pdu, AgentXTarget target,
   * TransportMapping transport)}.
   * @return
   *    an Object.
   */
  public Object getUserObject() {
    return userObject;
  }

  /**
   * Gets the exception object from the exception that has been generated
   * when the request processing has failed due to an error.
   * @return
   *    an <code>Exception</code> instance.
   */
  public Exception getError() {
    return error;
  }

  /**
   * Gets the transport address of the response sender.
   * @return
   *    the transport <code>Address</code> of the command responder that send
   *    this response, or <code>null</code> if no response has been received
   *    within the time-out interval or if an error occured (see
   *    {@link #getError()}).
   */
  public Address getPeerAddress() {
    return peerAddress;
  }

  public AgentXTarget getTarget() {
    return target;
  }

}
