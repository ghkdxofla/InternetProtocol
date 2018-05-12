/*_############################################################################
  _## 
  _##  SNMP4J-AgentX - AgentXMib.java  
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

//--AgentGen BEGIN=_BEGIN
//--AgentGen END

import org.snmp4j.smi.*;
import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.*;
import org.snmp4j.agent.mo.snmp.smi.*;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;

//--AgentGen BEGIN=_IMPORT
import org.snmp4j.agent.agentx.AgentXProtocol;
import org.snmp4j.agent.agentx.AgentXPeer;
import org.snmp4j.agent.util.IndexGenerator;
import org.snmp4j.agent.agentx.master.AgentXMib.AgentxConnectionEntryRow;
import org.snmp4j.agent.agentx.master.AgentXMib.AgentxRegistrationEntryRow;
import org.snmp4j.agent.mo.snmp.tc.TruthValueTC;
import org.snmp4j.agent.mo.snmp.TDomainAddressFactory;
import org.snmp4j.agent.mo.snmp.TimeStampScalar;
import org.snmp4j.agent.mo.snmp.SNMPv2MIB;
import org.snmp4j.agent.mo.snmp.TDomainAddressFactoryImpl;
import org.snmp4j.agent.mo.snmp.Enumerated;
import org.snmp4j.agent.agentx.AgentXSession;
import org.snmp4j.agent.request.SubRequest;
import org.snmp4j.agent.agentx.AgentX;
//--AgentGen END

public class AgentXMib
//--AgentGen BEGIN=_EXTENDS
//--AgentGen END
implements MOGroup, AgentXMasterListener, MOTableModelListener
//--AgentGen BEGIN=_IMPLEMENTS
//--AgentGen END
{

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(AgentXMib.class);

//--AgentGen BEGIN=_STATIC
//--AgentGen END

  // Factory
  private static MOFactory moFactory = DefaultMOFactory.getInstance();

  // Constants
  static final OID oidAgentxDefaultTimeout =
    new OID(new int[] { 1,3,6,1,2,1,74,1,1,1,0 });
  static final OID oidAgentxMasterAgentXVer =
    new OID(new int[] { 1,3,6,1,2,1,74,1,1,2,0 });
  static final OID oidAgentxConnTableLastChange =
    new OID(new int[] { 1,3,6,1,2,1,74,1,2,1,0 });
  static final OID oidAgentxSessionTableLastChange =
    new OID(new int[] { 1,3,6,1,2,1,74,1,3,1,0 });
  static final OID oidAgentxRegistrationTableLastChange =
    new OID(new int[] { 1,3,6,1,2,1,74,1,4,1,0 });

  // TextualConventions
  private static final String TC_MODULE_SNMPV2_TC = "SNMPv2-TC";
  private static final String TC_TIMESTAMP = "TimeStamp";

  // Scalars
  private MOScalar<VariantVariable> agentxDefaultTimeout;
  private MOScalar<Integer32> agentxMasterAgentXVer;
  private MOScalar<TimeTicks> agentxConnTableLastChange;
  private MOScalar<TimeTicks> agentxSessionTableLastChange;
  private MOScalar<TimeTicks> agentxRegistrationTableLastChange;

  // Tables
  static final OID oidAgentxConnectionEntry =
    new OID(new int[] { 1,3,6,1,2,1,74,1,2,2,1 });

  // Column sub-identifer defintions for agentxConnectionEntry:
  static final int colAgentxConnOpenTime = 2;
  static final int colAgentxConnTransportDomain = 3;
  static final int colAgentxConnTransportAddress = 4;

  // Column index defintions for agentxConnectionEntry:
  static final int idxAgentxConnOpenTime = 0;
  static final int idxAgentxConnTransportDomain = 1;
  static final int idxAgentxConnTransportAddress = 2;

  private static final MOTableSubIndex[] agentxConnectionEntryIndexes =
    new MOTableSubIndex[] {
        moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)  };

  private static final MOTableIndex agentxConnectionEntryIndex =
      moFactory.createIndex(agentxConnectionEntryIndexes,
                            false);


  private MOTable<AgentxConnectionEntryRow,MOColumn,MOMutableTableModel<AgentxConnectionEntryRow>>
      agentxConnectionEntry;
  private MOMutableTableModel<AgentxConnectionEntryRow> agentxConnectionEntryModel;
  static final OID oidAgentxSessionEntry =
    new OID(new int[] { 1,3,6,1,2,1,74,1,3,2,1 });

  // Column sub-identifer defintions for agentxSessionEntry:
  static final int colAgentxSessionObjectID = 2;
  static final int colAgentxSessionDescr = 3;
  static final int colAgentxSessionAdminStatus = 4;
  static final int colAgentxSessionOpenTime = 5;
  static final int colAgentxSessionAgentXVer = 6;
  static final int colAgentxSessionTimeout = 7;

  // Column index defintions for agentxSessionEntry:
  static final int idxAgentxSessionObjectID = 0;
  static final int idxAgentxSessionDescr = 1;
  static final int idxAgentxSessionAdminStatus = 2;
  static final int idxAgentxSessionOpenTime = 3;
  static final int idxAgentxSessionAgentXVer = 4;
  static final int idxAgentxSessionTimeout = 5;

  private static final MOTableSubIndex[] agentxSessionEntryIndexes =
    new MOTableSubIndex[] {
        moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1),
        moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)  };

  private static final MOTableIndex agentxSessionEntryIndex =
      moFactory.createIndex(agentxSessionEntryIndexes,
                            false);


  private MOTable<AgentxSessionEntryRow,MOColumn,MOMutableTableModel<AgentxSessionEntryRow>> agentxSessionEntry;
  private MOMutableTableModel<AgentxSessionEntryRow> agentxSessionEntryModel;
  static final OID oidAgentxRegistrationEntry =
    new OID(new int[] { 1,3,6,1,2,1,74,1,4,2,1 });

  // Column sub-identifer defintions for agentxRegistrationEntry:
  static final int colAgentxRegContext = 2;
  static final int colAgentxRegStart = 3;
  static final int colAgentxRegRangeSubId = 4;
  static final int colAgentxRegUpperBound = 5;
  static final int colAgentxRegPriority = 6;
  static final int colAgentxRegTimeout = 7;
  static final int colAgentxRegInstance = 8;

  // Column index defintions for agentxRegistrationEntry:
  static final int idxAgentxRegContext = 0;
  static final int idxAgentxRegStart = 1;
  static final int idxAgentxRegRangeSubId = 2;
  static final int idxAgentxRegUpperBound = 3;
  static final int idxAgentxRegPriority = 4;
  static final int idxAgentxRegTimeout = 5;
  static final int idxAgentxRegInstance = 6;

  private static final MOTableSubIndex[] agentxRegistrationEntryIndexes =
    new MOTableSubIndex[] {
        moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1),
        moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1),
        moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)  };

  private static final MOTableIndex agentxRegistrationEntryIndex =
      moFactory.createIndex(agentxRegistrationEntryIndexes, false);


  private MOTable<AgentxRegistrationEntryRow,MOColumn,MOMutableTableModel<AgentxRegistrationEntryRow>>
      agentxRegistrationEntry;
  private MOMutableTableModel<AgentxRegistrationEntryRow> agentxRegistrationEntryModel;


//--AgentGen BEGIN=_MEMBERS

  private AgentXCommandProcessor agentXProcessor;

  private IndexGenerator connIndexGenerator =
      new IndexGenerator(new UnsignedInteger32(1));
  private IndexGenerator regEntryIndexGenerator =
      new IndexGenerator(new UnsignedInteger32(1));
  private TDomainAddressFactory addrFactory = new TDomainAddressFactoryImpl();

//--AgentGen END

  @SuppressWarnings("unchecked")
  protected AgentXMib() {
    agentxDefaultTimeout =
      moFactory.createScalar(oidAgentxDefaultTimeout,
                             MOAccessImpl.ACCESS_READ_ONLY, new VariantVariable(new Integer32()));
    agentxDefaultTimeout.setVolatile(true);
    agentxMasterAgentXVer =
      moFactory.createScalar(oidAgentxMasterAgentXVer,
                             MOAccessImpl.ACCESS_READ_ONLY, new Integer32());
    agentxMasterAgentXVer.setVolatile(true);
    agentxConnTableLastChange =
      moFactory.createScalar(oidAgentxConnTableLastChange,
                             MOAccessImpl.ACCESS_READ_ONLY, new TimeTicks(),
                             TC_MODULE_SNMPV2_TC, TC_TIMESTAMP);
    agentxConnTableLastChange.setVolatile(true);
    agentxSessionTableLastChange =
      moFactory.createScalar(oidAgentxSessionTableLastChange,
                             MOAccessImpl.ACCESS_READ_ONLY, new TimeTicks(),
                             TC_MODULE_SNMPV2_TC, TC_TIMESTAMP);
    agentxSessionTableLastChange.setVolatile(true);
    agentxRegistrationTableLastChange =
      moFactory.createScalar(oidAgentxRegistrationTableLastChange,
                             MOAccessImpl.ACCESS_READ_ONLY, new TimeTicks(),
                             TC_MODULE_SNMPV2_TC, TC_TIMESTAMP);
    agentxRegistrationTableLastChange.setVolatile(true);
    createAgentxConnectionEntry();
    createAgentxSessionEntry();
    createAgentxRegistrationEntry();
    //--AgentGen BEGIN=_DEFAULTCONSTRUCTOR
    //--AgentGen END
  }

  //--AgentGen BEGIN=_CONSTRUCTORS
  public AgentXMib(AgentXCommandProcessor agentXProcessor) {
    this();
    this.agentXProcessor = agentXProcessor;
    VariantVariableCallback agentxDefaultTimeoutCallback =
        new ReadonlyVariableCallback() {
      public void updateVariable(VariantVariable variable) {
        variable.setValue(AgentXMib.this.agentXProcessor.getDefaultTimeout());
      }
    };
    agentxDefaultTimeout.setValue(new VariantVariable(new Integer32(),
        agentxDefaultTimeoutCallback));
    agentxMasterAgentXVer.setValue(
      new Integer32(agentXProcessor.getAgentXVersion()));
    ((DefaultMOMutableTableModel)
     agentxConnectionEntryModel).addMOTableModelListener(this);
    ((DefaultMOMutableTableModel)
     agentxSessionEntryModel).addMOTableModelListener(this);
    ((DefaultMOMutableTableModel)
     agentxRegistrationEntryModel).addMOTableModelListener(this);
  }
  //--AgentGen END


  public MOTable getAgentxConnectionEntry() {
    return agentxConnectionEntry;
  }


  @SuppressWarnings("unchecked")
  private void createAgentxConnectionEntry() {
    MOColumn[] agentxConnectionEntryColumns = new MOColumn[3];
    agentxConnectionEntryColumns[idxAgentxConnOpenTime] =
      moFactory.createColumn(colAgentxConnOpenTime,
                             SMIConstants.SYNTAX_TIMETICKS,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxConnectionEntryColumns[idxAgentxConnTransportDomain] =
      moFactory.createColumn(colAgentxConnTransportDomain,
                             SMIConstants.SYNTAX_OBJECT_IDENTIFIER,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxConnectionEntryColumns[idxAgentxConnTransportAddress] =
      moFactory.createColumn(colAgentxConnTransportAddress,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             MOAccessImpl.ACCESS_READ_ONLY);

    agentxConnectionEntryModel = new DefaultMOMutableTableModel<AgentxConnectionEntryRow>();
    agentxConnectionEntryModel.setRowFactory(new AgentxConnectionEntryRowFactory());
    agentxConnectionEntry =
      moFactory.createTable(oidAgentxConnectionEntry,
                            agentxConnectionEntryIndex,
                            agentxConnectionEntryColumns,
                            agentxConnectionEntryModel);
    if (agentxConnectionEntry instanceof DefaultMOTable) {
      ((DefaultMOTable)agentxConnectionEntry).setVolatile(true);
    }
  }

  public MOTable getAgentxSessionEntry() {
    return agentxSessionEntry;
  }


  @SuppressWarnings("unchecked")
  private void createAgentxSessionEntry() {
    MOColumn[] agentxSessionEntryColumns = new MOColumn[6];
    agentxSessionEntryColumns[idxAgentxSessionObjectID] =
      moFactory.createColumn(colAgentxSessionObjectID,
                             SMIConstants.SYNTAX_OBJECT_IDENTIFIER,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxSessionEntryColumns[idxAgentxSessionDescr] =
      moFactory.createColumn(colAgentxSessionDescr,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxSessionEntryColumns[idxAgentxSessionAdminStatus] =
      new Enumerated<Integer32>(colAgentxSessionAdminStatus,
                                SMIConstants.SYNTAX_INTEGER32,
                                MOAccessImpl.ACCESS_READ_WRITE,
                                null,
                                true);
    ValueConstraint agentxSessionAdminStatusVC = new EnumerationConstraint(
                     new int[] { AgentxSessionAdminStatusEnum.up,
                                 AgentxSessionAdminStatusEnum.down });
    ((MOMutableColumn)agentxSessionEntryColumns[idxAgentxSessionAdminStatus]).
      addMOValueValidationListener(new ValueConstraintValidator(agentxSessionAdminStatusVC));
    ((MOMutableColumn)agentxSessionEntryColumns[idxAgentxSessionAdminStatus]).
      addMOValueValidationListener(new AgentxSessionAdminStatusValidator());
    agentxSessionEntryColumns[idxAgentxSessionOpenTime] =
      moFactory.createColumn(colAgentxSessionOpenTime,
                             SMIConstants.SYNTAX_TIMETICKS,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxSessionEntryColumns[idxAgentxSessionAgentXVer] =
      moFactory.createColumn(colAgentxSessionAgentXVer,
                             SMIConstants.SYNTAX_INTEGER,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxSessionEntryColumns[idxAgentxSessionTimeout] =
      moFactory.createColumn(colAgentxSessionTimeout,
                             SMIConstants.SYNTAX_INTEGER,
                             MOAccessImpl.ACCESS_READ_ONLY);

    agentxSessionEntryModel = new DefaultMOMutableTableModel<AgentxSessionEntryRow>();
    agentxSessionEntryModel.setRowFactory(new AgentxSessionEntryRowFactory());
    agentxSessionEntry =
      moFactory.createTable(oidAgentxSessionEntry,
                            agentxSessionEntryIndex,
                            agentxSessionEntryColumns,
                            agentxSessionEntryModel);
    if (agentxSessionEntry instanceof DefaultMOTable) {
      ((DefaultMOTable)agentxSessionEntry).setVolatile(true);
    }
  }

  public MOTable getAgentxRegistrationEntry() {
    return agentxRegistrationEntry;
  }

  @SuppressWarnings("unchecked")
  private void createAgentxRegistrationEntry() {
    MOColumn[] agentxRegistrationEntryColumns = new MOColumn[7];
    agentxRegistrationEntryColumns[idxAgentxRegContext] =
      moFactory.createColumn(colAgentxRegContext,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxRegistrationEntryColumns[idxAgentxRegStart] =
      moFactory.createColumn(colAgentxRegStart,
                             SMIConstants.SYNTAX_OBJECT_IDENTIFIER,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxRegistrationEntryColumns[idxAgentxRegRangeSubId] =
      moFactory.createColumn(colAgentxRegRangeSubId,
                             SMIConstants.SYNTAX_GAUGE32,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxRegistrationEntryColumns[idxAgentxRegUpperBound] =
      moFactory.createColumn(colAgentxRegUpperBound,
                             SMIConstants.SYNTAX_GAUGE32,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxRegistrationEntryColumns[idxAgentxRegPriority] =
      moFactory.createColumn(colAgentxRegPriority,
                             SMIConstants.SYNTAX_GAUGE32,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxRegistrationEntryColumns[idxAgentxRegTimeout] =
      moFactory.createColumn(colAgentxRegTimeout,
                             SMIConstants.SYNTAX_INTEGER,
                             MOAccessImpl.ACCESS_READ_ONLY);
    agentxRegistrationEntryColumns[idxAgentxRegInstance] =
      moFactory.createColumn(colAgentxRegInstance,
                             SMIConstants.SYNTAX_INTEGER,
                             MOAccessImpl.ACCESS_READ_ONLY);

    agentxRegistrationEntryModel = new DefaultMOMutableTableModel<AgentxRegistrationEntryRow>();
    agentxRegistrationEntryModel.setRowFactory(new AgentxRegistrationEntryRowFactory());
    agentxRegistrationEntry =
      moFactory.createTable(oidAgentxRegistrationEntry,
                            agentxRegistrationEntryIndex,
                            agentxRegistrationEntryColumns,
                            agentxRegistrationEntryModel);
    if (agentxRegistrationEntry instanceof DefaultMOTable) {
      ((DefaultMOTable)agentxRegistrationEntry).setVolatile(true);
    }
  }



  public void registerMOs(MOServer server, OctetString context)
    throws DuplicateRegistrationException
  {
    // Scalar Objects
    server.register(this.agentxDefaultTimeout, context);
    server.register(this.agentxMasterAgentXVer, context);
    server.register(this.agentxConnTableLastChange, context);
    server.register(this.agentxSessionTableLastChange, context);
    server.register(this.agentxRegistrationTableLastChange, context);
    server.register(this.agentxConnectionEntry, context);
    server.register(this.agentxSessionEntry, context);
    server.register(this.agentxRegistrationEntry, context);
//--AgentGen BEGIN=_registerMOs
    ((TimeStampScalar)
     agentxConnTableLastChange).setSysUpTime(SNMPv2MIB.getSysUpTime(context));
    ((TimeStampScalar)
     agentxSessionTableLastChange).setSysUpTime(SNMPv2MIB.getSysUpTime(context));
    ((TimeStampScalar)
     agentxRegistrationTableLastChange).setSysUpTime(SNMPv2MIB.getSysUpTime(context));
//--AgentGen END
  }

  public void unregisterMOs(MOServer server, OctetString context) {
    // Scalar Objects
    server.unregister(this.agentxDefaultTimeout, context);
    server.unregister(this.agentxMasterAgentXVer, context);
    server.unregister(this.agentxConnTableLastChange, context);
    server.unregister(this.agentxSessionTableLastChange, context);
    server.unregister(this.agentxRegistrationTableLastChange, context);
    server.unregister(this.agentxConnectionEntry, context);
    server.unregister(this.agentxSessionEntry, context);
    server.unregister(this.agentxRegistrationEntry, context);
//--AgentGen BEGIN=_unregisterMOs
//--AgentGen END
  }

  // Notifications

  // Scalars

  // Value Validators

  /**
   * The <code>AgentxSessionAdminStatusValidator</code> implements the value
   * validation for <code>AgentxSessionAdminStatus</code>.
   */
  static class AgentxSessionAdminStatusValidator implements MOValueValidationListener {

    public void validate(MOValueValidationEvent validationEvent) {
      Variable newValue = validationEvent.getNewValue();
     //--AgentGen BEGIN=agentxSessionAdminStatus::validate
     //--AgentGen END
    }
  }

  // Enumerations

  public static final class AgentxSessionAdminStatusEnum {
    public static final int up = 1;
    public static final int down = 2;
  }

  // Rows and Factories
  class AgentxConnectionEntryRowFactory
        implements MOTableRowFactory<AgentxConnectionEntryRow>
  {
    public synchronized AgentxConnectionEntryRow createRow(OID index, Variable[] values)
        throws UnsupportedOperationException
    {
      AgentxConnectionEntryRow row = new AgentxConnectionEntryRow(index, values);
     //--AgentGen BEGIN=agentxConnectionEntry::createRow
     //--AgentGen END
      return row;
    }

    public synchronized void freeRow(AgentxConnectionEntryRow row) {
     //--AgentGen BEGIN=agentxConnectionEntry::freeRow
     //--AgentGen END
    }
  }

  class AgentxConnectionEntryRow extends DefaultMOMutableRow2PC {
    public AgentxConnectionEntryRow(OID index, Variable[] values) {
      super(index, values);
    }

    public TimeTicks getAgentxConnOpenTime() {
      return (TimeTicks) getValue(idxAgentxConnOpenTime);
    }

    public void setAgentxConnOpenTime(TimeTicks newValue) {
      setValue(idxAgentxConnOpenTime, newValue);
    }

    public OID getAgentxConnTransportDomain() {
      return (OID) getValue(idxAgentxConnTransportDomain);
    }

    public void setAgentxConnTransportDomain(OID newValue) {
      setValue(idxAgentxConnTransportDomain, newValue);
    }

    public OctetString getAgentxConnTransportAddress() {
      return (OctetString) getValue(idxAgentxConnTransportAddress);
    }

    public void setAgentxConnTransportAddress(OctetString newValue) {
      setValue(idxAgentxConnTransportAddress, newValue);
    }


     //--AgentGen BEGIN=agentxConnectionEntry::RowFactory
     //--AgentGen END
  }

  class AgentxSessionEntryRowFactory
        implements MOTableRowFactory<AgentxSessionEntryRow>
  {
    public synchronized AgentxSessionEntryRow createRow(OID index, Variable[] values)
        throws UnsupportedOperationException
    {
      AgentxSessionEntryRow row = new AgentxSessionEntryRow(index, values);
     //--AgentGen BEGIN=agentxSessionEntry::createRow
     //--AgentGen END
      return row;
    }

    public synchronized void freeRow(AgentxSessionEntryRow row) {
     //--AgentGen BEGIN=agentxSessionEntry::freeRow
     //--AgentGen END
    }
  }

  class AgentxSessionEntryRow extends DefaultMOMutableRow2PC {

    private AgentXSession session;

    public AgentxSessionEntryRow(OID index, Variable[] values) {
      super(index, values);
    }

    public OID getAgentxSessionObjectID() {
      return (OID) getValue(idxAgentxSessionObjectID);
    }

    public void setAgentxSessionObjectID(OID newValue) {
      setValue(idxAgentxSessionObjectID, newValue);
    }

    public OctetString getAgentxSessionDescr() {
      return (OctetString) getValue(idxAgentxSessionDescr);
    }

    public void setAgentxSessionDescr(OctetString newValue) {
      setValue(idxAgentxSessionDescr, newValue);
    }

    public Integer32 getAgentxSessionAdminStatus() {
      return (Integer32) getValue(idxAgentxSessionAdminStatus);
    }

    public void setAgentxSessionAdminStatus(Integer32 newValue) {
      setValue(idxAgentxSessionAdminStatus, newValue);
    }

    public TimeTicks getAgentxSessionOpenTime() {
      return (TimeTicks) getValue(idxAgentxSessionOpenTime);
    }

    public void setAgentxSessionOpenTime(TimeTicks newValue) {
      setValue(idxAgentxSessionOpenTime, newValue);
    }

    public Integer32 getAgentxSessionAgentXVer() {
      return (Integer32) getValue(idxAgentxSessionAgentXVer);
    }

    public void setAgentxSessionAgentXVer(Integer32 newValue) {
      setValue(idxAgentxSessionAgentXVer, newValue);
    }

    public Integer32 getAgentxSessionTimeout() {
      return (Integer32) getValue(idxAgentxSessionTimeout);
    }

    public void setAgentxSessionTimeout(Integer32 newValue) {
      setValue(idxAgentxSessionTimeout, newValue);
    }


    //--AgentGen BEGIN=agentxSessionEntry::RowFactory
    public void setSession(AgentXSession session) {
      this.session = session;
    }

    public AgentXSession getSession() {
      return session;
    }

    public void commitRow(SubRequest req, MOTableRow row) {
      Integer32 adminStatus =
          (Integer32) row.getValue(idxAgentxSessionAdminStatus);
      if ((adminStatus != null) &&
          (adminStatus.getValue() == AgentxSessionAdminStatusEnum.down)) {
        agentXProcessor.closeSession((AgentXMasterSession)session,
                                     AgentXProtocol.REASON_BY_MANAGER);
      }
    }
    //--AgentGen END
  }

  class AgentxRegistrationEntryRowFactory
        implements MOTableRowFactory<AgentxRegistrationEntryRow>
  {
    public synchronized AgentxRegistrationEntryRow createRow(OID index, Variable[] values)
        throws UnsupportedOperationException
    {
      AgentxRegistrationEntryRow row = new AgentxRegistrationEntryRow(index, values);
     //--AgentGen BEGIN=agentxRegistrationEntry::createRow
     //--AgentGen END
      return row;
    }

    public synchronized void freeRow(AgentxRegistrationEntryRow row) {
     //--AgentGen BEGIN=agentxRegistrationEntry::freeRow
     //--AgentGen END
    }
  }

  class AgentxRegistrationEntryRow extends DefaultMOMutableRow2PC {
    public AgentxRegistrationEntryRow(OID index, Variable[] values) {
      super(index, values);
    }

    public OctetString getAgentxRegContext() {
      return (OctetString) getValue(idxAgentxRegContext);
    }

    public void setAgentxRegContext(OctetString newValue) {
      setValue(idxAgentxRegContext, newValue);
    }

    public OID getAgentxRegStart() {
      return (OID) getValue(idxAgentxRegStart);
    }

    public void setAgentxRegStart(OID newValue) {
      setValue(idxAgentxRegStart, newValue);
    }

    public UnsignedInteger32 getAgentxRegRangeSubId() {
      return (UnsignedInteger32) getValue(idxAgentxRegRangeSubId);
    }

    public void setAgentxRegRangeSubId(UnsignedInteger32 newValue) {
      setValue(idxAgentxRegRangeSubId, newValue);
    }

    public UnsignedInteger32 getAgentxRegUpperBound() {
      return (UnsignedInteger32) getValue(idxAgentxRegUpperBound);
    }

    public void setAgentxRegUpperBound(UnsignedInteger32 newValue) {
      setValue(idxAgentxRegUpperBound, newValue);
    }

    public UnsignedInteger32 getAgentxRegPriority() {
      return (UnsignedInteger32) getValue(idxAgentxRegPriority);
    }

    public void setAgentxRegPriority(UnsignedInteger32 newValue) {
      setValue(idxAgentxRegPriority, newValue);
    }

    public Integer32 getAgentxRegTimeout() {
      return (Integer32) getValue(idxAgentxRegTimeout);
    }

    public void setAgentxRegTimeout(Integer32 newValue) {
      setValue(idxAgentxRegTimeout, newValue);
    }

    public Integer32 getAgentxRegInstance() {
      return (Integer32) getValue(idxAgentxRegInstance);
    }

    public void setAgentxRegInstance(Integer32 newValue) {
      setValue(idxAgentxRegInstance, newValue);
    }


     //--AgentGen BEGIN=agentxRegistrationEntry::RowFactory
     //--AgentGen END
  }



