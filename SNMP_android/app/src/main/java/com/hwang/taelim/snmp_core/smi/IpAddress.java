package com.hwang.taelim.snmp_core.smi;

import com.hwang.taelim.snmp_core.BER;
import com.hwang.taelim.snmp_core.BERInputStream;
import com.hwang.taelim.snmp_core.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class IpAddress implements BERSerializable{

  private static final long serialVersionUID = -146846354059565449L;
  // 빈 IP주소
  private static final byte[] INITADDRESS = { 0,0,0,0 };
  // 임의의 IP주소 생성
  public static final InetAddress ANY_IPADDRESS = createAnyAddress();

  private InetAddress inetAddress;

  public IpAddress() {
    this.inetAddress = ANY_IPADDRESS;
  }
  // InetAddress 형식으로 생성
  public IpAddress(InetAddress address) {
    if (address == null) {
      throw new NullPointerException();
    }
    this.inetAddress = address;
  }
  // String 형식의 IP주소로 생성
  public IpAddress(String address) {
    if (!parseAddress(address)) {
      throw new IllegalArgumentException(address);
    }
  }
  // Byte 형식으로 생성(snmp4j 참고)
  public IpAddress(byte[] addressBytes) {
    try {
      this.inetAddress = InetAddress.getByAddress(addressBytes);
    }
    catch (UnknownHostException ex) {
      throw new IllegalArgumentException("Unknown host: "+ex.getMessage());
    }
  }
  // IP주소 to String
  public String toString() {
    if (inetAddress != null) {
      String addressString = inetAddress.toString();
      return addressString.substring(addressString.indexOf('/') + 1);
    }
    return "0.0.0.0";
  }

  // IP parsing
  public boolean parseAddress(String address) {
    try {

      inetAddress = InetAddress.getByName(address);
      return true;
    }
    catch (UnknownHostException uhex) {
      return false;
    }
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    BER.MutableByte type = new BER.MutableByte();
    byte[] value = BER.decodeString(inputStream, type);
    if (type.getValue() != BER.IPADDRESS)
      throw new IOException("Wrong type: "+ type.getValue());

    if (value.length != 4)
      throw new IOException("Wrong length: " + value.length);

    inetAddress = InetAddress.getByAddress(value);
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    byte[] address = new byte[4];

    System.arraycopy(inetAddress.getAddress(), 0, address, 0, 4);

    BER.encodeString(outputStream, BER.IPADDRESS, address);
  }
  // IPv4는 4자리 이므로 4 + 2
  public int getBERLength() {
    return 6;
  }

  public void setAddress(byte[] rawValue) throws UnknownHostException {
    this.inetAddress = InetAddress.getByAddress(rawValue);
  }

  public void setInetAddress(InetAddress inetAddress) {
    this.inetAddress = inetAddress;
  }

  public InetAddress getInetAddress() {
    return inetAddress;
  }

  private static InetAddress createAnyAddress() {
    try {
      return InetAddress.getByAddress(INITADDRESS);
    }
    catch (Exception ex) {

    }
    return null;
  }

  public void setValue(String value) {
    if (!parseAddress(value))
      throw new IllegalArgumentException(value+" cannot be parsed by "+ getClass().getName());

  }

  public void setValue(byte[] value) {
    try {
      setAddress(value);
    }
    catch (UnknownHostException ex) {
      throw new RuntimeException(ex);
    }
  }
  public int getBERPayloadLength(){return 4;}
  public Object clone() {
    return new IpAddress(inetAddress);
  }

}

