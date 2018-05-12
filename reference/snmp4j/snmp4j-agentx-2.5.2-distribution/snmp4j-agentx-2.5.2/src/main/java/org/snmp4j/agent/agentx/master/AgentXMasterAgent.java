/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXMasterAgent.java  
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

import java.io.File;

import org.snmp4j.TransportMapping;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.agentx.AgentX;
import org.snmp4j.agent.agentx.AgentXMessageDispatcherImpl;
import org.snmp4j.agent.agentx.AgentXProtocol;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.MPv3;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.ConnectionOrientedTransportMapping;
import org.snmp4j.transport.TransportStateEvent;
import org.snmp4j.transport.TransportStateListener;
import java.net.InetAddress;
import org.snmp4j.agent.AgentConfigManager;
import org.snmp4j.agent.cfg.EngineBootsProvider;
import org.snmp4j.agent.MOServer;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.agent.io.MOPersistenceProvider;
import org.snmp4j.util.WorkerPool;
import org.snmp4j.agent.io.MOInputFactory;
import org.snmp4j.agent.security.VACM;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.agent.DefaultMOServer;
import org.snmp4j.util.ThreadPool;
import org.snmp4j.agent.io.DefaultMOPersistenceProvider;
import org.snmp4j.agent.cfg.EngineBootsCounterFile;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.agentx.version.VersionInfo;
import java.io.IOException;

/**
 * The <code>AgentXMasterAgent</code> is the base agent class for
 * AgentX master agents. It extends the {@link AgentConfigManager} class
 * provided by SNMP4J-Agent.
 * <p>
 * To implement a master agent, simply extend this class instead of
 * {@link AgentConfigManager} as you would do for a non-AgentX agent.
 *
 * @author Frank Fock
 * @version 1.1
 */
