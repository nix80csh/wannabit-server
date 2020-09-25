package io.wannabit.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionSha256Util {

  public static String getEncSHA256(String txt) {
    StringBuffer sbuf = new StringBuffer();

    MessageDigest mDigest;
    try {
      mDigest = MessageDigest.getInstance("SHA-256");
      mDigest.update(txt.getBytes());

      byte[] msgStr = mDigest.digest();

      for (int i = 0; i < msgStr.length; i++) {
        byte tmpStrByte = msgStr[i];
        String tmpEncTxt = Integer.toString((tmpStrByte & 0xff) + 0x100, 16).substring(1);
        sbuf.append(tmpEncTxt);
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return sbuf.toString();
  }
}
