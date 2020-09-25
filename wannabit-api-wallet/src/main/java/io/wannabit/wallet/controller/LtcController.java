package io.wannabit.wallet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.wannabit.wallet.dto.LtcDto.SignTxMaterialDto;
import io.wannabit.wallet.dto.LtcDto.SignedRawTransactionDto;
import io.wannabit.wallet.dto.LtcDto.TxInfoForSaveDto;
import io.wannabit.wallet.dto.LtcDto.TxMaterialDto;
import io.wannabit.wallet.dto.LtcDto.UTXODto;
import io.wannabit.wallet.service.LtcService;

@RestController
@RequestMapping("/ltc")
public class LtcController {

  @Autowired LtcService ltcService;

  @RequestMapping(value = "/getUTXOByAddr/{addr}", method = RequestMethod.GET)
  public List<UTXODto> getUTXOByAddr(@PathVariable String addr) {
    return ltcService.getUTXOByAddr(addr);
  }

  @RequestMapping(value = "/getRawTx/{txid}", method = RequestMethod.GET)
  public Map<String, String> getRawTx(@PathVariable String txid) {
    return ltcService.getRawTx(txid);
  }

  @RequestMapping(value = "/createRawTx", method = RequestMethod.POST)
  public Map<String, String> createRawTx(@RequestBody TxMaterialDto txMaterialDto) {
    return ltcService.createRawTx(txMaterialDto);
  }

  @RequestMapping(value = "/signRawTx", method = RequestMethod.POST)
  public SignedRawTransactionDto signRawTx(@RequestBody SignTxMaterialDto signTxMaterialDto) {
    return ltcService.signRawTx(signTxMaterialDto);
  }

  @RequestMapping(value = "/sendRawTx", method = RequestMethod.POST)
  public Map<String, String> sendRawTx(@RequestBody SignedRawTransactionDto signedRawTransactionDto)
      throws Exception {
    return ltcService.sendRawTx(signedRawTransactionDto);
  }

  @RequestMapping(value = "/saveTxInfo", method = RequestMethod.POST)
  public Map<String, String> saveTxInfo(@RequestBody TxInfoForSaveDto txInfoForSaveDto)
      throws Exception {
    return ltcService.saveTxInfo(txInfoForSaveDto);
  }
}
