package io.wannabit.wallet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.wannabit.wallet.dto.BchDto.SignTxMaterialDto;
import io.wannabit.wallet.dto.BchDto.SignedRawTransactionDto;
import io.wannabit.wallet.dto.BchDto.TxInfoForSaveDto;
import io.wannabit.wallet.dto.BchDto.TxMaterialDto;
import io.wannabit.wallet.dto.BchDto.UTXODto;
import io.wannabit.wallet.service.BchService;

@RestController
@RequestMapping("/bch")
public class BchController {

  @Autowired BchService bchService;

  @RequestMapping(value = "/getUTXOByAddr/{addr}", method = RequestMethod.GET)
  public List<UTXODto> getUTXOByAddr(@PathVariable String addr) {
    return bchService.getUTXOByAddr(addr);
  }

  @RequestMapping(value = "/getBalance/{addr}", method = RequestMethod.GET)
  public Map<String, String> getBalance(@PathVariable String addr) {
    return bchService.getBalance(addr);
  }

  @RequestMapping(value = "/getRawTx/{txid}", method = RequestMethod.GET)
  public Map<String, String> getRawTx(@PathVariable String txid) {
    return bchService.getRawTx(txid);
  }

  @RequestMapping(value = "/createRawTx", method = RequestMethod.POST)
  public Map<String, String> createRawTx(@RequestBody TxMaterialDto txMaterialDto) {
    return bchService.createRawTx(txMaterialDto);
  }

  @RequestMapping(value = "/signRawTx", method = RequestMethod.POST)
  public SignedRawTransactionDto signRawTx(@RequestBody SignTxMaterialDto signTxMaterialDto) {
    return bchService.signRawTx(signTxMaterialDto);
  }

  @RequestMapping(value = "/sendRawTx", method = RequestMethod.POST)
  public Map<String, String> sendRawTx(@RequestBody SignedRawTransactionDto signedRawTransactionDto)
      throws Exception {
    return bchService.sendRawTx(signedRawTransactionDto);
  }

  @RequestMapping(value = "/saveTxInfo", method = RequestMethod.POST)
  public Map<String, String> saveTxInfo(@RequestBody TxInfoForSaveDto txInfoForSaveDto)
      throws Exception {
    return bchService.saveTxInfo(txInfoForSaveDto);
  }

  @RequestMapping(value = "/validateAddress/{addr}", method = RequestMethod.GET)
  public Map<String, String> validateAddress(@PathVariable String addr) {
    return bchService.validateAddress(addr);
  }
}
