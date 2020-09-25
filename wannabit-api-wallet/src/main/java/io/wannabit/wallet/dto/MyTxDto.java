package io.wannabit.wallet.dto;

import lombok.Data;

@Data
public class MyTxDto {
  private int idfAccountTx;
  private int idfAccount;
  private String typeBlockchain;
  private String txhash;
  private String confirmations;
  private String timestamp;
  private String value;
  private String symbol;
}
