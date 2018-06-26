package com.hwang.taelim.snmp_core.smi;


import com.hwang.taelim.snmp_core.BER;

public class Gauge32 extends UnsignedInteger32 {

  static final long serialVersionUID = 1469573439175461445L;
  private int type = BER.GAUGE;
  public Gauge32() {
  }

  public Gauge32(long value) {
    super(value);
  }
  public Gauge32(String value) {
    super(value);
  }
  public Object clone() {
    return new Gauge32(value);
  }
}

