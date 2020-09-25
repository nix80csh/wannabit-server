package io.wannabit.wallet.dto;

import java.util.List;

import lombok.Data;

public class MyWalletDto {

  @Data
  public static class SaveAddrDto {
    private Integer idfAccount;
    private String addr;
    private String signMaterial;
    private String typeMedia;
    private String name;
    private String typeBlockchain;
  }

  @Data
  public static class RemoveAddrDto {
    private Integer idfAccount;
    private String addr;
  }

  @Data
  public static class GetAddrDto {
    private Integer idfAccount;
    private String addr;
    private String name;
    private String typeMedia;
    private String typeBlockchain;
    private String signMaterial;
  }

  @Data
  public static class TokenDto {
    private Integer idfAccount;
    private String typeBlockchain;
    private List<TokenInfoDto> tokenList;
  }

  @Data
  public static class TokenInfoDto {
    private String addr;
    private String name;
    private String symbol;
    private String decimals;
  }

  @Data
  public static class ModifyAddrNameDto {
    private Integer idfAccount;
    private String addr;
    private String name;
  }

}
