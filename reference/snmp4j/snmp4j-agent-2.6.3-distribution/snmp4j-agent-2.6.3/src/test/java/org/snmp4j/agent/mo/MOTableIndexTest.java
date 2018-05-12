package org.snmp4j.agent.mo;

import org.junit.Test;
import org.snmp4j.smi.*;

import static org.junit.Assert.*;

public class MOTableIndexTest {

    // Index OID definitions
    public static final OID oidId =
            new OID(new int[] { 1,3,6,1,4,1,4976,1,1,1,4,4,2,1,1 });
    public static final OID oidVersion =
            new OID(new int[] { 1,3,6,1,4,1,4976,1,1,1,4,4,2,1,2 });
    public static final OID oidInstance =
            new OID(new int[] { 1,3,6,1,4,1,4976,1,1,1,4,4,2,1,3 });

    @Test
    public void getIndexOIDFixedLength() {
        DefaultMOFactory moFactory = new DefaultMOFactory();
        MOTableSubIndex[] moTableSubIndices =
                new MOTableSubIndex[] {
                        moFactory.createSubIndex(oidId, SMIConstants.SYNTAX_OCTET_STRING, 1, 32),
                        moFactory.createSubIndex(oidVersion,
                                SMIConstants.SYNTAX_OCTET_STRING, 12, 12),
                        moFactory.createSubIndex(oidInstance, SMIConstants.SYNTAX_INTEGER, 1, 1) };
        MOTableIndex moTableIndex = new MOTableIndex(moTableSubIndices, false);
        Variable[] testIndex = new Variable[] {
                new OctetString("TestSignal"),
                new OctetString("V01.01.01.01"),
                new Integer32(5)
        };
        OID indexOID = moTableIndex.getIndexOID(testIndex);
        assertEquals(new OID("10.84.101.115.116.83.105.103.110.97.108.86.48.49.46.48.49.46.48.49.46.48.49.5"), indexOID);
    }

    @Test
    public void getIndexOIDVariableLength() {
        DefaultMOFactory moFactory = new DefaultMOFactory();
        MOTableSubIndex[] moTableSubIndices =
                new MOTableSubIndex[] {
                        moFactory.createSubIndex(oidId, SMIConstants.SYNTAX_OCTET_STRING, 1, 32),
                        moFactory.createSubIndex(oidVersion,
                                SMIConstants.SYNTAX_OCTET_STRING, 11, 12),
                        moFactory.createSubIndex(oidInstance, SMIConstants.SYNTAX_INTEGER, 1, 1) };
        MOTableIndex moTableIndex = new MOTableIndex(moTableSubIndices, false);
        Variable[] testIndex = new Variable[] {
                new OctetString("TestSignal"),
                new OctetString("V01.01.01.0"),
                new Integer32(5)
        };
        OID indexOID = moTableIndex.getIndexOID(testIndex);
        assertEquals(new OID("10.84.101.115.116.83.105.103.110.97.108.11.86.48.49.46.48.49.46.48.49.46.48.5"), indexOID);
    }

}