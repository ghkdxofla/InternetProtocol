/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JMXTestAgent.java  
  _## 
  _##  Copyright (C) 2005-2012  Frank Fock (SNMP4J.org)
  _##  
  _##  Licensed under the Apache License, Version 2.0 (the "License");
  _##  you may not use this file except in compliance with the License.
  _##  You may obtain a copy of the License at
  _##  
  _##      http://www.apache.org/licenses/LICENSE-2.0
  _##  
  _##  Unless required by applicable law or agreed to in writing, software
  _##  distributed under the License is distributed on an "AS IS" BASIS,
  _##  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  _##  See the License for the specific language governing permissions and
  _##  limitations under the License.
  _##  
  _##########################################################################*/


package org.snmp4j.agent.mo.jmx.example;

import java.io.*;

import org.snmp4j.*;
import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.*;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.mp.*;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.*;
import org.snmp4j.agent.io.ImportModes;
import org.snmp4j.util.ThreadPool;
import org.snmp4j.log.LogFactory;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.agent.mo.jmx.mibs.JvmManagementMib;
import org.snmp4j.agent.mo.jmx.*;
import org.snmp4j.agent.mo.jmx.mibs.JvmManagementMibInst;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.JavaLogFactory;

/**
 * The <code>JMXTestAgent</code> is a sample SNMP agent implementation of all
 * features (MIB implementations) provided by the SNMP4J-AgentJMX framework.
 * For demonstration purposes, the agent implements the JVM-MANAGEMENT-MIB as
 * far as this can be done without violating the SNMP standard. There are flaws
 * in that MIB module that violate the SNMP standard which have been implemented
 * in a standard conforming way.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class JMXTestAgent extends BaseAgent {

  // initialize Log4J logging
  static {
    LogFactory.setLogFactory(new JavaLogFactory());
  }

  private static final LogAdapter logger =
      LogFactory.getLogger(JMXTestAgent.class);

  protected String address;
  private JvmManagementMib jvmManagementMIB;

  /**
   * Creates the test agent with a file to read and store the boot counter and
   * a file to read and store its configuration.
   *
   * @param bootCounterFile
   *    a file containing the boot counter in serialized form (as expected by
   *    BaseAgent).
   * @param configFile
   *    a configuration file with serialized management information.
   * @throws IOException
   *    if the boot counter or config file cannot be read properly.
   */
  public JMXTestAgent(File bootCounterFile, File configFile) throws IOException {
    super(bootCounterFile, configFile,
          new CommandProcessor(new OctetString(MPv3.createLocalEngineID())));
// Alternatively:       OctetString.fromHexString("00:00:00:00:00:00:02", ':');
    agent.setWorkerPool(ThreadPool.create("RequestPool", 4));
  }

  protected void registerManagedObjects() {
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
                   SecurityLevel.NOAUTH_NOPRIV,
                   MutableVACM.VACM_MATCH_EXACT,
                   new OctetString("fullReadView"),
                   new OctetString("fullWriteView"),
                   new OctetString("fullNotifyView"),
                   StorageType.nonVolatile);
    vacm.addAccess(new OctetString("v3group"), new OctetString(),
                   SecurityModel.SECURITY_MODEL_USM,
                   SecurityLevel.AUTH_PRIV,
                   MutableVACM.VACM_MATCH_EXACT,
                   new OctetString("fullReadView"),
                   new OctetString("fullWriteView"),
                   new OctetString("fullNotifyView"),
                   StorageType.nonVolatile);
    vacm.addAccess(new OctetString("v3restricted"), new OctetString(),
                   SecurityModel.SECURITY_MODEL_USM,
                   SecurityLevel.AUTH_NOPRIV,
                   MutableVACM.VACM_MATCH_EXACT,
                   new OctetString("restrictedReadView"),
                   new OctetString("restrictedWriteView"),
                   new OctetString("restrictedNotifyView"),
                   StorageType.nonVolatile);
    vacm.addAccess(new OctetString("v3test"), new OctetString(),
                   SecurityModel.SECURITY_MODEL_USM,
                   SecurityLevel.AUTH_PRIV,
                   MutableVACM.VACM_MATCH_EXACT,
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

  protected void initTransportMappings() throws IOException {
    transportMappings = new TransportMapping[1];
    Address addr = GenericAddress.parse(address);
    TransportMapping tm =
        TransportMappings.getInstance().createTransportMapping(addr);
    transportMappings[0] = tm;
  }

  public static void main(String[] args) {
    String address;
    if (args.length > 0) {
      address = args[0];
    }
    else {
      address = "0.0.0.0/161";
    }
    try {
      JMXTestAgent testAgent1 =
          new JMXTestAgent(new File("SNMP4JJMXTestAgentBC.cfg"),
                           new File("SNMP4JJMXTestAgentConfig.cfg"));
      testAgent1.address = address;
      testAgent1.init();
      testAgent1.loadConfig(ImportModes.UPDATE_CREATE);
      testAgent1.addShutdownHook();
      testAgent1.finishInit();
      testAgent1.run();
      while (true) {
        try {
          Thread.sleep(1000);
        }
        catch (InterruptedException ex1) {
          break;
        }
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  protected void unregisterManagedObjects() {
    // here we should unregister those objects previously registered...
  }

  protected void addCommunities(SnmpCommunityMIB communityMIB) {
    Variable[] com2sec = new Variable[] {
        new OctetString("public"),              // community name
        new OctetString("public"),              // security name
        getAgent().getContextEngineID(),        // local engine ID
        new OctetString(),                      // default context name
        new OctetString(),                      // transport tag
        new Integer32(StorageType.nonVolatile), // storage type
        new Integer32(RowStatus.active)         // row status
    };
    SnmpCommunityMIB.SnmpCommunityEntryRow row =
        communityMIB.getSnmpCommunityEntry().createRow(
          new OctetString("public2public").toSubIndex(true), com2sec);
    communityMIB.getSnmpCommunityEntry().addRow(row);
//    snmpCommunityMIB.setSourceAddressFiltering(true);
  }

  protected void registerSnmpMIBs() {
    super.registerSnmpMIBs();
    try {
      jvmManagementMIB =
          new JvmManagementMibInst(super.agent.getNotificationOriginator());
      jvmManagementMIB.registerMOs(server, null);
    }
    catch (DuplicateRegistrationException ex) {
      logger.error("Unable to register JVM-MANAGEMENT-MIB", ex);
    }
  }

}
