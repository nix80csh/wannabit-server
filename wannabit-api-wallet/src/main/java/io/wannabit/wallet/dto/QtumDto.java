package io.wannabit.wallet.dto;

import java.util.List;

import lombok.Data;

public class QtumDto {

  @Data
  public static class UTXODto {
    private String txid;
    private Integer n;
    private String scriptPubKey;
    private String address;
    private Double value;
    private String type;
  }

  @Data
  public static class TokenBalanceDto {
    private String value;
    private Integer decimals;
  }

  @Data
  public static class TokenInfoDto {
    private String name;
    private String symbol;
    private Integer decimals;
  }

  @Data
  public static class TxMaterialDto {
    private List<InputDto> inputDtoList;
    private List<OutputDto> outputDtoList;
  }

  @Data
  public static class InputDto {
    private String txid;
    private String n;
    private String scriptPubKey;
  }

  @Data
  public static class OutputDto {
    private String address;
    private String value;
  }

  @Data
  public static class SignTxMaterialDto {
    private String rawTransaction;
    private List<InputDto> inputDtoList;
    private String privateKey;
  }

  @Data
  public static class SignedRawTransactionDto {
    private Integer idfAccount;
    private String signedRawTransaction;
  }

  @Data
  public static class TxInfoForSaveDto {
    private Integer idfAccount;
    private String symbol;
    private String txid;
  }

  @Data
  public static class ChangeRawTxForSendTokenDto {
    private String rawTransaction;
    private String contractAddress;
    private String to;
    private String tokenAmountHex;
    private String gasLimit;
    private String gasPrice;
  }
}
