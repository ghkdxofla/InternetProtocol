/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - TestSubagent.java  
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

package org.snmp4j.agent.agentx.subagent.test;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.snmp4j.agent.DefaultMOServer;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.agentx.*;
import org.snmp4j.agent.agentx.subagent.AgentXSubagent;
import org.snmp4j.agent.mo.DefaultMOFactory;
import org.snmp4j.agent.mo.MOFactory;
import org.snmp4j.agent.mo.snmp.SNMPv2MIB.SysUpTimeImpl;
import org.snmp4j.agent.mo.snmp4j.Snmp4jConfigMib;
import org.snmp4j.agent.mo.snmp4j.Snmp4jLogMib;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogLevel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.ConnectionOrientedTransportMapping;
import org.snmp4j.transport.TransportStateEvent;
import org.snmp4j.transport.TransportStateListener;
import org.snmp4j.util.ThreadPool;
import java.util.Iterator;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.agentx.subagent.RegistrationCallback;
import org.snmp4j.agent.mo.MOTable;

/**
 * The <code>TestSubagent</code> is an example implementation of a simple
 * AgentX subagent with shared tables and multi context registration.
 *
 * @author Frank Fock
 * @version 1.1
 */
public class TestSubagent implements Runnable, TransportStateListener,
    RegistrationCallback
{

  static {
    LogFactory.setLogFactory(new Log4jLogFactory());
    LogFactory.getLogFactory().getRootLogger().setLogLevel(LogLevel.ALL);
  }

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(TestSubagent.class);

  public static final OID SUBAGENT_ID = new OID();

  private AgentXSubagent subagent;
  private AgentX agentX;
  private AgentXMessageDispatcher dispatcher;
  private Address masterAddress;
  private Address localAddress;
  private AgentXSession session;
  private int sessionID = 0;

  private MOServer server;
  private AgentppTestMib agentppTestMib;
  private Snmp4jConfigMib snmp4jConfigMib;
  private Snmp4jLogMib snmp4jLogMib;

  private SysUpTimeImpl sessionContextUpTime = new SysUpTimeImpl();

  public TestSubagent(Address masterAddress, Address localAddress) {
    this.masterAddress = masterAddress;
    this.localAddress = localAddress;
    this.dispatcher = new AgentXMessageDispatcherImpl();
    this.agentX = new AgentX(dispatcher);
    server = new DefaultMOServer();
    server.addContext(new OctetString());
    this.subagent =
        new AgentXSubagent(agentX, SUBAGENT_ID,
                           new OctetString("AgentX4J Test agent"));
    this.subagent.setThreadPool(ThreadPool.create("AgentXSubAgent", 3));
    this.subagent.addMOServer(server);
  }

  public static void main(String[] args) {
    BasicConfigurator.configure();
    Address masterAddress = new TcpAddress(args[0]);
    TestSubagent testsubagent =
        new TestSubagent(masterAddress, new TcpAddress());
    testsubagent.run();
    try {
      Thread.sleep(100000000);
    }
    catch (InterruptedException iex) {
      iex.printStackTrace();
    }
  }


  protected void register() throws DuplicateRegistrationException {
    MOFactory factory = AgentppTestMib.getSharedTableFactory();
    DefaultMOFactory.addSNMPv2TCs(factory);
    agentppTestMib = new AgentppTestMib(factory);
    agentppTestMib.registerMOs(server, null);
  }

  protected void unregisterSessionDependent() {
    if (session != null) {
      OctetString sessionContext = getSessionContext(session.getSessionID());
      server.removeContext(sessionContext);
      if (snmp4jConfigMib != null) {
        snmp4jConfigMib.unregisterMOs(server, sessionContext);
      }
      if (snmp4jLogMib != null) {
        snmp4jLogMib.unregisterMOs(server, sessionContext);
      }
    }
  }

  protected void registerSessionDependent()
      throws DuplicateRegistrationException
  {
    OctetString sessionContext = getSessionContext(session.getSessionID());
    server.addContext(sessionContext);
    snmp4jConfigMib = new Snmp4jConfigMib(sessionContextUpTime);
    snmp4jConfigMib.registerMOs(server, sessionContext);
    snmp4jLogMib = new Snmp4jLogMib();
    snmp4jLogMib.registerMOs(server, sessionContext);
  }

  private static OctetString getSessionContext(int sessionID) {
    return new OctetString("session="+sessionID);
  }

  public void run() {
    try {
      Runtime.getRuntime().addShutdownHook(new AgentShutdown());
      register();
      unregisterSessionDependent();
      session = new AgentXSession(++sessionID);
      int status = subagent.connect(masterAddress, localAddress, session);
      if (status == AgentXProtocol.AGENTX_SUCCESS) {
        subagent.addAgentCaps(session, new OctetString(),
                              new OID("1.3.6.1.4.1.4976.10.1.1.100.4.1"),
                              new OctetString("AgentX-Test-Subagent"));
        registerSessionDependent();
        subagent.registerRegions(session, new OctetString(), null, this);
        TimeTicks upTime = new TimeTicks();
        /*
        subagent.registerRegions(session,
                                 getSessionContext(session.getSessionID()),
                                 upTime, this);
                                 */
        sessionContextUpTime.setValue(upTime);
        subagent.setPingDelay(30);
        subagent.notify(null, SnmpConstants.warmStart,
                        new VariableBinding[] {
                        new VariableBinding(SnmpConstants.sysDescr,
                                            new OctetString("SNMP4J-AgentX Test-Subagent")) });
        ((ConnectionOrientedTransportMapping)session.getPeer().getTransport()).
            addTransportStateListener(this);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Remove objects from the server, which could not be registered with
   * the master agent.
   *
   * @param failedRegistrations
   *    a List of ManagedObjects and MOTableRows which failed to register.
   */
  protected void unregisterFailed(List failedRegistrations) {
    for (Object failed : failedRegistrations) {
      if (failed instanceof ManagedObject) {
        server.unregister((ManagedObject) failed, null);
      }
      else if (failed instanceof MOTableRow) {
        MOTableRow r = (MOTableRow) failed;
      }
    }
  }

  public void connectionStateChanged(final TransportStateEvent change) {
    if (change.getNewState() ==
        TransportStateEvent.STATE_DISCONNECTED_REMOTELY) {
      // remove session dependent registrations
      session.setClosed(true);
      // try to reconnect
      Thread t = new Thread(new Runnable() {
        public void run() {
          Address addr = change.getPeerAddress();
          // try to reconnect ten times if we have been disconnected remotely
          for (int i=0; i<10; i++) {
            try {
              Thread.sleep(5000);
              if (subagent.connect(addr, localAddress, session) ==
                  AgentXProtocol.AGENTX_SUCCESS) {
                // if connected register our MIB objects
                try {
                  registerSessionDependent();
                }
                catch (DuplicateRegistrationException ex1) {
                  ex1.printStackTrace();
                }
                TimeTicks upTime = new TimeTicks();
                subagent.registerRegions(session, new OctetString(), upTime,
                                         TestSubagent.this);
                server.addContext(getSessionContext(session.getSessionID()));
                /*
                subagent.registerRegions(session,
                                         getSessionContext(session.getSessionID()),
                                         upTime, TestSubagent.this);
                                         */
                sessionContextUpTime.setValue(upTime);
                break;
              }
            }
            catch (IOException ex) {
              ex.printStackTrace();
            }
            catch (InterruptedException ex) {
              break;
            }
          }
        }
      });
      t.start();
    }
  }

  public void registrationEvent(OctetString context,
                                ManagedObject mo, int status) {
    if (status != AgentXProtocol.AGENTX_SUCCESS) {
      // optionally remove objects from the server,
      // which could not be registered with the master agent here, but
      // that would prevent their registration after a reconnect:
      //      server.unregister(mo, context);
    }
  }

  @SuppressWarnings("unchecked")
  public boolean tableRegistrationEvent(OctetString context,
                                        MOTable mo, MOTableRow row,
                                        boolean indexAllocation, int status,
                                        int retryCount) {
    if ((status != AgentXProtocol.AGENTX_SUCCESS) && (indexAllocation) &&
        ((context == null) || (context.length() == 0)) &&
        (retryCount < 2)) {
      if (AgentppTestMib.oidAgentppTestSparseEntry.equals(mo.getOID())) {
        OID failedIndex = row.getIndex();
        int n = failedIndex.get(1)-48;
        if (mo.removeRow(failedIndex) != null) {
          do {
//          failedIndex.set(1, failedIndex.get(1)+1);
            failedIndex.setValue(new OctetString("[" + session.getSessionID() +
                                                 "]" +
                                                 n++).toSubIndex(false).
                                 getValue());
          }
          while (mo.getModel().containsRow(failedIndex));
          mo.addRow(row);
          // retry index allocation
          return true;
        }
      }
    }
    return false;
  }

  public void unregistrationEvent(OctetString context,
                                  ManagedObject mo, int status) {
  }

  public void tableUnregistrationEvent(OctetString context,
                                       MOTable mo, MOTableRow row,
                                       boolean indexAllocation, int status) {
  }

  /**
   * The <code>AgentShutdown</code> is being executed when the agent is about
   * to be shut down (e.g. terminated by SIGTERM/Ctrl-C). Its purpose is to
   * send an AgentX Close PDU to the master agent to gracefully unregister
   * the current session.
   *
   * @author Frank Fock
   * @version 1.0
   */
  class AgentShutdown extends Thread {
    public void run() {
      try {
        subagent.close(session, AgentXProtocol.REASON_SHUTDOWN);
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
}
