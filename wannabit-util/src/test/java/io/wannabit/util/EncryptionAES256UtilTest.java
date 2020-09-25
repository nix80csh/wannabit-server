package io.wannabit.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

public class EncryptionAES256UtilTest {

  @Test
  public void testEncode() throws InvalidKeyException, UnsupportedEncodingException,
      NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
      IllegalBlockSizeException, BadPaddingException {
    System.out.println("암호화 : " + EncryptionAES256Util.encode("44'/88'/0'/0/0"));
    System.out.println("길이 : " + EncryptionAES256Util.encode("44'/88'/0'/0/0").length());
  }

  @Test
  public void testDecode() throws InvalidKeyException, UnsupportedEncodingException,
      NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
      IllegalBlockSizeException, BadPaddingException {
    System.out.println("복호화 : " + EncryptionAES256Util.decode("k228AJ9NHrba+1MFhssgaQ=="));
  }

}
