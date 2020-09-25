package io.wannabit.wallet.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.wannabit.wallet.dto.MyWalletDto.SaveAddrDto;
import io.wannabit.wallet.dto.MyWalletDto.TokenDto;

public class MyWalletServiceTest extends UnitTestConfig {

  @Autowired MyWalletService myWalletService;

  @Test
  public void testSaveToken() {
    TokenDto tokenDto = new TokenDto();
    tokenDto.setIdfAccount(77);
    tokenDto.setTypeBlockchain("QTUM");
    List<String> tokenList = new ArrayList<>();
    // tokenList.add("fe59cbc1704e89a6985713a81f0de9d8f00c69");
    // tokenList.add("b27d7bf95b03e02b55d5eb63d3f12762101bf9");
    // tokenDto.setTokenList(tokenList);
    myWalletService.saveToken(tokenDto);
  }

  @Test
  public void testRemoveToken() {
    TokenDto tokenDto = new TokenDto();
    tokenDto.setIdfAccount(77);
    List<String> tokenList = new ArrayList<>();
    // tokenList.add("fe59cbc1704e89a6985714131f0de9d8f00c69");
    // tokenList.add("b27d7bf95b03e02b55d5eb63d3692762101bf9");
    // tokenDto.setTokenList(tokenList);
    myWalletService.removeToken(tokenDto);
  }

  @Test
  public void saveAddr() {
    SaveAddrDto saveAddrDto = new SaveAddrDto();
    saveAddrDto.setIdfAccount(87);
    saveAddrDto.setAddr("0x900b8ccae58675f6ee0ea1f1f4364957bc88461");
    saveAddrDto.setSignMaterial("303369f189bdf48b9e0609680e88f37bba272bbf52338508a9c7b1ee8453bb");
    saveAddrDto.setTypeMedia("P");
    saveAddrDto.setName("ico 참여용");
    saveAddrDto.setTypeBlockchain("ETH");
    try {
      System.out.println(myWalletService.saveAddr(saveAddrDto));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testGetTokenList() {
    TokenDto tokenDto = new TokenDto();
    tokenDto.setIdfAccount(77);
    tokenDto.setTypeBlockchain("QTUM");
    myWalletService.getTokenList(tokenDto);
  }

}
