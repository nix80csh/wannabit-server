package io.wannabit.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class EncryptionSha256UtilTest {

  @Test
  public void testGetEncSHA256() {
    List<String> beforeEncryptList = new ArrayList<String>();
    beforeEncryptList.add("whtjdgns00!!");
    beforeEncryptList.add("whtjdgns");
    beforeEncryptList.add("12345!!");

    for (int i = 0; i <= beforeEncryptList.size() - 1; i++) {
      String afterEncrpyt = EncryptionSha256Util.getEncSHA256(beforeEncryptList.get(i));
      System.out.println(afterEncrpyt);
      System.out.println(afterEncrpyt.length());
      assertEquals(64, afterEncrpyt.length());
    }
  }



}
