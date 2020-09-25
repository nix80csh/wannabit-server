package io.wannabit.wallet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.wannabit.wallet.dto.MyTxDto;
import io.wannabit.wallet.service.MyTxService;

@RestController
@RequestMapping("/mytx")
public class MyTxController {

  @Autowired MyTxService myTxService;

  @RequestMapping(value = "/getMyTxList/{idfAccount}/{typeBlockchain}", method = RequestMethod.GET)
  public List<MyTxDto> getMyTxList(@PathVariable int idfAccount,
      @PathVariable String typeBlockchain) {
    return myTxService.getMyTxList(idfAccount, typeBlockchain);
  }
}
