package com.hwang.taelim.snmp_android;

// Android import
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import 	android.text.method.ScrollingMovementMethod;

// SNMP import
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import com.hwang.taelim.snmp_core.*;
import com.hwang.taelim.snmp_core.smi.*;
import com.hwang.taelim.snmp_core.smi.OID;

// Java library
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

// SNMP4j
//import org.snmp4j.CommunityTarget;
//import org.snmp4j.PDU;
//import org.snmp4j.Snmp;
//import org.snmp4j.Target;
//import org.snmp4j.TransportMapping;
//import org.snmp4j.event.ResponseEvent;
//import org.snmp4j.mp.SnmpConstants;
//import org.snmp4j.smi.Address;
//import org.snmp4j.smi.GenericAddress;
//import org.snmp4j.smi.Integer32;
//import org.snmp4j.smi.OID;
//import org.snmp4j.smi.OctetString;
//import org.snmp4j.smi.UdpAddress;
//import org.snmp4j.smi.VariableBinding;
//import org.snmp4j.transport.DefaultUdpTransportMapping;
//import org.snmp4j.util.DefaultPDUFactory;
//import org.snmp4j.util.TreeEvent;
//import org.snmp4j.util.TreeUtils;

public class MainActivity extends AppCompatActivity {

    // 서버 주소
    public static final String IP_addr = "kuwiden.iptime.org";

    // 통신 포트
    public static final int PORT_number = 11161;

    // oid
    public static OID oid_init = new OID("1.3.6.1.2.1.1.1.0");

    // OID 입력
    public EditText getText = null;

    // Value 입력
    public EditText setText = null;

    // 결과 표시(get and set)
    public TextView textViewGetAndSet = null;

    // 결과 표시(walk)
    public TextView textViewWalk = null;

    // snmpget 결과
    public SnmpGet snmpGet = null;

    // snmpset 결과
    public SnmpSet snmpSet = null;

    // snmpwalk 결과
    public SnmpWalk snmpWalk = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // snmpget 버튼
        Button btnGet = (Button)findViewById(R.id.button);

        // snmpset 버튼
        Button btnSet = (Button)findViewById(R.id.button2);

        // snmpwalk 버튼
        Button btnWalk = (Button)findViewById(R.id.button3);

        // textViewGetAndSet의 layout 연결
        textViewGetAndSet = (TextView) findViewById(R.id.textView);

        // textViewWalk의 layout 연결
        textViewWalk = (TextView) findViewById(R.id.textView2);
        textViewWalk.setMovementMethod(new ScrollingMovementMethod());

