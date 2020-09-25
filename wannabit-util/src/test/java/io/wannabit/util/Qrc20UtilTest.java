package io.wannabit.util;

import static org.junit.Assert.fail;

import org.junit.Test;

public class Qrc20UtilTest {

  @Test
  public void testAddressToHash160() {
    fail("Not yet implemented");
  }

  @Test
  public void testConvertAmountToHex() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetAmount() {

    String amountHex1 = "000000000000000000000000000000000000000000000013e20c57dddae00001";
    String amountHex2 = "0000000000000000000000000000000000000000000000000000000719f11100";

    Long decimal = Long.parseLong(amountHex1, 16);

    System.out.println(decimal);

  }



  private static long parse(byte[] data, int offset) {
    return ((data[offset] & 0xffL) << 24) | ((data[offset + 1] & 0xffL) << 16)
        | ((data[offset + 2] & 0xffL) << 8) | (data[offset + 3] & 0xffL);
  }

  private static byte[] parse(long number) {
    byte[] data = new byte[4];

    data[0] = (byte) ((number >> 24) & 0xff);
    data[1] = (byte) ((number >> 16) & 0xff);
    data[2] = (byte) ((number >> 8) & 0xff);
    data[3] = (byte) (number & 0xff);

    return data;
  }


  @Test
  public void testConvertHexToDec() {
    String hex = "0000000000000000000000000000000000000005f5e100";
    Long decimal = Long.parseLong(hex, 16);
    System.out.println(decimal);
  }


  @Test
  public void testTo32bytesArg() {
    fail("Not yet implemented");
  }

  @Test
  public void testByteArrayToHex() {
    fail("Not yet implemented");
  }

  @Test
  public void testLeftPad() {
    fail("Not yet implemented");
  }

  @Test
  public void testRound() {
    fail("Not yet implemented");
  }

}
