package io.wannabit.util;

import org.junit.Test;

public class AmazonSimpleEmailUtilTest {

  @Test
  public void testSend() {
    String toName = "jayb@wannabit.io";
    String subject = "TEST 테스트";
    String body = "테스트";
    AmazonSimpleEmailUtil.send(toName, subject, body);
  }

}
