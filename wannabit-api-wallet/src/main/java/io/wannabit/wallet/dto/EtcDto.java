package io.wannabit.wallet.dto;

import lombok.Data;

public class EtcDto {

  @Data
  public static class BalanceDto {
    private String value;
  }

  @Data
  public static class GetTxParamsDto {
    private Integer idfAccount;
    private String from;
    private String to;
    private String value;
    private String gasPrice;
    private String gasLimit;
  }

  @Data
  public static class RawTxDto {
    private String nonce;
    private String gasPrice;
    private String gasLimit;
    private String to;
    private String value;
    private int chainId;
  }

  @Data
  public static class SignedRawTxDto {
    private Integer idfAccount;
    private String signedRawTx;
  }

  @Data
  public static class TxInfoForSaveDto {
    private Integer idfAccount;
    private String symbol;
    private String txid;
  }

}
