package io.wannabit.wallet.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.wannabit.util.EncryptionSha256Util;
import io.wannabit.wallet.dto.AuthDto.EncryptionDto;
import io.wannabit.wallet.dto.AuthDto.QnaDto;
import io.wannabit.wallet.dto.AuthDto.ResetPwdDto;
import io.wannabit.wallet.dto.AuthDto.SigninDto;
import io.wannabit.wallet.dto.AuthDto.SignupDto;
import io.wannabit.wallet.dto.AuthDto.VerifyAuthCodePasswordDto;
import io.wannabit.wallet.dto.AuthDto.VerifyEmailDto;
import io.wannabit.wallet.dto.AuthDto.VerifyOTPDto;
import io.wannabit.wallet.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired AuthService authService;

  @RequestMapping(value = "/encryptionSha256", method = RequestMethod.POST)
  public EncryptionDto encryptionSha256(@RequestBody EncryptionDto encryptionDto) {
    String encStr = EncryptionSha256Util.getEncSHA256(encryptionDto.getBeforeEncryt());
    encryptionDto.setAfterEncryt(encStr);
    return encryptionDto;
  }

  @RequestMapping(value = "/signup", method = RequestMethod.POST)
  public SignupDto signup(@RequestBody SignupDto signupDto) {
    return authService.signup(signupDto);
  }

  @RequestMapping(value = "/signin", method = RequestMethod.POST)
  public SigninDto signin(@RequestBody SigninDto signinDto) {
    return authService.signin(signinDto);
  }

  @RequestMapping(value = "/sendAuthEmail", method = RequestMethod.POST)
  public SignupDto sendAuthEmail(@RequestBody SignupDto signupDto) {
    return authService.sendAuthEmail(signupDto);
  }

  @RequestMapping(value = "/verifyEmail", method = RequestMethod.POST)
  public Map<String, Boolean> verifyEmail(@RequestBody VerifyEmailDto verifyEmailDto) {
    return authService.verifyEmail(verifyEmailDto);
  }

  @RequestMapping(value = "/verifyOtp", method = RequestMethod.POST)
  public Map<String, Boolean> verifyOtp(@RequestBody VerifyOTPDto verifyOTPDto) {
    return authService.verifyOtp(verifyOTPDto);
  }

  @RequestMapping(value = "/sendResetPwdEmail/{email}/{countryCode}", method = RequestMethod.GET)
  public Map<String, Boolean> sendResetPwdEmail(@PathVariable String email,
      @PathVariable String countryCode) {
    return authService.sendResetPwdEmail(email, countryCode);
  }

  @RequestMapping(value = "/verifyAuthCodeEmail", method = RequestMethod.POST)
  public Map<String, Boolean> verifyAuthCodeEmail(
      @RequestBody VerifyAuthCodePasswordDto verifyAuthCodePasswordDto) {
    return authService.verifyAuthCodeEmail(verifyAuthCodePasswordDto);
  }

  @RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
  public Map<String, Boolean> resetPwd(@RequestBody ResetPwdDto resetPwdDto) {
    return authService.resetPwd(resetPwdDto);
  }

  @RequestMapping(value = "/sendQnaEmail", method = RequestMethod.POST)
  public Map<String, Boolean> sendQnaEmail(@RequestBody QnaDto qnaDto) {
    return authService.sendQnaEmail(qnaDto);
  }


}