//--AgentGen BEGIN=_METHODS
  public void masterChanged(AgentXMasterEvent event) {
    switch (event.getType()) {
      case AgentXMasterEvent.PEER_ADDED: {
        AgentXPeer changedPeer = (AgentXPeer) event.getChangedObject();
        Address addr = changedPeer.getAddress();
        AgentxConnectionEntryRow row = (AgentxConnectionEntryRow)
            agentxConnectionEntry.createRow(connIndexGenerator.getNextSubIndex());
        agentxConnectionEntry.addRow(row);
        row.setAgentxConnOpenTime(((TimeStampScalar)agentxConnTableLastChange).
                                  getSysUpTime().get());
        row.setAgentxConnTransportAddress(addrFactory.getAddress(addr));
        row.setAgentxConnTransportDomain(addrFactory.getTransportDomain(addr)[0]);
        changedPeer.setId(row.getIndex());
        break;
      }
      case AgentXMasterEvent.PEER_REMOVED: {
        AgentXPeer changedPeer = (AgentXPeer) event.getChangedObject();
        if (changedPeer != null) {
          OID index = (OID) changedPeer.getId();
          OID next = index.nextPeer();
          agentxConnectionEntry.removeRow(index);
          ((DefaultMOMutableTableModel) agentxSessionEntryModel).
              removeRows(index, next);
          ((DefaultMOMutableTableModel) agentxRegistrationEntryModel).
              removeRows(index, next);
        }
        break;
      }
      case AgentXMasterEvent.SESSION_ADDED: {
        AgentXMasterSession session =
            (AgentXMasterSession)event.getChangedObject();
        OID index = new OID((OID)session.getPeer().getId());
        index.append(session.getSessionID());
        AgentxSessionEntryRow row = (AgentxSessionEntryRow)
            agentxSessionEntry.createRow(index);
        row.setAgentxSessionAdminStatus(
            new Integer32(AgentxSessionAdminStatusEnum.up));
        row.setAgentxSessionAgentXVer(new Integer32(session.getAgentXVersion()));
        row.setAgentxSessionDescr(session.getDescr());
        row.setAgentxSessionObjectID(session.getOid());
        row.setAgentxSessionOpenTime(((TimeStampScalar)agentxSessionTableLastChange).
                                     getSysUpTime().get());
        row.setAgentxSessionTimeout(new Integer32(session.getTimeout() & 0xFF));
        row.setSession(session);
        agentxSessionEntry.addRow(row);
        break;
      }
      case AgentXMasterEvent.SESSION_REMOVED: {
        AgentXMasterSession session =
            (AgentXMasterSession)event.getChangedObject();
        OID index = new OID((OID)session.getPeer().getId());
        index.append(session.getSessionID());
        agentxSessionEntry.removeRow(index);
        break;
      }
      case AgentXMasterEvent.REGISTRATION_ADDED: {
        AgentXRegEntry entry = (AgentXRegEntry)event.getChangedObject();
        AgentXMasterSession session = entry.getSession();
        AgentXPeer peer = session.getPeer();
        if (peer.getId() == null) {
          LOGGER.error("Peer ID is null for peer "+peer+
                       ", cannot add registration "+
                       entry+" to AgentXMIB");
        }
        else {
          OID index = new OID((OID)peer.getId());
          index.append(session.getSessionID());
          index.append(regEntryIndexGenerator.getNextSubIndex());
          entry.setId(index);
          AgentxRegistrationEntryRow row = (AgentxRegistrationEntryRow)
              agentxRegistrationEntry.createRow(index);
          row.setAgentxRegContext(entry.getContext());
          Integer32 singleOID =
              TruthValueTC.getValue(entry.getRegion().isSingleOID());
          row.setAgentxRegInstance(singleOID);
          row.setAgentxRegPriority(new UnsignedInteger32(entry.getPriority()));
          row.setAgentxRegRangeSubId(
              new UnsignedInteger32(entry.getRegion().getRangeSubID()));
          row.setAgentxRegStart(entry.getRegion().getLowerBound());
          row.setAgentxRegTimeout(new Integer32(entry.getTimeout()));
          row.setAgentxRegUpperBound(
              new UnsignedInteger32(entry.getRegion().getUpperBoundSubID()));
          agentxRegistrationEntry.addRow(row);
        }
        break;
      }
      case AgentXMasterEvent.REGISTRATION_REMOVED: {
        AgentXRegEntry entry = (AgentXRegEntry)event.getChangedObject();
        OID index = entry.getId();
        if (index == null) {
          LOGGER.warn("Registration has been removed that was not previously "+
                      "added to the AgentXMib: "+entry);
        }
        else {
          agentxRegistrationEntry.removeRow(index);
        }
        break;
      }
      default: {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Unrecognized master change event: " + event);
        }
      }
    }
  }

  public void tableModelChanged(MOTableModelEvent changeEvent) {
    Object source = changeEvent.getSource();
    if (source.equals(agentxConnectionEntryModel)) {
      ((TimeStampScalar)agentxConnTableLastChange).update();
    }
    else if (source.equals(agentxSessionEntryModel)) {
      ((TimeStampScalar)agentxSessionTableLastChange).update();
    }
    else if (source.equals(agentxRegistrationEntryModel)) {
      ((TimeStampScalar)agentxRegistrationTableLastChange).update();
    }
  }

  //--AgentGen END

//--AgentGen BEGIN=_CLASSES
//--AgentGen END

//--AgentGen BEGIN=_END
//--AgentGen END
}


