package com.hwang.taelim.snmp_core.smi;

import com.hwang.taelim.snmp_core.BER;
import com.hwang.taelim.snmp_core.BERInputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

public class TimeTicks extends UnsignedInteger32 {

  private static final long serialVersionUID = 8663761323061572311L;
  // 시간 변환용 상수. snmp4j 참고.
  private static final String FORMAT_PATTERN =
      "{0,choice,0#|1#1 day, |1<{0,number,integer} days, }"+
      "{1,number,integer}:{2,number,00}:{3,number,00}.{4,number,00}";
  private static final int[] FORMAT_FACTORS = { 24 * 60 * 60 * 100, 60 * 60 * 100, 60 * 100, 100, 1 };
  private int type = BER.TIMETICKS;
  // 기본 생성자
  public TimeTicks() {
  }
  // 다른 시간 데이터를 이용해 생성(snmp4j 참고)
  public TimeTicks(TimeTicks other) {
    this.value = other.value;
  }

  public TimeTicks(long value) {
    super(value);
  }

  public TimeTicks(String value) {
        super(value);
    }

  public void encodeBER(OutputStream os) throws IOException {
    BER.encodeUnsignedInteger(os, BER.TIMETICKS, super.getValue());
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    long newValue = BER.decodeUnsignedInteger(inputStream, type);
    if (type.getValue() != BER.TIMETICKS) {
      throw new IOException("Wrong type: "+type.getValue());
    }
    setValue(newValue);
  }

  public String toString() {
    return toString(FORMAT_PATTERN);
  }

  @Override
  // String으로 받은 timetick을 변환 후 값 지정(snmp4j 참고)
  public final void setValue(String value) {
    try {
      long v = Long.parseLong(value);
      setValue(v);
    }
    catch (NumberFormatException nfe) {
      long v = 0;
      String[] num = value.split("[days :,\\.]");
      int i = 0;
      for (String n : num) {
        if (n.length()>0) {
          long f = FORMAT_FACTORS[i++];
          v += (Long.parseLong(n) * f);
        }
      }
      setValue(v);
    }
  }

  public String toString(String pattern) {
    long hseconds, second, minute, hour, day;
    long timeTicks = getValue();

    day = timeTicks / 8640000;
    timeTicks %= 8640000;

    hour = timeTicks / 360000;
    timeTicks %= 360000;

    minute = timeTicks / 6000;
    timeTicks %= 6000;

    second = timeTicks / 100;
    timeTicks %= 100;

    hseconds = timeTicks;


    Long[] result = new Long[5];
    result[0] = day;
    result[1] = hour;
    result[2] = minute;
    result[3] = second;
    result[4] = hseconds;


    return MessageFormat.format(pattern, (Object[])result);
  }

  public Object clone() {
    return new TimeTicks(value);
  }
}

