package io.wannabit.wallet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.wannabit.wallet.dto.BtcDto.SignTxMaterialDto;
import io.wannabit.wallet.dto.BtcDto.SignedRawTransactionDto;
import io.wannabit.wallet.dto.BtcDto.TxInfoForSaveDto;
import io.wannabit.wallet.dto.BtcDto.TxMaterialDto;
import io.wannabit.wallet.dto.BtcDto.UTXODto;
import io.wannabit.wallet.service.BtcService;

@RestController
@RequestMapping("/btc")
public class BtcController {

  @Autowired BtcService btcService;

  @RequestMapping(value = "/getUTXOByAddr/{addr}", method = RequestMethod.GET)
  public List<UTXODto> getUTXOByAddr(@PathVariable String addr) {
    return btcService.getUTXOByAddr(addr);
  }

  @RequestMapping(value = "/getRawTx/{txid}", method = RequestMethod.GET)
  public Map<String, String> getRawTx(@PathVariable String txid) {
    return btcService.getRawTx(txid);
  }

  @RequestMapping(value = "/createRawTx", method = RequestMethod.POST)
  public Map<String, String> createRawTx(@RequestBody TxMaterialDto txMaterialDto) {
    return btcService.createRawTx(txMaterialDto);
  }

  @RequestMapping(value = "/signRawTx", method = RequestMethod.POST)
  public SignedRawTransactionDto signRawTx(@RequestBody SignTxMaterialDto signTxMaterialDto) {
    return btcService.signRawTx(signTxMaterialDto);
  }

  @RequestMapping(value = "/sendRawTx", method = RequestMethod.POST)
  public Map<String, String> sendRawTx(@RequestBody SignedRawTransactionDto signedRawTransactionDto)
      throws Exception {
    return btcService.sendRawTx(signedRawTransactionDto);
  }

  @RequestMapping(value = "/saveTxInfo", method = RequestMethod.POST)
  public Map<String, String> saveTxInfo(@RequestBody TxInfoForSaveDto txInfoForSaveDto)
      throws Exception {
    return btcService.saveTxInfo(txInfoForSaveDto);
  }
}
