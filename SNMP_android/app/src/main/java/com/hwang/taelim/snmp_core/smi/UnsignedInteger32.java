package com.hwang.taelim.snmp_core.smi;

import com.hwang.taelim.snmp_core.BER;
import com.hwang.taelim.snmp_core.BERInputStream;
import com.hwang.taelim.snmp_core.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;


public class UnsignedInteger32 implements BERSerializable {

  private static final long serialVersionUID = -2155365655395258383L;

  protected long value = 0;
  private int type = BER.INTEGER;
  public UnsignedInteger32() {
  }

  public UnsignedInteger32(long value) {
    setValue(value);
  }

  public UnsignedInteger32(String value) {
    setValue(value);
  }

  public UnsignedInteger32(int signedIntValue) {
    setValue(signedIntValue & 0xFFFFFFFFL);
  }

  public UnsignedInteger32(byte signedByteValue) {
    setValue(signedByteValue & 0xFF);
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeUnsignedInteger(outputStream, BER.GAUGE, value);
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    long newValue = BER.decodeUnsignedInteger(inputStream, type);
    if (type.getValue() != BER.GAUGE)
      throw new IOException("Wrong type: "+ type.getValue());

    setValue(newValue);
  }

  public int getBERPayloadLength(){
    if (value < 0x80L)
      return 1;
    else if (value < 0x8000L)
      return 2;
    else if (value < 0x800000L)
      return 3;
    else if (value < 0x80000000L)
      return 4;
    return 5;
  }

  public int getBERLength() {
    if (value < 0x80L)
      return 3;
    else if (value < 0x8000L)
      return 4;
    else if (value < 0x800000L)
      return 5;
    else if (value < 0x80000000L)
      return 6;
    return 7;
  }

  public void setValue(String value) {
    setValue(Long.parseLong(value));
  }

  public void setValue(long value) {
    if ((value < 0) || (value > 4294967295L)) {
      throw new IllegalArgumentException();
    }
    this.value = value;
  }

  public long getValue() {
    return value;
  }
  public Object clone() {
    return new UnsignedInteger32(value);
  }

}

