package io.wannabit.wallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.wannabit.wallet.dto.OTPDto.EnableOTPDto;
import io.wannabit.wallet.service.OTPService;

@RestController
@RequestMapping("/otp")
public class OTPController {

  @Autowired OTPService otpService;

  @RequestMapping(value = "/enableOtp", method = RequestMethod.POST)
  public EnableOTPDto enableOtp(@RequestBody EnableOTPDto enableOTPDto) {
    return otpService.enableOtp(enableOTPDto);
  }

  @RequestMapping(value = "/disableOtp", method = RequestMethod.POST)
  public EnableOTPDto disableOtp(@RequestBody EnableOTPDto enableOTPDto) {
    return otpService.disableOtp(enableOTPDto);
  }


}
