package com.hwang.taelim.snmp_core.smi;

import com.hwang.taelim.snmp_core.BER;
import com.hwang.taelim.snmp_core.BERInputStream;

import java.io.IOException;
import java.io.OutputStream;

public class Opaque extends OctetString{

  private static final long serialVersionUID = -17056771587100877L;
  private int type = BER.OPAQUE;
  public Opaque() {
    super();
  }

  public Opaque(byte[] bytes) {
    super(bytes);
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    BER.encodeString(outputStream, BER.OPAQUE, getValue());
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    byte[] v = BER.decodeString(inputStream, type);
    if (type.getValue() != (BER.ASN_APPLICATION | 0x04)) {
      throw new IOException("Wrong type: "+
                            type.getValue());
    }
    setValue(v);
  }

  public void setValue(OctetString value) {
    this.setValue(new byte[0]);
    append(value);
  }
  public Object clone() {
    return new Opaque(super.getValue());
  }
}

