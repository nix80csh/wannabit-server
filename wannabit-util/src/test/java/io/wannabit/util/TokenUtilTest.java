package io.wannabit.util;

import org.junit.Test;

public class TokenUtilTest {

  @Test
  public void testCreateToken() {
    String email = "jayb@wannabit.io";
    String password = "d0500cb2a3e6f605204640770c1cbf4608503801fa326c1fb9fee86be8a3f3e7";
    String token = TokenUtil.createToken(email, password);
    System.out.println("createToken: " + token);
  }

  @Test
  public void testComputeSignature() {
    String email = "jayb@wannabit.io";
    String password = "d0500cb2a3e6f605204640770c1cbf4608503801fa326c1fb9fee86be8a3f3e7";
    Long expires = 604800L;
    String computeSignature = TokenUtil.computeSignature(email, password, expires);
    System.out.println("computeSignature: " + computeSignature);
  }

  @Test
  public void testGetUserIdFromToken() {
    String authToken = "jayb@wannabit.io=1518080687672=62377bfa39323542c2bd227781ddce7a";
    String email = TokenUtil.getUserIdFromToken(authToken);
    System.out.println("getUserIdFromToken: " + email);
  }

  @Test
  public void testValidateToken() {

    String authToken = "jayb@wannabit.io=1518080687672=62377bfa39323542c2bd227781ddce7a";
    String email = "jayb@wannabit.io";
    String password = "d0500cb2a3e6f605204640770c1cbf4608503801fa326c1fb9fee86be8a3f3e7";
    Boolean token = TokenUtil.validateToken(authToken, email, password);
    System.out.println("validateToken: " + token);
  }

}
