/*_############################################################################
  _## 
  _##  SNMP4J-AgentJMX 2 - JvmManagementMibInst.java  
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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.*;
import org.snmp4j.agent.mo.jmx.*;
import org.snmp4j.agent.mo.jmx.MBeanNotificationInfo;
import org.snmp4j.agent.mo.jmx.types.*;
import org.snmp4j.agent.mo.jmx.util.AbstractSyntheticJMXIndexSupport;
import org.snmp4j.agent.mo.snmp.smi.EnumerationConstraint;
import org.snmp4j.agent.mo.snmp.smi.ValueConstraint;
import org.snmp4j.agent.mo.snmp.smi.ValueConstraintValidator;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;

/**
 * The <code>JvmManagementMibInst</code> demonstrates how an AgenPro generated
 * MIB module class can be extended to provide JMX instrumentation for it. The
 * instrumentation is done mostly descriptive. Only for value mapping some code
 * is necessary.
 *
 * @author Frank Fock
 * @version 1.0
 */
public class JvmManagementMibInst extends JvmManagementMib {

  private static final OID[] ACTION_OIDS = new OID[] {
      JvmManagementMib.oidJvmMemoryGCCall
  };

  private static final MBeanNotificationObjectInfo[] jvmLowMemoryPoolUsageNotif =
      new MBeanNotificationObjectInfo[] {
      new MBeanNotificationObjectInfo(
        new OID(JvmManagementMib.oidJvmMemPoolEntry.getValue(),
                new int[] { JvmManagementMib.colJvmMemPoolName }),
        new OctetString(), new TypedAttribute("poolName", String.class)),
      new MBeanNotificationObjectInfo(
        new OID(JvmManagementMib.oidJvmMemPoolEntry.getValue(),
                new int[] { JvmManagementMib.colJvmMemPoolUsed }),
        new Counter64(),
        new TypedCompositeDataAttribute("usage", "used", Long.class)),
      new MBeanNotificationObjectInfo(
        new OID(JvmManagementMib.oidJvmMemPoolEntry.getValue(),
                new int[] { JvmManagementMib.colJvmMemPoolThreshdCount }),
        new Counter64(), new TypedAttribute("count", Long.class))
  };

  private static final MBeanNotificationObjectInfo[] jvmLowMemoryPoolCollectNotif =
      new MBeanNotificationObjectInfo[] {
      new MBeanNotificationObjectInfo(
        new OID(JvmManagementMib.oidJvmMemPoolEntry.getValue(),
                new int[] { JvmManagementMib.colJvmMemPoolName }),
        new OctetString(), new TypedAttribute("poolName", String.class)),
      new MBeanNotificationObjectInfo(
        new OID(JvmManagementMib.oidJvmMemPoolEntry.getValue(),
                new int[] { JvmManagementMib.colJvmMemPoolCollectUsed }),
        new Counter64(),
        new TypedCompositeDataAttribute("usage", "used", Long.class)),
      new MBeanNotificationObjectInfo(
        new OID(JvmManagementMib.oidJvmMemPoolEntry.getValue(),
                new int[] { JvmManagementMib.colJvmMemPoolCollectThreshdCount }),
        new Counter64(), new TypedAttribute("count", Long.class))
  };

  private static final Object[][] SCALAR_MBEANS_JVM_MEMORY_ACTIONS = {
      { JvmManagementMib.oidJvmMemoryGCCall,
            new MBeanStateInfo[] {
            new MBeanStateInfo(2, null, null)
        },
            new MBeanActionInfo[] {
            new MBeanActionInfo(3, "gc", new Object[0])
        }
      }
  };

