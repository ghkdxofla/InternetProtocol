/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - TestMasterAgent.java  
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

package org.snmp4j.agent.agentx.master.test;

import java.io.File;
import org.apache.log4j.BasicConfigurator;
import java.io.IOException;
import org.snmp4j.agent.io.ImportModes;
import org.snmp4j.agent.agentx.master.AgentXMasterAgent;
import org.snmp4j.log.*;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.UsmUser;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.smi.OID;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.USM;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.mo.snmp.TransportDomains;
import org.snmp4j.TransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.smi.Integer32;

import java.util.Map;
import org.snmp4j.agent.io.DefaultMOPersistenceProvider;
import org.snmp4j.mp.MPv3;
import org.snmp4j.agent.cfg.EngineBootsCounterFile;
import org.snmp4j.agent.MOServer;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.snmp4j.agent.DefaultMOServer;
import java.util.List;
import org.snmp4j.MessageDispatcherImpl;
import java.util.Iterator;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.transport.TransportMappings;
import org.snmp4j.util.ArgumentParser;

public class TestMasterAgent {

  // initialize Java logging
  static {
    LogFactory.setLogFactory(new Log4jLogFactory());
    LogFactory.getLogFactory().getRootLogger().setLogLevel(LogLevel.ALL);
    BasicConfigurator.configure();
  }

  public static LogAdapter logger = LogFactory.getLogger(TestMasterAgent.class);

  protected String address;
  protected TransportMapping masterTransport;

  protected AgentXMasterAgent agent;
  protected MOServer server;
  private String configFile;
  private File bootCounterFile;

  @SuppressWarnings("unchecked")
  public TestMasterAgent(Map args) throws
      IOException {
    configFile = (String)((List)args.get("c")).get(0);
    bootCounterFile = new File((String)((List)args.get("bc")).get(0));

    server = new DefaultMOServer();
    MOServer[] moServers = new MOServer[] { server };
    /* Optional configuration by a config file:
    InputStream configInputStream =
        SampleAgent.class.getResourceAsStream("SampleAgentConfig.properties");
    final Properties props = new Properties();
    try {
      props.load(configInputStream);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    MOInputFactory configurationFactory = new MOInputFactory() {
      public MOInput createMOInput() {
        return new PropertyMOInput(props, TestMasterAgent.this);
      }
    };
    */
    MessageDispatcher messageDispatcher = new MessageDispatcherImpl();
    addListenAddresses(messageDispatcher, (List<String>)args.get("address"));
    agent =
        new AgentXMasterAgent(new OctetString(MPv3.createLocalEngineID()),
                              messageDispatcher,
                              null,
                              moServers,
                              ThreadPool.create("SampleAgent", 3),
                              null, // by config file: configurationFactory,
                              new DefaultMOPersistenceProvider(moServers,
        configFile),
                              new EngineBootsCounterFile(bootCounterFile)) {
      public void configure() {
        super.configure();
        // Here configuration is added programmatically (alternatively
        // configuration can be done by properties as commented out above).
        addCommunities(communityMIB);
        addUsmUser(usm);
        addNotificationTargets(targetMIB, notificationMIB);
        addViews(vacmMIB);
      }
    };
  }

  protected void addListenAddresses(MessageDispatcher md, List<String> addresses) {
    for (String addr : addresses) {
      Address address = GenericAddress.parse(addr);
      TransportMapping<? extends Address> tm =
          TransportMappings.getInstance().createTransportMapping(address);
      if (tm != null) {
        try {
          tm.listen();
          md.addTransportMapping(tm);
        } catch (IOException e) {
          logger.error("IO exception while listening on address '" +
              address + "', transport mapping disabled.");
        }
      }
      else {
        logger.warn("No transport mapping available for address '" +
            address + "'.");
      }
    }
  }

  protected void addNotificationTargets(SnmpTargetMIB targetMIB,
                                        SnmpNotificationMIB notificationMIB) {
    targetMIB.addDefaultTDomains();

    targetMIB.addTargetAddress(new OctetString("notification"),
                               TransportDomains.transportDomainUdpIpv4,
                               new OctetString(new UdpAddress("127.0.0.1/162").getValue()),
                               200, 1,
                               new OctetString("notify"),
                               new OctetString("v2c"),
                               StorageType.permanent);
    targetMIB.addTargetParams(new OctetString("v2c"),
                              MessageProcessingModel.MPv2c,
                              SecurityModel.SECURITY_MODEL_SNMPv2c,
                              new OctetString("public"),
                              SecurityLevel.NOAUTH_NOPRIV,
                              StorageType.permanent);
    notificationMIB.addNotifyEntry(new OctetString("default"),
                                   new OctetString("notify"),
                                   SnmpNotificationMIB.SnmpNotifyTypeEnum.trap,
                                   StorageType.permanent);
  }

