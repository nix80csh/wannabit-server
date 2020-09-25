package io.wannabit.util;

import org.apache.commons.lang3.StringUtils;

public class Qrc20Util {

  public static String addressToHash160(String addr) {
    String decodedAddr = byteArrayToHex(EncryptionBase58Util.decode(addr));
    return decodedAddr.substring(2, 42);
  }

  public static Integer convertHexToDec(String hex) {
    return (int) Long.parseLong(hex, 16);
  }

  public static String to32bytesArg(String arg) {
    return leftPad(arg, 64, '0');
  }

  public static String byteArrayToHex(byte[] a) {
    StringBuilder sb = new StringBuilder();
    for (final byte b : a)
      sb.append(String.format("%02x", b & 0xff));
    return sb.toString();
  }

  public static String leftPad(String str, int size, char padChar) {
    return StringUtils.leftPad(str, size, padChar);
  }

  public static double round(double a) {
    double b = Math.round(a * 100000000d) / 100000000d;
    return b;
  }
}
