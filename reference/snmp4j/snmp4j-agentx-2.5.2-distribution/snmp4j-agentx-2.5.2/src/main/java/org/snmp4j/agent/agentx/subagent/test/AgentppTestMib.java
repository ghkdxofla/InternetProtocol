/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentppTestMib.java  
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

//--AgentGen BEGIN=_BEGIN
//--AgentGen END

import org.snmp4j.agent.agentx.AgentXProtocol;
import org.snmp4j.smi.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.*;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.agent.mo.snmp.smi.*;
import org.snmp4j.agent.request.*;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.agent.mo.snmp.tc.*;


//--AgentGen BEGIN=_IMPORT
import org.snmp4j.agent.agentx.subagent.DefaultAgentXSharedMOTable;
import org.snmp4j.agent.agentx.subagent.AgentXSharedMOTableSupport;
import java.util.GregorianCalendar;
//--AgentGen END

public class AgentppTestMib
//--AgentGen BEGIN=_EXTENDS
//--AgentGen END
implements MOGroup
//--AgentGen BEGIN=_IMPLEMENTS
//--AgentGen END
{

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(AgentppTestMib.class);

//--AgentGen BEGIN=_STATIC
//--AgentGen END

  // Factory
  private MOFactory moFactory =
    DefaultMOFactory.getInstance();

  // Constants

  /**
   * OID of this MIB module for usage which can be 
   * used for its identification.
   */
  public static final OID oidAgentppTestMib =
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3 });

  // Identities
  // Scalars
  public static final OID oidAgentppTestTimeout =
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,1,0 });
  // Tables

  // Notifications

  // Enumerations

  public static final class AgentppTestSessionIndexStrategyEnum {
    public static final int noIndexAllocation = 0;
    public static final int firstSubIndexOnly = 1;
    public static final int anyNonAllocatedSubIndex = 2;
    public static final int alwaysFirstSubIndex = 3;
    public static final int alwaysAnySubIndex = 4;
  }



  // TextualConventions
  private static final String TC_MODULE_SNMP_FRAMEWORK_MIB = "SNMP-FRAMEWORK-MIB";
  private static final String TC_MODULE_SNMPV2_TC = "SNMPv2-TC";
  private static final String TC_SNMPADMINSTRING = "SnmpAdminString";
  private static final String TC_DATEANDTIME = "DateAndTime";
  private static final String TC_ROWSTATUS = "RowStatus";

  // Scalars
  private MOScalar<UnsignedInteger32> agentppTestTimeout;

  // Tables
  public static final OID oidAgentppTestSharedEntry =
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,3,1 });

  // Index OID definitions
  public static final OID oidAgentppTestSharedTableIndex =
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,3,1,1 });

  // Column TC definitions for agentppTestSharedEntry:
  public static final String tcModuleSNMPv2Tc = "SNMPv2-TC";
  public static final String tcDefDateAndTime = "DateAndTime";
  public static final String tcDefRowStatus = "RowStatus";

  // Column sub-identifer definitions for agentppTestSharedEntry:
  public static final int colAgentppTestSharedTableCreationTime = 2;
  public static final int colAgentppTestSharedTableDelay = 3;
  public static final int colAgentppTestSharedTableSession = 4;
  public static final int colAgentppTestSharedTableRowStatus = 5;

  // Column index definitions for agentppTestSharedEntry:
  public static final int idxAgentppTestSharedTableCreationTime = 0;
  public static final int idxAgentppTestSharedTableDelay = 1;
  public static final int idxAgentppTestSharedTableSession = 2;
  public static final int idxAgentppTestSharedTableRowStatus = 3;

  private MOTableSubIndex[] agentppTestSharedEntryIndexes;
  private MOTableIndex agentppTestSharedEntryIndex;

  private MOTable<AgentppTestSharedEntryRow,
                  MOColumn,
                  MOTableModel<AgentppTestSharedEntryRow>>      agentppTestSharedEntry;
  private MOTableModel<AgentppTestSharedEntryRow> agentppTestSharedEntryModel;
  public static final OID oidAgentppTestSessionsEntry =
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,4,1 });

  // Index OID definitions
  public static final OID oidAgentppTestSessionIndex =
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,4,1,1 });

  // Column TC definitions for agentppTestSessionsEntry:

  // Column sub-identifer definitions for agentppTestSessionsEntry:
  public static final int colAgentppTestRowCreation = 2;

  // Column index definitions for agentppTestSessionsEntry:
  public static final int idxAgentppTestRowCreation = 0;

  private MOTableSubIndex[] agentppTestSessionsEntryIndexes;
  private MOTableIndex agentppTestSessionsEntryIndex;

  private MOTable<AgentppTestSessionsEntryRow,
                  MOColumn,
                  MOTableModel<AgentppTestSessionsEntryRow>> agentppTestSessionsEntry;
  private MOTableModel<AgentppTestSessionsEntryRow> agentppTestSessionsEntryModel;
  public static final OID oidAgentppTestSparseEntry =
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,5,1 });

  // Index OID definitions
  public static final OID oidAgentppTestSparseIndex =
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,5,1,1 });

  // Column TC definitions for agentppTestSparseEntry:
  public static final String tcModuleSnmpFrameworkMib = "SNMP-FRAMEWORK-MIB";
  public static final String tcDefSnmpAdminString = "SnmpAdminString";

  // Column sub-identifer definitions for agentppTestSparseEntry:
  public static final int colAgentppTestSparseCol1 = 2;
  public static final int colAgentppTestSparseCol2 = 3;
  public static final int colAgentppTestSparseCol3 = 4;
  public static final int colAgentppTestSparseRowStatus = 5;

  // Column index definitions for agentppTestSparseEntry:
  public static final int idxAgentppTestSparseCol1 = 0;
  public static final int idxAgentppTestSparseCol2 = 1;
  public static final int idxAgentppTestSparseCol3 = 2;
  public static final int idxAgentppTestSparseRowStatus = 3;

  private MOTableSubIndex[] agentppTestSparseEntryIndexes;
  private MOTableIndex agentppTestSparseEntryIndex;

  private MOTable<AgentppTestSparseEntryRow,
                  MOColumn,
                  MOTableModel<AgentppTestSparseEntryRow>> agentppTestSparseEntry;
  private MOTableModel<AgentppTestSparseEntryRow> agentppTestSparseEntryModel;
  public static final OID oidAgentppTestSharedExtEntry = 
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,6,1 });

  // Index OID definitions
  //public static final OID oidAgentppTestSharedTableIndex =
  //  new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,3,1,1 });
  public static final OID oidAgentppTestSharedExtTableIndex =
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,6,1,1 });

  // Column TC definitions for agentppTestSharedExtEntry:
    
  // Column sub-identifer definitions for agentppTestSharedExtEntry:
  public static final int colAgentppTestSharedExtValueInt = 2;
  public static final int colAgentppTestSharedExtValueString = 3;
  public static final int colAgentppTestSharedExtTableRowStatus = 4;

  // Column index definitions for agentppTestSharedExtEntry:
  public static final int idxAgentppTestSharedExtValueInt = 0;
  public static final int idxAgentppTestSharedExtValueString = 1;
  public static final int idxAgentppTestSharedExtTableRowStatus = 2;

  private MOTableSubIndex[] agentppTestSharedExtEntryIndexes;
  private MOTableIndex agentppTestSharedExtEntryIndex;
  
  private MOTable<AgentppTestSharedExtEntryRow,
                  MOColumn,
                  MOTableModel<AgentppTestSharedExtEntryRow>> agentppTestSharedExtEntry;
  private MOTableModel<AgentppTestSharedExtEntryRow> agentppTestSharedExtEntryModel;
  public static final OID oidAgentppTestSessionAttrEntry = 
    new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,7,1 });

  // Index OID definitions
  //public static final OID oidAgentppTestSessionIndex =
  //  new OID(new int[] { 1,3,6,1,4,1,4976,6,3,1,4,1,1 });

  // Column TC definitions for agentppTestSessionAttrEntry:
    
  // Column sub-identifer definitions for agentppTestSessionAttrEntry:
  public static final int colAgentppTestSessionIndexStrategy = 1;

  // Column index definitions for agentppTestSessionAttrEntry:
  public static final int idxAgentppTestSessionIndexStrategy = 0;

  private MOTableSubIndex[] agentppTestSessionAttrEntryIndexes;
  private MOTableIndex agentppTestSessionAttrEntryIndex;
  
  private MOTable<AgentppTestSessionAttrEntryRow,
                  MOColumn,
                  MOTableModel<AgentppTestSessionAttrEntryRow>> agentppTestSessionAttrEntry;
  private MOTableModel<AgentppTestSessionAttrEntryRow> agentppTestSessionAttrEntryModel;


