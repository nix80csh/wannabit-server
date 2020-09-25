package io.wannabit.wallet.service;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import io.wannabit.core.entity.Account;
import io.wannabit.core.entity.AccountTx;
import io.wannabit.core.repository.AccountRepo;
import io.wannabit.core.repository.AccountTxRepo;
import io.wannabit.wallet.dto.EtcDto.BalanceDto;
import io.wannabit.wallet.dto.EtcDto.GetTxParamsDto;
import io.wannabit.wallet.dto.EtcDto.RawTxDto;
import io.wannabit.wallet.dto.EtcDto.SignedRawTxDto;
import io.wannabit.wallet.dto.EtcDto.TxInfoForSaveDto;
import io.wannabit.wallet.exception.LogicErrorList;
import io.wannabit.wallet.exception.LogicException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class EtcService {

  @Value("${etcCoreUrl}") private String etcCoreUrl;

  @Autowired AccountRepo accountRepo;
  @Autowired AccountTxRepo accountTxRepo;

  public BalanceDto getBalance(String addr) {

    BalanceDto balanceDto = new BalanceDto();
    try {
      HttpResponse<JsonNode> getBalanceResp = Unirest.post(etcCoreUrl).body(
          "{\n  \"jsonrpc\": \"2.0\", \n  \"id\":\"wannabit\", \n  \"method\": \"eth_getBalance\", \n  \"params\": [\""
              + addr + "\", \"latest\"] \n}")
          .asJson();
      String hexValue = getBalanceResp.getBody().getObject().optString("result");
      // 첫 두단어 0x 삭제안할 시 형변환 불가
      Long longValue = Long.parseLong(hexValue.substring(2, hexValue.length()), 16);
      Double doubleValue = longValue.doubleValue() / Math.pow(10, 18);
      String value = String.format("%." + 18 + "f", doubleValue);
      balanceDto.setValue(value);
    } catch (Exception e) {
      System.out.println("Exception : " + e);
    }
    return balanceDto;
  }

  public RawTxDto getTxParams(GetTxParamsDto getTxParamsDto) {

    // 존재하지 않는 회원일경우
    if (!accountRepo.exists(getTxParamsDto.getIdfAccount()))
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    RawTxDto rawTxDto = new RawTxDto();

    String nonce = "";
    String from = getTxParamsDto.getFrom();
    String to = getTxParamsDto.getTo();
    long gasPrice = Long.parseLong(getTxParamsDto.getGasPrice());
    long gasLimit = Long.parseLong(getTxParamsDto.getGasLimit());
    double value = Double.parseDouble(getTxParamsDto.getValue());
    int chainId = 61;
    int decimals = 18;

    try {
      HttpResponse<JsonNode> getTxCountResp = Unirest.post(etcCoreUrl).body(
          "{\n  \"jsonrpc\": \"2.0\", \n  \"id\":\"wannabit\", \n  \"method\": \"eth_getTransactionCount\", \n  \"params\": [\""
              + from + "\", \"latest\"] \n}")
          .asJson();

      nonce = getTxCountResp.getBody().getObject().optString("result");
    } catch (Exception e) {
      System.out.println("Exception : " + e);
    }

    if (value < 1) {
      String stringValue = String.format("%.18f", value); // Prevent Exponential
                                                          // Scientific notation
      String array[] = String.valueOf(stringValue).split("\\.");
      int number = array[1].length();
      value = (long) (Math.floor(value * Math.pow(10, decimals) / Math.pow(10, (decimals - number)))
          * Math.pow(10, (decimals - number)));
    } else {
      value = (long) (value * Math.pow(10, decimals));
    }

    rawTxDto.setNonce(nonce);
    rawTxDto.setGasPrice("0x" + Long.toHexString(gasPrice * 1000000000));
    rawTxDto.setGasLimit("0x" + Long.toHexString(gasLimit));
    rawTxDto.setTo(to);
    rawTxDto.setValue("0x" + Long.toHexString((long) value));
    rawTxDto.setChainId(chainId);

    return rawTxDto;
  }

  public Map<String, String> sendRawTx(SignedRawTxDto signedRawTxDto) {

    // 존재하지 않는 회원일경우
    if (!accountRepo.exists(signedRawTxDto.getIdfAccount()))
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    String txid = "";
    try {
      HttpResponse<JsonNode> sendRawTxResp = Unirest.post(etcCoreUrl).body(
          "{\n  \"jsonrpc\": \"2.0\", \n  \"id\":\"wannabit\", \n  \"method\": \"eth_sendRawTransaction\", \n  \"params\": [\""
              + signedRawTxDto.getSignedRawTx() + "\"] \n}")
          .asJson();
      txid = sendRawTxResp.getBody().getObject().optString("result");
    } catch (Exception e) {
      System.out.println("Exception : " + e);
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("txid", txid);
    return map;
  }

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
    accountTx.setTypeBlockchain("ETC");
    accountTx.setSymbol(txInfoForSaveDto.getSymbol());
    BeanUtils.copyProperties(txInfoForSaveDto, accountTx);
    accountTxRepo.saveAndFlush(accountTx);

    Map<String, String> map = new HashMap<String, String>();
    map.put("isSaved", "true");
    return map;
  }
}
