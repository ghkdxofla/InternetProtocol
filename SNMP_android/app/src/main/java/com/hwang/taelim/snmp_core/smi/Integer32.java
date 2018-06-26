package com.hwang.taelim.snmp_core.smi;

import com.hwang.taelim.snmp_core.BER;
import com.hwang.taelim.snmp_core.BERInputStream;
import com.hwang.taelim.snmp_core.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;

public class Integer32 implements BERSerializable{
  private static final long serialVersionUID = 5046132399890132416L;
  private int value = 0;
  private int type = BER.INTEGER;

  public Integer32() {
  }

  public Integer32(int value) {
    setValue(value);
  }

  public Integer32(String value) {
    setValue(value);
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeInteger(outputStream, BER.INTEGER, value);
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    int newValue = BER.decodeInteger(inputStream, type);
    if (type.getValue() != BER.INTEGER) {
      throw new IOException("Wrong type: "+type.getValue());
    }
    setValue(newValue);
  }

  // 전체 BER 길이를 구함(tag + length + value의 length) -> SNMP4j 참고함
  public int getBERLength() {
    if ((value < 0x80) && (value >= -0x80)) {
      return 3;
    }
    else if ((value < 0x8000) && (value >= -0x8000)) {
      return 4;
    }
    else if ((value < 0x800000) && (value >= -0x800000)) {
      return 5;

    }
    return 6;
  }

  // integer value만의 길이를 구함
  public int getBERPayloadLength(){
    if ((value < 0x80) && (value >= -0x80)) {
      return 1;
    }
    else if ((value < 0x8000) && (value >= -0x8000)) {
      return 2;
    }
    else if ((value < 0x800000) && (value >= -0x800000)) {
      return 3;

    }
    return 4;
  }

  // 문자로된 정수 변환
  public final void setValue(String value) {
    this.value = Integer.parseInt(value);
  }
  // 받아온 정수 지정
  public final void setValue(int value) {
    this.value = value;
  }
  // get value
  public final int getValue() {
    return value;
  }
  public Object clone() {
    return new Integer32(value);
  }
}

