package io.wannabit.wallet.dto;

import lombok.Data;

public class AuthDto {

  @Data
  public static class SignupDto {
    private String email;
    private String password;
    private String authCodeEmail;
    private String countryCode;
    private Boolean isRegist;
    private Boolean isSent;
  }

  @Data
  public static class SigninDto {
    private Integer idfAccount;
    private String email;
    private String password;
    private String otpKey;
    private String xAuthToken;
    private String authCodeEmail;
  }

  @Data
  public static class EncryptionDto {
    private String beforeEncryt;
    private String afterEncryt;
  }

  @Data
  public static class VerifyEmailDto {
    private String email;
    private String authCodeEmail;
  }

  @Data
  public static class VerifyOTPDto {
    private Integer idfAccount;
    private String otpCode;
  }

  @Data
  public static class VerifyAuthCodePasswordDto {
    private String email;
    private String authCodePassword;
  }


  @Data
  public static class ForgotPwdDto {
    private String email;
    private String countryCode;
  }

  @Data
  public static class ResetPwdDto {
    private String password;
    private String email;
    private String authCodePassword;
  }

  @Data
  public static class QnaDto {
    private String email;
    private String content;
    private String countryCode;
  }

}
