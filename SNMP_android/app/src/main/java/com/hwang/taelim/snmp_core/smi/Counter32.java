package com.hwang.taelim.snmp_core.smi;


import com.hwang.taelim.snmp_core.BER;
import com.hwang.taelim.snmp_core.BERInputStream;

import java.io.IOException;
import java.io.OutputStream;


public class Counter32 extends UnsignedInteger32 {

  private static final long serialVersionUID = 6140742767439142144L;
  // 최대 counter32의 value(snmp4j 참조)
  public static final long MAX_COUNTER32_VALUE = 4294967295L;
  // 명시를 위한 타입
  private int type = BER.COUNTER32;

  // 기본 생성자
  public Counter32() {
  }

  // value를 받는 생성자
  public Counter32(long value) {
    super(value);
  }

  public Counter32(String value) {
    super(value);
  }

  //encode
  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeUnsignedInteger(outputStream, BER.COUNTER32, getValue());
  }

  //decode
  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    long newValue = BER.decodeUnsignedInteger(inputStream, type);
    if (type.getValue() != BER.COUNTER32) {
      throw new IOException("Wrong type: "+ type.getValue());
    }
    setValue(newValue);
  }

  // counter 증가
  public void increment() {
    if (value < MAX_COUNTER32_VALUE) {
      value++;
    } else {
      value = 0;
    }
  }

  // counter에 n만큼 증가하는 변수가 들어올 경우(snmp4j 참조)
  public long increment(long increment) {
    if (increment > 0) {
      if (value + increment < MAX_COUNTER32_VALUE) {
        value += increment;
      }
      else {
        value = increment - (MAX_COUNTER32_VALUE - value);
      }
    }
    else if (increment < 0) {
      throw new IllegalArgumentException("Negative increments not allowed for counters: "+increment);
    }
    return value;
  }
  public Object clone() {
    return new Counter32(value);
  }

}

