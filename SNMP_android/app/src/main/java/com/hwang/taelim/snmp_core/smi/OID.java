package com.hwang.taelim.snmp_core.smi;

import com.hwang.taelim.snmp_core.BER;
import com.hwang.taelim.snmp_core.BERInputStream;
import com.hwang.taelim.snmp_core.BERSerializable;


import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class OID implements BERSerializable
{

  private static final long serialVersionUID = 7521667239352941172L;

  // OID null
  private static final int[] NULL_OID = new int[0];

  // 전송가능한 형태의 숫자로만 이루어진 OID
  private int[] value = NULL_OID;

  private int type = BER.OID;

  // OID 기본 생성자
  public OID(){};
  // OID 생성자(dotted oid)
  public OID(String oid) {
    value = parseDottedString(oid);
  }

  // OID 생성자(숫자로만 입력됨)
  public OID(int[] rawOID) {
    this(rawOID, 0, rawOID.length);
  }


  public OID(int[] prefixOID, int[] suffixOID) {
    this.value = new int[prefixOID.length+suffixOID.length];
    System.arraycopy(prefixOID, 0, value, 0, prefixOID.length);
    System.arraycopy(suffixOID, 0, value, prefixOID.length, suffixOID.length);
  }


  public OID(int[] prefixOID, int suffixID) {
    this.value = new int[prefixOID.length+1];
    System.arraycopy(prefixOID, 0, value, 0, prefixOID.length);
    this.value[prefixOID.length] = suffixID;
  }

  public OID(int[] rawOID, int offset, int length) {
    setValue(rawOID, offset, length);
  }

  public OID(OID other) {
    this(other.getValue());
  }


  // oid를 전송가능한 형태로 변환
  private static int[] parseDottedString(String oid) {
    String[] oidString = oid.split("\\.");
    int[] oidNumber = new int[oidString.length];
    for(int i = 0;i < oidNumber.length;i++)
      oidNumber[i] = Integer.parseInt(oidString[i]);

    return oidNumber;

  }

  // 점이 포함된 OID로 변경
  public String toDottedString() {
    StringBuilder str = new StringBuilder();
    for(int i : value){
      str.append(Integer.toString(i)).append(".");
    }
    str.deleteCharAt(str.length() - 1);
    return str.toString();
  }


  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeOID(outputStream, BER.OID, value);
  }

  public int getBERLength() {

    int length = BER.getOIDLength(value);
    return length + BER.getBERLengthOfLength(length) + 1;
  }



  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    int[] v = BER.decodeOID(inputStream, type);
    if (type.getValue() != BER.OID) {
      throw new IOException("Wrong type: "+ type.getValue());
    }
    setValue(v);
  }

  public void setValue(String value) {
    this.value = parseDottedString(value);
  }

  public final void setValue(int[] value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }
    this.value = value;
  }

  private void setValue(int[] rawOID, int offset, int length) {
    value = new int[length];
    System.arraycopy(rawOID, offset, value, 0, length);
  }

  public final int[] getValue() {
    return value;
  }

  public int getBERPayloadLength(){
    return BER.getOIDLength(value);
  }
  public Object clone() {
    return new OID(value);
  }
}

