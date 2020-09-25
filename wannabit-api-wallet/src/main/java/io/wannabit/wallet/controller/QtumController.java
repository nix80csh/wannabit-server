package io.wannabit.wallet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.wannabit.wallet.dto.QtumDto.ChangeRawTxForSendTokenDto;
import io.wannabit.wallet.dto.QtumDto.SignTxMaterialDto;
import io.wannabit.wallet.dto.QtumDto.SignedRawTransactionDto;
import io.wannabit.wallet.dto.QtumDto.TokenBalanceDto;
import io.wannabit.wallet.dto.QtumDto.TokenInfoDto;
import io.wannabit.wallet.dto.QtumDto.TxInfoForSaveDto;
import io.wannabit.wallet.dto.QtumDto.TxMaterialDto;
import io.wannabit.wallet.dto.QtumDto.UTXODto;
import io.wannabit.wallet.service.QtumService;

@RestController
@RequestMapping("/qtum")
public class QtumController {

  @Autowired QtumService qtumService;

  @RequestMapping(value = "/getUTXOByAddr/{addr}", method = RequestMethod.GET)
  public List<UTXODto> getUTXOByAddr(@PathVariable String addr) {
    return qtumService.getUTXOByAddr(addr);
  }

  @RequestMapping(value = "/getTokenByAddr/{addr}/{contractAddr}", method = RequestMethod.GET)
  public TokenBalanceDto getTokenByAddr(@PathVariable String addr,
      @PathVariable String contractAddr) {
    return qtumService.getTokenByAddr(addr, contractAddr);
  }

  @RequestMapping(value = "/getTokenInfo/{contractAddr}", method = RequestMethod.GET)
  public TokenInfoDto getTokenInfo(@PathVariable String contractAddr) {
    return qtumService.getTokenInfo(contractAddr);
  }

  @RequestMapping(value = "/getRawTx/{txid}", method = RequestMethod.GET)
  public Map<String, String> getRawTx(@PathVariable String txid) {
    return qtumService.getRawTx(txid);
  }

  @RequestMapping(value = "/createRawTx", method = RequestMethod.POST)
  public Map<String, String> createRawTx(@RequestBody TxMaterialDto txMaterialDto) {
    return qtumService.createRawTx(txMaterialDto);
  }

  @RequestMapping(value = "/signRawTx", method = RequestMethod.POST)
  public SignedRawTransactionDto signRawTx(@RequestBody SignTxMaterialDto signTxMaterialDto) {
    return qtumService.signRawTx(signTxMaterialDto);
  }

  @RequestMapping(value = "/sendRawTx", method = RequestMethod.POST)
  public Map<String, String> sendRawTx(@RequestBody SignedRawTransactionDto signedRawTransactionDto)
      throws Exception {
    return qtumService.sendRawTx(signedRawTransactionDto);
  }

  @RequestMapping(value = "/saveTxInfo", method = RequestMethod.POST)
  public Map<String, String> saveTxInfo(@RequestBody TxInfoForSaveDto txInfoForSaveDto)
      throws Exception {
    return qtumService.saveTxInfo(txInfoForSaveDto);
  }

  @RequestMapping(value = "/changeRawTxForSendToken", method = RequestMethod.POST)
  public Map<String, String> changeRawTxForSendToken(
      @RequestBody ChangeRawTxForSendTokenDto changeRawTxForSendTokenDto) throws Exception {
    return qtumService.changeRawTxForSendToken(changeRawTxForSendTokenDto);
  }
}
