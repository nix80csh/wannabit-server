package io.wannabit.wallet.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import io.wannabit.core.entity.AccountTx;
import io.wannabit.core.repository.AccountRepo;
import io.wannabit.core.repository.AccountTxRepo;
import io.wannabit.util.Qrc20Util;
import io.wannabit.wallet.dto.MyTxDto;
import io.wannabit.wallet.exception.LogicErrorList;
import io.wannabit.wallet.exception.LogicException;

@Service
@Transactional
public class MyTxService {

  @Value("${blockchaininfoUrl}") private String blockchaininfoUrl;
  @Value("${blockdozerUrl}") private String blockdozerUrl;
  @Value("${qtumCoreUrl}") private String qtumCoreUrl;
  @Value("${ltcCoreUrl}") private String ltcCoreUrl;
  @Value("${etcCoreUrl}") private String etcCoreUrl;
  @Value("${corePassword}") private String corePassword;

  @Autowired AccountRepo accountRepo;
  @Autowired AccountTxRepo accountTxRepo;

  public List<MyTxDto> getMyTxList(int idfAccount, String typeBlockchain) {

    // 존재하지 않는 회원일경우
    if (!accountRepo.exists(idfAccount))
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    List<AccountTx> accountTxList =
        accountTxRepo.findByAccountIdfAccountAndTypeBlockchainOrderByIdfAccountTxDesc(idfAccount,
            typeBlockchain);

    List<MyTxDto> myTxDtoList = new ArrayList<MyTxDto>();
    for (AccountTx accountTx : accountTxList) {
      MyTxDto myTxDto = new MyTxDto();
      BeanUtils.copyProperties(accountTx, myTxDto);
      myTxDto.setIdfAccount(accountTx.getAccount().getIdfAccount());

      switch (typeBlockchain) {
        case "BTC":
          myTxDtoList = getRawTxBtc(myTxDtoList, myTxDto);
          break;
        case "BCH":
          myTxDtoList = getRawTxBch(myTxDtoList, myTxDto);
          break;
        case "LTC":
          myTxDtoList = getRawTxLtc(myTxDtoList, myTxDto);
          break;
        case "QTUM":
          myTxDtoList = getRawTxQtum(myTxDtoList, myTxDto);
          break;
        case "ETC":
          // Timestamp 형식을 Unix 시간으로 변환
          try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
            Date date = sdf.parse(String.valueOf(accountTx.getRegDate()));
            Long unixTime = Long.valueOf((date.getTime() / 1000));
            myTxDtoList = getRawTxEtc(myTxDtoList, myTxDto, unixTime);
          } catch (ParseException e) {
            e.printStackTrace();
          }
          break;
        case "ETH":
          // Timestamp 형식을 Unix 시간으로 변환
          try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
            Date date = sdf.parse(String.valueOf(accountTx.getRegDate()));
            String unixTime = String.valueOf((date.getTime() / 1000));
            myTxDto.setTimestamp(unixTime);
          } catch (ParseException e) {
            e.printStackTrace();
          }
          myTxDtoList.add(myTxDto);
          break;
        default:
          System.out.println("Wrong Type");
      }
    }
    return myTxDtoList;
  }

  private List<MyTxDto> getRawTxBtc(List<MyTxDto> myTxDtoList, MyTxDto myTxDto) {

    try {
      HttpResponse<JsonNode> response =
          Unirest.get(blockchaininfoUrl + "rawtx/" + myTxDto.getTxhash()).asJson();
      String result = response.getBody().getObject().optString("out");
      myTxDto.setTimestamp(response.getBody().getObject().optString("time"));
      JSONArray resultArry = new JSONArray(result);

      String value = resultArry.getJSONObject(0).optString("value");
      int decimal = 8;
      Double doubleValue = Double.valueOf(value) / Math.pow(10, decimal);
      value = String.format("%." + decimal + "f", doubleValue);
      myTxDto.setValue(value);

      int blockHeight = Integer.valueOf(response.getBody().getObject().optString("block_height"));
      response = Unirest.get(blockchaininfoUrl + "latestblock").asJson();
      int latestBlock = Integer.valueOf(response.getBody().getObject().optString("height"));
      myTxDto.setConfirmations(String.valueOf(latestBlock - blockHeight));

    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
    myTxDtoList.add(myTxDto);

    return myTxDtoList;
  }

  private List<MyTxDto> getRawTxBch(List<MyTxDto> myTxDtoList, MyTxDto myTxDto) {

    try {

      HttpResponse<JsonNode> response =
          Unirest.get(blockdozerUrl + "insight-api/tx/" + myTxDto.getTxhash()).asJson();
      String result = response.getBody().getObject().optString("vout");
      myTxDto.setTimestamp(response.getBody().getObject().optString("time"));
      JSONArray resultArry = new JSONArray(result);

      String value = resultArry.getJSONObject(0).optString("value");

      // blockdozer API의 value 값은 사토시 단위가 아니고 이미 소수점으로 되어 있음(8로 나눌 필요없음)
      int decimal = 8;
      Double doubleValue = Double.valueOf(value);
      value = String.format("%." + decimal + "f", doubleValue);
      myTxDto.setValue(value);

      // blockdozer API 에는 컨펌 횟수가 바로 표시됨(blockchain info 와 다름)
      // 컨펌횟수가 0 인 경우 confirmations 값이 전달되지 않도록 하기
      String confirmations = response.getBody().getObject().optString("confirmations");
      if (!confirmations.equals("0")) {
        myTxDto.setConfirmations(confirmations);
      }

    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
    myTxDtoList.add(myTxDto);

    return myTxDtoList;
  }

  private List<MyTxDto> getRawTxQtum(List<MyTxDto> myTxDtoList, MyTxDto myTxDto) {

    try {
      HttpResponse<JsonNode> response = Unirest.post(qtumCoreUrl)
          .header("Content-Type", "application/json")
          .body(
              "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"wannabit\", \n  \"method\": \"getrawtransaction\", \n  \"params\": [\""
                  + myTxDto.getTxhash() + "\", 1] \n}")
          .asJson();

      String result = response.getBody().getObject().optString("result");
      JSONObject resultObj = new JSONObject(result);
      myTxDto.setConfirmations(resultObj.optString("confirmations"));
      myTxDto.setTimestamp(resultObj.optString("time"));

      JSONArray vinArray = new JSONArray(resultObj.optString("vin"));
      String sender = vinArray.getJSONObject(0).optString("address");
      JSONArray voutArray = new JSONArray(resultObj.optString("vout"));

      for (int i = 0; i < voutArray.length(); i++) {
        JSONObject voutObj = voutArray.getJSONObject(i);
        JSONObject scriptPubKeyObj = new JSONObject(voutObj.optString("scriptPubKey"));
        String asm = scriptPubKeyObj.optString("asm");

        JSONArray addressArray = new JSONArray(scriptPubKeyObj.optString("addresses"));
        String recipient = addressArray.getString(0);

        if (!sender.equals(recipient)) {
          if (myTxDto.getSymbol().equals("QTUM")) { // 퀀텀 송금
            myTxDto.setValue(voutObj.optString("value"));
          } else { // 토큰 송금
            Map<String, String> tokenInfo = new HashMap<>();
            String lines[] = asm.split(" ");
            for (int j = lines.length - 1; 0 <= j; j--) {
              if (lines[j].length() == 40) { // contract address
                tokenInfo = getTokenInfo(lines[j]);
                myTxDto.setSymbol(tokenInfo.get("symbol"));
              }
              if (lines[j].length() == 136) { // amount of tokens sent
                Integer decimal = Integer.parseInt(tokenInfo.get("decimal"));
                Long hexValue = Long.parseLong(lines[j].substring(90, 136), 16);
                Double doubleValue = hexValue.doubleValue() / Math.pow(10, decimal);
                String value = String.format("%." + decimal + "f", doubleValue);
                myTxDto.setValue(value);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
    myTxDtoList.add(myTxDto);

    return myTxDtoList;
  }

  private List<MyTxDto> getRawTxLtc(List<MyTxDto> myTxDtoList, MyTxDto myTxDto) {

    try {
      HttpResponse<JsonNode> response = Unirest.post(ltcCoreUrl)
          .header("Content-Type", "application/json")
          .body(
              "{\n  \"jsonrpc\": \"1.0\", \n  \"id\":\"wannabit\", \n  \"method\": \"getrawtransaction\", \n  \"params\": [\""
                  + myTxDto.getTxhash() + "\", 1] \n}")
          .asJson();

      String result = response.getBody().getObject().optString("result");
      JSONObject resultObj = new JSONObject(result);
      myTxDto.setConfirmations(resultObj.optString("confirmations"));
      myTxDto.setTimestamp(resultObj.optString("time"));

      JSONArray vinArray = new JSONArray(resultObj.optString("vin"));
      String sender = vinArray.getJSONObject(0).optString("address");
      JSONArray voutArray = new JSONArray(resultObj.optString("vout"));

      for (int i = 0; i < voutArray.length(); i++) {
        JSONObject voutObj = voutArray.getJSONObject(i);
        JSONObject scriptPubKeyObj = new JSONObject(voutObj.optString("scriptPubKey"));
        JSONArray addressArray = new JSONArray(scriptPubKeyObj.optString("addresses"));
        String recipient = addressArray.getString(0);

        if (!sender.equals(recipient)) {
          if (myTxDto.getSymbol().equals("LTC")) { // LTC 송금
            myTxDto.setValue(voutObj.optString("value"));
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
    myTxDtoList.add(myTxDto);
    return myTxDtoList;
  }

  private List<MyTxDto> getRawTxEtc(List<MyTxDto> myTxDtoList, MyTxDto myTxDto, Long unixTime) {

    try {
      HttpResponse<JsonNode> getTxReceiptResp = Unirest.post(etcCoreUrl).body(
          "{\n  \"jsonrpc\": \"2.0\", \n  \"id\":\"wannabit\", \n  \"method\": \"eth_getTransactionReceipt\", \n  \"params\": [\""
              + myTxDto.getTxhash() + "\"] \n}")
          .asJson();
      String getTxReceiptResult = getTxReceiptResp.getBody().getObject().optString("result");
      if (getTxReceiptResult.equals(null) || getTxReceiptResult.equals("")) {
        if (Long.valueOf(new java.util.Date().getTime()) - (unixTime * 1000) > 60000) {
          myTxDto.setConfirmations("-1");
        } else {
          myTxDto.setTimestamp("");
        }
      } else {
        JSONObject getTxReceiptResultObj = new JSONObject(getTxReceiptResult);
        String hexBlockNumber = getTxReceiptResultObj.optString("blockNumber");
        Long longBlockNumber =
            Long.parseLong(hexBlockNumber.substring(2, hexBlockNumber.length()), 16);

        HttpResponse<JsonNode> highestBlockResp = Unirest.post(etcCoreUrl).body(
            "{\n  \"jsonrpc\": \"2.0\", \n  \"id\":\"wannabit\", \n  \"method\": \"eth_blockNumber\", \n  \"params\": [] \n}")
            .asJson();
        String highestBlockResult = highestBlockResp.getBody().getObject().optString("result");
        Long longHighestBlock =
            Long.parseLong(highestBlockResult.substring(2, highestBlockResult.length()), 16);
        String confirmations = String.valueOf(longHighestBlock - longBlockNumber);
        myTxDto.setConfirmations(confirmations);

        HttpResponse<JsonNode> getBlockByNumberResp = Unirest.post(etcCoreUrl)
            .body("{\n  \"" + "jsonrpc\": \"2.0\", \n  \"" + "id\":\"wannabit\", \n  \""
                + "method\": \"eth_getBlockByNumber\", \n  \"" + "params\": [\"" + hexBlockNumber
                + "\", true] \n}")
            .asJson();
        String getBlockByNumberResult =
            getBlockByNumberResp.getBody().getObject().optString("result");
        JSONObject blockByNumberResultObj = new JSONObject(getBlockByNumberResult);
        String timestamp = blockByNumberResultObj.optString("timestamp");
        Long longTimestamp = Long.parseLong(timestamp.substring(2, timestamp.length()), 16);
        myTxDto.setTimestamp(String.valueOf(longTimestamp));

        HttpResponse<JsonNode> getTxByHashResp = Unirest.post(etcCoreUrl).body(
            "{\n  \"jsonrpc\": \"2.0\", \n  \"id\":\"wannabit\", \n  \"method\": \"eth_getTransactionByHash\", \n  \"params\": [\""
                + myTxDto.getTxhash() + "\"] \n}")
            .asJson();
        String getTxByHashResult = getTxByHashResp.getBody().getObject().optString("result");
        JSONObject getTxByHashObj = new JSONObject(getTxByHashResult);
        String hexValue = getTxByHashObj.optString("value");
        Long longValue = Long.parseLong(hexValue.substring(2, hexValue.length()), 16);
        Double doubleValue = longValue.doubleValue() / Math.pow(10, 18);
        String value = String.format("%." + 18 + "f", doubleValue);
        myTxDto.setValue(String.valueOf(value));
      }
      myTxDtoList.add(myTxDto);
    } catch (Exception e) {
      System.out.println("Exception : " + e);
    }

    return myTxDtoList;
  }

  private Map<String, String> getTokenInfo(String contractAddr) {

    Map<String, String> tokenInfo = new HashMap<>();

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

    tokenInfo.put("name", outputArr[0].toString());
    tokenInfo.put("symbol", outputArr[1].toString());
    tokenInfo.put("decimal", Qrc20Util.convertHexToDec(outputArr[2]).toString());

    return tokenInfo;
  }

}
