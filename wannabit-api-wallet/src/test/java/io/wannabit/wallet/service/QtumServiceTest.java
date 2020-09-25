
package io.wannabit.wallet.service;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.wannabit.wallet.dto.QtumDto.ChangeRawTxForSendTokenDto;

public class QtumServiceTest extends UnitTestConfig {

  @Autowired QtumService qtumService;

  @Test
  public void testGetUTXOByAddr() {}

  @Test
  public void testGetTokenByAddr() {}

  @Test
  public void testGetTokenInfo() {}

  @Test
  public void testGetRawTransaction() {}

  @Test
  public void testCreateRawTransaction() {}

  @Test
  public void testSignRawTransaction() {}

  @Test
  public void testSendRawTransaction() {}

  @Test
  public void testChangeRawTxForSendToken() {
    // 잉크 토큰 송금 가정
    // 3 INK 송금 (3000000000 -> (16진수 변환) ->
    // QaNEpfs76GJA5LJM9zezHuCgCWDai4oBNx -> QQjxEZsLgF5JrcwWAb6r3VZj24BsKcSegQ
    // 사용가능한 잔액 : 2.2742228
    // 2.2742228 - 0.1 = 2.1742228
    // Gas Limit : 250000
    // Gas Price : 40
    ChangeRawTxForSendTokenDto changeRawTxForSendTokenDto = new ChangeRawTxForSendTokenDto();
    changeRawTxForSendTokenDto.setRawTransaction(
        "020000000275569e8fd02568cc21104e404b1b3e75a1f56aa68207424323d2b101d6f0e50e00000000ffffffff8b26aca9e3aaaba0e4555f9dae9e342bea4abeda2c78dbd4746e23fbb5eeeaba0000000000ffffffff01c899f50c000000001976a91496f8383521ea42c5826324095c73c4c5bdd2982b88ac00000000");
    changeRawTxForSendTokenDto.setContractAddress("fe59cbc17e89a698571413a81f0de9d8f00c69");
    changeRawTxForSendTokenDto.setTo("QQjxEZsLgF5JrcwW6r3VZj24BsKcSegQ");
    changeRawTxForSendTokenDto.setTokenAmountHex("b2d000");
    changeRawTxForSendTokenDto.setGasLimit("d090");
    changeRawTxForSendTokenDto.setGasPrice("8");

    Map<String, String> map = qtumService.changeRawTxForSendToken(changeRawTxForSendTokenDto);
    System.out.println("result: " + map.get("rawTransaction"));

    assertEquals(
        "020000000275569e8fd02568cc2116004e401b3e75a1f56aa68207424323d2b101d6f0e50e00000000ffffffff8b26aca9e3aaaba0e4555f9dae9e342bea4abeda2c78dbd4746e23fbb5eeeaba0000000000ffffffff02c899f50c000000001976a91496f8383521ea42c5826324095c73c4c5bdd2982b88ac00000000000000006301040390d003012844a9059cbb0000000000000000000000002d624413bd3b654057ef96120f8f5f371d825b7400000000000000000000000000000000000000000000000000000000b2d05e0014fe59cbc1704e89a698571413a81f0de9d8f00c69c200000000",
        map.get("rawTransaction"));
  }

  @Test
  public void testLength() {
    String txHash = "0x5994a9aa97099e6ef98ebe86a3768cb59dc12c62373609c4a41630e6c0c9e8";
    System.out.println(txHash.length());
  }
}

