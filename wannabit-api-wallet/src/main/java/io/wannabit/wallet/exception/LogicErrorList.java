package io.wannabit.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogicErrorList {

  DuplicateEntity_Account(101, "DuplicateEntity_Account"),

  DuplicateEntity_AccountWallet(102, "DuplicateEntity_AccountWallet"),

  DuplicateValue_AccountTx_Txhash(103, "DuplicateValue_AccountTx_Txhash"),

  DoesNotExist_Email(201, "DoesNotExist_Email"), DoesNotExist_Account(202, "DoesNotExist_Account"),

  DoesNotExist_OTPKey(203, "DoesNotExist_OTPKey"),

  DoesNotExist_AccountWallet(204, "DoesNotExist_AccountWallet"),

  FailedSlackNotification(301, "FailedSlackNotification"),

  FailedQtumTransaction(801, "FailedQtumTransaction"),

  FailedQtumTxnMempoolConflict(802, "FailedQtumTxnMempoolConflict"),

  FailedTxDueToDust(803, "FailedTxDueToDust"),

  NotMatched(901, "NotMatched"), SMSModuleException(902, "SMSModuleException"),

  MailModuleException(903, "MailModuleException"), NoLongerVaild(904, "NoLongerVaild"),

  NotConfirmed(905, "NotConfirmed"), EnabledOTP(906, "EnabledOTP"),

  NotMatchedOtpKey(907, "NotMatchedOtpKey"), NotMatchedOtpCode(908, "NotMatchedOtpCode"),

  NotVerifyEmail(909, "NotVerifyEmail"),

  NotMatchedAuthCodePassword(910, "NotMatchedAuthCodePassword");

  private final int errorCode;
  private final String errorMsg;

}
