package io.wannabit.wallet.dto;

import lombok.Data;

public class OTPDto {

  @Data
  public static class EnableOTPDto {
    private Integer idfAccount;
    private String otpKey;
    private String otpCode;
    private Boolean enableOTP;
  }

}
