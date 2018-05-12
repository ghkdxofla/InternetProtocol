/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JvmManagementMib.java  
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

package org.snmp4j.agent.mo.jmx.mibs;

//--AgentGen BEGIN=_BEGIN
//--AgentGen END

import org.snmp4j.smi.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.*;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.agent.mo.snmp.smi.*;
import org.snmp4j.agent.request.*;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;

import org.snmp4j.agent.mo.jmx.*;

//--AgentGen BEGIN=_IMPORT
//--AgentGen END

public class JvmManagementMib
//--AgentGen BEGIN=_EXTENDS
//--AgentGen END
implements MOGroup
//--AgentGen BEGIN=_IMPLEMENTS
//--AgentGen END
{

  private static final LogAdapter LOGGER =
      LogFactory.getLogger(JvmManagementMib.class);

//--AgentGen BEGIN=_STATIC
//--AgentGen END

  // Factory
  private MOFactory moFactory =
    DefaultMOFactory.getInstance();

  // Constants
  public static final OID oidJvmClassesLoadedCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,1,1,0 });
  public static final OID oidJvmClassesTotalLoadedCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,1,2,0 });
  public static final OID oidJvmClassesUnloadedCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,1,3,0 });
  public static final OID oidJvmClassesVerboseLevel =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,1,4,0 });
  public static final OID oidJvmMemoryPendingFinalCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,1,0 });
  public static final OID oidJvmMemoryGCVerboseLevel =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,2,0 });
  public static final OID oidJvmMemoryGCCall =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,3,0 });
  public static final OID oidJvmMemoryHeapInitSize =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,10,0 });
  public static final OID oidJvmMemoryHeapUsed =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,11,0 });
  public static final OID oidJvmMemoryHeapCommitted =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,12,0 });
  public static final OID oidJvmMemoryHeapMaxSize =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,13,0 });
  public static final OID oidJvmMemoryNonHeapInitSize =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,20,0 });
  public static final OID oidJvmMemoryNonHeapUsed =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,21,0 });
  public static final OID oidJvmMemoryNonHeapCommitted =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,22,0 });
  public static final OID oidJvmMemoryNonHeapMaxSize =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,23,0 });
  public static final OID oidJvmThreadCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,3,1,0 });
  public static final OID oidJvmThreadDaemonCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,3,2,0 });
  public static final OID oidJvmThreadPeakCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,3,3,0 });
  public static final OID oidJvmThreadTotalStartedCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,3,4,0 });
  public static final OID oidJvmThreadContentionMonitoring =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,3,5,0 });
  public static final OID oidJvmThreadCpuTimeMonitoring =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,3,6,0 });
  public static final OID oidJvmThreadPeakCountReset =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,3,7,0 });
  public static final OID oidJvmRTName =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,1,0 });
  public static final OID oidJvmRTVMName =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,2,0 });
  public static final OID oidJvmRTVMVendor =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,3,0 });
  public static final OID oidJvmRTVMVersion =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,4,0 });
  public static final OID oidJvmRTSpecName =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,5,0 });
  public static final OID oidJvmRTSpecVendor =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,6,0 });
  public static final OID oidJvmRTSpecVersion =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,7,0 });
  public static final OID oidJvmRTManagementSpecVersion =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,8,0 });
  public static final OID oidJvmRTBootClassPathSupport =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,9,0 });
  public static final OID oidJvmRTInputArgsCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,10,0 });
  public static final OID oidJvmRTUptimeMs =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,11,0 });
  public static final OID oidJvmRTStartTimeMs =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,12,0 });
  public static final OID oidJvmJITCompilerName =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,5,1,0 });
  public static final OID oidJvmJITCompilerTimeMs =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,5,2,0 });
  public static final OID oidJvmJITCompilerTimeMonitoring =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,5,3,0 });
  public static final OID oidJvmOSName =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,6,1,0 });
  public static final OID oidJvmOSArch =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,6,2,0 });
  public static final OID oidJvmOSVersion =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,6,3,0 });
  public static final OID oidJvmOSProcessorCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,6,4,0 });
  public static final OID oidJvmLowMemoryPoolUsageNotif =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,2,2,1,0,1 });
  public static final OID oidTrapVarJvmMemPoolName =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,110,1,2 });
  public static final OID oidTrapVarJvmMemPoolUsed =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,110,1,11 });
  public static final OID oidTrapVarJvmMemPoolThreshdCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,110,1,111 });

  public static final OID oidJvmLowMemoryPoolCollectNotif =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,2,2,1,0,2 });
  public static final OID oidTrapVarJvmMemPoolCollectUsed =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,110,1,31 });
  public static final OID oidTrapVarJvmMemPoolCollectThreshdCount =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,110,1,132 });


  // Enumerations
  public static final class JvmClassesVerboseLevelEnum {
    public static final int silent = 1;
    public static final int verbose = 2;
  }
  public static final class JvmMemoryGCVerboseLevelEnum {
    public static final int silent = 1;
    public static final int verbose = 2;
  }
  public static final class JvmMemoryGCCallEnum {
    public static final int unsupported = 1;
    public static final int supported = 2;
    public static final int start = 3;
    public static final int started = 4;
    public static final int failed = 5;
  }
  public static final class JvmThreadContentionMonitoringEnum {
    public static final int unsupported = 1;
    public static final int enabled = 3;
    public static final int disabled = 4;
  }
  public static final class JvmThreadCpuTimeMonitoringEnum {
    public static final int unsupported = 1;
    public static final int enabled = 3;
    public static final int disabled = 4;
  }
  public static final class JvmRTBootClassPathSupportEnum {
    public static final int unsupported = 1;
    public static final int supported = 2;
  }
  public static final class JvmJITCompilerTimeMonitoringEnum {
    public static final int unsupported = 1;
    public static final int supported = 2;
  }


  // TextualConventions
  private static final String TC_MODULE_SNMPV2_TC = "SNMPv2-TC";
  private static final String TC_MODULE_JVM_MANAGEMENT_MIB = "JVM-MANAGEMENT-MIB";
  private static final String TC_DISPLAYSTRING = "DisplayString";
  private static final String TC_JVMJAVAOBJECTNAMETC = "JvmJavaObjectNameTC";
  private static final String TC_JVMIMPLSUPPORTSTATETC = "JvmImplSupportStateTC";
  private static final String TC_JVMUNSIGNED64TC = "JvmUnsigned64TC";
  private static final String TC_JVMPOSITIVE32TC = "JvmPositive32TC";
  private static final String TC_JVMIMPLOPTFEATURESTATETC = "JvmImplOptFeatureStateTC";
  private static final String TC_JVMVERBOSELEVELTC = "JvmVerboseLevelTC";
  private static final String TC_JVMTIMEMILLIS64TC = "JvmTimeMillis64TC";

  // Scalars
  private MOScalar jvmClassesLoadedCount;
  private MOScalar jvmClassesTotalLoadedCount;
  private MOScalar jvmClassesUnloadedCount;
  private MOScalar jvmClassesVerboseLevel;
  private MOScalar jvmMemoryPendingFinalCount;
  private MOScalar jvmMemoryGCVerboseLevel;
  private MOScalar jvmMemoryGCCall;
  private MOScalar jvmMemoryHeapInitSize;
  private MOScalar jvmMemoryHeapUsed;
  private MOScalar jvmMemoryHeapCommitted;
  private MOScalar jvmMemoryHeapMaxSize;
  private MOScalar jvmMemoryNonHeapInitSize;
  private MOScalar jvmMemoryNonHeapUsed;
  private MOScalar jvmMemoryNonHeapCommitted;
  private MOScalar jvmMemoryNonHeapMaxSize;
  private MOScalar jvmThreadCount;
  private MOScalar jvmThreadDaemonCount;
  private MOScalar jvmThreadPeakCount;
  private MOScalar jvmThreadTotalStartedCount;
  private MOScalar jvmThreadContentionMonitoring;
  private MOScalar jvmThreadCpuTimeMonitoring;
  private MOScalar jvmThreadPeakCountReset;
  private MOScalar jvmRTName;
  private MOScalar jvmRTVMName;
  private MOScalar jvmRTVMVendor;
  private MOScalar jvmRTVMVersion;
  private MOScalar jvmRTSpecName;
  private MOScalar jvmRTSpecVendor;
  private MOScalar jvmRTSpecVersion;
  private MOScalar jvmRTManagementSpecVersion;
  private MOScalar jvmRTBootClassPathSupport;
  private MOScalar jvmRTInputArgsCount;
  private MOScalar jvmRTUptimeMs;
  private MOScalar jvmRTStartTimeMs;
  private MOScalar jvmJITCompilerName;
  private MOScalar jvmJITCompilerTimeMs;
  private MOScalar jvmJITCompilerTimeMonitoring;
  private MOScalar jvmOSName;
  private MOScalar jvmOSArch;
  private MOScalar jvmOSVersion;
  private MOScalar jvmOSProcessorCount;

  // Tables
  public static final OID oidJvmMemManagerEntry =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,100,1 });

  // Column sub-identifer defintions for jvmMemManagerEntry:
  public static final int colJvmMemManagerName = 2;
  public static final int colJvmMemManagerState = 3;

  // Column index defintions for jvmMemManagerEntry:
  public static final int idxJvmMemManagerName = 0;
  public static final int idxJvmMemManagerState = 1;

  private MOTableSubIndex[] jvmMemManagerEntryIndexes;
  private MOTableIndex jvmMemManagerEntryIndex;

  private MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>>
      jvmMemManagerEntry;
  private MOTableModel<DefaultMOMutableRow2PC> jvmMemManagerEntryModel;
  public static final OID oidJvmMemGCEntry =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,101,1 });

  // Column sub-identifier definitions for jvmMemGCEntry:
  public static final int colJvmMemGCCount = 2;
  public static final int colJvmMemGCTimeMs = 3;

  // Column index definitions for jvmMemGCEntry:
  public static final int idxJvmMemGCCount = 0;
  public static final int idxJvmMemGCTimeMs = 1;

  private MOTableSubIndex[] jvmMemGCEntryIndexes;
  private MOTableIndex jvmMemGCEntryIndex;

  private MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>>
      jvmMemGCEntry;
  private MOTableModel<DefaultMOMutableRow2PC> jvmMemGCEntryModel;
  public static final OID oidJvmMemPoolEntry =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,110,1 });

  // Column sub-identifer defintions for jvmMemPoolEntry:
  public static final int colJvmMemPoolName = 2;
  public static final int colJvmMemPoolType = 3;
  public static final int colJvmMemPoolState = 4;
  public static final int colJvmMemPoolPeakReset = 5;
  public static final int colJvmMemPoolInitSize = 10;
  public static final int colJvmMemPoolUsed = 11;
  public static final int colJvmMemPoolCommitted = 12;
  public static final int colJvmMemPoolMaxSize = 13;
  public static final int colJvmMemPoolPeakUsed = 21;
  public static final int colJvmMemPoolPeakCommitted = 22;
  public static final int colJvmMemPoolPeakMaxSize = 23;
  public static final int colJvmMemPoolCollectUsed = 31;
  public static final int colJvmMemPoolCollectCommitted = 32;
  public static final int colJvmMemPoolCollectMaxSize = 33;
  public static final int colJvmMemPoolThreshold = 110;
  public static final int colJvmMemPoolThreshdCount = 111;
  public static final int colJvmMemPoolThreshdSupport = 112;
  public static final int colJvmMemPoolCollectThreshold = 131;
  public static final int colJvmMemPoolCollectThreshdCount = 132;
  public static final int colJvmMemPoolCollectThreshdSupport = 133;

  // Column index defintions for jvmMemPoolEntry:
  public static final int idxJvmMemPoolName = 0;
  public static final int idxJvmMemPoolType = 1;
  public static final int idxJvmMemPoolState = 2;
  public static final int idxJvmMemPoolPeakReset = 3;
  public static final int idxJvmMemPoolInitSize = 4;
  public static final int idxJvmMemPoolUsed = 5;
  public static final int idxJvmMemPoolCommitted = 6;
  public static final int idxJvmMemPoolMaxSize = 7;
  public static final int idxJvmMemPoolPeakUsed = 8;
  public static final int idxJvmMemPoolPeakCommitted = 9;
  public static final int idxJvmMemPoolPeakMaxSize = 10;
  public static final int idxJvmMemPoolCollectUsed = 11;
  public static final int idxJvmMemPoolCollectCommitted = 12;
  public static final int idxJvmMemPoolCollectMaxSize = 13;
  public static final int idxJvmMemPoolThreshold = 14;
  public static final int idxJvmMemPoolThreshdCount = 15;
  public static final int idxJvmMemPoolThreshdSupport = 16;
  public static final int idxJvmMemPoolCollectThreshold = 17;
  public static final int idxJvmMemPoolCollectThreshdCount = 18;
  public static final int idxJvmMemPoolCollectThreshdSupport = 19;

  private MOTableSubIndex[] jvmMemPoolEntryIndexes;
  private MOTableIndex jvmMemPoolEntryIndex;

  private MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>>
      jvmMemPoolEntry;
  private MOTableModel<DefaultMOMutableRow2PC> jvmMemPoolEntryModel;
  public static final OID oidJvmMemMgrPoolRelEntry =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,2,120,1 });

  // Column sub-identifier definitions for jvmMemMgrPoolRelEntry:
  public static final int colJvmMemMgrRelManagerName = 2;
  public static final int colJvmMemMgrRelPoolName = 3;

  // Column index definitions for jvmMemMgrPoolRelEntry:
  public static final int idxJvmMemMgrRelManagerName = 0;
  public static final int idxJvmMemMgrRelPoolName = 1;

  private MOTableSubIndex[] jvmMemMgrPoolRelEntryIndexes;
  private MOTableIndex jvmMemMgrPoolRelEntryIndex;

  private MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>>
      jvmMemMgrPoolRelEntry;
  private MOTableModel<DefaultMOMutableRow2PC> jvmMemMgrPoolRelEntryModel;
  public static final OID oidJvmThreadInstanceEntry =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,3,10,1 });

  // Column sub-identifier definitions for jvmThreadInstanceEntry:
  public static final int colJvmThreadInstId = 2;
  public static final int colJvmThreadInstState = 3;
  public static final int colJvmThreadInstBlockCount = 4;
  public static final int colJvmThreadInstBlockTimeMs = 5;
  public static final int colJvmThreadInstWaitCount = 6;
  public static final int colJvmThreadInstWaitTimeMs = 7;
  public static final int colJvmThreadInstCpuTimeNs = 8;
  public static final int colJvmThreadInstName = 9;
  public static final int colJvmThreadInstLockName = 10;
  public static final int colJvmThreadInstLockOwnerPtr = 11;

  // Column index definitions for jvmThreadInstanceEntry:
  public static final int idxJvmThreadInstId = 0;
  public static final int idxJvmThreadInstState = 1;
  public static final int idxJvmThreadInstBlockCount = 2;
  public static final int idxJvmThreadInstBlockTimeMs = 3;
  public static final int idxJvmThreadInstWaitCount = 4;
  public static final int idxJvmThreadInstWaitTimeMs = 5;
  public static final int idxJvmThreadInstCpuTimeNs = 6;
  public static final int idxJvmThreadInstName = 7;
  public static final int idxJvmThreadInstLockName = 8;
  public static final int idxJvmThreadInstLockOwnerPtr = 9;

  private MOTableSubIndex[] jvmThreadInstanceEntryIndexes;
  private MOTableIndex jvmThreadInstanceEntryIndex;

  private MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>>
      jvmThreadInstanceEntry;
  private MOTableModel<DefaultMOMutableRow2PC> jvmThreadInstanceEntryModel;
  public static final OID oidJvmRTInputArgsEntry =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,20,1 });

  // Column sub-identifier definitions for jvmRTInputArgsEntry:
  public static final int colJvmRTInputArgsItem = 2;

  // Column index definitions for jvmRTInputArgsEntry:
  public static final int idxJvmRTInputArgsItem = 0;

  private MOTableSubIndex[] jvmRTInputArgsEntryIndexes;
  private MOTableIndex jvmRTInputArgsEntryIndex;

  private MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>>
      jvmRTInputArgsEntry;
  private MOTableModel<DefaultMOMutableRow2PC> jvmRTInputArgsEntryModel;
  public static final OID oidJvmRTBootClassPathEntry =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,21,1 });

  // Column sub-identifier definitions for jvmRTBootClassPathEntry:
  public static final int colJvmRTBootClassPathItem = 2;

  // Column index definitions for jvmRTBootClassPathEntry:
  public static final int idxJvmRTBootClassPathItem = 0;

  private MOTableSubIndex[] jvmRTBootClassPathEntryIndexes;
  private MOTableIndex jvmRTBootClassPathEntryIndex;

  private MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>>
      jvmRTBootClassPathEntry;
  private MOTableModel<DefaultMOMutableRow2PC> jvmRTBootClassPathEntryModel;
  public static final OID oidJvmRTClassPathEntry =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,22,1 });

  // Column sub-identifier definitions for jvmRTClassPathEntry:
  public static final int colJvmRTClassPathItem = 2;

  // Column index definitions for jvmRTClassPathEntry:
  public static final int idxJvmRTClassPathItem = 0;

  private MOTableSubIndex[] jvmRTClassPathEntryIndexes;
  private MOTableIndex jvmRTClassPathEntryIndex;

  private MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>>
      jvmRTClassPathEntry;
  private MOTableModel<DefaultMOMutableRow2PC> jvmRTClassPathEntryModel;
  public static final OID oidJvmRTLibraryPathEntry =
    new OID(new int[] { 1,3,6,1,4,1,42,2,145,3,163,1,1,4,23,1 });

  // Column sub-identifier definitions for jvmRTLibraryPathEntry:
  public static final int colJvmRTLibraryPathItem = 2;

  // Column index definitions for jvmRTLibraryPathEntry:
  public static final int idxJvmRTLibraryPathItem = 0;

  private MOTableSubIndex[] jvmRTLibraryPathEntryIndexes;
  private MOTableIndex jvmRTLibraryPathEntryIndex;

  private MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>>
      jvmRTLibraryPathEntry;
  private MOTableModel<DefaultMOMutableRow2PC> jvmRTLibraryPathEntryModel;


