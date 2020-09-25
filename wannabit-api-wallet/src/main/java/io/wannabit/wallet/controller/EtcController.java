package io.wannabit.wallet.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.wannabit.wallet.dto.EtcDto.BalanceDto;
import io.wannabit.wallet.dto.EtcDto.GetTxParamsDto;
import io.wannabit.wallet.dto.EtcDto.RawTxDto;
import io.wannabit.wallet.dto.EtcDto.SignedRawTxDto;
import io.wannabit.wallet.dto.EtcDto.TxInfoForSaveDto;
import io.wannabit.wallet.service.EtcService;

@RestController
@RequestMapping("/etc")
public class EtcController {

  @Autowired EtcService etcService;

  @RequestMapping(value = "/getBalance/{addr}", method = RequestMethod.GET)
  public BalanceDto getBalance(@PathVariable String addr) throws Exception {
    return etcService.getBalance(addr);
  }

  @RequestMapping(value = "/getTxParams", method = RequestMethod.POST)
  public RawTxDto getTxParams(@RequestBody GetTxParamsDto getTxParamsDto) throws Exception {
    return etcService.getTxParams(getTxParamsDto);
  }

  @RequestMapping(value = "/sendRawTx", method = RequestMethod.POST)
  public Map<String, String> sendRawTx(@RequestBody SignedRawTxDto signedRawTxDto)
      throws Exception {
    return etcService.sendRawTx(signedRawTxDto);
  }

  @RequestMapping(value = "/saveTxInfo", method = RequestMethod.POST)
  public Map<String, String> saveTxInfo(@RequestBody TxInfoForSaveDto txInfoForSaveDto)
      throws Exception {
    return etcService.saveTxInfo(txInfoForSaveDto);
  }

}
