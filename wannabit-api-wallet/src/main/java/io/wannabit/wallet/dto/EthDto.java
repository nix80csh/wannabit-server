package io.wannabit.wallet.dto;

import lombok.Data;

public class EthDto {

  @Data
  public static class TxInfoForSaveDto {
    private Integer idfAccount;
    private String symbol;
    private String txid;
  }
}