//--AgentGen BEGIN=_MEMBERS
//--AgentGen END

  /**
   * Constructs a JvmManagementMib instance without actually creating its
   * <code>ManagedObject</code> instances. This has to be done in a
   * sub-class constructor or after construction by calling
   * {@link #createMO(MOFactory moFactory)}.
   */
  protected JvmManagementMib() {
//--AgentGen BEGIN=_DEFAULTCONSTRUCTOR
//--AgentGen END
  }

  /**
   * Constructs a JvmManagementMib instance and actually creates its
   * <code>ManagedObject</code> instances using the supplied
   * <code>MOFactory</code> (by calling
   * {@link #createMO(MOFactory moFactory)}).
   * @param moFactory
   *    the <code>MOFactory</code> to be used to create the
   *    managed objects for this module.
   */
  public JvmManagementMib(MOFactory moFactory) {
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
    jvmClassesLoadedCount =
      moFactory.createScalar(oidJvmClassesLoadedCount,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Gauge32());
    jvmClassesTotalLoadedCount =
      moFactory.createScalar(oidJvmClassesTotalLoadedCount,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64());
    jvmClassesUnloadedCount =
      moFactory.createScalar(oidJvmClassesUnloadedCount,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64());
    jvmClassesVerboseLevel =
      moFactory.createScalar(oidJvmClassesVerboseLevel,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
                             new Integer32(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMVERBOSELEVELTC);
    ValueConstraint jvmClassesVerboseLevelVC = new EnumerationConstraint(
      new int[] { JvmClassesVerboseLevelEnum.silent,
                  JvmClassesVerboseLevelEnum.verbose });
    jvmClassesVerboseLevel.
      addMOValueValidationListener(new ValueConstraintValidator(jvmClassesVerboseLevelVC));
    jvmMemoryPendingFinalCount =
      moFactory.createScalar(oidJvmMemoryPendingFinalCount,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Gauge32());
    jvmMemoryGCVerboseLevel =
      moFactory.createScalar(oidJvmMemoryGCVerboseLevel,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
                             new Integer32(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMVERBOSELEVELTC);
    ValueConstraint jvmMemoryGCVerboseLevelVC = new EnumerationConstraint(
      new int[] { JvmMemoryGCVerboseLevelEnum.silent,
                  JvmMemoryGCVerboseLevelEnum.verbose });
    jvmMemoryGCVerboseLevel.
      addMOValueValidationListener(new ValueConstraintValidator(jvmMemoryGCVerboseLevelVC));
    jvmMemoryGCCall =
      moFactory.createScalar(oidJvmMemoryGCCall,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
                             new Integer32());
    ValueConstraint jvmMemoryGCCallVC = new EnumerationConstraint(
      new int[] { JvmMemoryGCCallEnum.unsupported,
                  JvmMemoryGCCallEnum.supported,
                  JvmMemoryGCCallEnum.start,
                  JvmMemoryGCCallEnum.started,
                  JvmMemoryGCCallEnum.failed });
    jvmMemoryGCCall.
      addMOValueValidationListener(new ValueConstraintValidator(jvmMemoryGCCallVC));
    jvmMemoryHeapInitSize =
      moFactory.createScalar(oidJvmMemoryHeapInitSize,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMUNSIGNED64TC);
    jvmMemoryHeapUsed =
      moFactory.createScalar(oidJvmMemoryHeapUsed,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMUNSIGNED64TC);
    jvmMemoryHeapCommitted =
      moFactory.createScalar(oidJvmMemoryHeapCommitted,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMUNSIGNED64TC);
    jvmMemoryHeapMaxSize =
      moFactory.createScalar(oidJvmMemoryHeapMaxSize,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMUNSIGNED64TC);
    jvmMemoryNonHeapInitSize =
      moFactory.createScalar(oidJvmMemoryNonHeapInitSize,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMUNSIGNED64TC);
    jvmMemoryNonHeapUsed =
      moFactory.createScalar(oidJvmMemoryNonHeapUsed,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMUNSIGNED64TC);
    jvmMemoryNonHeapCommitted =
      moFactory.createScalar(oidJvmMemoryNonHeapCommitted,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMUNSIGNED64TC);
    jvmMemoryNonHeapMaxSize =
      moFactory.createScalar(oidJvmMemoryNonHeapMaxSize,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMUNSIGNED64TC);
    jvmThreadCount =
      moFactory.createScalar(oidJvmThreadCount,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Gauge32());
    jvmThreadDaemonCount =
      moFactory.createScalar(oidJvmThreadDaemonCount,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Gauge32());
    jvmThreadPeakCount =
      moFactory.createScalar(oidJvmThreadPeakCount,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter32());
    jvmThreadTotalStartedCount =
      moFactory.createScalar(oidJvmThreadTotalStartedCount,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64());
    jvmThreadContentionMonitoring =
      moFactory.createScalar(oidJvmThreadContentionMonitoring,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
                             new Integer32(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMIMPLOPTFEATURESTATETC);
    ValueConstraint jvmThreadContentionMonitoringVC = new EnumerationConstraint(
      new int[] { JvmThreadContentionMonitoringEnum.unsupported,
                  JvmThreadContentionMonitoringEnum.enabled,
                  JvmThreadContentionMonitoringEnum.disabled });
    jvmThreadContentionMonitoring.
      addMOValueValidationListener(new ValueConstraintValidator(jvmThreadContentionMonitoringVC));
    jvmThreadCpuTimeMonitoring =
      moFactory.createScalar(oidJvmThreadCpuTimeMonitoring,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
                             new Integer32(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMIMPLOPTFEATURESTATETC);
    ValueConstraint jvmThreadCpuTimeMonitoringVC = new EnumerationConstraint(
      new int[] { JvmThreadCpuTimeMonitoringEnum.unsupported,
                  JvmThreadCpuTimeMonitoringEnum.enabled,
                  JvmThreadCpuTimeMonitoringEnum.disabled });
    jvmThreadCpuTimeMonitoring.
      addMOValueValidationListener(new ValueConstraintValidator(jvmThreadCpuTimeMonitoringVC));
    jvmThreadPeakCountReset =
      moFactory.createScalar(oidJvmThreadPeakCountReset,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMTIMEMILLIS64TC);
    jvmRTName =
      moFactory.createScalar(oidJvmRTName,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_SNMPV2_TC, TC_DISPLAYSTRING);
    jvmRTVMName =
      moFactory.createScalar(oidJvmRTVMName,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMJAVAOBJECTNAMETC);
    jvmRTVMVendor =
      moFactory.createScalar(oidJvmRTVMVendor,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_SNMPV2_TC, TC_DISPLAYSTRING);
    jvmRTVMVersion =
      moFactory.createScalar(oidJvmRTVMVersion,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_SNMPV2_TC, TC_DISPLAYSTRING);
    jvmRTSpecName =
      moFactory.createScalar(oidJvmRTSpecName,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_SNMPV2_TC, TC_DISPLAYSTRING);
    jvmRTSpecVendor =
      moFactory.createScalar(oidJvmRTSpecVendor,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_SNMPV2_TC, TC_DISPLAYSTRING);
    jvmRTSpecVersion =
      moFactory.createScalar(oidJvmRTSpecVersion,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_SNMPV2_TC, TC_DISPLAYSTRING);
    jvmRTManagementSpecVersion =
      moFactory.createScalar(oidJvmRTManagementSpecVersion,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_SNMPV2_TC, TC_DISPLAYSTRING);
    jvmRTBootClassPathSupport =
      moFactory.createScalar(oidJvmRTBootClassPathSupport,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Integer32(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMIMPLSUPPORTSTATETC);
    jvmRTInputArgsCount =
      moFactory.createScalar(oidJvmRTInputArgsCount,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Integer32(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMPOSITIVE32TC);
    jvmRTUptimeMs =
      moFactory.createScalar(oidJvmRTUptimeMs,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMTIMEMILLIS64TC);
    jvmRTStartTimeMs =
      moFactory.createScalar(oidJvmRTStartTimeMs,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMTIMEMILLIS64TC);
    jvmJITCompilerName =
      moFactory.createScalar(oidJvmJITCompilerName,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMJAVAOBJECTNAMETC);
    jvmJITCompilerTimeMs =
      moFactory.createScalar(oidJvmJITCompilerTimeMs,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Counter64(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMTIMEMILLIS64TC);
    jvmJITCompilerTimeMonitoring =
      moFactory.createScalar(oidJvmJITCompilerTimeMonitoring,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Integer32(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMIMPLSUPPORTSTATETC);
    jvmOSName =
      moFactory.createScalar(oidJvmOSName,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_JVM_MANAGEMENT_MIB, TC_JVMJAVAOBJECTNAMETC);
    jvmOSArch =
      moFactory.createScalar(oidJvmOSArch,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_SNMPV2_TC, TC_DISPLAYSTRING);
    jvmOSVersion =
      moFactory.createScalar(oidJvmOSVersion,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new OctetString(),
                             TC_MODULE_SNMPV2_TC, TC_DISPLAYSTRING);
    jvmOSProcessorCount =
      moFactory.createScalar(oidJvmOSProcessorCount,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                             new Integer32());
    createJvmMemManagerEntry(moFactory);
    createJvmMemGCEntry(moFactory);
    createJvmMemPoolEntry(moFactory);
    createJvmMemMgrPoolRelEntry(moFactory);
    createJvmThreadInstanceEntry(moFactory);
    createJvmRTInputArgsEntry(moFactory);
    createJvmRTBootClassPathEntry(moFactory);
    createJvmRTClassPathEntry(moFactory);
    createJvmRTLibraryPathEntry(moFactory);
  }


  public MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>> getJvmMemManagerEntry() {
    return jvmMemManagerEntry;
  }


  @SuppressWarnings("unchecked")
  private void createJvmMemManagerEntry(MOFactory moFactory) {
    // Index definition
    jvmMemManagerEntryIndexes =
      new MOTableSubIndex[] {
            moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    jvmMemManagerEntryIndex =
      moFactory.createIndex(jvmMemManagerEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=jvmMemManagerEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] jvmMemManagerEntryColumns = new MOColumn[2];
    jvmMemManagerEntryColumns[idxJvmMemManagerName] =
      moFactory.createColumn(colJvmMemManagerName,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemManagerEntryColumns[idxJvmMemManagerState] =
      moFactory.createColumn(colJvmMemManagerState,
                             SMIConstants.SYNTAX_INTEGER,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    // Table model
    jvmMemManagerEntryModel =
      moFactory.createTableModel(oidJvmMemManagerEntry,
                                 jvmMemManagerEntryIndex,
                                 jvmMemManagerEntryColumns);
    jvmMemManagerEntry = (MOTableJMX<DefaultMOMutableRow2PC, MOColumn, MOTableModel<DefaultMOMutableRow2PC>>)
      moFactory.createTable(oidJvmMemManagerEntry,
                            jvmMemManagerEntryIndex,
                            jvmMemManagerEntryColumns,
                            jvmMemManagerEntryModel);
  }

  public MOTable getJvmMemGCEntry() {
    return jvmMemGCEntry;
  }

  @SuppressWarnings("unchecked")
  private void createJvmMemGCEntry(MOFactory moFactory) {
    // Index definition
    jvmMemGCEntryIndexes =
      new MOTableSubIndex[] {
            moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    jvmMemGCEntryIndex =
      moFactory.createIndex(jvmMemGCEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=jvmMemGCEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] jvmMemGCEntryColumns = new MOColumn[2];
    jvmMemGCEntryColumns[idxJvmMemGCCount] =
      moFactory.createColumn(colJvmMemGCCount,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemGCEntryColumns[idxJvmMemGCTimeMs] =
      moFactory.createColumn(colJvmMemGCTimeMs,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    // Table model
    jvmMemGCEntryModel =
      moFactory.createTableModel(oidJvmMemGCEntry,
                                 jvmMemGCEntryIndex,
                                 jvmMemGCEntryColumns);
    jvmMemGCEntry = (MOTableJMX<DefaultMOMutableRow2PC, MOColumn, MOTableModel<DefaultMOMutableRow2PC>>)
        moFactory.createTable(oidJvmMemGCEntry,
                            jvmMemGCEntryIndex,
                            jvmMemGCEntryColumns,
                            jvmMemGCEntryModel);
  }

  public MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>> getJvmMemPoolEntry() {
    return jvmMemPoolEntry;
  }


  @SuppressWarnings("unchecked")
  private void createJvmMemPoolEntry(MOFactory moFactory) {
    // Index definition
    jvmMemPoolEntryIndexes =
      new MOTableSubIndex[] {
            moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    jvmMemPoolEntryIndex =
      moFactory.createIndex(jvmMemPoolEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=jvmMemPoolEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] jvmMemPoolEntryColumns = new MOColumn[20];
    jvmMemPoolEntryColumns[idxJvmMemPoolName] =
      moFactory.createColumn(colJvmMemPoolName,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolType] =
      moFactory.createColumn(colJvmMemPoolType,
                             SMIConstants.SYNTAX_INTEGER,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolState] =
      moFactory.createColumn(colJvmMemPoolState,
                             SMIConstants.SYNTAX_INTEGER,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolPeakReset] =
      new MOMutableColumn(colJvmMemPoolPeakReset,
                          SMIConstants.SYNTAX_COUNTER64,
                          moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
                          null);
    jvmMemPoolEntryColumns[idxJvmMemPoolInitSize] =
      moFactory.createColumn(colJvmMemPoolInitSize,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolUsed] =
      moFactory.createColumn(colJvmMemPoolUsed,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolCommitted] =
      moFactory.createColumn(colJvmMemPoolCommitted,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolMaxSize] =
      moFactory.createColumn(colJvmMemPoolMaxSize,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolPeakUsed] =
      moFactory.createColumn(colJvmMemPoolPeakUsed,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolPeakCommitted] =
      moFactory.createColumn(colJvmMemPoolPeakCommitted,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolPeakMaxSize] =
      moFactory.createColumn(colJvmMemPoolPeakMaxSize,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolCollectUsed] =
      moFactory.createColumn(colJvmMemPoolCollectUsed,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolCollectCommitted] =
      moFactory.createColumn(colJvmMemPoolCollectCommitted,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolCollectMaxSize] =
      moFactory.createColumn(colJvmMemPoolCollectMaxSize,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolThreshold] =
      new MOMutableColumn(colJvmMemPoolThreshold,
                          SMIConstants.SYNTAX_COUNTER64,
                          moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
                          null);
    jvmMemPoolEntryColumns[idxJvmMemPoolThreshdCount] =
      moFactory.createColumn(colJvmMemPoolThreshdCount,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolThreshdSupport] =
      moFactory.createColumn(colJvmMemPoolThreshdSupport,
                             SMIConstants.SYNTAX_INTEGER,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolCollectThreshold] =
      new MOMutableColumn(colJvmMemPoolCollectThreshold,
                          SMIConstants.SYNTAX_COUNTER64,
                          moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
                          null);
    jvmMemPoolEntryColumns[idxJvmMemPoolCollectThreshdCount] =
      moFactory.createColumn(colJvmMemPoolCollectThreshdCount,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemPoolEntryColumns[idxJvmMemPoolCollectThreshdSupport] =
      moFactory.createColumn(colJvmMemPoolCollectThreshdSupport,
                             SMIConstants.SYNTAX_INTEGER,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    // Table model
    jvmMemPoolEntryModel =
      moFactory.createTableModel(oidJvmMemPoolEntry,
                                 jvmMemPoolEntryIndex,
                                 jvmMemPoolEntryColumns);
    jvmMemPoolEntry = (MOTableJMX<DefaultMOMutableRow2PC, MOColumn, MOTableModel<DefaultMOMutableRow2PC>>)
      moFactory.createTable(oidJvmMemPoolEntry,
                            jvmMemPoolEntryIndex,
                            jvmMemPoolEntryColumns,
                            jvmMemPoolEntryModel);
  }

  public MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>> getJvmMemMgrPoolRelEntry() {
    return jvmMemMgrPoolRelEntry;
  }

  @SuppressWarnings("unchecked")
  private void createJvmMemMgrPoolRelEntry(MOFactory moFactory) {
    // Index definition
    jvmMemMgrPoolRelEntryIndexes =
      new MOTableSubIndex[] {
            moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1),
            moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    jvmMemMgrPoolRelEntryIndex =
      moFactory.createIndex(jvmMemMgrPoolRelEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=jvmMemMgrPoolRelEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] jvmMemMgrPoolRelEntryColumns = new MOColumn[2];
    jvmMemMgrPoolRelEntryColumns[idxJvmMemMgrRelManagerName] =
      moFactory.createColumn(colJvmMemMgrRelManagerName,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmMemMgrPoolRelEntryColumns[idxJvmMemMgrRelPoolName] =
      moFactory.createColumn(colJvmMemMgrRelPoolName,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    // Table model
    jvmMemMgrPoolRelEntryModel =
      moFactory.createTableModel(oidJvmMemMgrPoolRelEntry,
                                 jvmMemMgrPoolRelEntryIndex,
                                 jvmMemMgrPoolRelEntryColumns);
    jvmMemMgrPoolRelEntry = (MOTableJMX<DefaultMOMutableRow2PC, MOColumn, MOTableModel<DefaultMOMutableRow2PC>>)
        moFactory.createTable(oidJvmMemMgrPoolRelEntry,
                            jvmMemMgrPoolRelEntryIndex,
                            jvmMemMgrPoolRelEntryColumns,
                            jvmMemMgrPoolRelEntryModel);
  }

  public MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>> getJvmThreadInstanceEntry() {
    return jvmThreadInstanceEntry;
  }


  @SuppressWarnings("unchecked")
  private void createJvmThreadInstanceEntry(MOFactory moFactory) {
    // Index definition
    jvmThreadInstanceEntryIndexes =
      new MOTableSubIndex[] {
            moFactory.createSubIndex(null, SMIConstants.SYNTAX_OCTET_STRING, 8, 8)
    };

    jvmThreadInstanceEntryIndex =
      moFactory.createIndex(jvmThreadInstanceEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=jvmThreadInstanceEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] jvmThreadInstanceEntryColumns = new MOColumn[10];
    jvmThreadInstanceEntryColumns[idxJvmThreadInstId] =
      moFactory.createColumn(colJvmThreadInstId,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmThreadInstanceEntryColumns[idxJvmThreadInstState] =
      moFactory.createColumn(colJvmThreadInstState,
                             SMIConstants.SYNTAX_BITS,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmThreadInstanceEntryColumns[idxJvmThreadInstBlockCount] =
      moFactory.createColumn(colJvmThreadInstBlockCount,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmThreadInstanceEntryColumns[idxJvmThreadInstBlockTimeMs] =
      moFactory.createColumn(colJvmThreadInstBlockTimeMs,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmThreadInstanceEntryColumns[idxJvmThreadInstWaitCount] =
      moFactory.createColumn(colJvmThreadInstWaitCount,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmThreadInstanceEntryColumns[idxJvmThreadInstWaitTimeMs] =
      moFactory.createColumn(colJvmThreadInstWaitTimeMs,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmThreadInstanceEntryColumns[idxJvmThreadInstCpuTimeNs] =
      moFactory.createColumn(colJvmThreadInstCpuTimeNs,
                             SMIConstants.SYNTAX_COUNTER64,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmThreadInstanceEntryColumns[idxJvmThreadInstName] =
      moFactory.createColumn(colJvmThreadInstName,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmThreadInstanceEntryColumns[idxJvmThreadInstLockName] =
      moFactory.createColumn(colJvmThreadInstLockName,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    jvmThreadInstanceEntryColumns[idxJvmThreadInstLockOwnerPtr] =
      moFactory.createColumn(colJvmThreadInstLockOwnerPtr,
                             SMIConstants.SYNTAX_OBJECT_IDENTIFIER,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    // Table model
    jvmThreadInstanceEntryModel =
      moFactory.createTableModel(oidJvmThreadInstanceEntry,
                                 jvmThreadInstanceEntryIndex,
                                 jvmThreadInstanceEntryColumns);
    jvmThreadInstanceEntry = (MOTableJMX<DefaultMOMutableRow2PC, MOColumn, MOTableModel<DefaultMOMutableRow2PC>>)
        moFactory.createTable(oidJvmThreadInstanceEntry,
                            jvmThreadInstanceEntryIndex,
                            jvmThreadInstanceEntryColumns,
                            jvmThreadInstanceEntryModel);
  }

  public MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>> getJvmRTInputArgsEntry() {
    return jvmRTInputArgsEntry;
  }


  @SuppressWarnings("unchecked")
  private void createJvmRTInputArgsEntry(MOFactory moFactory) {
    // Index definition
    jvmRTInputArgsEntryIndexes =
      new MOTableSubIndex[] {
            moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    jvmRTInputArgsEntryIndex =
      moFactory.createIndex(jvmRTInputArgsEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=jvmRTInputArgsEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] jvmRTInputArgsEntryColumns = new MOColumn[1];
    jvmRTInputArgsEntryColumns[idxJvmRTInputArgsItem] =
      moFactory.createColumn(colJvmRTInputArgsItem,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    // Table model
    jvmRTInputArgsEntryModel =
      moFactory.createTableModel(oidJvmRTInputArgsEntry,
                                 jvmRTInputArgsEntryIndex,
                                 jvmRTInputArgsEntryColumns);
    jvmRTInputArgsEntry = (MOTableJMX<DefaultMOMutableRow2PC, MOColumn, MOTableModel<DefaultMOMutableRow2PC>>)
        moFactory.createTable(oidJvmRTInputArgsEntry,
                            jvmRTInputArgsEntryIndex,
                            jvmRTInputArgsEntryColumns,
                            jvmRTInputArgsEntryModel);
  }

  public MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>> getJvmRTBootClassPathEntry() {
    return jvmRTBootClassPathEntry;
  }


  @SuppressWarnings("unchecked")
  private void createJvmRTBootClassPathEntry(MOFactory moFactory) {
    // Index definition
    jvmRTBootClassPathEntryIndexes =
      new MOTableSubIndex[] {
            moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    jvmRTBootClassPathEntryIndex =
      moFactory.createIndex(jvmRTBootClassPathEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=jvmRTBootClassPathEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] jvmRTBootClassPathEntryColumns = new MOColumn[1];
    jvmRTBootClassPathEntryColumns[idxJvmRTBootClassPathItem] =
      moFactory.createColumn(colJvmRTBootClassPathItem,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    // Table model
    jvmRTBootClassPathEntryModel =
      moFactory.createTableModel(oidJvmRTBootClassPathEntry,
                                 jvmRTBootClassPathEntryIndex,
                                 jvmRTBootClassPathEntryColumns);
    jvmRTBootClassPathEntry = (MOTableJMX<DefaultMOMutableRow2PC, MOColumn, MOTableModel<DefaultMOMutableRow2PC>>)
        moFactory.createTable(oidJvmRTBootClassPathEntry,
                            jvmRTBootClassPathEntryIndex,
                            jvmRTBootClassPathEntryColumns,
                            jvmRTBootClassPathEntryModel);
  }

  public MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>> getJvmRTClassPathEntry() {
    return jvmRTClassPathEntry;
  }


  @SuppressWarnings("unchecked")
  private void createJvmRTClassPathEntry(MOFactory moFactory) {
    // Index definition
    jvmRTClassPathEntryIndexes =
      new MOTableSubIndex[] {
            moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    jvmRTClassPathEntryIndex =
      moFactory.createIndex(jvmRTClassPathEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=jvmRTClassPathEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] jvmRTClassPathEntryColumns = new MOColumn[1];
    jvmRTClassPathEntryColumns[idxJvmRTClassPathItem] =
      moFactory.createColumn(colJvmRTClassPathItem,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    // Table model
    jvmRTClassPathEntryModel =
      moFactory.createTableModel(oidJvmRTClassPathEntry,
                                 jvmRTClassPathEntryIndex,
                                 jvmRTClassPathEntryColumns);
    jvmRTClassPathEntry = (MOTableJMX<DefaultMOMutableRow2PC, MOColumn, MOTableModel<DefaultMOMutableRow2PC>>)
        moFactory.createTable(oidJvmRTClassPathEntry,
                            jvmRTClassPathEntryIndex,
                            jvmRTClassPathEntryColumns,
                            jvmRTClassPathEntryModel);
  }

  public MOTableJMX<DefaultMOMutableRow2PC,MOColumn,MOTableModel<DefaultMOMutableRow2PC>> getJvmRTLibraryPathEntry() {
    return jvmRTLibraryPathEntry;
  }


  @SuppressWarnings("unchecked")
  private void createJvmRTLibraryPathEntry(MOFactory moFactory) {
    // Index definition
    jvmRTLibraryPathEntryIndexes =
      new MOTableSubIndex[] {
            moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)    };

    jvmRTLibraryPathEntryIndex =
      moFactory.createIndex(jvmRTLibraryPathEntryIndexes,
                            false,
                            new MOTableIndexValidator() {
      public boolean isValidIndex(OID index) {
        boolean isValidIndex = true;
     //--AgentGen BEGIN=jvmRTLibraryPathEntry::isValidIndex
     //--AgentGen END
        return isValidIndex;
      }
    });

    // Columns
    MOColumn[] jvmRTLibraryPathEntryColumns = new MOColumn[1];
    jvmRTLibraryPathEntryColumns[idxJvmRTLibraryPathItem] =
      moFactory.createColumn(colJvmRTLibraryPathItem,
                             SMIConstants.SYNTAX_OCTET_STRING,
                             moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
    // Table model
    jvmRTLibraryPathEntryModel =
      moFactory.createTableModel(oidJvmRTLibraryPathEntry,
                                 jvmRTLibraryPathEntryIndex,
                                 jvmRTLibraryPathEntryColumns);
    jvmRTLibraryPathEntry = (MOTableJMX<DefaultMOMutableRow2PC, MOColumn, MOTableModel<DefaultMOMutableRow2PC>>)
        moFactory.createTable(oidJvmRTLibraryPathEntry,
                            jvmRTLibraryPathEntryIndex,
                            jvmRTLibraryPathEntryColumns,
                            jvmRTLibraryPathEntryModel);
  }



  public void registerMOs(MOServer server, OctetString context)
    throws DuplicateRegistrationException
  {
    // Scalar Objects
    server.register(this.jvmClassesLoadedCount, context);
    server.register(this.jvmClassesTotalLoadedCount, context);
    server.register(this.jvmClassesUnloadedCount, context);
    server.register(this.jvmClassesVerboseLevel, context);
    server.register(this.jvmMemoryPendingFinalCount, context);
    server.register(this.jvmMemoryGCVerboseLevel, context);
    server.register(this.jvmMemoryGCCall, context);
    server.register(this.jvmMemoryHeapInitSize, context);
    server.register(this.jvmMemoryHeapUsed, context);
    server.register(this.jvmMemoryHeapCommitted, context);
    server.register(this.jvmMemoryHeapMaxSize, context);
    server.register(this.jvmMemoryNonHeapInitSize, context);
    server.register(this.jvmMemoryNonHeapUsed, context);
    server.register(this.jvmMemoryNonHeapCommitted, context);
    server.register(this.jvmMemoryNonHeapMaxSize, context);
    server.register(this.jvmThreadCount, context);
    server.register(this.jvmThreadDaemonCount, context);
    server.register(this.jvmThreadPeakCount, context);
    server.register(this.jvmThreadTotalStartedCount, context);
    server.register(this.jvmThreadContentionMonitoring, context);
    server.register(this.jvmThreadCpuTimeMonitoring, context);
    server.register(this.jvmThreadPeakCountReset, context);
    server.register(this.jvmRTName, context);
    server.register(this.jvmRTVMName, context);
    server.register(this.jvmRTVMVendor, context);
    server.register(this.jvmRTVMVersion, context);
    server.register(this.jvmRTSpecName, context);
    server.register(this.jvmRTSpecVendor, context);
    server.register(this.jvmRTSpecVersion, context);
    server.register(this.jvmRTManagementSpecVersion, context);
    server.register(this.jvmRTBootClassPathSupport, context);
    server.register(this.jvmRTInputArgsCount, context);
    server.register(this.jvmRTUptimeMs, context);
    server.register(this.jvmRTStartTimeMs, context);
    server.register(this.jvmJITCompilerName, context);
    server.register(this.jvmJITCompilerTimeMs, context);
    server.register(this.jvmJITCompilerTimeMonitoring, context);
    server.register(this.jvmOSName, context);
    server.register(this.jvmOSArch, context);
    server.register(this.jvmOSVersion, context);
    server.register(this.jvmOSProcessorCount, context);
    server.register(this.jvmMemManagerEntry, context);
    server.register(this.jvmMemGCEntry, context);
    server.register(this.jvmMemPoolEntry, context);
    server.register(this.jvmMemMgrPoolRelEntry, context);
    server.register(this.jvmThreadInstanceEntry, context);
    server.register(this.jvmRTInputArgsEntry, context);
    server.register(this.jvmRTBootClassPathEntry, context);
    server.register(this.jvmRTClassPathEntry, context);
    server.register(this.jvmRTLibraryPathEntry, context);
//--AgentGen BEGIN=_registerMOs
//--AgentGen END
  }

  public void unregisterMOs(MOServer server, OctetString context) {
    // Scalar Objects
    server.unregister(this.jvmClassesLoadedCount, context);
    server.unregister(this.jvmClassesTotalLoadedCount, context);
    server.unregister(this.jvmClassesUnloadedCount, context);
    server.unregister(this.jvmClassesVerboseLevel, context);
    server.unregister(this.jvmMemoryPendingFinalCount, context);
    server.unregister(this.jvmMemoryGCVerboseLevel, context);
    server.unregister(this.jvmMemoryGCCall, context);
    server.unregister(this.jvmMemoryHeapInitSize, context);
    server.unregister(this.jvmMemoryHeapUsed, context);
    server.unregister(this.jvmMemoryHeapCommitted, context);
    server.unregister(this.jvmMemoryHeapMaxSize, context);
    server.unregister(this.jvmMemoryNonHeapInitSize, context);
    server.unregister(this.jvmMemoryNonHeapUsed, context);
    server.unregister(this.jvmMemoryNonHeapCommitted, context);
    server.unregister(this.jvmMemoryNonHeapMaxSize, context);
    server.unregister(this.jvmThreadCount, context);
    server.unregister(this.jvmThreadDaemonCount, context);
    server.unregister(this.jvmThreadPeakCount, context);
    server.unregister(this.jvmThreadTotalStartedCount, context);
    server.unregister(this.jvmThreadContentionMonitoring, context);
    server.unregister(this.jvmThreadCpuTimeMonitoring, context);
    server.unregister(this.jvmThreadPeakCountReset, context);
    server.unregister(this.jvmRTName, context);
    server.unregister(this.jvmRTVMName, context);
    server.unregister(this.jvmRTVMVendor, context);
    server.unregister(this.jvmRTVMVersion, context);
    server.unregister(this.jvmRTSpecName, context);
    server.unregister(this.jvmRTSpecVendor, context);
    server.unregister(this.jvmRTSpecVersion, context);
    server.unregister(this.jvmRTManagementSpecVersion, context);
    server.unregister(this.jvmRTBootClassPathSupport, context);
    server.unregister(this.jvmRTInputArgsCount, context);
    server.unregister(this.jvmRTUptimeMs, context);
    server.unregister(this.jvmRTStartTimeMs, context);
    server.unregister(this.jvmJITCompilerName, context);
    server.unregister(this.jvmJITCompilerTimeMs, context);
    server.unregister(this.jvmJITCompilerTimeMonitoring, context);
    server.unregister(this.jvmOSName, context);
    server.unregister(this.jvmOSArch, context);
    server.unregister(this.jvmOSVersion, context);
    server.unregister(this.jvmOSProcessorCount, context);
    server.unregister(this.jvmMemManagerEntry, context);
    server.unregister(this.jvmMemGCEntry, context);
    server.unregister(this.jvmMemPoolEntry, context);
    server.unregister(this.jvmMemMgrPoolRelEntry, context);
    server.unregister(this.jvmThreadInstanceEntry, context);
    server.unregister(this.jvmRTInputArgsEntry, context);
    server.unregister(this.jvmRTBootClassPathEntry, context);
    server.unregister(this.jvmRTClassPathEntry, context);
    server.unregister(this.jvmRTLibraryPathEntry, context);
//--AgentGen BEGIN=_unregisterMOs
//--AgentGen END
  }

  // Notifications
  public void jvmLowMemoryPoolUsageNotif(NotificationOriginator notificationOriginator,
                                         OctetString context,
                                         VariableBinding[] vbs) {
    if (vbs.length < 3) {
      throw new IllegalArgumentException("Too few notification objects: "+
                                         vbs.length+"<3");
    }
    if (!(vbs[0].getOid().startsWith(oidTrapVarJvmMemPoolName))) {
      throw new IllegalArgumentException("Variable 0 has wrong OID: "+vbs[0].getOid()+
                                         " does not start with "+oidTrapVarJvmMemPoolName);
    }
    if (!jvmMemPoolEntryIndex.isValidIndex(jvmMemPoolEntry.getIndexPart(vbs[0].getOid()))) {
      throw new IllegalArgumentException("Illegal index for variable 0 specified: "+
                                         jvmMemPoolEntry.getIndexPart(vbs[0].getOid()));
    }
    if (!(vbs[1].getOid().startsWith(oidTrapVarJvmMemPoolUsed))) {
      throw new IllegalArgumentException("Variable 1 has wrong OID: "+vbs[1].getOid()+
                                         " does not start with "+oidTrapVarJvmMemPoolUsed);
    }
    if (!jvmMemPoolEntryIndex.isValidIndex(jvmMemPoolEntry.getIndexPart(vbs[1].getOid()))) {
      throw new IllegalArgumentException("Illegal index for variable 1 specified: "+
                                         jvmMemPoolEntry.getIndexPart(vbs[1].getOid()));
    }
    if (!(vbs[2].getOid().startsWith(oidTrapVarJvmMemPoolThreshdCount))) {
      throw new IllegalArgumentException("Variable 2 has wrong OID: "+vbs[2].getOid()+
                                         " does not start with "+oidTrapVarJvmMemPoolThreshdCount);
    }
    if (!jvmMemPoolEntryIndex.isValidIndex(jvmMemPoolEntry.getIndexPart(vbs[2].getOid()))) {
      throw new IllegalArgumentException("Illegal index for variable 2 specified: "+
                                         jvmMemPoolEntry.getIndexPart(vbs[2].getOid()));
    }
    notificationOriginator.notify(context, oidJvmLowMemoryPoolUsageNotif, vbs);
  }

  public void jvmLowMemoryPoolCollectNotif(NotificationOriginator notificationOriginator,
                                           OctetString context,
                                           VariableBinding[] vbs) {
    if (vbs.length < 3) {
      throw new IllegalArgumentException("Too few notification objects: "+
                                         vbs.length+"<3");
    }
    if (!(vbs[0].getOid().startsWith(oidTrapVarJvmMemPoolName))) {
      throw new IllegalArgumentException("Variable 0 has wrong OID: "+vbs[0].getOid()+
                                         " does not start with "+oidTrapVarJvmMemPoolName);
    }
    if (!jvmMemPoolEntryIndex.isValidIndex(jvmMemPoolEntry.getIndexPart(vbs[0].getOid()))) {
      throw new IllegalArgumentException("Illegal index for variable 0 specified: "+
                                         jvmMemPoolEntry.getIndexPart(vbs[0].getOid()));
    }
    if (!(vbs[1].getOid().startsWith(oidTrapVarJvmMemPoolCollectUsed))) {
      throw new IllegalArgumentException("Variable 1 has wrong OID: "+vbs[1].getOid()+
                                         " does not start with "+oidTrapVarJvmMemPoolCollectUsed);
    }
    if (!jvmMemPoolEntryIndex.isValidIndex(jvmMemPoolEntry.getIndexPart(vbs[1].getOid()))) {
      throw new IllegalArgumentException("Illegal index for variable 1 specified: "+
                                         jvmMemPoolEntry.getIndexPart(vbs[1].getOid()));
    }
    if (!(vbs[2].getOid().startsWith(oidTrapVarJvmMemPoolCollectThreshdCount))) {
      throw new IllegalArgumentException("Variable 2 has wrong OID: "+vbs[2].getOid()+
                                         " does not start with "+oidTrapVarJvmMemPoolCollectThreshdCount);
    }
    if (!jvmMemPoolEntryIndex.isValidIndex(jvmMemPoolEntry.getIndexPart(vbs[2].getOid()))) {
      throw new IllegalArgumentException("Illegal index for variable 2 specified: "+
                                         jvmMemPoolEntry.getIndexPart(vbs[2].getOid()));
    }
    notificationOriginator.notify(context, oidJvmLowMemoryPoolCollectNotif, vbs);
  }


  // Scalars

  // Value Validators


  // Rows and Factories


//--AgentGen BEGIN=_METHODS
//--AgentGen END

//--AgentGen BEGIN=_CLASSES
//--AgentGen END

//--AgentGen BEGIN=_END
//--AgentGen END
}