public class AgentXMasterAgent extends AgentConfigManager implements
    TransportStateListener {

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(AgentXMasterAgent.class);

  private static short maxGetBulkRepetitions = Short.MAX_VALUE;
  private AgentX agentX;
  private AgentXCommandProcessor commandProcessor;
  private AgentXQueue queue = new AgentXQueue();
  private AgentXMib agentXMIB;
  private boolean localhostSubagentsOnly;

  /**
   * Creates a SNMP agent configuration which can be run by calling
   * {@link #run()} later.
   *
   * @param agentsOwnEngineID
   *    the authoritative engine ID of the agent.
   * @param messageDispatcher
   *    the MessageDispatcher to use. The message dispatcher must be configured
   *    outside, i.e. transport mappings have to be added before this
   *    constructor is being called.
   * @param vacm
   *    a view access control model. Typically, this parameter is set to
   *    <code>null</code> to use the default VACM associated with the
   *    <code>VacmMIB</code>.
   * @param moServers
   *    the managed object server(s) that server the managed objects available
   *    to this agent.
   * @param workerPool
   *    the <code>WorkerPool</code> to be used to process incoming request.
   * @param configurationFactory
   *    a <code>MOInputFactory</code> that creates a <code>MOInput</code> stream
   *    with containing serialized ManagedObject information with the agent's
   *    configuration or <code>null</code> otherwise.
   * @param persistenceProvider
   *    the primary <code>MOPersistenceProvider</code> to be used to load
   *    and store persistent MOs.
   * @param engineBootsProvider
   *    the provider of engine boots counter.
   */
  public AgentXMasterAgent(OctetString agentsOwnEngineID,
                           MessageDispatcher messageDispatcher,
                           VACM vacm,
                           MOServer[] moServers,
                           WorkerPool workerPool,
                           MOInputFactory configurationFactory,
                           MOPersistenceProvider persistenceProvider,
                           EngineBootsProvider engineBootsProvider) {
    super(agentsOwnEngineID, messageDispatcher, vacm, moServers,
          workerPool, configurationFactory, persistenceProvider,
          engineBootsProvider);
    sysDescr.setValue("SNMP4J-AgentX "+
                      VersionInfo.getVersion()+" [" +
                      org.snmp4j.agent.version.VersionInfo.getVersion()+
                      ","+org.snmp4j.version.VersionInfo.getVersion()+
                      "]"+
                      " - "+System.getProperty("os.name","")+
                      " - "+System.getProperty("os.arch")+
                      " - "+System.getProperty("os.version"));
    agentX = new AgentX(new AgentXMessageDispatcherImpl());
  }

  private AgentXMasterAgent(File bootCounterFile,
                            File configFile,
                            MOServer[] servers) {
    this(new OctetString(MPv3.createLocalEngineID()),
         new MessageDispatcherImpl(),
         null,
         servers,
         ThreadPool.create("AgentXMasterAgent", 3),
         null,
         new DefaultMOPersistenceProvider(servers,
                                          configFile.getPath()),
         new EngineBootsCounterFile(bootCounterFile));
    agentX = new AgentX(new AgentXMessageDispatcherImpl());
  }

  /**
   * Creates a simple AgentX master agent using a boot counter file and
   * config file for persistent storage.
   *
   * @param bootCounterFile
   *    a file that stores the boot counter.
   * @param configFile
   *    a file that stores persistent MIB data.
   */
  public AgentXMasterAgent(File bootCounterFile,
                           File configFile) {
    this(bootCounterFile, configFile, new MOServer[] { new DefaultMOServer() });
  }

  /**
   * Creates the command processor.
   *
   * @param engineID
   *    the engine ID of the agent.
   * @return
   *    a new CommandProcessor instance.
   */
  protected CommandProcessor createCommandProcessor(OctetString engineID) {
    AgentXCommandProcessor cp =
        new AgentXCommandProcessor(engineID, queue, agentX, servers);
    agentX.addCommandResponder(cp);
    this.agentXMIB = new AgentXMib(cp);
    cp.setNotificationOriginator(getNotificationOriginator());
    cp.addAgentXMasterListener(this.agentXMIB);
    this.commandProcessor = cp;
    return cp;
  }

  public void addAgentXTransportMapping(TransportMapping transport) {
    agentX.getMessageDispatcher().addTransportMapping(transport);
    if (transport instanceof ConnectionOrientedTransportMapping) {
      ConnectionOrientedTransportMapping cotm =
          (ConnectionOrientedTransportMapping)transport;
      cotm.addTransportStateListener(this);
      cotm.setConnectionTimeout(0);
      cotm.setMessageLengthDecoder(new AgentXProtocol());
    }
  }

  public void removeAgentXTransportMapping(TransportMapping transport) {
    agentX.getMessageDispatcher().removeTransportMapping(transport);
    if (transport instanceof ConnectionOrientedTransportMapping) {
      ConnectionOrientedTransportMapping cotm =
          (ConnectionOrientedTransportMapping)transport;
      cotm.removeTransportStateListener(this);
    }
  }

  /**
   * Gets the upper limit for AgentX Get Bulk repetitions field send on behalf
   * of all master agents of this JVM.
   * @return
   *    the upper limit for the maximum repetitions field for AgentX Get Bulk
   *    requests.
   * @see #setMaxGetBulkRepetitions
   */
  public static short getMaxGetBulkRepetitions() {
    return maxGetBulkRepetitions;
  }

  public AgentXMib getAgentXMIB() {
    return agentXMIB;
  }

  public AgentXCommandProcessor getCommandProcessor() {
    return commandProcessor;
  }

  /**
   * Gets the local engine ID.
   * @return
   *    the engine ID of the master agent.
   */
  public OctetString getLocalEngineID() {
    return super.engineID;
  }

  /**
   * Indicates whether only subagents from the local host or from any host
   * are allowed to connect to this master agent (default is any host).
   * @return
   *    <code>true</code> if only connections from the local host are allowed
   *    and <code>false</code> if connections from any host are allowed.
   */
  public boolean isLocalhostSubagentsOnly() {
    return localhostSubagentsOnly;
  }

  /**
   * Sets the maximum repetitions value used by this master agent for its
   * AgentX Get Bulk requests to subagents. The default is the maximum short
   * value. The SNMP GETBULK request already defines a maximum repetitions
   * value that is always the upper limit also for AgentX Get Bulk requests on
   * its behalf.
   * <p>
   * The NET-SNMP AgentX sub-agent has a bug in its AgentX Get Bulk processing
   * that causes endless loops in the sub-agent when the max-repetitions value
   * is greater than one. Since this bug is in NET-SNMP since v4.2 and still
   * present in version 5.4, it is likely, that you will need to set this
   * value to one, if your master agent should ever communicate with a NET-SNMP
   * sub-agent.
   *
   * @param maxRepetitions
   *    the upper limit of the maximum repetitions for AgentX Get Bulk
   *    sub-requests.
   */
  public static void setMaxGetBulkRepetitions(short maxRepetitions) {
    if (maxRepetitions < 1) {
      throw new IllegalArgumentException(
          "Max repetitions needs an unsigned value");
    }
    maxGetBulkRepetitions = maxRepetitions;
  }

  /**
   * Sets the local engine ID. This method must not be called after
   * {@link #initialize()} has been called for the first time.
   * @param localEngineID
   *   the (authoritative) engine ID of the master agent.
   */
  public void setLocalEngineID(OctetString localEngineID) {
    super.engineID = localEngineID;
  }

  /**
   * Sets the local host only connection filter flag.
   * @param localhostSubagentsOnly
   *    <code>true</code> if only connections from the local host are allowed
   *    and <code>false</code> if connections from any host are allowed.
   */
  public void setLocalhostSubagentsOnly(boolean localhostSubagentsOnly) {
    this.localhostSubagentsOnly = localhostSubagentsOnly;
  }

  public void connectionStateChanged(TransportStateEvent change) {
    if (localhostSubagentsOnly &&
        (change.getNewState() == TransportStateEvent.STATE_CONNECTED) &&
        (change.getPeerAddress() instanceof IpAddress)) {
      IpAddress peerAddress = (IpAddress)change.getPeerAddress();
      if (!peerAddress.getInetAddress().isLoopbackAddress()) {
        LOGGER.warn("Connection attempt made from non loopback (i.e. local) address '"+
                    peerAddress+"' which will be ignored");
        change.setCancelled(true);
        return;
      }
    }
    ((AgentXCommandProcessor)agent).connectionStateChanged(change);
  }

  protected void registerMIBs(OctetString context) throws
      DuplicateRegistrationException
  {
    super.registerMIBs(context);
    try {
      agentXMIB.registerMOs(agent.getServer(context), context);
    }
    catch (DuplicateRegistrationException ex) {
      String txt = "Unable to register AgentX MIB";
      LOGGER.error(txt, ex);
      throw new DuplicateRegistrationException(txt);
    }
  }

  protected void unregisterMIBs(OctetString context) {
    agentXMIB.unregisterMOs(agent.getServer(context), null);
  }

  /**
   * Launch (or relaunch) AgentX transport mappings.
   * @throws IOException
   *    if the necessary ports could not be opened or bound.
   */
  protected void launchTransportMappings() throws IOException {
    super.launchTransportMappings();
    launchTransportMappings(agentX.getMessageDispatcher().getTransportMappings());
  }

  /**
   * Shutdown communication be stopping all AgentX transport mappings.
   */
  public void shutdown() {
    try {
      stopTransportMappings(agentX.getMessageDispatcher().getTransportMappings());
    }
    catch (IOException ex) {
      LOGGER.error("Failed to shutdown AgentX: "+ex.getMessage(), ex);
    }
    super.shutdown();
  }

}
