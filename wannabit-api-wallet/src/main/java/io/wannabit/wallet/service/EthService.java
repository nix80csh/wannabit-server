package io.wannabit.wallet.service;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.wannabit.core.entity.Account;
import io.wannabit.core.entity.AccountTx;
import io.wannabit.core.repository.AccountRepo;
import io.wannabit.core.repository.AccountTxRepo;
import io.wannabit.wallet.dto.EthDto.TxInfoForSaveDto;
import io.wannabit.wallet.exception.LogicErrorList;
import io.wannabit.wallet.exception.LogicException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class EthService {

  @Autowired AccountRepo accountRepo;
  @Autowired AccountTxRepo accountTxRepo;

  public Map<String, String> saveTxInfo(TxInfoForSaveDto txInfoForSaveDto) {

    // 존재하지 않는 회원일경우
    if (!accountRepo.exists(txInfoForSaveDto.getIdfAccount()))
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    // 이미 저장된 거래내역일 경우
    if (accountTxRepo.existsByTxhash(txInfoForSaveDto.getTxid()))
      throw new LogicException(LogicErrorList.DuplicateValue_AccountTx_Txhash);

    AccountTx accountTx = new AccountTx();
    Account account = new Account();
    account.setIdfAccount(txInfoForSaveDto.getIdfAccount());
    accountTx.setAccount(account);
    accountTx.setTxhash(txInfoForSaveDto.getTxid());
    accountTx.setTypeBlockchain("ETH");
    accountTx.setSymbol(txInfoForSaveDto.getSymbol());
    BeanUtils.copyProperties(txInfoForSaveDto, accountTx);
    accountTxRepo.saveAndFlush(accountTx);

    Map<String, String> map = new HashMap<String, String>();
    map.put("isSaved", "true");
    return map;
  }


}