  protected void addViews(VacmMIB vacm) {
    vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv1,
                  new OctetString("public"),
                  new OctetString("v1v2group"),
                  StorageType.nonVolatile);
    vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c,
                  new OctetString("public"),
                  new OctetString("v1v2group"),
                  StorageType.nonVolatile);
    vacm.addGroup(SecurityModel.SECURITY_MODEL_USM,
                  new OctetString("SHADES"),
                  new OctetString("v3group"),
                  StorageType.nonVolatile);
    vacm.addGroup(SecurityModel.SECURITY_MODEL_USM,
                  new OctetString("TEST"),
                  new OctetString("v3test"),
                  StorageType.nonVolatile);
    vacm.addGroup(SecurityModel.SECURITY_MODEL_USM,
                  new OctetString("SHA"),
                  new OctetString("v3restricted"),
                  StorageType.nonVolatile);

    vacm.addAccess(new OctetString("v1v2group"), new OctetString(),
                   SecurityModel.SECURITY_MODEL_ANY,
                   SecurityLevel.NOAUTH_NOPRIV, VacmMIB.vacmExactMatch,
                   new OctetString("fullReadView"),
                   new OctetString("fullWriteView"),
                   new OctetString("fullNotifyView"),
                   StorageType.nonVolatile);
    vacm.addAccess(new OctetString("v3group"), new OctetString(),
                   SecurityModel.SECURITY_MODEL_USM,
                   SecurityLevel.AUTH_PRIV, VacmMIB.vacmExactMatch,
                   new OctetString("fullReadView"),
                   new OctetString("fullWriteView"),
                   new OctetString("fullNotifyView"),
                   StorageType.nonVolatile);
    vacm.addAccess(new OctetString("v3restricted"), new OctetString(),
                   SecurityModel.SECURITY_MODEL_USM,
                   SecurityLevel.AUTH_NOPRIV, VacmMIB.vacmExactMatch,
                   new OctetString("restrictedReadView"),
                   new OctetString("restrictedWriteView"),
                   new OctetString("restrictedNotifyView"),
                   StorageType.nonVolatile);
    vacm.addAccess(new OctetString("v3test"), new OctetString(),
                   SecurityModel.SECURITY_MODEL_USM,
                   SecurityLevel.AUTH_PRIV, VacmMIB.vacmExactMatch,
                   new OctetString("testReadView"),
                   new OctetString("testWriteView"),
                   new OctetString("testNotifyView"),
                   StorageType.nonVolatile);

    vacm.addViewTreeFamily(new OctetString("fullReadView"), new OID("1.3"),
                           new OctetString(), VacmMIB.vacmViewIncluded,
                           StorageType.nonVolatile);
    vacm.addViewTreeFamily(new OctetString("fullWriteView"), new OID("1.3"),
                           new OctetString(), VacmMIB.vacmViewIncluded,
                           StorageType.nonVolatile);
    vacm.addViewTreeFamily(new OctetString("fullNotifyView"), new OID("1.3"),
                           new OctetString(), VacmMIB.vacmViewIncluded,
                           StorageType.nonVolatile);

    vacm.addViewTreeFamily(new OctetString("restrictedReadView"),
                           new OID("1.3.6.1.2"),
                           new OctetString(), VacmMIB.vacmViewIncluded,
                           StorageType.nonVolatile);
    vacm.addViewTreeFamily(new OctetString("restrictedWriteView"),
                           new OID("1.3.6.1.2.1"),
                           new OctetString(),
                           VacmMIB.vacmViewIncluded,
                           StorageType.nonVolatile);
    vacm.addViewTreeFamily(new OctetString("restrictedNotifyView"),
                           new OID("1.3.6.1.2"),
                           new OctetString(), VacmMIB.vacmViewIncluded,
                           StorageType.nonVolatile);

    vacm.addViewTreeFamily(new OctetString("testReadView"),
                           new OID("1.3.6.1.2"),
                           new OctetString(), VacmMIB.vacmViewIncluded,
                           StorageType.nonVolatile);
    vacm.addViewTreeFamily(new OctetString("testReadView"),
                           new OID("1.3.6.1.2.1.1"),
                           new OctetString(), VacmMIB.vacmViewExcluded,
                           StorageType.nonVolatile);
    vacm.addViewTreeFamily(new OctetString("testWriteView"),
                           new OID("1.3.6.1.2.1"),
                           new OctetString(),
                           VacmMIB.vacmViewIncluded,
                           StorageType.nonVolatile);
    vacm.addViewTreeFamily(new OctetString("testNotifyView"),
                           new OID("1.3.6.1.2"),
                           new OctetString(), VacmMIB.vacmViewIncluded,
                           StorageType.nonVolatile);

  }

  @SuppressWarnings("unchecked")
  protected void addCommunities(SnmpCommunityMIB communityMIB) {
    Variable[] com2sec = new Variable[] {
        new OctetString("public"),              // community name
        new OctetString("public"),              // security name
        agent.getLocalEngineID(),               // local engine ID
        new OctetString(),                      // default context name
        new OctetString(),                      // transport tag
        new Integer32(StorageType.nonVolatile), // storage type
        new Integer32(RowStatus.active)         // row status
    };
    SnmpCommunityMIB.SnmpCommunityEntryRow row =
        communityMIB.getSnmpCommunityEntry().createRow(
          new OctetString("public2public").toSubIndex(true), com2sec);
    communityMIB.getSnmpCommunityEntry().addRow(row);
  }

  protected void addUsmUser(USM usm) {
    UsmUser user = new UsmUser(new OctetString("SHADES"),
                               AuthSHA.ID,
                               new OctetString("SHADESAuthPassword"),
                               PrivDES.ID,
                               new OctetString("SHADESPrivPassword"));
    usm.addUser(user.getSecurityName(), usm.getLocalEngineID(), user);
    user = new UsmUser(new OctetString("TEST"),
                               AuthSHA.ID,
                               new OctetString("maplesyrup"),
                               PrivDES.ID,
                               new OctetString("maplesyrup"));
    usm.addUser(user.getSecurityName(), usm.getLocalEngineID(), user);
    user = new UsmUser(new OctetString("SHA"),
                               AuthSHA.ID,
                               new OctetString("SHAAuthPassword"),
                               null,
                               null);
    usm.addUser(user.getSecurityName(), usm.getLocalEngineID(), user);
  }


  public static void main(String[] args) {
    ArgumentParser parser =
        new ArgumentParser("-c[s{=SampleAgent.cfg}] -bc[s{=SampleAgent.bc}] "+
                           "-X[s{=tcp:0.0.0.0/705}<tcp:.*[/[0-9]+]?>] +h +v",
                           "#address[s<(udp|tcp):.*[/[0-9]+]?>] ..");
    Map commandLineParameters = null;
    try {
      commandLineParameters = parser.parse(args);
      if (commandLineParameters.containsKey("h")) {
        printUsage();
        System.exit(0);
      }
      if (commandLineParameters.containsKey("v")) {
        System.out.println("Options: "+commandLineParameters);
      }
      TestMasterAgent sampleAgent = new TestMasterAgent(commandLineParameters);
      String agentXAddress =
          ((String)((List)commandLineParameters.get("X")).get(0)).substring(4);
      sampleAgent.agent.addAgentXTransportMapping(
          new DefaultTcpTransportMapping(new TcpAddress(agentXAddress)));
      sampleAgent.agent.run();
      sampleAgent.agent.getCommandProcessor().setAcceptNewContexts(true);
      while (true) {
        try {
          Thread.sleep(1000);
        }
        catch (InterruptedException ex1) {
          break;
        }
      }
    }
    catch (ArgumentParser.ArgumentParseException ax) {
      printUsage();
      System.out.println(ax.getMessage());
    }
    catch (Exception ex) {
      logger.fatal("Caught exception while starting the agent", ex);
      ex.printStackTrace();
    }
  }

  static void printUsage() {
    String[] txt = {
        "Usage: TestMasterAgent [-c <config-file>] [-bc <boot-counter-file>] [-h] [-v]",
        "                       [-X <masterAddress>] <address1> [<address2> ..]",
        "",
        "where ",
        "  <config-file>        is the file where persistent MIB data is stored/read.",
        "  <boot-counter-file>  is the file to store the SNMPv3 boot counter.",
        "  -h                   prints this usage help information and exit.",
        "  -v                   print command line parameters.",
        "  <masterAddress>      is the TCP AgentX master agent address of the local",
        "                       host following the format 'tcp:<host>/<port>'.",
        "  <address>            a listen address following the format ",
        "                       'udp|tcp:<host>/<port>', for example udp:0.0.0.0/161",
        ""
    };
    for (String line : txt) {
      System.out.println(line);
    }
  }
}
