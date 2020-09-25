package io.wannabit.wallet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.wannabit.core.entity.Account;
import io.wannabit.core.entity.AccountTx;
import io.wannabit.core.repository.AccountRepo;
import io.wannabit.core.repository.AccountTxRepo;
import io.wannabit.wallet.dto.BtcDto.SignTxMaterialDto;
import io.wannabit.wallet.dto.BtcDto.SignedRawTransactionDto;
import io.wannabit.wallet.dto.BtcDto.TxInfoForSaveDto;
import io.wannabit.wallet.dto.BtcDto.TxMaterialDto;
import io.wannabit.wallet.dto.BtcDto.UTXODto;
import io.wannabit.wallet.exception.LogicErrorList;
import io.wannabit.wallet.exception.LogicException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class BtcService {

  @Value("${btcCoreUrl}") private String btcCoreUrl;
  @Value("${corePassword}") private String corePassword;

  @Autowired AccountRepo accountRepo;
  @Autowired AccountTxRepo accountTxRepo;

  public List<UTXODto> getUTXOByAddr(String addr) {
    List<UTXODto> UTXODtoList = new ArrayList<>();

    try {
      HttpResponse<String> response =
          Unirest.get("http://blockchain.info/unspent?active=" + addr).asString();
      JSONObject unspentOutputs = new JSONObject(response.getBody());
      JSONArray resultJsonArray = new JSONArray(unspentOutputs.optString("unspent_outputs"));

      for (int i = 0; i < resultJsonArray.length(); i++) {
        JSONObject txs = resultJsonArray.getJSONObject(i);

        UTXODto utxoDto = new UTXODto();
        utxoDto.setTxid(txs.get("tx_hash_big_endian").toString());
        utxoDto.setN((int) txs.get("tx_output_n"));
        utxoDto.setScriptPubKey((String) txs.get("script"));
        utxoDto.setValue((int) txs.get("value") / 100000000d);
        utxoDto.setAddress(addr);

        UTXODtoList.add(utxoDto);
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }

    return UTXODtoList;
  }

  public Map<String, String> getRawTx(String txid) {
    String result = "";

    // getRawTransaction 변환해서 가져오기
    try {
      HttpResponse<String> response =
          Unirest.get("http://blockchain.info/rawtx/" + txid + "?format=hex").asString();
      System.out.println("response" + response.getBody());
      result = response.getBody();

    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("rawTransaction", result);
    return map;
  }

  public Map<String, String> createRawTx(TxMaterialDto txMaterialDto) {

    // 받은 json 을 createrawtransaction 명령어에 넣을 매개변수포맷으로 변경하기
    // 문자열 2개 : inputs, outputs
    Integer inputDtoListLength = txMaterialDto.getInputDtoList().size();
    String paramInput = "[";
    for (int i = 0; i < inputDtoListLength; i++) {
      paramInput += "{";
      paramInput += "\"txid\":";
      paramInput += "\"" + txMaterialDto.getInputDtoList().get(i).getTxid() + "\"";
      paramInput += ",";
      paramInput += "\"vout\":";
      paramInput += txMaterialDto.getInputDtoList().get(i).getN();
      paramInput += "}";
      if (i < inputDtoListLength - 1) {
        paramInput += ",";
      }
    }
    paramInput += "]";

    Integer outputDtoListLength = txMaterialDto.getOutputDtoList().size();
    String paramOutput = "{";
    for (int i = 0; i < outputDtoListLength; i++) {
      paramOutput += "\"" + txMaterialDto.getOutputDtoList().get(i).getAddress() + "\"";
      paramOutput += ":";
      paramOutput += txMaterialDto.getOutputDtoList().get(i).getValue();
      if (i < outputDtoListLength - 1) {
        paramOutput += ",";
      }
    }
    paramOutput += "}";

    // RPC 통신(createrawtransaction)
    String result = "";
    try {
      HttpResponse<JsonNode> response = Unirest.post(btcCoreUrl).body(
          "{\n \"jsonrpc\": \"1.0\", \n \"id\":\"curltest\", \n \"method\":\"createrawtransaction\", \n \"params\": [\n \t\t"
              + paramInput + ",\n \t\t" + paramOutput + "\n ] \n}")
          .asJson();
      result = response.getBody().getObject().optString("result");

    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("rawTransaction", result);
    return map;
  }

  public SignedRawTransactionDto signRawTx(SignTxMaterialDto signTxMaterialDto) {
    Integer listLength = signTxMaterialDto.getInputDtoList().size();

    String inputParam = "[";
    for (int i = 0; i < listLength; i++) {
      inputParam += "{";
      inputParam += "\"txid\":";
      inputParam += "\"" + signTxMaterialDto.getInputDtoList().get(i).getTxid() + "\"";
      inputParam += ",";
      inputParam += "\"vout\":";
      inputParam += signTxMaterialDto.getInputDtoList().get(i).getN();
      inputParam += ",";
      inputParam += "\"scriptPubKey\":";
      inputParam += "\"" + signTxMaterialDto.getInputDtoList().get(i).getScriptPubKey() + "\"";
      inputParam += ",";
      inputParam += "\"redeemScript\":";
      inputParam += "\"" + "" + "\"";
      inputParam += "}";
      if (i < listLength - 1) {
        inputParam += ",";
      }
    }
    inputParam += "]";

    String result = "";
    String resultHex = "";
    try {
      HttpResponse<JsonNode> response = Unirest.post(btcCoreUrl).body(
          "{\n \"jsonrpc\": \"1.0\", \n \"id\":\"curltest\", \n \"method\":\"signrawtransaction\", \n \"params\": \n [\n \t\""
              + signTxMaterialDto.getRawTransaction() + "\",\n \t" + inputParam + ",\n\t[\""
              + signTxMaterialDto.getPrivateKey() + "\"]\n ]\n}")
          .asJson();
      result = response.getBody().getObject().optString("result");
      JSONObject resultJson = new JSONObject(result);
      resultHex = resultJson.optString("hex");
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }

    SignedRawTransactionDto signedRawTransactionDto = new SignedRawTransactionDto();
    signedRawTransactionDto.setSignedRawTransaction(resultHex);
    return signedRawTransactionDto;
  }

  public Map<String, String> sendRawTx(SignedRawTransactionDto signedRawTransactionDto) {

    // 존재하지 않는 회원일경우
    if (!accountRepo.exists(signedRawTransactionDto.getIdfAccount()))
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    String resTxid = "";
    HttpResponse<JsonNode> response = null;
    try {
      response = Unirest.post(btcCoreUrl).body(
          "{\n \"jsonrpc\": \"1.0\", \n \"id\":\"curltest\", \n \"method\":\"sendrawtransaction\", \n \"params\": [\""
              + signedRawTransactionDto.getSignedRawTransaction() + "\"] \n}")
          .asJson();
    } catch (UnirestException e) {
      e.printStackTrace();
    }
    resTxid = response.getBody().getObject().optString("result");

    // 통신실패 또는 Transaction실패시
    if (resTxid.equals("")) {
      String errorString = response.getBody().getObject().optString("error");
      log.info(errorString);

      // Double Spending 인 경우
      if (errorString.contains("txn-mempool-conflict")) {
        throw new LogicException(LogicErrorList.FailedQtumTxnMempoolConflict);
      }

      throw new LogicException(LogicErrorList.FailedQtumTransaction);
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("txid", resTxid);
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
    accountTx.setTypeBlockchain("BTC");
    accountTx.setSymbol(txInfoForSaveDto.getSymbol());
    BeanUtils.copyProperties(txInfoForSaveDto, accountTx);
    accountTxRepo.saveAndFlush(accountTx);

    Map<String, String> map = new HashMap<String, String>();
    map.put("complete", "true");
    return map;
  }
}
