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
import io.wannabit.wallet.dto.LtcDto.SignTxMaterialDto;
import io.wannabit.wallet.dto.LtcDto.SignedRawTransactionDto;
import io.wannabit.wallet.dto.LtcDto.TxInfoForSaveDto;
import io.wannabit.wallet.dto.LtcDto.TxMaterialDto;
import io.wannabit.wallet.dto.LtcDto.UTXODto;
import io.wannabit.wallet.exception.LogicErrorList;
import io.wannabit.wallet.exception.LogicException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class LtcService {

  @Value("${ltcCoreUrl}") private String ltcCoreUrl;
  @Value("${corePassword}") private String corePassword;

  @Autowired AccountRepo accountRepo;
  @Autowired AccountTxRepo accountTxRepo;

  public List<UTXODto> getUTXOByAddr(String addr) {
    List<UTXODto> UTXODtoList = new ArrayList<>();
    String result = "";
    String result2 = "";

    // 라이트코인 core 통신
    try {
      HttpResponse<JsonNode> response = Unirest.post(ltcCoreUrl).body(
          "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"curltest\", \n  \"method\": \"getaddressutxos\", \n  \"params\": [{\"addresses\": [\""
              + addr + "\"]}]\n}")
          .asJson();
      result = response.getBody().getObject().optString("result");
      JSONArray resultJsonArray = new JSONArray(result);

      for (int i = 0; i < resultJsonArray.length(); i++) {
        JSONObject txs = resultJsonArray.getJSONObject(i);
        // System.out.println("txs.get(\"outputIndex\"): " +
        // txs.get("outputIndex"));

        UTXODto utxoDto = new UTXODto();
        utxoDto.setTxid(txs.get("txid").toString());
        utxoDto.setN((int) txs.get("outputIndex"));
        utxoDto.setScriptPubKey((String) txs.get("script"));
        utxoDto.setValue((int) txs.get("satoshis") / 100000000d);
        utxoDto.setAddress((String) txs.get("address"));

        // LTC Bitcore 에서 txid 를 조회해서 coinbase transaction 인지 확인하기
        try {
          HttpResponse<JsonNode> response2 = Unirest.post(ltcCoreUrl).body(
              "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"curltest\", \n  \"method\": \"getrawtransaction\", \n  \"params\": [\n  \t\t\""
                  + utxoDto.getTxid() + "\",\n  \t\t1\n  \t] \n}")
              .asJson();
          result2 = response2.getBody().getObject().optString("result");
          JSONObject resultJson = new JSONObject(result2);

          // LTC 에서 Coinbase Transaction 타입 찾는 방법
          // 1) txid 의 fee 가 0보다 작은 경우
          // 2) vout 의 첫번째 output 안에서 scriptPubKey 의 type 이 "nonstandard" 인 경우
          // Coinbase Transaction 은 confirmations 가 500 이상일때만 유효한 UTXO 로 사용가능
          JSONArray vout = resultJson.getJSONArray("vout");
          JSONObject vout0 = vout.getJSONObject(0);
          JSONObject scriptPubKeyJson = vout0.getJSONObject("scriptPubKey");

          if (scriptPubKeyJson.get("type").equals("nonstandard")
              && resultJson.getInt("confirmations") < 100) {
            utxoDto.setType("Coinbase Transaction");
          } else {
            utxoDto.setType("");
          }

        } catch (Exception e) {
          System.out.println("Exception: " + e);
        }

        UTXODtoList.add(utxoDto);
      }

    } catch (Exception e) {
      System.out.println("Exception : " + e);
    }

    return UTXODtoList;
  }

  public Map<String, String> getRawTx(String txid) {
    String result = "";

    // getRawTransaction 변환해서 가져오기
    try {
      HttpResponse<JsonNode> response = Unirest.post(ltcCoreUrl)
          .header("Content-Type", "application/json")
          .body(
              "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"curltest\", \n  \"method\": \"getrawtransaction\", \n  \"params\": [\""
                  + txid + "\"] \n}")
          .asJson();
      result = response.getBody().getObject().optString("result");

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
      HttpResponse<JsonNode> response = Unirest.post(ltcCoreUrl).body(
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
      HttpResponse<JsonNode> response = Unirest.post(ltcCoreUrl).body(
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
      response = Unirest.post(ltcCoreUrl).body(
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
      if (errorString.contains("txn-mempool-conflict"))
        throw new LogicException(LogicErrorList.FailedQtumTxnMempoolConflict);

      // errorString ======> {"code":-26,"message":"64: dust"}
      if (errorString.contains("dust"))
        throw new LogicException(LogicErrorList.FailedTxDueToDust);

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
    accountTx.setTypeBlockchain("LTC");
    accountTx.setSymbol(txInfoForSaveDto.getSymbol());
    BeanUtils.copyProperties(txInfoForSaveDto, accountTx);
    accountTxRepo.saveAndFlush(accountTx);

    Map<String, String> map = new HashMap<String, String>();
    map.put("complete", "true");
    return map;
  }
}
