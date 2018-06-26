package com.hwang.taelim.snmp_core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import java.util.Vector;

// SMI
import com.hwang.taelim.snmp_core.smi.*;


public class PDU implements BERSerializable, Serializable, Cloneable {

    // GET
    public static final int GET      = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR);

    // GETNEXT
    public static final int GETNEXT  = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x1);

    // RESPONSE
    public static final int RESPONSE = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x2);

    // SET
    public static final int SET      = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x3);

    // GETBULK
    public static final int GETBULK  = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x5);

    // INFORM
    public static final int INFORM   = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x6);

    // TRAP
    public static final int TRAP     = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x7);

    // NOTIFICATION
    public static final int NOTIFICATION = TRAP;

    // REPORT
    public static final int REPORT   = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x8);


    // noError
    public static final int noError = SNMPConstants.SNMP_ERROR_SUCCESS;

    // tooBig
    public static final int tooBig = SNMPConstants.SNMP_ERROR_TOO_BIG;

    // noSuchName
    public static final int noSuchName = SNMPConstants.SNMP_ERROR_NO_SUCH_NAME;

    // badValue
    public static final int badValue = SNMPConstants.SNMP_ERROR_BAD_VALUE;

    // readOnly
    public static final int readOnly = SNMPConstants.SNMP_ERROR_READ_ONLY;

    // genErr
    public static final int genErr = SNMPConstants.SNMP_ERROR_GENERAL_ERROR;


    protected Vector<Variable> variableBindings = new Vector<Variable>();
    protected Integer32 errorStatus = new Integer32();
    protected Integer32 errorIndex = new Integer32();

    public Integer32 getRequestID() {
        return requestID;
    }

    protected Integer32 requestID = new Integer32();
    protected Integer32 type = new Integer32(GET);



    // PDU type과 variable 포함 생성자
    public PDU(Integer32 requestID, Integer32 pduType, Vector<Variable> variableBindings) {
        this.requestID = new Integer32(requestID.getValue() + 1); // 새로 들어올 ID에 +1 하는 부분
        this.type = pduType;
        for(Variable v : variableBindings)
            this.variableBindings.add((Variable)v.clone());


    }

    void setSNMPGetHeader(OutputStream outputStream) throws IOException{
        int length = getBERPayloadLength();
        BER.encodeHeader(outputStream, this.type.getValue(), length); // 명령 header BER 변환
        BER.encodeInteger(outputStream, BER.INTEGER, 0); // request ID BER 변환, 우선은 테스트로 0을 넣는다
        BER.encodeInteger(outputStream, BER.INTEGER, 0); // errorStatus BER 변환
        BER.encodeInteger(outputStream, BER.INTEGER, 0); // errorIndex BER 변환
    }

    public int getBERLength(){
        int length = getBERPayloadLength();
        length += BER.getBERLengthOfLength(length) + 1;
        return length;
    }

    // variablebindings의 Payload(Varbind)의 총 길이
    int getBERVariableBindingsPayloadLength(){
        int length = 0;
        for (Variable v : variableBindings)
            length += v.getBERLength();
        return length;
    }
    int getBERVariableBindingsLength(){
        int length = getBERVariableBindingsPayloadLength();

        length += BER.getBERLengthOfLength(length) + 1;
        return length;
    }
    public int getBERPayloadLength(){
        int length = 0;
        length = requestID.getBERLength() + errorIndex.getBERLength() + errorStatus.getBERLength() + getBERVariableBindingsLength();
        return length;

    }



    public void decodeBER(BERInputStream inputStream) throws IOException{

    }

    public void encodeBER(OutputStream outputStream) throws IOException{


        // header -> variableBindings -> variable 순으로 write

        // header
        setSNMPGetHeader(outputStream);

        // variableBindings
        outputStream.write(BER.SEQUENCE);
        BER.encodeLength(outputStream, getBERVariableBindingsPayloadLength());

        // variable

        for (Variable v : variableBindings) {
            outputStream.write(BER.SEQUENCE);
            BER.encodeLength(outputStream, v.getBERPayloadLength());
            v.encodeBER(outputStream);
        }



    }
}
