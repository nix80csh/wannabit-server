package io.wannabit.util;

import org.junit.Test;

public class EncryptionBase58UtilTest {

  @Test
  public void testEncode() {

  }

  @Test
  public void testDecode() {
    String addr = "QjNvFQsYjpPkQGPVMW81F2JpuAhySvk29j";
    byte[] bytes = EncryptionBase58Util.decode(addr);
    System.out.println("decode: " + bytes);
  }

}