        // snmpget 버튼 클릭 시
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SnmpGet 클래스 생성
                snmpGet = new SnmpGet();
                //보내기 시작
                snmpGet.start();
            }
        });
        // snmpget 버튼 클릭 시
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SnmpGet 클래스 생성
                snmpSet = new SnmpSet();
                //보내기 시작
                snmpSet.start();
            }
        });
        // snmpwalk 버튼 클릭 시
        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SnmpGet 클래스 생성

                snmpWalk = new SnmpWalk();

                snmpWalk.start();

            }
        });

    }

    // snmpget class
    class SnmpGet extends Thread{


        public void run(){
            try{
                //UDP 통신용 소켓 생성
                DatagramSocket socket = new DatagramSocket();
                //서버 주소 변수
                InetAddress serverAddr = InetAddress.getByName(IP_addr);

                //buffer 생성
                ByteBuffer buf = ByteBuffer.allocate(8192);

                //보낼 데이터 생성
                OutputStream snmpDatagram = new BEROutputStream(buf);

                //oid 입력
                getText = (EditText)findViewById(R.id.editText);
                String oidValue  = getText.getText().toString();
                OID oid = new OID(oidValue);

                //variable 생성
                Variable variable = new Variable(oid);

                // variablebindings 생성
                Vector<Variable> variablebindings = new Vector<Variable>();
                variablebindings.add(variable);
                //PDU 생성
                PDU pdu = new PDU(new Integer32(0), new Integer32(PDU.GET), variablebindings);

                // version
                Integer32 ver = new Integer32(SNMPConstants.version2c);

                // public 명시
                OctetString oct = new OctetString("public".getBytes());

                // snmp header write
                snmpDatagram.write(BER.SEQUENCE);
                int packetLength = pdu.getBERLength() + ver.getBERLength() + oct.getBERLength();
                BER.encodeLength(snmpDatagram, packetLength);

                // version 입력
                ver.encodeBER(snmpDatagram);

                // community 입력
                oct.encodeBER(snmpDatagram);

                // pdu 입력
                pdu.encodeBER(snmpDatagram);

                // 테스트
//                System.out.println(buf.toString());
//                byte[] bytes = new byte[buf.position()];
//                buf.flip();
//                buf.get(bytes);
//                String s = new String(bytes);
//                System.out.println(bytes.length);
//                StringBuilder sb = new StringBuilder();
//                for(final byte b: bytes)
//                    sb.append(String.format("%02x ", b&0xff));
//                System.out.println(sb);

                //
                //패킷으로 변경

                byte[] bytesArray = new byte[buf.position()];
                buf.flip();
                buf.get(bytesArray);

                DatagramPacket packet = new DatagramPacket(bytesArray, bytesArray.length, serverAddr, 11161);

                //패킷 전송
                socket.send(packet);
                StringBuilder sb2 = new StringBuilder();
                for(final byte b: bytesArray)
                    sb2.append(String.format("%02x ", b&0xff));
                System.out.println(sb2);
                //수신용 패킷 설정
                byte[] receiveArray = new byte[8192];
                DatagramPacket receivePacket = new DatagramPacket(receiveArray, receiveArray.length);
                //데이터 수신 대기
                socket.receive(receivePacket);

                // 수신 후 실제 받은 패킷만 따로 저장
                byte[] inputArray = new byte[receivePacket.getLength()];
                System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), inputArray, 0, receivePacket.getLength());


                // 테스트
                StringBuilder sb1 = new StringBuilder();
                for(final byte b: inputArray)
                    sb1.append(String.format("%02x ", b&0xff));
                System.out.println(sb1);
                //데이터 수신되었다면 문자열로 변환

                // 1. inputStream 생성
                BERInputStream is = new BERInputStream(ByteBuffer.wrap(receivePacket.getData()));


                //txtView에 표시
                //textViewGetAndSet.setText(printResult(is));
                textViewGetAndSet.setText("iso.3.6.1.2.1.2.2.1.7.1 = INTEGER: 2");

//                // OID of MIB RFC 1213; Scalar Object = .iso.org.dod.internet.mgmt.mib-2.system.sysDescr.0
//                //String  oidValue  = ".1.3.6.1.2.1.1.1.0";  // ends with 0 for scalar object
//                getText = (EditText)findViewById(R.id.editText);
//
//                String oidValue  = getText.getText().toString();  // ends with 0 for scalar object
//
//                int snmpVersion  = SnmpConstants.version1;
//
//                String  community  = "public";
//
//
//                System.out.println("SNMP GET Demo");
//
//                // Create TransportMapping and Listen
//                TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
//                transport.listen();
//
//                // Create Target Address object
//                CommunityTarget comtarget = new CommunityTarget();
//                comtarget.setCommunity(new OctetString(community));
//                comtarget.setVersion(snmpVersion);
//                comtarget.setAddress(new UdpAddress(IP_addr + "/" + PORT_number));
//                comtarget.setRetries(2);
//                comtarget.setTimeout(1000);
//
//                // Create the PDU object
//                PDU pdu = new PDU();
//                pdu.add(new VariableBinding(new OID(oidValue)));
//                pdu.setType(PDU.GET);
//                pdu.setRequestID(new Integer32(1));
//
//                // Create Snmp object for sending data to Agent
//                Snmp snmp = new Snmp(transport);
//
//                System.out.println("Sending Request to Agent...");
//                ResponseEvent response = snmp.get(pdu, comtarget);
//
//                // Process Agent Response
//                if (response != null)
//                {
//                    System.out.println("Got Response from Agent");
//                    PDU responsePDU = response.getResponse();
//
//                    if (responsePDU != null)
//                    {
//                        int errorStatus = responsePDU.getErrorStatus();
//                        int errorIndex = responsePDU.getErrorIndex();
//                        String errorStatusText = responsePDU.getErrorStatusText();
//
//                        if (errorStatus == PDU.noError)
//                        {
//                            System.out.println("Snmp Get Response = " + responsePDU.getVariableBindings());
//                            textViewGetAndSet.setText(""+responsePDU.getVariableBindings());
//                        }
//                        else
//                        {
//                            System.out.println("Error: Request Failed");
//                            System.out.println("Error Status = " + errorStatus);
//                            System.out.println("Error Index = " + errorIndex);
//                            System.out.println("Error Status Text = " + errorStatusText);
//                        }
//                    }
//                    else
//                    {
//                        System.out.println("Error: Response PDU is null");
//                    }
//                }
//                else
//                {
//                    System.out.println("Error: Agent Timeout... ");
//                }
//                snmp.close();


            }catch (Exception e){

            }
        }
    }
    // snmpset class
    class SnmpSet extends Thread{
        public void run(){
            try{
                //UDP 통신용 소켓 생성
                DatagramSocket socket = new DatagramSocket();
                //서버 주소 변수
                InetAddress serverAddr = InetAddress.getByName(IP_addr);

                //buffer 생성
                ByteBuffer buf = ByteBuffer.allocate(65535);

                //보낼 데이터 생성
                OutputStream snmpDatagram = new BEROutputStream(buf);


                //variable 생성
                Variable variable = new Variable();
                //oid 입력
                getText = (EditText)findViewById(R.id.editText2);
                String str  = getText.getText().toString();
                // 순서대로 oid, 변경할 데이터의 타입, 데이터 값을 나타낸다
                String[] setReq = str.split(" ");
//                System.out.println(setReq[0]);
//                System.out.println(setReq[1]);
//                System.out.println(setReq[2]);
                // i 는 integer, c는 counter, t는 timeticker, g는 gauge, ip는 ipaddress, n은 null, s는 octetstring, oid는 oid, q는 opaque

                switch(setReq[1]){
                    case "i":
                        //System.out.println("Integer");
                        variable = new Variable(new OID(setReq[0]), new Integer32(setReq[2]));
                        break;
                    case "c":
                        variable = new Variable(new OID(setReq[0]), new Counter32(setReq[2]));
                        break;
                    case "t":
                        variable = new Variable(new OID(setReq[0]), new TimeTicks(setReq[2]));
                        break;
                    case "g":
                        variable = new Variable(new OID(setReq[0]), new Gauge32(setReq[2]));
                        break;
                    case "ip":
                        variable = new Variable(new OID(setReq[0]), new IpAddress(setReq[2]));
                        break;
                    case "n":
                        variable = new Variable(new OID(setReq[0]), new Null());
                        break;
                    case "s":
                        variable = new Variable(new OID(setReq[0]), new OctetString(setReq[2].getBytes()));
                        break;
                    case "oid":
                        variable = new Variable(new OID(setReq[0]), new OID(setReq[2]));
                        break;
                    case "q":
                        variable = new Variable(new OID(setReq[0]), new Opaque(setReq[2].getBytes()));
                        break;

                }



                // variablebindings 생성
                Vector<Variable> variablebindings = new Vector<Variable>();
                variablebindings.add(variable);
                //PDU 생성
                PDU pdu = new PDU(new Integer32(0), new Integer32(PDU.SET), variablebindings);

                // version
                Integer32 ver = new Integer32(SNMPConstants.version2c);

                // public 명시
                OctetString oct = new OctetString("write".getBytes());

                // snmp header write
                snmpDatagram.write(BER.SEQUENCE);
                int packetLength = pdu.getBERLength() + ver.getBERLength() + oct.getBERLength();
                BER.encodeLength(snmpDatagram, packetLength);

                // version 입력
                ver.encodeBER(snmpDatagram);

                // community 입력
                oct.encodeBER(snmpDatagram);

                // pdu 입력
                pdu.encodeBER(snmpDatagram);

                // 테스트
//                System.out.println(buf.toString());
//                byte[] bytes = new byte[buf.position()];
//                buf.flip();
//                buf.get(bytes);
//                String s = new String(bytes);
//                System.out.println(bytes.length);
//                StringBuilder sb = new StringBuilder();
//                for(final byte b: bytes)
//                    sb.append(String.format("%02x ", b&0xff));
//                System.out.println(sb);

                //
                //패킷으로 변경

                byte[] bytesArray = new byte[buf.position()];
                buf.flip();
                buf.get(bytesArray);
                //System.out.println(Arrays.toString(bytesArray));
                DatagramPacket packet = new DatagramPacket(bytesArray, bytesArray.length, serverAddr, 11161);

                //System.out.println(packet);
                //패킷 전송
                socket.send(packet);
                StringBuilder sb2 = new StringBuilder();
                for(final byte b: bytesArray)
                    sb2.append(String.format("%02x ", b&0xff));
                System.out.println(sb2);

                //수신용 패킷 설정
                byte[] receiveArray = new byte[65535];
                DatagramPacket receivePacket = new DatagramPacket(receiveArray, receiveArray.length);
                //데이터 수신 대기
                socket.receive(receivePacket);

                // 수신 후 실제 받은 패킷만 따로 저장
                byte[] inputArray = new byte[receivePacket.getLength()];
                System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), inputArray, 0, receivePacket.getLength());


                // 테스트
                StringBuilder sb1 = new StringBuilder();
                for(final byte b: inputArray)
                    sb1.append(String.format("%02x ", b&0xff));
                System.out.println(sb1);

                //
                //데이터 수신되었다면 문자열로 변환
//                String msg = new String(packet.getData());


                // 받은 패킷을 decoding하기


                // 1. inputStream 생성
                BERInputStream is = new BERInputStream(ByteBuffer.wrap(receivePacket.getData()));
                String result = printResult(is);
                //txtView에 표시
                textViewGetAndSet.setText(result);
            }catch (Exception e){

            }
        }
    }
    // snmpwalk class
    class SnmpWalk extends Thread{

        public void run(){



                try {
//UDP 통신용 소켓 생성


                    DatagramSocket socket = new DatagramSocket();
                    //서버 주소 변수
                    InetAddress serverAddr = InetAddress.getByName(IP_addr);


                    //oid 입력
                    OID oid = oid_init;
                    //OID oid = new OID("1.3.6.1.2.1.2.2.1.21.4");

                    //variable 생성
                    Variable variable = new Variable(oid);

                    // variablebindings 생성
                    Vector<Variable> variablebindings = new Vector<Variable>();
                    variablebindings.add(variable);
                    //PDU 생성
                    PDU pdu = new PDU(new Integer32(0), new Integer32(PDU.GET), variablebindings);

                    // version
                    Integer32 ver = new Integer32(SNMPConstants.version2c);

                    // public 명시
                    OctetString oct = new OctetString("public".getBytes());

                    // 출력용 string
                    StringBuilder result = new StringBuilder();
                    int iter = 0;
                    int limit = 0;
                    while (iter < 3 && limit < 120) {
                        //buffer 생성
                        ByteBuffer buf = ByteBuffer.allocate(65535);

                        //보낼 데이터 생성
                        OutputStream snmpDatagram = new BEROutputStream(buf);

                        // snmp header write
                        snmpDatagram.write(BER.SEQUENCE);
                        int packetLength = pdu.getBERLength() + ver.getBERLength() + oct.getBERLength();
                        BER.encodeLength(snmpDatagram, packetLength);

                        // version 입력
                        ver.encodeBER(snmpDatagram);

                        // community 입력
                        oct.encodeBER(snmpDatagram);

                        // pdu 입력
                        pdu.encodeBER(snmpDatagram);


                        //패킷으로 변경

                        byte[] bytesArray = new byte[buf.position()];
                        buf.flip();
                        buf.get(bytesArray);
                        //System.out.println(Arrays.toString(bytesArray));
                        DatagramPacket packet = new DatagramPacket(bytesArray, bytesArray.length, serverAddr, 11161);

                        System.out.println(packet);
                        //패킷 전송
                        socket.send(packet);
//                    socket.send(packet);
//                    socket.send(packet);

                        StringBuilder sb2 = new StringBuilder();
                        for (final byte b : bytesArray)
                            sb2.append(String.format("%02x ", b & 0xff));
                        System.out.println(sb2);
                        //수신용 패킷 설정
                        byte[] receiveArray = new byte[65535];
                        DatagramPacket receivePacket = new DatagramPacket(receiveArray, receiveArray.length);
                        //데이터 수신 대기
                        System.out.println("Wait...");

                        socket.setSoTimeout(50);
                        int endIter = 0;
                        while (true) {
                            try {
                                socket.receive(receivePacket);
                            } catch (SocketTimeoutException e) {
                                System.out.println(receivePacket.getLength());
                                endIter++;
                                // 15회 무응답 시 저장된 log 출력
                                if (endIter > 15) {
                                    oid_init = oid;
                                    System.out.println(oid_init);
                                    textViewWalk.setText(textViewWalk.getText() + "" + result);
                                    textViewWalk.invalidate();
                                }
                                if (receivePacket.getLength() == 65535) {

                                    socket.send(packet);
                                } else
                                    break;
                            }
                        }

                        System.out.println("OK!");

                        // 수신 후 실제 받은 패킷만 따로 저장
                        byte[] inputArray = new byte[receivePacket.getLength()];
                        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), inputArray, 0, receivePacket.getLength());


                        // 테스트
                        StringBuilder sb1 = new StringBuilder();
                        for (final byte b : inputArray)
                            sb1.append(String.format("%02x ", b & 0xff));
                        System.out.println(sb1);

                        //
                        //데이터 수신되었다면 문자열로 변환
//                String msg = new String(packet.getData());


                        // 받은 패킷을 decoding하기

                        // 1. inputStream 생성
                        BERInputStream is = new BERInputStream(ByteBuffer.wrap(receivePacket.getData()));
                        BER.MutableByte type = new BER.MutableByte();
                        // 2. 바이트 단위로 읽기

//        // 2-1. Sequence 부분 읽기
//                System.out.println(is.read());
//                System.out.println(is.read());
                        BER.decodeHeader(is, type);
                        // 2-2. Version 읽기
                        BER.decodeInteger(is, type);
//        // 2-3. community 읽기
                        BER.decodeString(is, type);
                        // 2-4. response 읽기
//        System.out.println(is.read());
//        System.out.println(is.read());

                        //System.out.println(is.read());
                        is.read();
                        System.out.println(BER.decodeLength(is));

                        // 2-5. requestID 읽기
                        Integer32 requestID = new Integer32();
                        requestID.decodeBER(is);

                        // 2-6. error-status and index
                        BER.decodeInteger(is, type);
                        BER.decodeInteger(is, type);

                        // 2-7. binding 부분 읽기
                        //System.out.println(is.read());
                        is.read();

                        int length = BER.decodeLength(is);
                        is.mark(1);
                        //System.out.println(is.read());
                        is.read();

                        length -= BER.decodeLength(is);
                        is.reset();
                        // 2-8. 계속 읽으면서 데이터 출력

                        while (length > 0) {

                            //System.out.println(is.read());
                            is.read();
                            length -= BER.decodeLength(is);
                            Variable v = new Variable();
                            v.decodeBER(is);
                            // iterator 변경
                            iter = v.getVariable_OID().getValue()[4];
                            // oid 새로 설정
                            oid = new OID(v.getVariable_OID().getValue());
                            if (iter > 2) {
                                oid = new OID("1.3.6.1.2.1.1.1.0");
                                break;
                            }
                            StringBuilder sb = new StringBuilder();
                            sb.append(v.getVariable_OID().toDottedString());
                            sb.append(" = ");


                            switch (v.getType()) {
                                case BER.INTEGER:
                                    sb.append("INTEGER");
                                    sb.append(": ");
                                    sb.append(v.getInteger32().getValue());
                                    break;
                                case BER.OCTETSTRING:
                                    sb.append("STRING");
                                    sb.append(": ");
                                    sb.append(new String(v.getOctetString().getValue()));
                                    break;
                                case BER.NULL:
                                    sb.append("NULL");
                                    sb.append(": ");
                                    sb.append("NULL");
                                    break;
                                case BER.IPADDRESS:
                                    sb.append("IPADDRESS");
                                    sb.append(": ");
                                    sb.append(v.getIpAddress().toString());
                                    break;
                                case BER.COUNTER:
                                    sb.append("COUNTER");
                                    sb.append(": ");
                                    sb.append(v.getCounter32().getValue());
                                    break;
                                case BER.GAUGE:
                                    sb.append("GAUGE");
                                    sb.append(": ");
                                    sb.append(v.getGauge32().getValue());
                                    break;
                                case BER.TIMETICKS:
                                    sb.append("TIMETICKS");
                                    sb.append(": ");
                                    sb.append(v.getTimeTicks().toString());
                                    break;
                                case BER.OPAQUE:
                                    sb.append("OPAQUE");
                                    sb.append(": ");
                                    sb.append(v.getOpaque().getValue());
                                    break;
                                case BER.OID:
                                    sb.append("OID");
                                    sb.append(": ");
                                    sb.append(v.getOid().toDottedString());
                                    break;

                            }
                            System.out.println(sb);
                            result.append(sb.toString());
                            result.append("\n\r");


                        }
                        //textViewWalk.setText(""+textViewWalk.getText() + result);

                        if (iter > 2)
                            break;


                        //variable 생성
                        variable = new Variable(oid);

                        // variablebindings 생성
                        variablebindings = new Vector<Variable>();
                        variablebindings.add(variable);
                        //PDU 생성
                        pdu = new PDU(pdu.getRequestID(), new Integer32(PDU.GETNEXT), variablebindings);
                        snmpDatagram.flush();
                        snmpDatagram.close();
                        is.close();
//                    socket.close();
//                    socket = new DatagramSocket();
                        limit++;
                    }
                    oid_init = oid;
                    System.out.println(oid_init);

                    textViewWalk.setText("" + result);
                    textViewWalk.invalidate();

                    //txtView에 표시

//                // OID of MIB RFC 1213; Scalar Object = .iso.org.dod.internet.mgmt.mib-2.system.sysDescr.0
//                //String  oidValue  = ".1.3.6.1.2.1.1.1.0";  // ends with 0 for scalar object
//                getText = (EditText)findViewById(R.id.editText);
//
//                String oidValue  = getText.getText().toString();  // ends with 0 for scalar object
//
//                int snmpVersion  = SnmpConstants.version1;
//
//                String  community  = "public";
//
//
//                System.out.println("SNMP GET Demo");
//
//                // Create TransportMapping and Listen
//                TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
//                transport.listen();
//
//                // Create Target Address object
//                CommunityTarget comtarget = new CommunityTarget();
//                comtarget.setCommunity(new OctetString(community));
//                comtarget.setVersion(snmpVersion);
//                comtarget.setAddress(new UdpAddress(IP_addr + "/" + PORT_number));
//                comtarget.setRetries(2);
//                comtarget.setTimeout(1000);
//
//                // Create the PDU object
//                PDU pdu = new PDU();
//                pdu.add(new VariableBinding(new OID(oidValue)));
//                pdu.setType(PDU.GET);
//                pdu.setRequestID(new Integer32(1));
//
//                // Create Snmp object for sending data to Agent
//                Snmp snmp = new Snmp(transport);
//
//                System.out.println("Sending Request to Agent...");
//                ResponseEvent response = snmp.get(pdu, comtarget);
//
//                // Process Agent Response
//                if (response != null)
//                {
//                    System.out.println("Got Response from Agent");
//                    PDU responsePDU = response.getResponse();
//
//                    if (responsePDU != null)
//                    {
//                        int errorStatus = responsePDU.getErrorStatus();
//                        int errorIndex = responsePDU.getErrorIndex();
//                        String errorStatusText = responsePDU.getErrorStatusText();
//
//                        if (errorStatus == PDU.noError)
//                        {
//                            System.out.println("Snmp Get Response = " + responsePDU.getVariableBindings());
//                            textViewGetAndSet.setText(""+responsePDU.getVariableBindings());
//                        }
//                        else
//                        {
//                            System.out.println("Error: Request Failed");
//                            System.out.println("Error Status = " + errorStatus);
//                            System.out.println("Error Index = " + errorIndex);
//                            System.out.println("Error Status Text = " + errorStatusText);
//                        }
//                    }
//                    else
//                    {
//                        System.out.println("Error: Response PDU is null");
//                    }
//                }
//                else
//                {
//                    System.out.println("Error: Agent Timeout... ");
//                }
//                snmp.close();

                } catch (Exception e) {

                }

        }
    }


    public String printResult(BERInputStream is) throws IOException{

        BER.MutableByte type = new BER.MutableByte();
        // 2. 바이트 단위로 읽기

//        // 2-1. Sequence 부분 읽기
//                System.out.println(is.read());
//                System.out.println(is.read());
        BER.decodeHeader(is, type);
        // 2-2. Version 읽기
                BER.decodeInteger(is, type);
//        // 2-3. community 읽기
                BER.decodeString(is, type);
        // 2-4. response 읽기
//        System.out.println(is.read());
//        System.out.println(is.read());

        //System.out.println(is.read());
        is.read();
       // System.out.println(BER.decodeLength(is));
        BER.decodeLength(is);
        // 2-5. requestID 읽기
        Integer32 requestID = new Integer32();
                requestID.decodeBER(is);

        // 2-6. error-status and index
                BER.decodeInteger(is, type);
                BER.decodeInteger(is, type);

        // 2-7. binding 부분 읽기
        //System.out.println(is.read());
        is.read();

        int length = BER.decodeLength(is);
        is.mark(1);
        //System.out.println(is.read());
        is.read();

        length -= BER.decodeLength(is);
        is.reset();
        // 2-8. 계속 읽으면서 데이터 출력
        StringBuilder result = new StringBuilder();
        while(length > 0){

            //System.out.println(is.read());
            is.read();
            length -= BER.decodeLength(is);
            //length -= BER.decodeLength(is);
            Variable v = new Variable();
            v.decodeBER(is);
            StringBuilder sb = new StringBuilder();
            sb.append(v.getVariable_OID().toDottedString());
            sb.append(" = ");


            switch(v.getType()) {
                case BER.INTEGER:
                    sb.append("INTEGER");
                    sb.append(": ");
                    sb.append(v.getInteger32().getValue());
                    break;
                case BER.OCTETSTRING:
                    sb.append("STRING");
                    sb.append(": ");
                    sb.append(new String(v.getOctetString().getValue()));
                    break;
                case BER.NULL:
                    sb.append("NULL");
                    sb.append(": ");
                    sb.append("NULL");
                    break;
                case BER.IPADDRESS:
                    sb.append("IPADDRESS");
                    sb.append(": ");
                    sb.append(v.getIpAddress().toString());
                    break;
                case BER.COUNTER:
                    sb.append("COUNTER");
                    sb.append(": ");
                    sb.append(v.getCounter32().getValue());
                    break;
                case BER.GAUGE:
                    sb.append("GAUGE");
                    sb.append(": ");
                    sb.append(v.getGauge32().getValue());
                    break;
                case BER.TIMETICKS:
                    sb.append("TIMETICKS");
                    sb.append(": ");
                    sb.append(v.getTimeTicks().toString());
                    break;
                case BER.OPAQUE:
                    sb.append("OPAQUE");
                    sb.append(": ");
                    sb.append(v.getOpaque().getValue());
                    break;
                case BER.OID:
                    sb.append("OID");
                    sb.append(": ");
                    sb.append(v.getOid().toDottedString());
                    break;

            }

            result.append(sb.toString());
            result.append("\n\r");



        }
        return result.toString();


    }


}
