package io.wannabit.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SlackNotificationUtilTest {

  @Test
  public void testSend() {
    assertTrue(SlackNotificationUtil.send("api-test1", "Wannabit Bot",
        "\n " + "현재까지 가입한 수: 1\n" + "가입한 이메일: test@gmail.com"));
  }
}