  private static final Object[][] SCALAR_MBEANS_JVM_MEMORY = {
     { JvmManagementMib.oidJvmMemoryPendingFinalCount,
           "ObjectPendingFinalizationCount", Long.class },
     { JvmManagementMib.oidJvmMemoryGCVerboseLevel,
       new InverseBooleanType("Verbose") },
//     { JvmManagementMib.oidJvmMemoryGCCall,   "VmVendor",      String.class },
     { JvmManagementMib.oidJvmMemoryHeapInitSize,
       new TypedCompositeDataAttribute("HeapMemoryUsage", "init", Long.class) },
     { JvmManagementMib.oidJvmMemoryHeapUsed,
       new TypedCompositeDataAttribute("HeapMemoryUsage", "used", Long.class) },
     { JvmManagementMib.oidJvmMemoryHeapCommitted,
       new TypedCompositeDataAttribute("HeapMemoryUsage", "committed", Long.class) },
     { JvmManagementMib.oidJvmMemoryHeapMaxSize,
       new TypedCompositeDataAttribute("HeapMemoryUsage", "max", Long.class) },

     { JvmManagementMib.oidJvmMemoryNonHeapInitSize,
       new TypedCompositeDataAttribute("NonHeapMemoryUsage", "init", Long.class) },
     { JvmManagementMib.oidJvmMemoryNonHeapUsed,
       new TypedCompositeDataAttribute("NonHeapMemoryUsage", "used", Long.class) },
     { JvmManagementMib.oidJvmMemoryNonHeapCommitted,
       new TypedCompositeDataAttribute("NonHeapMemoryUsage", "committed", Long.class) },
     { JvmManagementMib.oidJvmMemoryNonHeapMaxSize,
       new TypedCompositeDataAttribute("NonHeapMemoryUsage", "max", Long.class) }

  };

  private static final Object[][] SCALAR_MBEANS_JVM_CLASSES = {
     { JvmManagementMib.oidJvmClassesLoadedCount,
           "LoadedClassCount", Integer.class },
     { JvmManagementMib.oidJvmClassesTotalLoadedCount,
           "TotalLoadedClassCount", Long.class },
     { JvmManagementMib.oidJvmClassesUnloadedCount,
           "UnloadedClassCount", Long.class },
     { JvmManagementMib.oidJvmClassesVerboseLevel,
           new InverseBooleanType("Verbose")},
  };

  private static final Object[][] SCALAR_MBEANS_JVM_RUNTIME = {
     { JvmManagementMib.oidJvmRTName,         "Name",          String.class },
     { JvmManagementMib.oidJvmRTVMName,       "VmName",        String.class },
     { JvmManagementMib.oidJvmRTVMVendor,     "VmVendor",      String.class },
     { JvmManagementMib.oidJvmRTVMVersion,    "VmVersion",     String.class },
     { JvmManagementMib.oidJvmRTSpecName,     "SpecName",      String.class },
     { JvmManagementMib.oidJvmRTSpecVendor,   "SpecVendor",    String.class },
     { JvmManagementMib.oidJvmRTSpecVersion,  "SpecVersion",   String.class },
     { JvmManagementMib.oidJvmRTManagementSpecVersion, "ManagementSpecVersion",
     String.class },
     { JvmManagementMib.oidJvmRTBootClassPathSupport,
       new InverseBooleanType("BootClassPathSupported") },
     { JvmManagementMib.oidJvmRTInputArgsCount,"InputArguments", Long.class },
     { JvmManagementMib.oidJvmRTUptimeMs,     "Uptime", Long.class },
     { JvmManagementMib.oidJvmRTStartTimeMs,  "StartTime", Long.class }

  };

  private static final Object[][] SCALAR_MBEANS_JIT_COMPILER = {
     { JvmManagementMib.oidJvmJITCompilerName,  "Name", String.class },
     { JvmManagementMib.oidJvmJITCompilerTimeMs,
     "TotalCompilationTime", Long.class },
     { JvmManagementMib.oidJvmJITCompilerTimeMonitoring,
       new InverseBooleanType("CompilationTimeMonitoringSupported") }
  };

  private static final Object[][] SCALAR_MBEANS_JVM_OS = {
     { JvmManagementMib.oidJvmOSName,  "Name", String.class },
     { JvmManagementMib.oidJvmOSArch,  "Arch", String.class },
     { JvmManagementMib.oidJvmOSVersion, "Version", String.class },
     { JvmManagementMib.oidJvmOSProcessorCount, "AvailableProcessors",
     Integer.class }
  };

