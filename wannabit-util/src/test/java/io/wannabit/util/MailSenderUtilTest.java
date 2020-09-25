package io.wannabit.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class MailSenderUtilTest {

  @Test
  public void testSend() {
    assertTrue(MailSenderUtil.send("hoon@wannabit.io", "test", "asdflsadhfkjashdfkjh"));
  }

}
