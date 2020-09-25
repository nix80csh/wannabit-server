package io.wannabit.wallet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.wannabit.wallet.dto.MyWalletDto.GetAddrDto;
import io.wannabit.wallet.dto.MyWalletDto.ModifyAddrNameDto;
import io.wannabit.wallet.dto.MyWalletDto.RemoveAddrDto;
import io.wannabit.wallet.dto.MyWalletDto.SaveAddrDto;
import io.wannabit.wallet.dto.MyWalletDto.TokenDto;
import io.wannabit.wallet.service.MyWalletService;

@RestController
@RequestMapping("/mywallet")
public class MyWalletController {

  @Autowired MyWalletService myWalletService;

  @RequestMapping(value = "/saveToken", method = RequestMethod.POST)
  public Map<String, Boolean> saveToken(@RequestBody TokenDto tokenDto) {
    return myWalletService.saveToken(tokenDto);
  }

  @RequestMapping(value = "/removeToken", method = RequestMethod.POST)
  public Map<String, Boolean> removeToken(@RequestBody TokenDto tokenDto) {
    return myWalletService.removeToken(tokenDto);
  }

  @RequestMapping(value = "/saveAddr", method = RequestMethod.POST)
  public Map<String, Boolean> saveAddr(@RequestBody SaveAddrDto saveAddrDto) throws Exception {
    return myWalletService.saveAddr(saveAddrDto);
  }

  @RequestMapping(value = "/removeAddr", method = RequestMethod.POST)
  public Map<String, Boolean> removeAddr(@RequestBody RemoveAddrDto removeAddrDto) {
    return myWalletService.removeAddr(removeAddrDto);
  }

  @RequestMapping(value = "/getAddrList/{typeBlockchain}/{idfAccount}", method = RequestMethod.GET)
  public List<GetAddrDto> getAddrList(@PathVariable String typeBlockchain,
      @PathVariable Integer idfAccount) throws Exception {
    return myWalletService.getAddrList(typeBlockchain, idfAccount);
  }

  @RequestMapping(value = "/getTokenList", method = RequestMethod.POST)
  public TokenDto getTokenList(@RequestBody TokenDto getTokenDto) {
    return myWalletService.getTokenList(getTokenDto);
  }

  @RequestMapping(value = "/modifyAddrName", method = RequestMethod.POST)
  public Map<String, Boolean> modifyAddrName(@RequestBody ModifyAddrNameDto modifyAddrName) {
    return myWalletService.modifyAddrName(modifyAddrName);
  }

}