  private static final Object[][] SCALAR_MBEANS_JVM_THREADING = {
     { JvmManagementMib.oidJvmThreadCount,  "ThreadCount", Integer.class },
     { JvmManagementMib.oidJvmThreadDaemonCount,  "DaemonThreadCount", Integer.class },
     { JvmManagementMib.oidJvmThreadPeakCount, "PeakThreadCount", Integer.class },
     { JvmManagementMib.oidJvmThreadTotalStartedCount, "TotalStartedThreadCount",
     Integer.class },
     { JvmManagementMib.oidJvmThreadPeakCount, "PeakThreadCount", Integer.class },
     { JvmManagementMib.oidJvmThreadContentionMonitoring,
       new CombinedTypedAttribute("",
                                  Integer.class,
                                  new TypedAttribute[] {
          new Boolean2IntegerType("ThreadContentionMonitoringSupported",
                                  null, 1),
          new Boolean2IntegerType("ThreadContentionMonitoringEnabled",
                                  3, 4)})
     },
     { JvmManagementMib.oidJvmThreadCpuTimeMonitoring,
       new CombinedTypedAttribute("",
                                  Integer.class,
                                  new TypedAttribute[] {
          new Boolean2IntegerType("ThreadCpuTimeSupported",
                                  null, 1),
          new Boolean2IntegerType("ThreadCpuTimeEnabled",
                                  3, 4)})
     }
  };

  private NotificationOriginator notificationOriginator;

  public JvmManagementMibInst(NotificationOriginator notificationOriginator) {
    super();
    this.notificationOriginator = notificationOriginator;
    addJvmManagementMibInstrumentaton();
  }

  class TimeAction extends TypedAttribute {

    private Map<ObjectName, Long> values = new HashMap<ObjectName, Long>();
    private MBeanServerConnection server;
    private String action;

    public TimeAction(String name, String action,
                      MBeanServerConnection server) {
      // name of a dummy attribute that is not really used
      super(name, Long.class);
      this.server = server;
      this.action = action;
    }

    public boolean isNativeValueAlwaysNeeded() {
      return true;
    }

