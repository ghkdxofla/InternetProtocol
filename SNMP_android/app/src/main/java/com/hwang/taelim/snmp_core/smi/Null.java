package com.hwang.taelim.snmp_core.smi;

import com.hwang.taelim.snmp_core.BER;
import com.hwang.taelim.snmp_core.BERInputStream;
import com.hwang.taelim.snmp_core.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;

public class Null implements BERSerializable {

  private static final long serialVersionUID = 6907924131098190092L;
  private int value = BER.NULL;
  private int type = BER.NULL;
  public Null() {
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    BER.decodeNull(inputStream, type);
  }

  public int getBERLength() {
    return 2;
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeHeader(outputStream, BER.NULL, 0);
  }

  public int getBERPayloadLength() {return 0;}
  public Object clone() {
    return new Null();
  }
}

