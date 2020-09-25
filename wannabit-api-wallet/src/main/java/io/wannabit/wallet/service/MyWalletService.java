package io.wannabit.wallet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.wannabit.core.entity.Account;
import io.wannabit.core.entity.AccountToken;
import io.wannabit.core.entity.AccountTokenPK;
import io.wannabit.core.entity.AccountWallet;
import io.wannabit.core.entity.AccountWalletPK;
import io.wannabit.core.repository.AccountRepo;
import io.wannabit.core.repository.AccountTokenRepo;
import io.wannabit.core.repository.AccountWalletRepo;
import io.wannabit.util.EncryptionAES256Util;
import io.wannabit.wallet.dto.MyWalletDto.GetAddrDto;
import io.wannabit.wallet.dto.MyWalletDto.ModifyAddrNameDto;
import io.wannabit.wallet.dto.MyWalletDto.RemoveAddrDto;
import io.wannabit.wallet.dto.MyWalletDto.SaveAddrDto;
import io.wannabit.wallet.dto.MyWalletDto.TokenDto;
import io.wannabit.wallet.dto.MyWalletDto.TokenInfoDto;
import io.wannabit.wallet.exception.LogicErrorList;
import io.wannabit.wallet.exception.LogicException;

@Service
@Transactional
public class MyWalletService {

  @Autowired AccountRepo accountRepo;
  @Autowired AccountTokenRepo accountTokenRepo;
  @Autowired AccountWalletRepo accountWalletRepo;

  public Map<String, Boolean> saveToken(TokenDto tokenDto) {

    // 존재하지 않는 회원일경우
    if (!accountRepo.exists(tokenDto.getIdfAccount()))
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    // 사용자가 추가한 토큰 저장
    List<AccountToken> accountTokenList = new ArrayList<AccountToken>();
    for (TokenInfoDto token : tokenDto.getTokenList()) {
      AccountToken accountToken = new AccountToken();
      AccountTokenPK id = new AccountTokenPK();
      id.setIdfAccount(tokenDto.getIdfAccount());
      id.setContractAddr(token.getAddr());
      accountToken.setName(token.getName());
      accountToken.setSymbol(token.getSymbol());
      accountToken.setDecimals(token.getDecimals());
      accountToken.setId(id);
      accountToken.setTypeBlockchain(tokenDto.getTypeBlockchain());
      accountTokenList.add(accountToken);
    }

    accountTokenRepo.save(accountTokenList);

    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isSaved", true);
    return map;
  }

  public Map<String, Boolean> removeToken(TokenDto tokenDto) {

    Account account = accountRepo.findOne(tokenDto.getIdfAccount());

    if (account == null)
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    for (TokenInfoDto token : tokenDto.getTokenList()) {
      AccountTokenPK id = new AccountTokenPK();
      id.setContractAddr(token.getAddr());
      id.setIdfAccount(account.getIdfAccount());
      AccountToken accountToken = new AccountToken();
      accountToken.setId(id);
      accountTokenRepo.delete(accountToken);
    }

    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isRemoved", true);
    return map;
  }

  public Map<String, Boolean> saveAddr(SaveAddrDto saveAddrDto) throws Exception {

    // 존재하지 않는 회원일경우
    if (!accountRepo.exists(saveAddrDto.getIdfAccount()))
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    AccountWalletPK id = new AccountWalletPK();
    id.setAddr(saveAddrDto.getAddr());
    id.setIdfAccount(saveAddrDto.getIdfAccount());

    // 이미 존재하는 지갑주소일경우
    if (accountWalletRepo.exists(id))
      throw new LogicException(LogicErrorList.DuplicateEntity_AccountWallet);

    AccountWallet accountWallet = new AccountWallet();
    BeanUtils.copyProperties(saveAddrDto, accountWallet);
    accountWallet.setSignMaterial(EncryptionAES256Util.encode(saveAddrDto.getSignMaterial()));
    accountWallet.setId(id);
    accountWalletRepo.save(accountWallet);

    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isSaved", true);
    return map;
  }

  public Map<String, Boolean> removeAddr(RemoveAddrDto removeAddrDto) {

    // 존재하지 않는 회원일경우
    if (!accountRepo.exists(removeAddrDto.getIdfAccount()))
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    AccountWalletPK id = new AccountWalletPK();
    id.setIdfAccount(removeAddrDto.getIdfAccount());
    id.setAddr(removeAddrDto.getAddr());

    // 존재하지 않는 지갑주소일경우
    if (!accountWalletRepo.exists(id))
      throw new LogicException(LogicErrorList.DoesNotExist_AccountWallet);

    accountWalletRepo.delete(id);

    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isRemoved", true);

    return map;
  }

  public List<GetAddrDto> getAddrList(String typeBlockchain, Integer idfAccount) throws Exception {

    // 존재하지 않는 회원일경우
    if (!accountRepo.exists(idfAccount))
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    List<AccountWallet> accountWalletList = accountWalletRepo
        .findByAccountIdfAccountAndTypeBlockchainOrderByRegDateAsc(idfAccount, typeBlockchain);

    List<GetAddrDto> getAddrDtoList = new ArrayList<GetAddrDto>();
    for (AccountWallet accountWallet : accountWalletList) {
      GetAddrDto getAddrDto = new GetAddrDto();

      BeanUtils.copyProperties(accountWallet, getAddrDto);
      getAddrDto.setIdfAccount(accountWallet.getId().getIdfAccount());
      getAddrDto.setAddr(accountWallet.getId().getAddr());
      getAddrDto.setSignMaterial(EncryptionAES256Util.decode(accountWallet.getSignMaterial()));

      getAddrDtoList.add(getAddrDto);
    }

    return getAddrDtoList;
  }

  public TokenDto getTokenList(TokenDto tokenDto) {

    // 존재하지 않는 회원일경우
    if (!accountRepo.exists(tokenDto.getIdfAccount()))
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    // 사용자별 추가한 토큰
    List<AccountToken> accountTokenList =
        accountRepo.findOne(tokenDto.getIdfAccount()).getAccountTokens();

    List<TokenInfoDto> tokenInfoList = new ArrayList<TokenInfoDto>();
    for (AccountToken accountToken : accountTokenList) {
      TokenInfoDto tokenInfoDto = new TokenInfoDto();
      BeanUtils.copyProperties(accountToken, tokenInfoDto);
      String typeBlockchain = accountToken.getTypeBlockchain();
      if (typeBlockchain.equals(tokenDto.getTypeBlockchain())) {
        tokenInfoDto.setAddr(accountToken.getId().getContractAddr());
        tokenInfoDto.setName(accountToken.getName());
        tokenInfoDto.setSymbol(accountToken.getSymbol());
        tokenInfoDto.setDecimals(accountToken.getDecimals());
        tokenInfoList.add(tokenInfoDto);
      }
    }
    tokenDto.setTokenList(tokenInfoList);

    return tokenDto;
  }

  public Map<String, Boolean> modifyAddrName(ModifyAddrNameDto modifyAddrNameDto) {
    AccountWalletPK id = new AccountWalletPK();
    id.setAddr(modifyAddrNameDto.getAddr());
    id.setIdfAccount(modifyAddrNameDto.getIdfAccount());

    if (!accountWalletRepo.exists(id))
      throw new LogicException(LogicErrorList.DoesNotExist_AccountWallet);

    AccountWallet accountWallet = accountWalletRepo.findOne(id);
    accountWallet.setName(modifyAddrNameDto.getName());
    accountWalletRepo.save(accountWallet);

    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isModified", true);
    return map;
  }

}
