package io.wannabit.wallet.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.wannabit.wallet.dto.AuthDto.SignupDto;


public class AuthServiceTest extends UnitTestConfig {

  @Autowired AuthService authService;

  @Before
  public void init() {}

  @Test
  public void testSignup() {}

  @Test
  public void testSignin() {}

  @Test
  public void testSendAuthEmail() {
    SignupDto signupDto = new SignupDto();
    signupDto.setEmail("jayb@wannabit.io");
    signupDto.setPassword("asdf");
    authService.sendAuthEmail(signupDto);
  }

  @Test
  public void testForgotPwd() {}

  @Test
  public void testResetPwd() {}

}