//--AgentGen BEGIN=_MEMBERS
//--AgentGen END

  /**
   * Constructs a AgentppTestMib instance without actually creating its
   * <code>ManagedObject</code> instances. This has to be done in a
   * sub-class constructor or after construction by calling
   * {@link #createMO(MOFactory moFactory)}.
   */
  protected AgentppTestMib() {
//--AgentGen BEGIN=_DEFAULTCONSTRUCTOR
//--AgentGen END
  }

  /**
   * Constructs a AgentppTestMib instance and actually creates its
   * <code>ManagedObject</code> instances using the supplied
   * <code>MOFactory</code> (by calling
   * {@link #createMO(MOFactory moFactory)}).
   * @param moFactory
   *    the <code>MOFactory</code> to be used to create the
   *    managed objects for this module.
   */
  public AgentppTestMib(MOFactory moFactory) {
    this();
    createMO(moFactory);
//--AgentGen BEGIN=_FACTORYCONSTRUCTOR
//--AgentGen END
  }

//--AgentGen BEGIN=_CONSTRUCTORS
//--AgentGen END

  /**
   * Create the ManagedObjects defined for this MIB module
   * using the specified {@link MOFactory}.
   * @param moFactory
   *    the <code>MOFactory</code> instance to use for object
   *    creation.
   */
  protected void createMO(MOFactory moFactory) {
    addTCsToFactory(moFactory);
    agentppTestTimeout =
      new AgentppTestTimeout(oidAgentppTestTimeout,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE));
    agentppTestTimeout.addMOValueValidationListener(new AgentppTestTimeoutValidator());
    createAgentppTestSharedEntry(moFactory);
    createAgentppTestSessionsEntry(moFactory);
    createAgentppTestSparseEntry(moFactory);
    createAgentppTestSharedExtEntry(moFactory);
    createAgentppTestSessionAttrEntry(moFactory);
  }

  public MOScalar<UnsignedInteger32> getAgentppTestTimeout() {
    return agentppTestTimeout;
  }


  public MOTable<AgentppTestSharedEntryRow,MOColumn,MOTableModel<AgentppTestSharedEntryRow>> getAgentppTestSharedEntry() {
    return agentppTestSharedEntry;
  }


  @SuppressWarnings(value={"unchecked"})
  private void createAgentppTestSharedEntry(MOFactory moFactory) {
    // Index definition
    agentppTestSharedEntryIndexes =
      new MOTableSubIndex[] {
      moFactory.createSubIndex(oidAgentppTestSharedTableIndex,
                               SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    agentppTestSharedEntryIndex =
      moFactory.createIndex(agentppTestSharedEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=agentppTestSharedEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] agentppTestSharedEntryColumns = new MOColumn[4];
    agentppTestSharedEntryColumns[idxAgentppTestSharedTableCreationTime] =
      moFactory.createColumn(colAgentppTestSharedTableCreationTime,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             tcModuleSNMPv2Tc,
                             tcDefDateAndTime);
    agentppTestSharedEntryColumns[idxAgentppTestSharedTableDelay] =
      moFactory.createColumn(colAgentppTestSharedTableDelay,
                          SMIConstants.SYNTAX_INTEGER32,
                          moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_CREATE),
                             new Integer32(0),
                             true);
    ValueConstraint agentppTestSharedTableDelayVC = new ConstraintsImpl();
    ((ConstraintsImpl)agentppTestSharedTableDelayVC).add(new Constraint(0L, 6000L));
    ((MOMutableColumn)agentppTestSharedEntryColumns[idxAgentppTestSharedTableDelay]).
      addMOValueValidationListener(new ValueConstraintValidator(agentppTestSharedTableDelayVC));
    ((MOMutableColumn)agentppTestSharedEntryColumns[idxAgentppTestSharedTableDelay]).
      addMOValueValidationListener(new AgentppTestSharedTableDelayValidator());
    agentppTestSharedEntryColumns[idxAgentppTestSharedTableSession] =
      moFactory.createColumn(colAgentppTestSharedTableSession,
                             SMIConstants.SYNTAX_GAUGE32,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    agentppTestSharedEntryColumns[idxAgentppTestSharedTableRowStatus] =
      moFactory.createColumn(colAgentppTestSharedTableRowStatus,
                             SMIConstants.SYNTAX_INTEGER,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_CREATE),
                             tcModuleSNMPv2Tc,
                             tcDefRowStatus);
    ((MOMutableColumn)agentppTestSharedEntryColumns[idxAgentppTestSharedTableRowStatus]).
      addMOValueValidationListener(new AgentppTestSharedTableRowStatusValidator());
    // Table model
    agentppTestSharedEntryModel =
      moFactory.createTableModel(oidAgentppTestSharedEntry,
                                 agentppTestSharedEntryIndex,
                                 agentppTestSharedEntryColumns);
    ((MOMutableTableModel<AgentppTestSharedEntryRow>)agentppTestSharedEntryModel).setRowFactory(
      new AgentppTestSharedEntryRowFactory());
    agentppTestSharedEntry =
      moFactory.createTable(oidAgentppTestSharedEntry,
                            agentppTestSharedEntryIndex,
                            agentppTestSharedEntryColumns,
                            agentppTestSharedEntryModel);
  }

  public MOTable<AgentppTestSessionsEntryRow,MOColumn,MOTableModel<AgentppTestSessionsEntryRow>> getAgentppTestSessionsEntry() {
    return agentppTestSessionsEntry;
  }


  @SuppressWarnings(value={"unchecked"})
  private void createAgentppTestSessionsEntry(MOFactory moFactory) {
    // Index definition
    agentppTestSessionsEntryIndexes =
      new MOTableSubIndex[] {
      moFactory.createSubIndex(oidAgentppTestSessionIndex,
                               SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    agentppTestSessionsEntryIndex =
      moFactory.createIndex(agentppTestSessionsEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=agentppTestSessionsEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] agentppTestSessionsEntryColumns = new MOColumn[1];
    agentppTestSessionsEntryColumns[idxAgentppTestRowCreation] =
      moFactory.createColumn(colAgentppTestRowCreation,
                             SMIConstants.SYNTAX_GAUGE32,
                             moFactory.createAccess(
                                 MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
                             new UnsignedInteger32(0),
                             true);
    ((MOMutableColumn)agentppTestSessionsEntryColumns[idxAgentppTestRowCreation]).
      addMOValueValidationListener(new AgentppTestRowCreationValidator());
    // Table model
    agentppTestSessionsEntryModel =
      moFactory.createTableModel(oidAgentppTestSessionsEntry,
                                 agentppTestSessionsEntryIndex,
                                 agentppTestSessionsEntryColumns);
    ((MOMutableTableModel<AgentppTestSessionsEntryRow>)agentppTestSessionsEntryModel).setRowFactory(
      new AgentppTestSessionsEntryRowFactory());
    agentppTestSessionsEntry =
      moFactory.createTable(oidAgentppTestSessionsEntry,
                            agentppTestSessionsEntryIndex,
                            agentppTestSessionsEntryColumns,
                            agentppTestSessionsEntryModel);
    ((DefaultMOTable)agentppTestSessionsEntry).setVolatile(true);
  }

  public MOTable<AgentppTestSparseEntryRow,MOColumn,MOTableModel<AgentppTestSparseEntryRow>> getAgentppTestSparseEntry() {
    return agentppTestSparseEntry;
  }


  @SuppressWarnings(value={"unchecked"})
  private void createAgentppTestSparseEntry(MOFactory moFactory) {
    // Index definition
    agentppTestSparseEntryIndexes =
      new MOTableSubIndex[] {
      moFactory.createSubIndex(oidAgentppTestSparseIndex,
                               SMIConstants.SYNTAX_OCTET_STRING, 0, 255)
    };

    agentppTestSparseEntryIndex =
      moFactory.createIndex(agentppTestSparseEntryIndexes,
                            false,
                            null);

    // Columns
    MOColumn[] agentppTestSparseEntryColumns = new MOColumn[4];
    agentppTestSparseEntryColumns[idxAgentppTestSparseCol1] =
      moFactory.createColumn(colAgentppTestSparseCol1,
                             SMIConstants.SYNTAX_INTEGER32,
                             moFactory.createAccess(
                                 MOAccessImpl.ACCESSIBLE_FOR_READ_CREATE),
                             new Integer32(1),
                             true);
    ((MOMutableColumn)agentppTestSparseEntryColumns[idxAgentppTestSparseCol1]).
      addMOValueValidationListener(new AgentppTestSparseCol1Validator());
    agentppTestSparseEntryColumns[idxAgentppTestSparseCol2] =
      moFactory.createColumn(colAgentppTestSparseCol2,
                             SMIConstants.SYNTAX_GAUGE32,
                             moFactory.createAccess(
                                 MOAccessImpl.ACCESSIBLE_FOR_READ_CREATE),
                             new Gauge32(2),
                             true);
    ((MOMutableColumn)agentppTestSparseEntryColumns[idxAgentppTestSparseCol2]).
      addMOValueValidationListener(new AgentppTestSparseCol2Validator());
    agentppTestSparseEntryColumns[idxAgentppTestSparseCol3] =
      moFactory.createColumn(colAgentppTestSparseCol3,
                          SMIConstants.SYNTAX_OCTET_STRING,
                          moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_CREATE),
                             new OctetString(new byte[] { (byte)51 }),
                             true);
    ValueConstraint agentppTestSparseCol3VC = new ConstraintsImpl();
    ((ConstraintsImpl)agentppTestSparseCol3VC).add(new Constraint(0L, 255L));
    ((MOMutableColumn)agentppTestSparseEntryColumns[idxAgentppTestSparseCol3]).
      addMOValueValidationListener(new ValueConstraintValidator(agentppTestSparseCol3VC));
    ((MOMutableColumn)agentppTestSparseEntryColumns[idxAgentppTestSparseCol3]).
      addMOValueValidationListener(new AgentppTestSparseCol3Validator());
    agentppTestSparseEntryColumns[idxAgentppTestSparseRowStatus] =
      moFactory.createColumn(colAgentppTestSparseRowStatus,
                             SMIConstants.SYNTAX_INTEGER,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_CREATE),
                             tcModuleSNMPv2Tc,
                             tcDefRowStatus);
    ((MOMutableColumn)agentppTestSparseEntryColumns[idxAgentppTestSparseRowStatus]).
      addMOValueValidationListener(new AgentppTestSparseRowStatusValidator());
    // Table model
    agentppTestSparseEntryModel =
      moFactory.createTableModel(oidAgentppTestSparseEntry,
                                 agentppTestSparseEntryIndex,
                                 agentppTestSparseEntryColumns);
    ((MOMutableTableModel<AgentppTestSparseEntryRow>)agentppTestSparseEntryModel).setRowFactory(
      new AgentppTestSparseEntryRowFactory());
    agentppTestSparseEntry =
      moFactory.createTable(oidAgentppTestSparseEntry,
                            agentppTestSparseEntryIndex,
                            agentppTestSparseEntryColumns,
                            agentppTestSparseEntryModel);
  }

  public MOTable<AgentppTestSharedExtEntryRow,MOColumn,MOTableModel<AgentppTestSharedExtEntryRow>> getAgentppTestSharedExtEntry() {
    return agentppTestSharedExtEntry;
  }


  @SuppressWarnings(value={"unchecked"})
  private void createAgentppTestSharedExtEntry(MOFactory moFactory) {
    // Index definition
    agentppTestSharedExtEntryIndexes = 
      new MOTableSubIndex[] {
      moFactory.createSubIndex(oidAgentppTestSharedTableIndex, 
                               SMIConstants.SYNTAX_INTEGER, 1, 1),
      moFactory.createSubIndex(oidAgentppTestSharedExtTableIndex, 
                               SMIConstants.SYNTAX_OCTET_STRING, 0, 255)
    };

    agentppTestSharedExtEntryIndex = 
      moFactory.createIndex(agentppTestSharedExtEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=agentppTestSharedExtEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] agentppTestSharedExtEntryColumns = new MOColumn[3];
    agentppTestSharedExtEntryColumns[idxAgentppTestSharedExtValueInt] = 
      new MOMutableColumn<Integer32>(colAgentppTestSharedExtValueInt,
                          SMIConstants.SYNTAX_INTEGER32,
                          moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_CREATE),
                          new Integer32(0));
    ValueConstraint agentppTestSharedExtValueIntVC = new ConstraintsImpl();
    ((ConstraintsImpl)agentppTestSharedExtValueIntVC).add(new Constraint(-1024L, 1023L));
    ((MOMutableColumn)agentppTestSharedExtEntryColumns[idxAgentppTestSharedExtValueInt]).
      addMOValueValidationListener(new ValueConstraintValidator(agentppTestSharedExtValueIntVC));                                  
    ((MOMutableColumn)agentppTestSharedExtEntryColumns[idxAgentppTestSharedExtValueInt]).
      addMOValueValidationListener(new AgentppTestSharedExtValueIntValidator());
    agentppTestSharedExtEntryColumns[idxAgentppTestSharedExtValueString] = 
      new MOMutableColumn<OctetString>(colAgentppTestSharedExtValueString,
                          SMIConstants.SYNTAX_OCTET_STRING,
                          moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_CREATE),
                          (OctetString)null);
    ValueConstraint agentppTestSharedExtValueStringVC = new ConstraintsImpl();
    ((ConstraintsImpl)agentppTestSharedExtValueStringVC).add(new Constraint(0L, 255L));
    ((MOMutableColumn)agentppTestSharedExtEntryColumns[idxAgentppTestSharedExtValueString]).
      addMOValueValidationListener(new ValueConstraintValidator(agentppTestSharedExtValueStringVC));                                  
    ((MOMutableColumn)agentppTestSharedExtEntryColumns[idxAgentppTestSharedExtValueString]).
      addMOValueValidationListener(new AgentppTestSharedExtValueStringValidator());
    agentppTestSharedExtEntryColumns[idxAgentppTestSharedExtTableRowStatus] = 
      new RowStatus(colAgentppTestSharedExtTableRowStatus);
    // Table model
    agentppTestSharedExtEntryModel = 
      moFactory.createTableModel(oidAgentppTestSharedExtEntry,
                                 agentppTestSharedExtEntryIndex,
                                 agentppTestSharedExtEntryColumns);
    ((MOMutableTableModel<AgentppTestSharedExtEntryRow>)agentppTestSharedExtEntryModel).setRowFactory(
      new AgentppTestSharedExtEntryRowFactory());
    agentppTestSharedExtEntry = 
      moFactory.createTable(oidAgentppTestSharedExtEntry,
                            agentppTestSharedExtEntryIndex,
                            agentppTestSharedExtEntryColumns,
                            agentppTestSharedExtEntryModel);
  }

  public MOTable<AgentppTestSessionAttrEntryRow,MOColumn,MOTableModel<AgentppTestSessionAttrEntryRow>> getAgentppTestSessionAttrEntry() {
    return agentppTestSessionAttrEntry;
  }


  @SuppressWarnings(value={"unchecked"})
  private void createAgentppTestSessionAttrEntry(MOFactory moFactory) {
    // Index definition
    agentppTestSessionAttrEntryIndexes = 
      new MOTableSubIndex[] {
      moFactory.createSubIndex(oidAgentppTestSessionIndex, 
                               SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    agentppTestSessionAttrEntryIndex = 
      moFactory.createIndex(agentppTestSessionAttrEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=agentppTestSessionAttrEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] agentppTestSessionAttrEntryColumns = new MOColumn[1];
    agentppTestSessionAttrEntryColumns[idxAgentppTestSessionIndexStrategy] = 
      new Enumerated<Integer32>(colAgentppTestSessionIndexStrategy,
                     SMIConstants.SYNTAX_INTEGER,
                     moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_CREATE),
                     new Integer32(1));
    ValueConstraint agentppTestSessionIndexStrategyVC = new EnumerationConstraint(
      new int[] { AgentppTestSessionIndexStrategyEnum.noIndexAllocation,
                  AgentppTestSessionIndexStrategyEnum.firstSubIndexOnly,
                  AgentppTestSessionIndexStrategyEnum.anyNonAllocatedSubIndex,
                  AgentppTestSessionIndexStrategyEnum.alwaysFirstSubIndex,
                  AgentppTestSessionIndexStrategyEnum.alwaysAnySubIndex });
    ((MOMutableColumn)agentppTestSessionAttrEntryColumns[idxAgentppTestSessionIndexStrategy]).
      addMOValueValidationListener(new ValueConstraintValidator(agentppTestSessionIndexStrategyVC));                                  
    ((MOMutableColumn)agentppTestSessionAttrEntryColumns[idxAgentppTestSessionIndexStrategy]).
      addMOValueValidationListener(new AgentppTestSessionIndexStrategyValidator());
    // Table model
    agentppTestSessionAttrEntryModel = 
      moFactory.createTableModel(oidAgentppTestSessionAttrEntry,
                                 agentppTestSessionAttrEntryIndex,
                                 agentppTestSessionAttrEntryColumns);
    ((MOMutableTableModel<AgentppTestSessionAttrEntryRow>)agentppTestSessionAttrEntryModel).setRowFactory(
      new AgentppTestSessionAttrEntryRowFactory());
    agentppTestSessionAttrEntry = 
      moFactory.createTable(oidAgentppTestSessionAttrEntry,
                            agentppTestSessionAttrEntryIndex,
                            agentppTestSessionAttrEntryColumns,
                            agentppTestSessionAttrEntryModel);
  }



  public void registerMOs(MOServer server, OctetString context)
    throws DuplicateRegistrationException
  {
    // Scalar Objects
    server.register(this.agentppTestTimeout, context);
    server.register(this.agentppTestSharedEntry, context);
    server.register(this.agentppTestSessionsEntry, context);
    server.register(this.agentppTestSparseEntry, context);
    server.register(this.agentppTestSharedExtEntry, context);
    server.register(this.agentppTestSessionAttrEntry, context);
//--AgentGen BEGIN=_registerMOs
    for (int i=0; i<10; i++) {
      Variable[] vbs = agentppTestSparseEntry.getDefaultValues();
      vbs[idxAgentppTestSparseRowStatus] = new Integer32(RowStatus.active);
      AgentppTestSparseEntryRow row =
          agentppTestSparseEntry.createRow(new OID(
          new int[] { 1, 48+i }), vbs);
      agentppTestSparseEntry.addRow(row);
    }
//--AgentGen END
  }

  public void unregisterMOs(MOServer server, OctetString context) {
    // Scalar Objects
    server.unregister(this.agentppTestTimeout, context);
    server.unregister(this.agentppTestSharedEntry, context);
    server.unregister(this.agentppTestSessionsEntry, context);
    server.unregister(this.agentppTestSparseEntry, context);
    server.unregister(this.agentppTestSharedExtEntry, context);
    server.unregister(this.agentppTestSessionAttrEntry, context);
//--AgentGen BEGIN=_unregisterMOs
//--AgentGen END
  }

  // Notifications

  // Scalars
  public class AgentppTestTimeout extends MOScalar<UnsignedInteger32> {
    AgentppTestTimeout(OID oid, MOAccess access) {
      super(oid, access, new UnsignedInteger32());
//--AgentGen BEGIN=agentppTestTimeout
//--AgentGen END
    }

    public int isValueOK(SubRequest request) {
      Variable newValue =
        request.getVariableBinding().getVariable();
      int valueOK = super.isValueOK(request);
      if (valueOK != SnmpConstants.SNMP_ERROR_SUCCESS) {
      	return valueOK;
      }
      long v = ((UnsignedInteger32)newValue).getValue();
      if (!(((v >= 0L) && (v <= 1000000L)))) {
        valueOK = SnmpConstants.SNMP_ERROR_WRONG_VALUE;
      }
     //--AgentGen BEGIN=agentppTestTimeout::isValueOK
     //--AgentGen END
      return valueOK;
    }

    public UnsignedInteger32 getValue() {
     //--AgentGen BEGIN=agentppTestTimeout::getValue
     //--AgentGen END
      return super.getValue();
    }

    public int setValue(UnsignedInteger32 newValue) {
     //--AgentGen BEGIN=agentppTestTimeout::setValue
     //--AgentGen END
      return super.setValue(newValue);
    }

     //--AgentGen BEGIN=agentppTestTimeout::_METHODS
     //--AgentGen END

  }


  // Value Validators
  /**
   * The <code>AgentppTestTimeoutValidator</code> implements the value
   * validation for <code>AgentppTestTimeout</code>.
   */
  static class AgentppTestTimeoutValidator implements MOValueValidationListener {

    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
      long v = ((UnsignedInteger32)newValue).getValue();
      if (!(((v >= 0L) && (v <= 1000000L)))) {
        validationEvent.setValidationStatus(SnmpConstants.SNMP_ERROR_WRONG_VALUE);
        return;
      }
     //--AgentGen BEGIN=agentppTestTimeout::validate
     //--AgentGen END
    }
  }

  /**
   * The <code>AgentppTestSharedTableDelayValidator</code> implements the value
   * validation for <code>AgentppTestSharedTableDelay</code>.
   */
  static class AgentppTestSharedTableDelayValidator implements MOValueValidationListener {

    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
      long v = ((Integer32)newValue).getValue();
      if (!(((v >= 0L) && (v <= 6000L)))) {
        validationEvent.setValidationStatus(SnmpConstants.SNMP_ERROR_WRONG_VALUE);
        return;
      }
     //--AgentGen BEGIN=agentppTestSharedTableDelay::validate
     //--AgentGen END
    }
  }
  /**
   * The <code>AgentppTestSharedTableRowStatusValidator</code> implements the value
   * validation for <code>AgentppTestSharedTableRowStatus</code>.
   */
  static class AgentppTestSharedTableRowStatusValidator implements MOValueValidationListener {

    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
     //--AgentGen BEGIN=agentppTestSharedTableRowStatus::validate
     //--AgentGen END
    }
  }
  /**
   * The <code>AgentppTestRowCreationValidator</code> implements the value
   * validation for <code>AgentppTestRowCreation</code>.
   */
  static class AgentppTestRowCreationValidator implements MOValueValidationListener {

    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
     //--AgentGen BEGIN=agentppTestRowCreation::validate
     //--AgentGen END
    }
  }
  /**
   * The <code>AgentppTestSparseCol1Validator</code> implements the value
   * validation for <code>AgentppTestSparseCol1</code>.
   */
  static class AgentppTestSparseCol1Validator implements MOValueValidationListener {

    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
     //--AgentGen BEGIN=agentppTestSparseCol1::validate
     //--AgentGen END
    }
  }
  /**
   * The <code>AgentppTestSparseCol2Validator</code> implements the value
   * validation for <code>AgentppTestSparseCol2</code>.
   */
  static class AgentppTestSparseCol2Validator implements MOValueValidationListener {

    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
     //--AgentGen BEGIN=agentppTestSparseCol2::validate
     //--AgentGen END
    }
  }
  /**
   * The <code>AgentppTestSparseCol3Validator</code> implements the value
   * validation for <code>AgentppTestSparseCol3</code>.
   */
  static class AgentppTestSparseCol3Validator implements MOValueValidationListener {

    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
      OctetString os = (OctetString)newValue;
      if (!(((os.length() >= 0) && (os.length() <= 255)))) {
        validationEvent.setValidationStatus(SnmpConstants.SNMP_ERROR_WRONG_LENGTH);
        return;
      }
     //--AgentGen BEGIN=agentppTestSparseCol3::validate
     //--AgentGen END
    }
  }
  /**
   * The <code>AgentppTestSparseRowStatusValidator</code> implements the value
   * validation for <code>AgentppTestSparseRowStatus</code>.
   */
  static class AgentppTestSparseRowStatusValidator implements MOValueValidationListener {

    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
     //--AgentGen BEGIN=agentppTestSparseRowStatus::validate
     //--AgentGen END
    }
  }
  /**
   * The <code>AgentppTestSharedExtValueIntValidator</code> implements the value
   * validation for <code>AgentppTestSharedExtValueInt</code>.
   */
  static class AgentppTestSharedExtValueIntValidator implements MOValueValidationListener {
    
    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
      long v = ((Integer32)newValue).getValue();
      if (!(((v >= -1024L) && (v <= 1023L)))) {
        validationEvent.setValidationStatus(SnmpConstants.SNMP_ERROR_WRONG_VALUE);
        return;
      }
     //--AgentGen BEGIN=agentppTestSharedExtValueInt::validate
     //--AgentGen END
    }
  }
  /**
   * The <code>AgentppTestSharedExtValueStringValidator</code> implements the value
   * validation for <code>AgentppTestSharedExtValueString</code>.
   */
  static class AgentppTestSharedExtValueStringValidator implements MOValueValidationListener {
    
    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
      OctetString os = (OctetString)newValue;
      if (!(((os.length() >= 0) && (os.length() <= 255)))) {
        validationEvent.setValidationStatus(SnmpConstants.SNMP_ERROR_WRONG_LENGTH);
        return;
      }
     //--AgentGen BEGIN=agentppTestSharedExtValueString::validate
     //--AgentGen END
    }
  }
  /**
   * The <code>AgentppTestSharedExtTableRowStatusValidator</code> implements the value
   * validation for <code>AgentppTestSharedExtTableRowStatus</code>.
   */
  static class AgentppTestSharedExtTableRowStatusValidator implements MOValueValidationListener {
    
    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
     //--AgentGen BEGIN=agentppTestSharedExtTableRowStatus::validate
     //--AgentGen END
    }
  }
  /**
   * The <code>AgentppTestSessionIndexStrategyValidator</code> implements the value
   * validation for <code>AgentppTestSessionIndexStrategy</code>.
   */
  static class AgentppTestSessionIndexStrategyValidator implements MOValueValidationListener {
    
    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
     //--AgentGen BEGIN=agentppTestSessionIndexStrategy::validate
     //--AgentGen END
    }
  }

  // Rows and Factories

  public class AgentppTestSharedEntryRow extends DefaultMOMutableRow2PC {

     //--AgentGen BEGIN=agentppTestSharedEntry::RowMembers
     //--AgentGen END

    public AgentppTestSharedEntryRow(OID index, Variable[] values) {
      super(index, values);
     //--AgentGen BEGIN=agentppTestSharedEntry::RowConstructor
     //--AgentGen END
    }

    public OctetString getAgentppTestSharedTableCreationTime() {
     //--AgentGen BEGIN=agentppTestSharedEntry::getAgentppTestSharedTableCreationTime
     //--AgentGen END
      return (OctetString) super.getValue(idxAgentppTestSharedTableCreationTime);
    }

    public void setAgentppTestSharedTableCreationTime(OctetString newValue) {
     //--AgentGen BEGIN=agentppTestSharedEntry::setAgentppTestSharedTableCreationTime
     //--AgentGen END
      super.setValue(idxAgentppTestSharedTableCreationTime, newValue);
    }

    public Integer32 getAgentppTestSharedTableDelay() {
     //--AgentGen BEGIN=agentppTestSharedEntry::getAgentppTestSharedTableDelay
     //--AgentGen END
      return (Integer32) super.getValue(idxAgentppTestSharedTableDelay);
    }

    public void setAgentppTestSharedTableDelay(Integer32 newValue) {
     //--AgentGen BEGIN=agentppTestSharedEntry::setAgentppTestSharedTableDelay
     //--AgentGen END
      super.setValue(idxAgentppTestSharedTableDelay, newValue);
    }

    public UnsignedInteger32 getAgentppTestSharedTableSession() {
     //--AgentGen BEGIN=agentppTestSharedEntry::getAgentppTestSharedTableSession
     //--AgentGen END
      return (UnsignedInteger32) super.getValue(idxAgentppTestSharedTableSession);
    }

    public void setAgentppTestSharedTableSession(UnsignedInteger32 newValue) {
     //--AgentGen BEGIN=agentppTestSharedEntry::setAgentppTestSharedTableSession
     //--AgentGen END
      super.setValue(idxAgentppTestSharedTableSession, newValue);
    }

    public Integer32 getAgentppTestSharedTableRowStatus() {
     //--AgentGen BEGIN=agentppTestSharedEntry::getAgentppTestSharedTableRowStatus
     //--AgentGen END
      return (Integer32) super.getValue(idxAgentppTestSharedTableRowStatus);
    }

    public void setAgentppTestSharedTableRowStatus(Integer32 newValue) {
     //--AgentGen BEGIN=agentppTestSharedEntry::setAgentppTestSharedTableRowStatus
     //--AgentGen END
      super.setValue(idxAgentppTestSharedTableRowStatus, newValue);
    }
    
    public Variable getValue(int column) {
     //--AgentGen BEGIN=agentppTestSharedEntry::RowGetValue
     //--AgentGen END
      switch(column) {
        case idxAgentppTestSharedTableCreationTime: 
        	return getAgentppTestSharedTableCreationTime();
        case idxAgentppTestSharedTableDelay: 
        	return getAgentppTestSharedTableDelay();
        case idxAgentppTestSharedTableSession: 
        	return getAgentppTestSharedTableSession();
        case idxAgentppTestSharedTableRowStatus: 
        	return getAgentppTestSharedTableRowStatus();
        default:
          return super.getValue(column);
      }
    }
    
    public void setValue(int column, Variable value) {
     //--AgentGen BEGIN=agentppTestSharedEntry::RowSetValue
      delayRequest();
     //--AgentGen END
      switch(column) {
        case idxAgentppTestSharedTableCreationTime: 
        	setAgentppTestSharedTableCreationTime((OctetString)value);
        	break;
        case idxAgentppTestSharedTableDelay: 
        	setAgentppTestSharedTableDelay((Integer32)value);
        	break;
        case idxAgentppTestSharedTableSession: 
        	setAgentppTestSharedTableSession((UnsignedInteger32)value);
        	break;
        case idxAgentppTestSharedTableRowStatus: 
        	setAgentppTestSharedTableRowStatus((Integer32)value);
        	break;
        default:
          super.setValue(column, value);
      }
    }

    //--AgentGen BEGIN=agentppTestSharedEntry::Row

    private void delayRequest() {
      long delay = values[idxAgentppTestSharedTableDelay].toInt() * 10;
      if (delay > 0) {
        try {
          Thread.sleep(delay);
        }
        catch (InterruptedException ex) {
        }
      }
    }

    public void commit(SubRequest subRequest, MOTableRow changeSet, int column) {
      delayRequest();
      super.commit(subRequest, changeSet, column);
    }

     //--AgentGen END
  }

  class AgentppTestSharedEntryRowFactory
        implements MOTableRowFactory<AgentppTestSharedEntryRow>
  {
    public synchronized AgentppTestSharedEntryRow createRow(OID index, Variable[] values)
        throws UnsupportedOperationException
    {
      AgentppTestSharedEntryRow row = 
        new AgentppTestSharedEntryRow(index, values);
     //--AgentGen BEGIN=agentppTestSharedEntry::createRow
     //--AgentGen END
      return row;
    }

    public synchronized void freeRow(AgentppTestSharedEntryRow row) {
     //--AgentGen BEGIN=agentppTestSharedEntry::freeRow
     //--AgentGen END
    }

     //--AgentGen BEGIN=agentppTestSharedEntry::RowFactory
     //--AgentGen END
  }

  public class AgentppTestSessionsEntryRow extends DefaultMOMutableRow2PC {

     //--AgentGen BEGIN=agentppTestSessionsEntry::RowMembers
    private AgentXSharedMOTableSupport<AgentppTestSharedExtEntryRow> sharedTableSupport;
     //--AgentGen END

    public AgentppTestSessionsEntryRow(OID index, Variable[] values) {
      super(index, values);
     //--AgentGen BEGIN=agentppTestSessionsEntry::RowConstructor
     //--AgentGen END
    }

    public UnsignedInteger32 getAgentppTestRowCreation() {
     //--AgentGen BEGIN=agentppTestSessionsEntry::getAgentppTestRowCreation
     //--AgentGen END
      return (UnsignedInteger32) super.getValue(idxAgentppTestRowCreation);
    }

    public void setAgentppTestRowCreation(UnsignedInteger32 newValue) {
     //--AgentGen BEGIN=agentppTestSessionsEntry::setAgentppTestRowCreation
     //--AgentGen END
      super.setValue(idxAgentppTestRowCreation, newValue);
    }
    
    public Variable getValue(int column) {
     //--AgentGen BEGIN=agentppTestSessionsEntry::RowGetValue
     //--AgentGen END
      switch(column) {
        case idxAgentppTestRowCreation: 
        	return getAgentppTestRowCreation();
        default:
          return super.getValue(column);
      }
    }
    
    public void setValue(int column, Variable value) {
     //--AgentGen BEGIN=agentppTestSessionsEntry::RowSetValue
     //--AgentGen END
      switch(column) {
        case idxAgentppTestRowCreation: 
        	setAgentppTestRowCreation((UnsignedInteger32)value);
        	break;
        default:
          super.setValue(column, value);
      }
    }

    //--AgentGen BEGIN=agentppTestSessionsEntry::Row
    public AgentXSharedMOTableSupport<AgentppTestSharedExtEntryRow> getSharedTableSupport() {
      return sharedTableSupport;
    }

    public void commitRow(SubRequest subRequest, MOTableRow changeSet) {
      int newRowIndex =
          changeSet.getValue(idxAgentppTestRowCreation).toInt();
      if (newRowIndex != 0) {
        OID newIndex = new OID(new int[] { newRowIndex});
        Variable[] values = agentppTestSharedEntry.getDefaultValues();
        values[idxAgentppTestSharedTableRowStatus] =
            new Integer32(RowStatus.active);
        values[idxAgentppTestSharedTableCreationTime] =
            DateAndTime.makeDateAndTime(new GregorianCalendar());
        values[idxAgentppTestSharedTableSession] =
            new UnsignedInteger32(getIndex().get(0));
        AgentppTestSharedEntryRow row = agentppTestSharedEntry.createRow(newIndex, values);
        if (row != null) {
          if (!agentppTestSharedEntry.addRow(row)) {
            ((UnsignedInteger32) changeSet.getValue(idxAgentppTestRowCreation)).
                setValue(0);
          }
          else {
            // register region for extension table
            getSharedTableSupport().setPriority((byte)130);
            getSharedTableSupport().registerRow(agentppTestSharedExtEntry,
                new AgentppTestSharedExtEntryRow(newIndex, new Variable[0]));
            getSharedTableSupport().setPriority(AgentXProtocol.DEFAULT_PRIORITY);
          }
        }
        else {
          ((UnsignedInteger32) changeSet.getValue(idxAgentppTestRowCreation)).
              setValue(0);
        }

      }
    }
    //--AgentGen END
  }

  class AgentppTestSessionsEntryRowFactory
        implements MOTableRowFactory<AgentppTestSessionsEntryRow>
  {
    public synchronized AgentppTestSessionsEntryRow createRow(OID index, Variable[] values)
        throws UnsupportedOperationException
    {
      AgentppTestSessionsEntryRow row = 
        new AgentppTestSessionsEntryRow(index, values);
     //--AgentGen BEGIN=agentppTestSessionsEntry::createRow
     //--AgentGen END
      return row;
    }

    public synchronized void freeRow(AgentppTestSessionsEntryRow row) {
     //--AgentGen BEGIN=agentppTestSessionsEntry::freeRow
     //--AgentGen END
    }

     //--AgentGen BEGIN=agentppTestSessionsEntry::RowFactory
     //--AgentGen END
  }

  public class AgentppTestSparseEntryRow extends DefaultMOMutableRow2PC {

     //--AgentGen BEGIN=agentppTestSparseEntry::RowMembers
     //--AgentGen END

    public AgentppTestSparseEntryRow(OID index, Variable[] values) {
      super(index, values);
     //--AgentGen BEGIN=agentppTestSparseEntry::RowConstructor
     //--AgentGen END
    }

    public Integer32 getAgentppTestSparseCol1() {
     //--AgentGen BEGIN=agentppTestSparseEntry::getAgentppTestSparseCol1
     //--AgentGen END
      return (Integer32) super.getValue(idxAgentppTestSparseCol1);
    }

    public void setAgentppTestSparseCol1(Integer32 newValue) {
     //--AgentGen BEGIN=agentppTestSparseEntry::setAgentppTestSparseCol1
     //--AgentGen END
      super.setValue(idxAgentppTestSparseCol1, newValue);
    }

    public Gauge32 getAgentppTestSparseCol2() {
     //--AgentGen BEGIN=agentppTestSparseEntry::getAgentppTestSparseCol2
     //--AgentGen END
      return (Gauge32) super.getValue(idxAgentppTestSparseCol2);
    }

    public void setAgentppTestSparseCol2(Gauge32 newValue) {
     //--AgentGen BEGIN=agentppTestSparseEntry::setAgentppTestSparseCol2
     //--AgentGen END
      super.setValue(idxAgentppTestSparseCol2, newValue);
    }

    public OctetString getAgentppTestSparseCol3() {
     //--AgentGen BEGIN=agentppTestSparseEntry::getAgentppTestSparseCol3
     //--AgentGen END
      return (OctetString) super.getValue(idxAgentppTestSparseCol3);
    }

    public void setAgentppTestSparseCol3(OctetString newValue) {
     //--AgentGen BEGIN=agentppTestSparseEntry::setAgentppTestSparseCol3
     //--AgentGen END
      super.setValue(idxAgentppTestSparseCol3, newValue);
    }

    public Integer32 getAgentppTestSparseRowStatus() {
     //--AgentGen BEGIN=agentppTestSparseEntry::getAgentppTestSparseRowStatus
     //--AgentGen END
      return (Integer32) super.getValue(idxAgentppTestSparseRowStatus);
    }

    public void setAgentppTestSparseRowStatus(Integer32 newValue) {
     //--AgentGen BEGIN=agentppTestSparseEntry::setAgentppTestSparseRowStatus
     //--AgentGen END
      super.setValue(idxAgentppTestSparseRowStatus, newValue);
    }
    
    public Variable getValue(int column) {
     //--AgentGen BEGIN=agentppTestSparseEntry::RowGetValue
     //--AgentGen END
      switch(column) {
        case idxAgentppTestSparseCol1: 
        	return getAgentppTestSparseCol1();
        case idxAgentppTestSparseCol2: 
        	return getAgentppTestSparseCol2();
        case idxAgentppTestSparseCol3: 
        	return getAgentppTestSparseCol3();
        case idxAgentppTestSparseRowStatus: 
        	return getAgentppTestSparseRowStatus();
        default:
          return super.getValue(column);
      }
    }
    
    public void setValue(int column, Variable value) {
     //--AgentGen BEGIN=agentppTestSparseEntry::RowSetValue
      if (column != idxAgentppTestSparseRowStatus) {
        if (value.equals(getValue(column))) {
          super.setValue(column, null);
          return;
        }
      }
     //--AgentGen END
      switch(column) {
        case idxAgentppTestSparseCol1: 
        	setAgentppTestSparseCol1((Integer32)value);
        	break;
        case idxAgentppTestSparseCol2: 
        	setAgentppTestSparseCol2((Gauge32)value);
        	break;
        case idxAgentppTestSparseCol3: 
        	setAgentppTestSparseCol3((OctetString)value);
        	break;
        case idxAgentppTestSparseRowStatus: 
        	setAgentppTestSparseRowStatus((Integer32)value);
        	break;
        default:
          super.setValue(column, value);
      }
    }

    //--AgentGen BEGIN=agentppTestSparseEntry::Row
     //--AgentGen END
  }

  class AgentppTestSparseEntryRowFactory
        implements MOTableRowFactory<AgentppTestSparseEntryRow>
  {
    public synchronized AgentppTestSparseEntryRow createRow(OID index, Variable[] values)
        throws UnsupportedOperationException
    {
      AgentppTestSparseEntryRow row = 
        new AgentppTestSparseEntryRow(index, values);
     //--AgentGen BEGIN=agentppTestSparseEntry::createRow
     //--AgentGen END
      return row;
    }

    public synchronized void freeRow(AgentppTestSparseEntryRow row) {
     //--AgentGen BEGIN=agentppTestSparseEntry::freeRow
     //--AgentGen END
    }

     //--AgentGen BEGIN=agentppTestSparseEntry::RowFactory
     //--AgentGen END
  }

  public class AgentppTestSharedExtEntryRow extends DefaultMOMutableRow2PC {

     //--AgentGen BEGIN=agentppTestSharedExtEntry::RowMembers
     //--AgentGen END

    public AgentppTestSharedExtEntryRow(OID index, Variable[] values) {
      super(index, values);
     //--AgentGen BEGIN=agentppTestSharedExtEntry::RowConstructor
     //--AgentGen END
    }
    
    public Integer32 getAgentppTestSharedExtValueInt() {
     //--AgentGen BEGIN=agentppTestSharedExtEntry::getAgentppTestSharedExtValueInt
     //--AgentGen END
      return (Integer32) super.getValue(idxAgentppTestSharedExtValueInt);
    }  
    
    public void setAgentppTestSharedExtValueInt(Integer32 newValue) {
     //--AgentGen BEGIN=agentppTestSharedExtEntry::setAgentppTestSharedExtValueInt
     //--AgentGen END
      super.setValue(idxAgentppTestSharedExtValueInt, newValue);
    }
    
    public OctetString getAgentppTestSharedExtValueString() {
     //--AgentGen BEGIN=agentppTestSharedExtEntry::getAgentppTestSharedExtValueString
     //--AgentGen END
      return (OctetString) super.getValue(idxAgentppTestSharedExtValueString);
    }  
    
    public void setAgentppTestSharedExtValueString(OctetString newValue) {
     //--AgentGen BEGIN=agentppTestSharedExtEntry::setAgentppTestSharedExtValueString
     //--AgentGen END
      super.setValue(idxAgentppTestSharedExtValueString, newValue);
    }
    
    public Integer32 getAgentppTestSharedExtTableRowStatus() {
     //--AgentGen BEGIN=agentppTestSharedExtEntry::getAgentppTestSharedExtTableRowStatus
     //--AgentGen END
      return (Integer32) super.getValue(idxAgentppTestSharedExtTableRowStatus);
    }  
    
    public void setAgentppTestSharedExtTableRowStatus(Integer32 newValue) {
     //--AgentGen BEGIN=agentppTestSharedExtEntry::setAgentppTestSharedExtTableRowStatus
     //--AgentGen END
      super.setValue(idxAgentppTestSharedExtTableRowStatus, newValue);
    }
    
    public Variable getValue(int column) {
     //--AgentGen BEGIN=agentppTestSharedExtEntry::RowGetValue
     //--AgentGen END
      switch(column) {
        case idxAgentppTestSharedExtValueInt: 
        	return getAgentppTestSharedExtValueInt();
        case idxAgentppTestSharedExtValueString: 
        	return getAgentppTestSharedExtValueString();
        case idxAgentppTestSharedExtTableRowStatus: 
        	return getAgentppTestSharedExtTableRowStatus();
        default:
          return super.getValue(column);
      }
    }
    
    public void setValue(int column, Variable value) {
     //--AgentGen BEGIN=agentppTestSharedExtEntry::RowSetValue
     //--AgentGen END
      switch(column) {
        case idxAgentppTestSharedExtValueInt: 
        	setAgentppTestSharedExtValueInt((Integer32)value);
        	break;
        case idxAgentppTestSharedExtValueString: 
        	setAgentppTestSharedExtValueString((OctetString)value);
        	break;
        case idxAgentppTestSharedExtTableRowStatus: 
        	setAgentppTestSharedExtTableRowStatus((Integer32)value);
        	break;
        default:
          super.setValue(column, value);
      }
    }

     //--AgentGen BEGIN=agentppTestSharedExtEntry::Row
     //--AgentGen END
  }
  
  class AgentppTestSharedExtEntryRowFactory 
        implements MOTableRowFactory<AgentppTestSharedExtEntryRow>
  {
    public synchronized AgentppTestSharedExtEntryRow createRow(OID index, Variable[] values)
        throws UnsupportedOperationException 
    {
      AgentppTestSharedExtEntryRow row = 
        new AgentppTestSharedExtEntryRow(index, values);
     //--AgentGen BEGIN=agentppTestSharedExtEntry::createRow
     //--AgentGen END
      return row;
    }
    
    public synchronized void freeRow(AgentppTestSharedExtEntryRow row) {
     //--AgentGen BEGIN=agentppTestSharedExtEntry::freeRow
     //--AgentGen END
    }

     //--AgentGen BEGIN=agentppTestSharedExtEntry::RowFactory
     //--AgentGen END
  }

  public class AgentppTestSessionAttrEntryRow extends DefaultMOMutableRow2PC {

     //--AgentGen BEGIN=agentppTestSessionAttrEntry::RowMembers
    private AgentXSharedMOTableSupport sharedTableSupport;
     //--AgentGen END

    public AgentppTestSessionAttrEntryRow(OID index, Variable[] values) {
      super(index, values);
     //--AgentGen BEGIN=agentppTestSessionAttrEntry::RowConstructor
     //--AgentGen END
    }
    
    public Integer32 getAgentppTestSessionIndexStrategy() {
     //--AgentGen BEGIN=agentppTestSessionAttrEntry::getAgentppTestSessionIndexStrategy
      super.setValue(idxAgentppTestSessionIndexStrategy, new Integer32(sharedTableSupport.getIndexStrategy().ordinal()));
     //--AgentGen END
      return (Integer32) super.getValue(idxAgentppTestSessionIndexStrategy);
    }  
    
    public void setAgentppTestSessionIndexStrategy(Integer32 newValue) {
     //--AgentGen BEGIN=agentppTestSessionAttrEntry::setAgentppTestSessionIndexStrategy
     sharedTableSupport.setIndexStrategy(AgentXSharedMOTableSupport.IndexStrategy.values()[newValue.toInt()]);
     //--AgentGen END
      super.setValue(idxAgentppTestSessionIndexStrategy, newValue);
    }
    
    public Variable getValue(int column) {
     //--AgentGen BEGIN=agentppTestSessionAttrEntry::RowGetValue
     //--AgentGen END
      switch(column) {
        case idxAgentppTestSessionIndexStrategy: 
        	return getAgentppTestSessionIndexStrategy();
        default:
          return super.getValue(column);
      }
    }
    
    public void setValue(int column, Variable value) {
     //--AgentGen BEGIN=agentppTestSessionAttrEntry::RowSetValue
     //--AgentGen END
      switch(column) {
        case idxAgentppTestSessionIndexStrategy: 
        	setAgentppTestSessionIndexStrategy((Integer32)value);
        	break;
        default:
          super.setValue(column, value);
      }
    }

     //--AgentGen BEGIN=agentppTestSessionAttrEntry::Row
     //--AgentGen END
  }
  
  class AgentppTestSessionAttrEntryRowFactory 
        implements MOTableRowFactory<AgentppTestSessionAttrEntryRow>
  {
    public synchronized AgentppTestSessionAttrEntryRow createRow(OID index, Variable[] values)
        throws UnsupportedOperationException 
    {
      AgentppTestSessionAttrEntryRow row = 
        new AgentppTestSessionAttrEntryRow(index, values);
     //--AgentGen BEGIN=agentppTestSessionAttrEntry::createRow
     //--AgentGen END
      return row;
    }
    
    public synchronized void freeRow(AgentppTestSessionAttrEntryRow row) {
     //--AgentGen BEGIN=agentppTestSessionAttrEntry::freeRow
     //--AgentGen END
    }

     //--AgentGen BEGIN=agentppTestSessionAttrEntry::RowFactory
     //--AgentGen END
  }