    public Object transformToNative(Object transformedValue,
                                    Object oldNativeValue,
                                    ObjectName objectName) {
      Long nl = (Long)transformedValue;
      Long ol = values.get(objectName);
      if ((ol == null) || (nl > ol)) {
        try {
          values.put(objectName, System.currentTimeMillis());
          server.invoke(objectName, action, null, null);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      // suppress setting of the dummy attribute we use by returning null here
      return null;
    }

    public Object transformFromNative(Object nativeValue,
                                      ObjectName objectName) {
      Long value = values.get(objectName);
      if (value == null) {
        return new Long(0);
      }
      return value;
    }

  }

  public void registerMOs(MOServer server, OctetString context) throws
      DuplicateRegistrationException {
    super.registerMOs(server, context);
    if (server instanceof DefaultMOServer) {
      addJvmManagementMibConstraints((DefaultMOServer)server);
    }
  }

  private void addJvmManagementMibInstrumentaton() {
    final MBeanServerConnection server =
        ManagementFactory.getPlatformMBeanServer();

    final MBeanAttributeMOTableSupport tableSupport =
        new MBeanAttributeMOTableSupport(server);
    final MBeanAttributeMOScalarSupport scalarSupport =
        new MBeanAttributeMOScalarSupport(server);
    final MBeanActionMOScalarSupport scalarSupportActions =
        new MBeanActionMOScalarSupport(server);
    final JMXNotificationSupport notificationSupport =
        new JMXNotificationSupport(notificationOriginator);

    JMXDefaultMOFactory jmxFactory =
        new JMXDefaultMOFactory(server, scalarSupport) {
      public <V extends Variable> MOScalar<V> createScalar(OID id, MOAccess access, V value) {
        if (Arrays.binarySearch(ACTION_OIDS, id) >= 0) {
          return new MOScalarJMX<V>(scalarSupportActions, id, access, value);
        }
        return super.createScalar(id, access, value);
      }
    };
    // create MOs with factory
    createMO(jmxFactory);

    try {
      ObjectName onameJvmMemManagerEntry =
          new ObjectName(ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE+
                         ",*");

      final AbstractSyntheticJMXIndexSupport jvmMemManagerIndexSupport =
          new AbstractSyntheticJMXIndexSupport() {
          public ObjectName mapToRowMBean(Object rowIdentifier) {
            try {
              return new ObjectName(ManagementFactory.
                                    MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE +
                                    ",name=" + rowIdentifier);
            }
            catch (Exception ex) {
              ex.printStackTrace();
              return null;
            }
          }
      };

      tableSupport.add(oidJvmMemManagerEntry,
          new MBeanAttributeMOTableInfo(onameJvmMemManagerEntry,
                                        null,
                                        new TypedAttribute[] {
                                        new TypedAttribute("Name", String.class),
                                        new InverseBooleanType("Valid")},
                                        new String[] { "Name" },
                                        jvmMemManagerIndexSupport));

      ObjectName onameJvmMem =
         new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME);
      notificationSupport.add(JvmManagementMib.oidJvmLowMemoryPoolUsageNotif,
                              new MBeanNotificationInfo(jvmLowMemoryPoolUsageNotif,
          new JMXAttributeNotificationIndexSupport(
          new TypedAttribute("poolName", String.class),
          jvmMemManagerIndexSupport)));
      try {
        server.addNotificationListener(onameJvmMem,
                                       notificationSupport, null,
                                       JvmManagementMib.
                                       oidJvmLowMemoryPoolUsageNotif);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      notificationSupport.add(JvmManagementMib.oidJvmLowMemoryPoolCollectNotif,
                              new MBeanNotificationInfo(jvmLowMemoryPoolCollectNotif,
          new JMXAttributeNotificationIndexSupport(
          new TypedAttribute("poolName", String.class),
          jvmMemManagerIndexSupport)));
      try {
        server.addNotificationListener(onameJvmMem,
                                       notificationSupport, null,
                                       JvmManagementMib.
                                       oidJvmLowMemoryPoolCollectNotif);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

      ObjectName onameJvmMemPoolEntry =
          new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE+",*");

      final AbstractSyntheticJMXIndexSupport jvmMemPoolIndexSupport =
          new AbstractSyntheticJMXIndexSupport() {
        public ObjectName mapToRowMBean(Object rowIdentifier) {
          try {
            return new ObjectName(ManagementFactory.
                                  MEMORY_POOL_MXBEAN_DOMAIN_TYPE +
                                  ",name=" + rowIdentifier);
          }
          catch (Exception ex) {
            ex.printStackTrace();
            return null;
          }
        }
      };

      tableSupport.add(oidJvmMemPoolEntry,
          new MBeanAttributeMOTableInfo(onameJvmMemPoolEntry,
          null,
          new TypedAttribute[] {
            new TypedAttribute("Name", String.class),
            new EnumStringType("Type", MemoryType.class, MemoryType.values() ),
            new InverseBooleanType("Valid"),
            new TimeAction("Name","resetPeakUsage", server),
            new TypedCompositeDataAttribute("Usage", "init", Long.class),
            new TypedCompositeDataAttribute("Usage", "used", Long.class),
            new TypedCompositeDataAttribute("Usage", "committed", Long.class),
            new TypedCompositeDataAttribute("Usage", "max", Long.class),
            new TypedCompositeDataAttribute("PeakUsage", "used", Long.class),
            new TypedCompositeDataAttribute("PeakUsage", "committed", Long.class),
            new TypedCompositeDataAttribute("PeakUsage", "max", Long.class),
            new TypedCompositeDataAttribute("CollectionUsage", "used", Long.class),
            new TypedCompositeDataAttribute("CollectionUsage", "committed", Long.class),
            new TypedCompositeDataAttribute("CollectionUsage", "max", Long.class),
            new TypedAttribute("UsageThreshold", Long.class),
            new TypedAttribute("UsageThresholdCount", Long.class),
            new InverseBooleanType("UsageThresholdSupported"),
            new TypedAttribute("CollectionUsageThreshold", Long.class),
            new TypedAttribute("CollectionUsageThresholdCount", Long.class),
            new InverseBooleanType("CollectionUsageThresholdSupported")
          },
          new String[] { "Name" },
          jvmMemPoolIndexSupport));

      ObjectName onameJvmMemMgrPoolRelEntry =
          new ObjectName(ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE+",*");

      MBeanInvokationKeyProvider jvmMemMgrPoolRelKeyProvider =
          new MBeanInvokationKeyProvider(new ObjectName(ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE),
                                         new TypedAttribute("MemoryPoolNames", String.class),
                                         "getMemoryPoolNames", true);

      tableSupport.add(oidJvmMemMgrPoolRelEntry,
          new MBeanAttributeMOTableInfo(onameJvmMemMgrPoolRelEntry,
                                        new MBeanAttributeKeyProvider(onameJvmMemMgrPoolRelEntry,
                                        null, true, jvmMemMgrPoolRelKeyProvider,
                                        new String[] { "Name" })  {
  protected ObjectName getSubKeyProviderObjectName(Object key) throws
      MalformedObjectNameException {
    return new ObjectName(ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE+
                          ",name="+key);
  }
},
          new TypedAttribute[] {
            new TypedAttribute("Name", String.class),
            new TypedAttribute("MemoryPoolName", String.class)
          },
          new String[] { "Name", "MemoryPoolName" },
          new JMXIndexSupport() {
        public ObjectName mapToRowMBean(Object rowIdentifier) {
          return null;
        }

        public Object getRowIdentifier(Object nativeRowId, int nativeIndex) {
          return nativeRowId;
        }

        public OID mapToIndex(Object rowIdentifier) {
          Object[] key = (Object[])rowIdentifier;
          OID index = new OID(jvmMemManagerIndexSupport.mapToIndex(key[0]));
          index.append(jvmMemPoolIndexSupport.mapToIndex(key[1]));
          return index;
        }

        public Object mapToRowIdentifier(OID rowIndex) {
          if (rowIndex == null) {
           return null;
         }
         Object[] rowIdentifier = new Object[2];
         rowIdentifier[0] =
             jvmMemManagerIndexSupport.mapToRowIdentifier(
          new OID(rowIndex.toIntArray(), 0, 1));
         rowIdentifier[1] =
             jvmMemPoolIndexSupport.mapToRowIdentifier(
          new OID(rowIndex.toIntArray(), 1, 1));
         return rowIdentifier;
        }
      }
      ));


      ObjectName onameJvmThreading =
          new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME);

      tableSupport.add(oidJvmThreadInstanceEntry,
          new MBeanAttributeMOTableInfo(onameJvmThreading,
          new MBeanInvokationKeyProvider(onameJvmThreading,
                                         new TypedAttribute("AllThreadIds", long.class),
                                         "getThreadInfo", true),
          new TypedAttribute[] {
          new TypedCompositeDataAttribute(new TypedAttribute("threadId", Long.class)),
          new CombinedBitsType(new TypedAttribute[] {
                               new EnumBitsType("threadState", Thread.State.class, Thread.State.values(), 3),
                               new BooleanBitsType("inNative", 1),
                               new BooleanBitsType("suspended", 2)}),
          new TypedCompositeDataAttribute(new TypedAttribute("blockedCount", Long.class)),
          new TypedCompositeDataAttribute(new TypedAttribute("blockedTime", Long.class)),
          new TypedCompositeDataAttribute(new TypedAttribute("waitedCount", Long.class)),
          new TypedCompositeDataAttribute(new TypedAttribute("waitedTime", Long.class)),
          new MBeanProxyType(server, onameJvmThreading, Long.class,
                             "getThreadUserTime",
                             new TypedCompositeDataAttribute(new TypedAttribute("threadId", long.class))) {
            public Object transformFromNative(Object nativeValue, ObjectName objectName) {
              Long result = (Long) super.transformFromNative(nativeValue, objectName);
              if ((result == null) || (result < 0)) {
                return 0L;
              }
              return result;
            }
          },
          new TypedCompositeDataAttribute(new TypedAttribute("threadName", String.class)),
          new TypedCompositeDataAttribute(new TypedAttribute("lockOwnerName", String.class)),
          new TypedCompositeDataAttribute(new TypedAttribute("lockOwnerId", Long.class)) {
            public Object transformFromNative(Object nativeValue, ObjectName objectName) {
              Long result = (Long)super.transformFromNative(nativeValue, objectName);
              if ((result == null) || (result < 0)) {
                return "0.0";
              }
              OID rowPointer = new OID(JvmManagementMib.oidJvmThreadInstanceEntry);
              rowPointer.append(JvmManagementMib.colJvmThreadInstId);
              String index = Long.toHexString(result);
              OctetString os = OctetString.fromHexString(index);
              rowPointer.append(os.toSubIndex(true));
              return rowPointer.toString();
            }
          }},
          new String[] { "ThreadId" },
          new JMXIndexSupport() {
        public ObjectName mapToRowMBean(Object rowIdentifier) {
          return null;
        }

        public Object getRowIdentifier(Object nativeRowId, int nativeIndex) {
          return nativeRowId;
        }

        public OID mapToIndex(Object rowIdentifier) {
          Long l = (Long)rowIdentifier;
          return OctetString.fromHexString(Long.toHexString(l)).toSubIndex(true);
        }

        public Object mapToRowIdentifier(OID rowIndex) {
          if (rowIndex == null) {
            return null;
          }
          OctetString os = new OctetString();
          os.fromSubIndex(rowIndex, true);
          String hexString = os.toHexString();
          return Long.parseLong(hexString, 16);
        }
      }));

      ObjectName onameJvmClasses =
          new ObjectName(ManagementFactory.CLASS_LOADING_MXBEAN_NAME);
      ObjectName onameJvmRT =
          new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME);
      ObjectName onameJitCompiler =
          new ObjectName(ManagementFactory.COMPILATION_MXBEAN_NAME);
      ObjectName onameJvmOS =
          new ObjectName(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
      ObjectName onameJvmMemory =
          new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME);

      scalarSupport.addAll(onameJvmClasses, SCALAR_MBEANS_JVM_CLASSES);
      scalarSupport.addAll(onameJvmRT, SCALAR_MBEANS_JVM_RUNTIME);
      scalarSupport.addAll(onameJitCompiler, SCALAR_MBEANS_JIT_COMPILER);
      scalarSupport.addAll(onameJvmOS, SCALAR_MBEANS_JVM_OS);
      scalarSupport.addAll(onameJvmMemory, SCALAR_MBEANS_JVM_MEMORY);
      scalarSupport.addAll(onameJvmThreading, SCALAR_MBEANS_JVM_THREADING);

      scalarSupportActions.addAll(onameJvmMemory,
                                  SCALAR_MBEANS_JVM_MEMORY_ACTIONS);

      tableSupport.add(oidJvmRTInputArgsEntry,
          new MBeanAttributeListMOTableInfo(onameJvmRT,
          new MBeanArrayIndexKeyProvider(onameJvmRT,
                                         new TypedAttribute("InputArguments",
          String[].class))));

      tableSupport.add(oidJvmRTBootClassPathEntry,
          new MBeanAttributeListMOTableInfo(onameJvmRT,
          new MBeanArrayIndexKeyProvider(onameJvmRT,
                                         new SplitStringType("BootClassPath",
          File.pathSeparator))));

      tableSupport.add(oidJvmRTClassPathEntry,
          new MBeanAttributeListMOTableInfo(onameJvmRT,
          new MBeanArrayIndexKeyProvider(onameJvmRT,
                                         new SplitStringType("ClassPath",
          File.pathSeparator))));

      tableSupport.add(oidJvmRTLibraryPathEntry,
          new MBeanAttributeListMOTableInfo(onameJvmRT,
          new MBeanArrayIndexKeyProvider(onameJvmRT,
                                         new SplitStringType("LibraryPath",
          File.pathSeparator))));

    }
    catch (NullPointerException ex) {
      ex.printStackTrace();
    }
    catch (MalformedObjectNameException ex) {
      ex.printStackTrace();
    }

