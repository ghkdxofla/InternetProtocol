package com.hwang.taelim.snmp_core;

import com.hwang.taelim.snmp_core.BER;
import com.hwang.taelim.snmp_core.BERInputStream;
import com.hwang.taelim.snmp_core.BERSerializable;
import com.hwang.taelim.snmp_core.smi.*;

import java.io.IOException;
import java.io.OutputStream;

public class Variable implements BERSerializable  {
    Counter32 counter32 = new Counter32();
    Gauge32 gauge32 = new Gauge32();
    Integer32 integer32 = new Integer32();
    IpAddress ipAddress = new IpAddress();
    Null Null = new Null();
    OctetString octetString = new OctetString();
    OID oid = new OID();
    Opaque opaque = new Opaque();
    TimeTicks timeTicks = new TimeTicks();


    public Counter32 getCounter32() {
        return counter32;
    }

    public void setCounter32(Counter32 counter32) {
        this.counter32 = counter32;
    }

    public Gauge32 getGauge32() {
        return gauge32;
    }

    public void setGauge32(Gauge32 gauge32) {
        this.gauge32 = gauge32;
    }

    public Integer32 getInteger32() {
        return integer32;
    }

    public void setInteger32(Integer32 integer32) {
        this.integer32 = integer32;
    }

    public IpAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(IpAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public com.hwang.taelim.snmp_core.smi.Null getNull() {
        return Null;
    }

    public void setNull(com.hwang.taelim.snmp_core.smi.Null aNull) {
        Null = aNull;
    }

    public OctetString getOctetString() {
        return octetString;
    }

    public void setOctetString(OctetString octetString) {
        this.octetString = octetString;
    }

    public OID getOid() {
        return oid;
    }

    public void setOid(OID oid) {
        this.oid = oid;
    }

    public Opaque getOpaque() {
        return opaque;
    }

    public void setOpaque(Opaque opaque) {
        this.opaque = opaque;
    }

    public TimeTicks getTimeTicks() {
        return timeTicks;
    }

    public void setTimeTicks(TimeTicks timeTicks) {
        this.timeTicks = timeTicks;
    }

    public OID getVariable_OID() {
        return variable_OID;
    }

    public void setVariable_OID(OID variable_OID) {
        this.variable_OID = variable_OID;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }



    OID variable_OID = new OID();
    byte type = 0;

    public Variable(){};

    public Variable(OID variable_OID){
        this.Null = (Null)Null.clone();
        this.variable_OID = (OID)variable_OID.clone();
        this.type = BER.NULL;
    }

    public Variable(OID variable_OID, Counter32 counter32){
        this.counter32 = (Counter32)counter32.clone();
        this.variable_OID = (OID)variable_OID.clone();
        this.type = BER.COUNTER32;
    }

    public Variable(OID variable_OID, Gauge32 gauge32){
        this.gauge32 = (Gauge32)gauge32.clone();
        this.variable_OID = (OID)variable_OID.clone();
        this.type = BER.GAUGE32;
    }

    public Variable(OID variable_OID, Integer32 integer32){
        this.integer32 = (Integer32)integer32.clone();
        this.variable_OID = (OID)variable_OID.clone();
        this.type = BER.INTEGER32;
    }

    public Variable(OID variable_OID, IpAddress ipAddress){
        this.ipAddress = (IpAddress)ipAddress.clone();
        this.variable_OID = (OID)variable_OID.clone();
        this.type = BER.IPADDRESS;
    }

    public Variable(OID variable_OID, Null Null){
        this.Null = (Null)Null.clone();
        this.variable_OID = (OID)variable_OID.clone();
        this.type = BER.NULL;
    }

    public Variable(OID variable_OID, OctetString octetString){
        this.octetString = (OctetString)octetString.clone();
        this.variable_OID = (OID)variable_OID.clone();
        this.type = BER.OCTETSTRING;
    }

    public Variable(OID variable_OID, OID oid){
        this.oid = (OID)oid.clone();
        this.variable_OID = (OID)variable_OID.clone();
        this.type = BER.OID;
    }

    public Variable(OID variable_OID, Opaque opaque){
        this.opaque = (Opaque)opaque.clone();
        this.variable_OID = (OID)variable_OID.clone();
        this.type = BER.OPAQUE;
    }

    public Variable(OID variable_OID, TimeTicks timeTicks){
        this.timeTicks = (TimeTicks)timeTicks.clone();
        this.variable_OID = (OID)variable_OID.clone();
        this.type = BER.TIMETICKS;
    }


    public void encodeBER(OutputStream outputStream) throws IOException {
        // OID의 type, length, value 입력
        variable_OID.encodeBER(outputStream);

        switch(this.type) {
            case BER.INTEGER:
                integer32.encodeBER(outputStream);
                break;
            case BER.OCTETSTRING:
                octetString.encodeBER(outputStream);
                break;
            case BER.NULL:
                Null.encodeBER(outputStream);
                break;
            case BER.IPADDRESS:
                ipAddress.encodeBER(outputStream);
                break;
            case BER.COUNTER:
                counter32.encodeBER(outputStream);
                break;
            case BER.GAUGE:
                gauge32.encodeBER(outputStream);
                break;
            case BER.TIMETICKS:
                timeTicks.encodeBER(outputStream);
                break;
            case BER.OPAQUE:
                opaque.encodeBER(outputStream);
                break;
        }
    }

    public void decodeBER(BERInputStream inputStream) throws IOException {
        BER.MutableByte type_OID = new BER.MutableByte();


        // decode variable_OID
        int[] newValueOID = BER.decodeOID(inputStream, type_OID);


        this.variable_OID.setValue(newValueOID);
        inputStream.mark(1);

        this.type = (byte)inputStream.read();
        System.out.println(this.type);
        inputStream.reset();
        switch(this.type) {

            case BER.INTEGER:
                integer32.decodeBER(inputStream);
                break;
            case BER.OCTETSTRING:
                octetString.decodeBER(inputStream);
                break;
            case BER.NULL:
                Null.decodeBER(inputStream);
                break;
            case BER.IPADDRESS:
                ipAddress.decodeBER(inputStream);
                break;
            case BER.COUNTER:
                counter32.decodeBER(inputStream);
                break;
            case BER.GAUGE:
                gauge32.decodeBER(inputStream);
                break;
            case BER.TIMETICKS:
                timeTicks.decodeBER(inputStream);
                break;
            case BER.OPAQUE:
                opaque.decodeBER(inputStream);
                break;
            case BER.OID:
                oid.decodeBER(inputStream);
                break;
        }

    }

    // 전체 BER 길이를 구함(tag + length + value의 length) -> SNMP4j 참고함
    public final int getBERLength() {
        int length = getBERPayloadLength();

        length += BER.getBERLengthOfLength(length) + 1;
        return length;
    }

    public int getBERPayloadLength(){
        int length_OID = variable_OID.getBERLength();
        int length_variable = 0;
        switch(this.type) {
            case BER.INTEGER:
                length_variable = integer32.getBERLength();
                break;
            case BER.OCTETSTRING:
                length_variable = octetString.getBERLength();
                break;
            case BER.NULL:
                length_variable = Null.getBERLength();
                break;
            case BER.IPADDRESS:
                length_variable = ipAddress.getBERLength();
                break;
            case BER.COUNTER:
                length_variable = counter32.getBERLength();
                break;
            case BER.GAUGE:
                length_variable = gauge32.getBERLength();
                break;
            case BER.TIMETICKS:
                length_variable = timeTicks.getBERLength();
                break;
            case BER.OID:
                length_variable = oid.getBERLength();
                break;
            case BER.OPAQUE:
                length_variable = opaque.getBERLength();
                break;
        }

        return length_OID + length_variable;
    }
    public Object clone(){
        switch(this.type) {
            case BER.INTEGER:
                return new Variable(variable_OID, integer32);
            case BER.OCTETSTRING:
                return new Variable(variable_OID, octetString);
            case BER.NULL:
                return new Variable(variable_OID, Null);
            case BER.IPADDRESS:
                return new Variable(variable_OID, ipAddress);
            case BER.COUNTER:
                return new Variable(variable_OID, counter32);
            case BER.GAUGE:
                return new Variable(variable_OID, gauge32);
            case BER.TIMETICKS:
                return new Variable(variable_OID, timeTicks);
            case BER.OPAQUE:
                return new Variable(variable_OID, opaque);
            default:
                return null;
        }
    }

}

