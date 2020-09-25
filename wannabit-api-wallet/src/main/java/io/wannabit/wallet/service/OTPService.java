package io.wannabit.wallet.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.wannabit.core.entity.Account;
import io.wannabit.core.entity.AccountWallet;
import io.wannabit.core.repository.AccountRepo;
import io.wannabit.core.repository.AccountWalletRepo;
import io.wannabit.util.OTPUtil;
import io.wannabit.wallet.dto.OTPDto.EnableOTPDto;
import io.wannabit.wallet.exception.LogicErrorList;
import io.wannabit.wallet.exception.LogicException;

@Service
@Transactional
public class OTPService {

  @Autowired AccountRepo accountRepo;
  @Autowired AccountWalletRepo accountWalletRepo;

  public EnableOTPDto enableOtp(EnableOTPDto enableOTPDto) {

    if (!enableOTPDto.getOtpKey().contains("-temp"))
      throw new LogicException(LogicErrorList.EnabledOTP);

    Account account = accountRepo.findOne(enableOTPDto.getIdfAccount());

    if (!account.getOtpKey().equals(enableOTPDto.getOtpKey()))
      throw new LogicException(LogicErrorList.NotMatchedOtpKey);

    String otpKey = enableOTPDto.getOtpKey().replace("-temp", "");
    Boolean isVerify = OTPUtil.verify(otpKey, Integer.valueOf(enableOTPDto.getOtpCode()));
    enableOTPDto.setEnableOTP(isVerify);

    if (!isVerify)
      throw new LogicException(LogicErrorList.NotMatchedOtpCode);

    account.setOtpKey(otpKey);
    accountRepo.save(account);

    enableOTPDto.setOtpCode(null);
    enableOTPDto.setOtpKey(null);

    return enableOTPDto;
  }

  public EnableOTPDto disableOtp(EnableOTPDto enableOTPDto) {

    Account account = accountRepo.findOne(enableOTPDto.getIdfAccount());

    if (!account.getOtpKey().equals(enableOTPDto.getOtpKey()))
      throw new LogicException(LogicErrorList.NotMatchedOtpKey);

    Boolean isVerify =
        OTPUtil.verify(enableOTPDto.getOtpKey(), Integer.valueOf(enableOTPDto.getOtpCode()));

    if (!isVerify)
      throw new LogicException(LogicErrorList.NotMatchedOtpCode);

    String otpKey = OTPUtil.create() + "-temp";
    account.setOtpKey(otpKey);
    accountRepo.save(account);
    enableOTPDto.setEnableOTP(false);

    enableOTPDto.setOtpKey(otpKey);
    enableOTPDto.setOtpCode(null);

    List<AccountWallet> accountWalletList =
        accountWalletRepo.findByAccountIdfAccount(account.getIdfAccount());
    for (AccountWallet accountWallet : accountWalletList) {
      BeanUtils.copyProperties(accountWallet, accountWalletList);
    }

    accountWalletRepo.deleteInBatch(accountWalletList);

    return enableOTPDto;
  }



}