    JMXTableModel<DefaultMOMutableRow2PC> jvmMemManagerEntryModel =
        JMXTableModel.getDefaultInstance(oidJvmMemManagerEntry,
            tableSupport,
            super.getJvmMemManagerEntry().getColumns());
    super.getJvmMemManagerEntry().setModel(jvmMemManagerEntryModel);

    JMXTableModel<DefaultMOMutableRow2PC> jvmMemMgrPoolRelEntryModel =
        JMXTableModel.getDefaultInstance(oidJvmMemMgrPoolRelEntry,
                          tableSupport,
                          super.getJvmMemMgrPoolRelEntry().getColumns());
    super.getJvmMemMgrPoolRelEntry().setModel(jvmMemMgrPoolRelEntryModel);

    JMXTableModel<DefaultMOMutableRow2PC> jvmMemPoolEntryModel =
        JMXTableModel.getDefaultInstance(oidJvmMemPoolEntry,
                          tableSupport,
                          super.getJvmMemPoolEntry().getColumns());
    super.getJvmMemPoolEntry().setModel(jvmMemPoolEntryModel);

    JMXTableModel<DefaultMOMutableRow2PC> jvmThreadInstanceEntryModel =
        JMXTableModel.getDefaultInstance(oidJvmThreadInstanceEntry,
                          tableSupport,
                          super.getJvmThreadInstanceEntry().getColumns());
    super.getJvmThreadInstanceEntry().setModel(jvmThreadInstanceEntryModel);


