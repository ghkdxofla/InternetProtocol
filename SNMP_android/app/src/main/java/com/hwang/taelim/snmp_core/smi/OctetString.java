package com.hwang.taelim.snmp_core.smi;

import com.hwang.taelim.snmp_core.BER;
import com.hwang.taelim.snmp_core.BERInputStream;
import com.hwang.taelim.snmp_core.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;

public class OctetString implements BERSerializable {

  private static final long serialVersionUID = 4125661211046256289L;


  private byte[] value = new byte[0];
  private int length = 0;
  private int type = BER.OCTETSTRING;

  public OctetString() {
  }


  public OctetString(byte[] rawValue) {
    this.value = rawValue;
    this.length = rawValue.length;
  }


  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeString(outputStream, BER.OCTETSTRING, getValue());
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();

    byte[] v = BER.decodeString(inputStream, type);

    if (type.getValue() != BER.OCTETSTRING) {
      throw new IOException("Wrong type: "+ type.getValue());
    }
    setValue(v);
  }

  public int getBERLength() {
    return value.length + BER.getBERLengthOfLength(value.length) + 1;
  }


  public final byte get(int index) {
    return value[index];
  }

  public final void set(int index, byte b) {
    value[index] = b;
  }

  public void setValue(String value) {
    setValue(value.getBytes());
  }

  public void setValue(byte[] value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }
    this.value = value;
  }

  public byte[] getValue() {
    return value;
  }

  public int getBERPayloadLength() {
    return value.length;
  }
  // append는 string간 연결을 위한 부분. snmp4j 참고함.
  public void append(byte b) {
    byte[] newValue = new byte[value.length+1];
    System.arraycopy(value, 0, newValue, 0, value.length);
    newValue[value.length] = b;
    value = newValue;
  }
  public void append(byte[] bytes) {
    byte[] newValue = new byte[value.length + bytes.length];
    System.arraycopy(value, 0, newValue, 0, value.length);
    System.arraycopy(bytes, 0, newValue, value.length, bytes.length);
    value = newValue;
  }
  public void append(OctetString octetString) {
    append(octetString.getValue());
  }
  public void append(String string) {
    append(string.getBytes());
  }
  public Object clone() {
    return new OctetString(value);
  }
}


