package io.wannabit.wallet.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.wannabit.wallet.dto.EthDto.TxInfoForSaveDto;
import io.wannabit.wallet.service.EthService;

@RestController
@RequestMapping("/eth")
public class EthController {

  @Autowired EthService etherService;

  @RequestMapping(value = "/saveTxInfo", method = RequestMethod.POST)
  public Map<String, String> saveTxInfo(@RequestBody TxInfoForSaveDto txInfoForSaveDto)
      throws Exception {
    return etherService.saveTxInfo(txInfoForSaveDto);
  }


}