    JMXTableModel<DefaultMOMutableRow2PC> jvmJvmRTInputArgsEntryModel =
        JMXTableModel.getDefaultInstance(oidJvmRTInputArgsEntry,
                          tableSupport,
                          super.getJvmRTInputArgsEntry().getColumns());
    super.getJvmRTInputArgsEntry().setModel(jvmJvmRTInputArgsEntryModel);


    JMXTableModel<DefaultMOMutableRow2PC> jvmJvmRTBootClassPathEntryModel =
        JMXTableModel.getDefaultInstance(oidJvmRTBootClassPathEntry,
                          tableSupport,
                          super.getJvmRTBootClassPathEntry().getColumns());
    super.getJvmRTBootClassPathEntry().setModel(jvmJvmRTBootClassPathEntryModel);

    JMXTableModel<DefaultMOMutableRow2PC> jvmJvmRTClassPathEntryModel =
        JMXTableModel.getDefaultInstance(oidJvmRTClassPathEntry,
                          tableSupport,
                          super.getJvmRTClassPathEntry().getColumns());
    super.getJvmRTClassPathEntry().setModel(jvmJvmRTClassPathEntryModel);

    JMXTableModel<DefaultMOMutableRow2PC> jvmJvmRTLibraryPathEntryModel =
        JMXTableModel.getDefaultInstance(oidJvmRTLibraryPathEntry,
                          tableSupport,
                          super.getJvmRTLibraryPathEntry().getColumns());
    super.getJvmRTLibraryPathEntry().setModel(jvmJvmRTLibraryPathEntryModel);
  }