//--AgentGen BEGIN=_METHODS
  public static MOFactory getSharedTableFactory() {
    return new AgentppTestMOFactory();
  }
//--AgentGen END

  // Textual Definitions of MIB module AgentppTestMib
  protected void addTCsToFactory(MOFactory moFactory) {
  }


//--AgentGen BEGIN=_TC_CLASSES_IMPORTED_MODULES_BEGIN
//--AgentGen END

  // Textual Definitions of other MIB modules
  public void addImportedTCsToFactory(MOFactory moFactory) {
    moFactory.addTextualConvention(new SnmpAdminString());
  }

  // Textual Convention SnmpAdminString from MIB module SNMP-FRAMEWORK-MIB

  public class SnmpAdminString implements TextualConvention {
  	
    public SnmpAdminString() {
    }

    public String getModuleName() {
      return TC_MODULE_SNMP_FRAMEWORK_MIB;
    }
  	
    public String getName() {
      return TC_SNMPADMINSTRING;
    }
    
    public Variable createInitialValue() {
    	Variable v = new OctetString();
      if (v instanceof AssignableFromLong) {
      	((AssignableFromLong)v).setValue(0L);
      }
    	// further modify value to comply with TC constraints here:
     //--AgentGen BEGIN=SnmpAdminString::createInitialValue
     //--AgentGen END
	    return v;
    }
  	
    public MOScalar createScalar(OID oid, MOAccess access, Variable value) {
      MOScalar scalar = moFactory.createScalar(oid, access, value);
      ValueConstraint vc = new ConstraintsImpl();
      ((ConstraintsImpl)vc).add(new Constraint(0L, 255L));
      scalar.addMOValueValidationListener(new ValueConstraintValidator(vc));                                  
     //--AgentGen BEGIN=SnmpAdminString::createScalar
     //--AgentGen END
      return scalar;
    }
  	
    public MOColumn createColumn(int columnID, int syntax, MOAccess access,
                                 Variable defaultValue, boolean mutableInService) {
      MOColumn col = moFactory.createColumn(columnID, syntax, access, 
                                            defaultValue, mutableInService);
      if (col instanceof MOMutableColumn) {
        MOMutableColumn mcol = (MOMutableColumn)col;
        ValueConstraint vc = new ConstraintsImpl();
        ((ConstraintsImpl)vc).add(new Constraint(0L, 255L));
        mcol.addMOValueValidationListener(new ValueConstraintValidator(vc));                                  
      }
     //--AgentGen BEGIN=SnmpAdminString::createColumn
     //--AgentGen END
      return col;      
    }
  }


