package com.hwang.taelim.snmp;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;



public class SNMP_core {
	public static Map<String, String> doWalk(String tableOid, Target target) throws IOException{
		Map<String, String> result = new TreeMap<>();
		//???
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();
		
		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		List<TreeEvent> events = treeUtils.getSubtree(target,  new OID(tableOid));
		
		if (events == null || events.size() == 0){
			System.out.println("Error: Unable to read table...");
			return result;
		}
		
		for(TreeEvent event : events){
			if (event == null)
				continue;
			if(event.isError()){
				System.out.println("Error: table OID [" + tableOid + "] " + event.getErrorMessage());
				continue;
			}
			
			VariableBinding[] varBindings = event.getVariableBindings();
			if(varBindings == null || varBindings.length == 0)
				continue;
			for(VariableBinding varBinding : varBindings){
				if(varBinding == null)
					continue;
				
				result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());

			}
		}
		snmp.close();
		
		return result;
	}
	
	public static void doWalkTest() throws IOException{
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));
		target.setAddress(GenericAddress.parse("udp:kuwiden.iptime.org/11161"));
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);
		Map<String, String> result = doWalk(".1.3.6.1.2.1.1.", target); // ifTable, mib-2 interfaces
		
		for (Map.Entry<String, String> entry : result.entrySet()) {
			System.out.println(entry.getValue());
//			if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.2."))
//				System.out.println("ifDescr" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.2", "") + ": " + entry.getValue());
//			if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.3."))
//				System.out.println("ifType" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.3", "") + ": " + entry.getValue());
//	
		}
	}
	

	public static void doGetTest() throws IOException{
		String  ipAddress  = "kuwiden.iptime.org";

		String  port    = "11161";
		
		// OID of MIB RFC 1213; Scalar Object = .iso.org.dod.internet.mgmt.mib-2.system.sysDescr.0
		//String  oidValue  = ".1.3.6.1.2.1.1.1.0";  // ends with 0 for scalar object
		String  oidValue  = ".1.3.6.1.2.1.2.2.1.7.1.";  // ends with 0 for scalar object
		
		int snmpVersion  = SnmpConstants.version1;
		
		String  community  = "public";


	    System.out.println("SNMP GET Demo");
	
	    // Create TransportMapping and Listen
	    TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
	    transport.listen();
	
	    // Create Target Address object
	    CommunityTarget comtarget = new CommunityTarget();
	    comtarget.setCommunity(new OctetString(community));
	    comtarget.setVersion(snmpVersion);
	    comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
	    comtarget.setRetries(2);
	    comtarget.setTimeout(1000);
	
	    // Create the PDU object
	    PDU pdu = new PDU();
	    pdu.add(new VariableBinding(new OID(oidValue)));
	    pdu.setType(PDU.GET);
	    pdu.setRequestID(new Integer32(1));
	
	    // Create Snmp object for sending data to Agent
	    Snmp snmp = new Snmp(transport);
	
	    System.out.println("Sending Request to Agent...");
	    ResponseEvent response = snmp.get(pdu, comtarget);
	
	    // Process Agent Response
	    if (response != null)
	    {
	      System.out.println("Got Response from Agent");
	      PDU responsePDU = response.getResponse();
	
	      if (responsePDU != null)
	      {
	        int errorStatus = responsePDU.getErrorStatus();
	        int errorIndex = responsePDU.getErrorIndex();
	        String errorStatusText = responsePDU.getErrorStatusText();
	
	        if (errorStatus == PDU.noError)
	        {
	          System.out.println("Snmp Get Response = " + responsePDU.getVariableBindings());
	        }
	        else
	        {
	          System.out.println("Error: Request Failed");
	          System.out.println("Error Status = " + errorStatus);
	          System.out.println("Error Index = " + errorIndex);
	          System.out.println("Error Status Text = " + errorStatusText);
	        }
	      }
	      else
	      {
	        System.out.println("Error: Response PDU is null");
	      }
	    }
	    else
	    {
	      System.out.println("Error: Agent Timeout... ");
	    }
	    snmp.close();
	}
	

	public static void main(String[] args) throws Exception{
		doWalkTest();
		//doGetTest();
		
	}
}




