  private void addJvmManagementMibConstraints(DefaultMOServer server) {
    MOScalar scalar = (MOScalar)
        server.getManagedObject(JvmManagementMib.oidJvmThreadContentionMonitoring,
                                null);
    ValueConstraint jvmThreadContentionMonitoringVC = new EnumerationConstraint(
      new int[] { JvmManagementMib.JvmThreadContentionMonitoringEnum.enabled,
                  JvmManagementMib.JvmThreadContentionMonitoringEnum.disabled });
    scalar.addMOValueValidationListener(
        new ValueConstraintValidator(jvmThreadContentionMonitoringVC));
    scalar = (MOScalar)
        server.getManagedObject(JvmManagementMib.oidJvmThreadCpuTimeMonitoring,
                                null);
    ValueConstraint jvmThreadCpuTimeMonitoringVC = new EnumerationConstraint(
      new int[] { JvmManagementMib.JvmThreadContentionMonitoringEnum.enabled,
                  JvmManagementMib.JvmThreadContentionMonitoringEnum.disabled });
    scalar.addMOValueValidationListener(
        new ValueConstraintValidator(jvmThreadCpuTimeMonitoringVC));
  }

/*
  class JvmMemMgrPoolRelTableSupport extends AbstractJMXTableSupport {

    private MBeanAttributeMOTableInfo memManagers;
    private MBeanAttributeMOTableInfo memPools;

    JvmMemMgrPoolRelTableSupport(MBeanAttributeMOTableInfo memManagers,
                                 MBeanAttributeMOTableInfo memPools) {
      super();
      this.memManagers = memManagers;
      this.memPools = memPools;
    }

    public OID getLastIndex(OID tableOID) {
      JMXRowSupport rowSupport = super.getRowSupport(tableOID);
      rowSupport.getRowMBean();
      return null;
    }

    public int getRowCount(OID tableOID) {
      return 0;
    }

    public Iterator rowIdIterator(OID tableOID) {
      return null;
    }

    public OID mapToIndex(OID tableOID, Object rowIdentifier) {
      return null;
    }

    public Object mapToRowId(OID tableOID, OID rowIndex) {
      return null;
    }

    public OID mapToIndex(OID tableOID, Object nativeRowID, int nativeIndex) {
      return null;
    }

    public Iterator rowIdTailIterator(OID tableOID, Object firstRowId) {
      return null;
    }

    public int setRow(OID tableOID, MOTableRow row, int column) {
      return 0;
    }

  }
*/
}