//--AgentGen BEGIN=_TC_CLASSES_IMPORTED_MODULES_END
//--AgentGen END

//--AgentGen BEGIN=_CLASSES
  @SuppressWarnings("unchecked")
  static class AgentppTestMOFactory extends DefaultMOFactory {
    public MOTable createTable(OID oid, MOTableIndex indexDef,
                               MOColumn[] columns) {
      if (oidAgentppTestSessionsEntry.equals(oid)) {
        return new DefaultAgentXSharedMOTable(oid, indexDef, columns) {
          public void setAgentXSharedMOTableSupport(AgentXSharedMOTableSupport
              sharedTableSupport) {
            super.setAgentXSharedMOTableSupport(sharedTableSupport);
            ((MOMutableTableModel)model).clear();
            OID index =
                new OID(new int[] { sharedTableSupport.getSession().getSessionID() });

            AgentppTestSessionsEntryRow row =
                ((MOMutableTableModel<AgentppTestSessionsEntryRow>) model).createRow(index, getDefaultValues());
            if (row != null) {
              row.sharedTableSupport = sharedTableSupport;
              addRow(row);
            }
          }
        };
      }
      else if (oidAgentppTestSessionAttrEntry.equals(oid)) {
        return new DefaultAgentXSharedMOTable(oid, indexDef, columns) {
          public void setAgentXSharedMOTableSupport(AgentXSharedMOTableSupport
                                                        sharedTableSupport) {
            super.setAgentXSharedMOTableSupport(sharedTableSupport);
            ((MOMutableTableModel)model).clear();
            OID index =
                new OID(new int[] { sharedTableSupport.getSession().getSessionID() });

            AgentppTestSessionAttrEntryRow row =
                ((MOMutableTableModel<AgentppTestSessionAttrEntryRow>) model).createRow(index, getDefaultValues());
            if (row != null) {
              row.sharedTableSupport = sharedTableSupport;
              addRow(row);
            }
          }
        };
      }
      return new DefaultAgentXSharedMOTable(oid, indexDef, columns);
    }

    public MOTable createTable(OID oid, MOTableIndex indexDef, MOColumn[] columns,
                               MOTableModel model) {
      DefaultAgentXSharedMOTable table =
          (DefaultAgentXSharedMOTable) createTable(oid, indexDef, columns);
      table.setModel(model);
      return table;
    }

  }
//--AgentGen END

//--AgentGen BEGIN=_END
//--AgentGen END
}


