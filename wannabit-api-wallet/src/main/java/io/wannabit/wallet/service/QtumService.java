package io.wannabit.wallet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Hex;
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
import io.wannabit.util.Qrc20Util;
import io.wannabit.wallet.dto.QtumDto.ChangeRawTxForSendTokenDto;
import io.wannabit.wallet.dto.QtumDto.SignTxMaterialDto;
import io.wannabit.wallet.dto.QtumDto.SignedRawTransactionDto;
import io.wannabit.wallet.dto.QtumDto.TokenBalanceDto;
import io.wannabit.wallet.dto.QtumDto.TokenInfoDto;
import io.wannabit.wallet.dto.QtumDto.TxInfoForSaveDto;
import io.wannabit.wallet.dto.QtumDto.TxMaterialDto;
import io.wannabit.wallet.dto.QtumDto.UTXODto;
import io.wannabit.wallet.exception.LogicErrorList;
import io.wannabit.wallet.exception.LogicException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class QtumService {

  @Value("${qtumCoreUrl}") private String qtumCoreUrl;
  @Value("${corePassword}") private String corePassword;

  @Autowired AccountRepo accountRepo;
  @Autowired AccountTxRepo accountTxRepo;

  public List<UTXODto> getUTXOByAddr(String addr) {
    List<UTXODto> UTXODtoList = new ArrayList<>();
    String result = "";
    String result2 = "";

    // QTUM bitcore 통신
    try {
      HttpResponse<JsonNode> response = Unirest.post(qtumCoreUrl).body(
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

        // QTUM Bitcore 에서 txid 를 조회해서 coinbase transaction 인지 확인하기
        try {
          HttpResponse<JsonNode> response2 = Unirest.post(qtumCoreUrl).body(
              "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"curltest\", \n  \"method\": \"getrawtransaction\", \n  \"params\": [\n  \t\t\""
                  + utxoDto.getTxid() + "\",\n  \t\t1\n  \t] \n}")
              .asJson();
          result2 = response2.getBody().getObject().optString("result");
          JSONObject resultJson = new JSONObject(result2);

          // QTUM 에서 Coinbase Transaction 타입 찾는 방법
          // 1) txid 의 fee 가 0보다 작은 경우
          // 2) vout 의 첫번째 output 안에서 scriptPubKey 의 type 이 "nonstandard" 인 경우
          // Coinbase Transaction 은 confirmations 가 500 이상일때만 유효한 UTXO 로 사용가능
          JSONArray vout = resultJson.getJSONArray("vout");
          JSONObject vout0 = vout.getJSONObject(0);
          JSONObject scriptPubKeyJson = vout0.getJSONObject("scriptPubKey");

          if (scriptPubKeyJson.get("type").equals("nonstandard")
              && resultJson.getInt("confirmations") < 500) {
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

  public TokenBalanceDto getTokenByAddr(String addr, String contractAddr) {

    String hash160 = Qrc20Util.to32bytesArg(Qrc20Util.addressToHash160(addr));
    String result = "";
    String outputTokenBalance = "";
    String outputDecimals = "";

    // RPC 통신
    try {
      // 토큰 잔액 조회
      HttpResponse<JsonNode> response = Unirest.post(qtumCoreUrl).body(
          "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"curltest\", \n  \"method\": \"callcontract\", \n  \"params\": [\""
              + contractAddr + "\", \"70a08231" + hash160 + "\"] \n}")
          .asJson();
      result = response.getBody().getObject().optString("result");
      JSONObject resultJson = new JSONObject(result);
      outputTokenBalance = resultJson.getJSONObject("executionResult").optString("output");

      if (result == null) {
        System.out.println("result 가 null 임...");
      }
    } catch (Exception e) {
      System.out.println("Exception : " + e);
    }

    // 해당 토큰의 token decimal 을 구해야함
    try {
      HttpResponse<JsonNode> response = Unirest.post(qtumCoreUrl).body(
          "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"curltest\", \n  \"method\": \"callcontract\", \n  \"params\": [\""
              + contractAddr + "\", \"313ce567\"] \n}")
          .asJson();

      result = response.getBody().getObject().optString("result");
      JSONObject resultJson = new JSONObject(result);
      outputDecimals = resultJson.getJSONObject("executionResult").optString("output");

    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }

    // tokenBalance =
    // Qrc20Util.getAmount(outputTokenBalance,
    // Qrc20Util.convertHexToDec(outputDecimals));

    // output(hex)과 decimals 를 보내기
    TokenBalanceDto tokenBalanceDto = new TokenBalanceDto();
    tokenBalanceDto.setValue(outputTokenBalance);
    tokenBalanceDto.setDecimals(Qrc20Util.convertHexToDec(outputDecimals));

    return tokenBalanceDto;
  }

  public TokenInfoDto getTokenInfo(String contractAddr) {

    TokenInfoDto tokenInfoDto = new TokenInfoDto();

    String result = "";
    String[] outputArr = new String[3];
    String[] functionSignaturesArr = new String[3];

    // name, symbol, decimals
    functionSignaturesArr[0] = "06fdde03";
    functionSignaturesArr[1] = "95d89b41";
    functionSignaturesArr[2] = "313ce567";

    for (int i = 0; i < 3; i++) {
      try {
        HttpResponse<JsonNode> response = Unirest.post(qtumCoreUrl).body(
            "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"curltest\", \n  \"method\": \"callcontract\", \n  \"params\": [\""
                + contractAddr + "\", \"" + functionSignaturesArr[i] + "\"] \n}")
            .asJson();

        result = response.getBody().getObject().optString("result");
        JSONObject resultJson = new JSONObject(result);
        outputArr[i] = resultJson.getJSONObject("executionResult").optString("output");

        if (i == 0 || i == 1) {
          // name, symbol 의 hex 값을 String 으로 변환
          outputArr[i] = new String(Hex.decodeHex(outputArr[i].toCharArray()), "UTF-8");

          // 유니코드 문자열 제거 : \u0000, \u000b, \u0003
          outputArr[i] = outputArr[i].replaceAll("\u0000", "");
          outputArr[i] = outputArr[i].replaceAll("\u000b", "");
          outputArr[i] = outputArr[i].replaceAll("\u0003", "");
          outputArr[i] = outputArr[i].trim();
        }

      } catch (Exception e) {
        System.out.println("Exception: " + e);
      }
    }

    tokenInfoDto.setName(outputArr[0]);
    tokenInfoDto.setSymbol(outputArr[1]);
    tokenInfoDto.setDecimals(Qrc20Util.convertHexToDec(outputArr[2]));

    return tokenInfoDto;
  }

  public Map<String, String> getRawTx(String txid) {
    String result = "";

    // getRawTransaction 변환해서 가져오기
    try {
      HttpResponse<JsonNode> response = Unirest.post(qtumCoreUrl)
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

    System.out.println("paramInput: " + paramInput);
    System.out.println("paramOutput: " + paramOutput);

    // RPC 통신(createrawtransaction)
    String result = "";
    try {
      HttpResponse<JsonNode> response = Unirest.post(qtumCoreUrl).body(
          "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"curltest\", \n  \"method\": \"createrawtransaction\", \n  \"params\": [\n  \t\t"
              + paramInput + ",\n  \t\t" + paramOutput + "\n  ] \n}")
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
      HttpResponse<JsonNode> response = Unirest.post(qtumCoreUrl).body(
          "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"curltest\", \n  \"method\": \"signrawtransaction\", \n  \"params\": \n  [\n  \t\""
              + signTxMaterialDto.getRawTransaction() + "\",\n  \t" + inputParam + ",\n  \t[\""
              + signTxMaterialDto.getPrivateKey() + "\"]\n  ]\n}")
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
      response = Unirest.post(qtumCoreUrl).body(
          "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"curltest\", \n  \"method\": \"sendrawtransaction\", \n  \"params\": [\""
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
    accountTx.setTypeBlockchain("QTUM");
    accountTx.setSymbol(txInfoForSaveDto.getSymbol());
    BeanUtils.copyProperties(txInfoForSaveDto, accountTx);
    accountTxRepo.saveAndFlush(accountTx);

    Map<String, String> map = new HashMap<String, String>();
    map.put("isSaved", "true");
    return map;
  }

  public Map<String, String> changeRawTxForSendToken(
      ChangeRawTxForSendTokenDto changeRawTxForSendTokenDto) {

    // to(주소): hex값으로 변환하기
    String toAddrHash160 =
        Qrc20Util.to32bytesArg(Qrc20Util.addressToHash160(changeRawTxForSendTokenDto.getTo()));

    String tokenAmountHex = Qrc20Util.to32bytesArg(changeRawTxForSendTokenDto.getTokenAmountHex());

    // 기존 rawTransaction 에 추가할 내용(스마트컨트랙트 스크립트) 작성하기
    // 0000000000000000 : 사토시(16자리) 토큰 전송이라 QTUM 은 0개를 보낸다.
    // 63 : scriptPubKey 타입이 call 인 경우
    // 01040390d003012844 : 가스비용 hex + 변환된 주소
    // a9059cbb : 토큰 transfer Function Signature
    // 토큰을 받을 주소(변환된 주소)
    // 토큰 amount(변환됨) 2 INK. Qrc20.php 사용해서 변환
    // 14 : Push 20 bytes as data
    // 토큰의 Contract Address
    // c2 : OP_CALL

    // 가스 limit과 가스 price 가 250000, 40 인 경우 hex 변환값 : 01040390d003012844
    // 가스 limit과 가스 price 가 500000, 40 인 경우 hex 변환값 : 01040320a107012844
    String gasTotal = "";

    // if (changeRawTxForSendTokenDto.getGasLimit() == 250000
    // && changeRawTxForSendTokenDto.getGasPrice() == 40) {
    // gasTotal = "01040390d003012844";
    // } else if (changeRawTxForSendTokenDto.getGasLimit() == 500000
    // && changeRawTxForSendTokenDto.getGasPrice() == 40) {
    // gasTotal = "01040320a107012844";
    // } else {
    // // default
    // gasTotal = "01040390d003012844";
    // }


    // 가스 Limit : 250000 ~ 1000000 (16진수로 받음)
    // 주의 : 퀀텀 코어에서는 가스 Limit 이 25만~100만으로 처리하지만 유저에게 보여줄때는 250만~1000만으로 보여줘야함
    String gasLimit = changeRawTxForSendTokenDto.getGasLimit();

    // abcdef 라면
    // 뒤에서부터 2자리씩 끊어서 다시 붙이기
    // 5자리면 A에 0 을 넣어야함
    // efcdab
    String ab = "";
    String cd = "";
    String ef = "";

    if (gasLimit.length() == 5) {
      ef = gasLimit.substring(3, 5);
      cd = gasLimit.substring(1, 3);
      ab = "0" + gasLimit.substring(0, 1);
    } else if (gasLimit.length() == 6) {
      ef = gasLimit.substring(4, 6);
      cd = gasLimit.substring(2, 4);
      ab = gasLimit.substring(0, 2);
    }

    // 가스 Limit을 블록체인이 원하는 형식으로 바꾼다
    String convertGasLimit = ef + cd + ab;

    // 가스 Price : 40 ~ 100 (40은 QTUM Node 의 최솟값. 100 이 최댓값. 16진수로 받음)
    // 예) 40 을 16진수로 받으면 gasPrice 는 28 을 입력받아야 한다.
    gasTotal = "010403" + convertGasLimit + "01" + changeRawTxForSendTokenDto.getGasPrice() + "44";

    String scriptForSendToken = "";
    scriptForSendToken += "0000000000000000";
    scriptForSendToken += "63";
    scriptForSendToken += gasTotal;
    scriptForSendToken += "a9059cbb";
    scriptForSendToken += toAddrHash160;
    scriptForSendToken += tokenAmountHex;
    scriptForSendToken += "14";
    scriptForSendToken += changeRawTxForSendTokenDto.getContractAddress();
    scriptForSendToken += "c2";

    // 조합된 내용을 rawTransaction 에 삽입
    // 끝에서 앞으로 가면서 나오는 첫번째 ffffffff 가 input 의 마지막 정보
    // 그 ffffffff 다음에 나오는 01(output 갯수)를 02로 수정해야함
    String rawTx = changeRawTxForSendTokenDto.getRawTransaction();
    int idx1 = rawTx.lastIndexOf("ffffffff");
    String rawTx1 = rawTx.substring(0, idx1 + 8);
    // output 개수 부분(2칸) 제외
    String tempRawTx = rawTx.substring(idx1 + 8 + 2);
    // 제일 마지막 00000000 앞에 토큰 스마트컨트랙트 스크립트가 추가되어야함
    int idx2 = tempRawTx.lastIndexOf("00000000");
    String rawTx2 = tempRawTx.substring(0, idx2);
    String rawTx3 = tempRawTx.substring(idx2);

    String resultRawTx = "";
    resultRawTx += rawTx1;
    resultRawTx += "02";
    resultRawTx += rawTx2;
    resultRawTx += scriptForSendToken;
    resultRawTx += rawTx3;

    Map<String, String> map = new HashMap<String, String>();
    map.put("rawTransaction", resultRawTx);

    return map;

  }
}
