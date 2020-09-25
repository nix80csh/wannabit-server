package io.wannabit.wallet.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.transaction.Transactional;

import org.json.JSONException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.wannabit.core.entity.Account;
import io.wannabit.core.repository.AccountRepo;
import io.wannabit.core.repository.AccountTokenRepo;
import io.wannabit.util.MailSenderUtil;
import io.wannabit.util.OTPUtil;
import io.wannabit.util.SlackNotificationUtil;
import io.wannabit.util.TokenUtil;
import io.wannabit.wallet.dto.AuthDto.QnaDto;
import io.wannabit.wallet.dto.AuthDto.ResetPwdDto;
import io.wannabit.wallet.dto.AuthDto.SigninDto;
import io.wannabit.wallet.dto.AuthDto.SignupDto;
import io.wannabit.wallet.dto.AuthDto.VerifyAuthCodePasswordDto;
import io.wannabit.wallet.dto.AuthDto.VerifyEmailDto;
import io.wannabit.wallet.dto.AuthDto.VerifyOTPDto;
import io.wannabit.wallet.exception.LogicErrorList;
import io.wannabit.wallet.exception.LogicException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class AuthService {

  @Value("${utils.mailboxLayer.key}") private String emailApiKey;
  @Value("${wannabit.domain}") private String serviceDomain;
  @Value("${spring.profiles.active}") private String serverName;

  @Autowired AccountRepo accountRepo;
  @Autowired AccountTokenRepo accountTokenRepo;
  @Autowired AuthenticationManager authenticationManager;

  public SignupDto signup(SignupDto signupDto) {

    // 이메일 중복여부
    if (accountRepo.findByEmail(signupDto.getEmail()) != null)
      throw new LogicException(LogicErrorList.DuplicateEntity_Account);

    // 존재하지 않은 이메일 체크
    if (!emailCheckValid(signupDto.getEmail()))
      throw new LogicException(LogicErrorList.DoesNotExist_Email);

    Account account = new Account();
    BeanUtils.copyProperties(signupDto, account);

    // 임시 OTP발급 후 DB저장
    account.setOtpKey(OTPUtil.create() + "-temp");
    accountRepo.save(account);

    signupDto.setEmail(null);
    signupDto.setPassword(null);
    signupDto.setIsRegist(true);

    return signupDto;
  }

  public SigninDto signin(SigninDto signinDto) {
    // 인증
    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(signinDto.getEmail(), signinDto.getPassword());
    Authentication authentication = authenticationManager.authenticate(token);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 토큰생성
    Account account = accountRepo.findByEmail(signinDto.getEmail());
    String xAuthToken = TokenUtil.createToken(account.getEmail(), account.getPassword());

    BeanUtils.copyProperties(account, signinDto);
    signinDto.setEmail(account.getEmail());
    signinDto.setIdfAccount(account.getIdfAccount());
    signinDto.setXAuthToken(xAuthToken);
    signinDto.setPassword(null);
    signinDto.setAuthCodeEmail(account.getAuthCodeEmail());
    signinDto.setOtpKey(account.getOtpKey());

    return signinDto;
  }

  public SignupDto sendAuthEmail(SignupDto signupDto) {

    Account account = accountRepo.findByEmail(signupDto.getEmail());
    if (account == null)
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    if (!account.getPassword().equals(signupDto.getPassword()))
      throw new LogicException(LogicErrorList.NotMatched);

    String authCode = getAuthCode();
    String confirmedUrl = "https://" + serviceDomain + "/confirmMail/" + authCode + "/"
        + signupDto.getEmail() + "/" + signupDto.getCountryCode();
    String title = "";
    String content = "";
    if (signupDto.getCountryCode().equals("KR")) {
      title = "wannabit 회원가입 인증";
      content =
          "<!doctype HTML><html lang=\"ko\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, width=device-width\"><title>wannabit</title></head><body><div style=\"width: 98%; max-width: 950px; margin: 0 auto; padding: 20px; text-align: center; font-family: '맑은 고딕', Helvetica Neue, Helvetica, Arial, sans-serif;\"><div style=\"overflow: hidden; margin: 0 auto; border: 6px solid #65a7d2; background: #fff;\"><div style=\"width: 100%; background: #fbfbfb; margin: 0 auto; padding-top: 32px; padding-bottom: 26px; border-bottom: 1px solid #e8e8e8;\"><img src=\"https://wallet-dev.wannabit.io/assets/imgs/home/wannabit_logo.png\" style=\"display: block;margin: 0 auto;\" height=\"28\" alt=\"\"></div><div style=\"padding:50px 25px 50px;\"><h2 style=\"font-size: 24px; font-weight: bold; color: #396485; margin: 0; margin-bottom: 30px;\">회원가입 인증</h2><p style=\"line-height: 1.6;\">WannaBit 회원가입을 환영합니다.<br>지갑 서비스 이용은 이메일 인증 후 가능합니다.<br>아래 버튼을 클릭하여 이메일 인증을 해주세요.</p>"
              + "<a href=\"" + confirmedUrl
              + "\" style=\"display: inline-block; margin: 22px 0 30px; padding: 20px; color: #fff; background-color: #65a7d2; text-decoration: none; border-radius: 5px;\">이메일 인증하기</a><p style=\"line-height: 1.6; margin-bottom: 14px;\">저희 서비스를 이용해주셔서 감사합니다.</p><p style=\"font-size: 12px; line-height: 1.6; margin: 0;\">* 해당 메일은 발신전용 메일입니다. 문의사항은 <a href=\"mailto:help@wannabit.io\" style=\"color:#396485;\">help@wannabit.io</a>로 문의해주세요.</p></div><div style=\"padding: 20px 25px 40px; color: #636363;\"><span style=\"display: block; margin-bottom: 10px; font-size: 12px;\">© Wannabit Inc. All rights reserved.</span><p style=\"line-height: 1.6; font-size: 12px;\">고객센터 <a href=\"mailto:help@wannabit.io\" style=\"color: #396485;\">help@wannabit.io</a></p></div></div></div></body></html>";
    } else if (signupDto.getCountryCode().equals("CN")) {
      title = "欢迎加入 wannabit";
      content =
          "<!doctype HTML><html lang=\"cn\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, width=device-width\"><title>wannabit</title></head><body><div style=\"width: 98%; max-width: 950px; margin: 0 auto; padding: 20px; text-align: center; font-family: '맑은 고딕', Helvetica Neue, Helvetica, Arial, sans-serif;\"><div style=\"overflow: hidden; margin: 0 auto; border: 6px solid #65a7d2; background: #fff;\"><div style=\"width: 100%; background: #fbfbfb; margin: 0 auto; padding-top: 32px; padding-bottom: 26px; border-bottom: 1px solid #e8e8e8;\"><img src=\"https://wallet-dev.wannabit.io/assets/imgs/home/wannabit_logo.png\" style=\"display: block;margin: 0 auto;\" height=\"28\" alt=\"\"></div><div style=\"padding:50px 25px 50px;\"><h2 style=\"font-size: 24px; font-weight: bold; color: #396485; margin: 0; margin-bottom: 30px;\">欢迎加入Wannabit</h2><p style=\"line-height: 1.6;\">欢迎加入Wannabit<br>验证邮箱以后能使用钱包服务<br>为了验证您的邮箱请点击按钮</p>"
              + "<a href=\"" + confirmedUrl
              + "\" style=\"display: inline-block; margin: 22px 0 30px; padding: 20px; color: #fff; background-color: #65a7d2; text-decoration: none; border-radius: 5px;\">邮箱验证</a><p style=\"line-height: 1.6; margin-bottom: 14px;\">谢谢使用我们的服务。</p><p style=\"font-size: 12px; line-height: 1.6; margin: 0;\">* 请勿回复此邮件。有问题请联系客户服务 <a href=\"mailto:help@wannabit.io\" style=\"color:#396485;\">help@wannabit.io</a></p></div><div style=\"padding: 20px 25px 40px; color: #636363;\"><span style=\"display: block; margin-bottom: 10px; font-size: 12px;\">© Wannabit Inc. All rights reserved.</span><p style=\"line-height: 1.6; font-size: 12px;\">고객센터 <a href=\"mailto:help@wannabit.io\" style=\"color: #396485;\">help@wannabit.io</a></p></div></div></div></body></html>";
    } else {
      title = "Welcome to All-In-One Cryptocurrency Wallet, wannabit";
      content =
          "<!doctype HTML><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, width=device-width\"><title>wannabit</title></head><body><div style=\"width: 98%; max-width: 950px; margin: 0 auto; padding: 20px; text-align: center; font-family: '맑은 고딕', Helvetica Neue, Helvetica, Arial, sans-serif;\"><div style=\"overflow: hidden; margin: 0 auto; border: 6px solid #65a7d2; background: #fff;\"><div style=\"width: 100%; background: #fbfbfb; margin: 0 auto; padding-top: 32px; padding-bottom: 26px; border-bottom: 1px solid #e8e8e8;\"><img src=\"https://wallet-dev.wannabit.io/assets/imgs/home/wannabit_logo.png\" style=\"display: block;margin: 0 auto;\" height=\"28\" alt=\"\"></div><div style=\"padding:50px 25px 50px;\"><h2 style=\"font-size: 24px; font-weight: bold; color: #396485; margin: 0; margin-bottom: 30px;\">Registration</h2><p style=\"line-height: 1.6;\">Welcome to Wannabit, All-In-One Cryptocurrency Wallet Solution<br>You can use your wallet service after email verification.<br>Click the button below to verify your e-mail address.</p>"
              + "<a href=\"" + confirmedUrl
              + "\" style=\"display: inline-block; margin: 22px 0 30px; padding: 20px; color: #fff; background-color: #65a7d2; text-decoration: none; border-radius: 5px;\">Verify My Email Address</a><p style=\"line-height: 1.6; margin-bottom: 14px;\">Thank you for using our service.</p><p style=\"font-size: 12px; line-height: 1.6; margin: 0;\">* This is an e-mail only. For any inquiries, please contact <a href=\"mailto:help@wannabit.io\" style=\"color:#396485;\">help@wannabit.io</a></p></div><div style=\"padding: 20px 25px 40px; color: #636363;\"><span style=\"display: block; margin-bottom: 10px; font-size: 12px;\">© Wannabit Inc. All rights reserved.</span><p style=\"line-height: 1.6; font-size: 12px;\">Support Center <a href=\"mailto:help@wannabit.io\" style=\"color: #396485;\">help@wannabit.io</a></p></div></div></div></body></html>";
    }

    boolean isSent = MailSenderUtil.send(signupDto.getEmail(), title, content);

    if (!isSent)
      throw new LogicException(LogicErrorList.MailModuleException);

    account.setAuthCodeEmail(authCode);
    accountRepo.save(account);

    signupDto.setEmail(null);
    signupDto.setPassword(null);
    signupDto.setCountryCode(null);
    signupDto.setIsSent(true);

    return signupDto;
  }

  private boolean emailCheckValid(String email) {
    HttpResponse<JsonNode> response;
    try {
      response = Unirest.get("http://apilayer.net/api/check?access_key=" + emailApiKey + "&email="
          + email + "&smtp=1&format=1").header("Accept", "application/json").asJson();

      Boolean format_valid = (Boolean) response.getBody().getObject().get("format_valid");
      Boolean mx_found = (Boolean) response.getBody().getObject().get("mx_found");
      Boolean smtp_check = (Boolean) response.getBody().getObject().get("smtp_check");

      if (format_valid == true && mx_found == true && smtp_check == true) {
        return true;
      } else {
        return false;
      }
    } catch (UnirestException | JSONException e) {
      return false;
    }
  }

  public Map<String, Boolean> verifyEmail(VerifyEmailDto verifyEmailDto) {

    // 존재하지 않는 회원
    if (accountRepo.findByEmail(verifyEmailDto.getEmail()) == null)
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    Account account = accountRepo.findByEmail(verifyEmailDto.getEmail());

    // 이미 인증된 경우
    if (account.getAuthCodeEmail().equals("-confirmed"))
      throw new LogicException(LogicErrorList.NoLongerVaild);

    // AuthCode 불일치
    if (!verifyEmailDto.getAuthCodeEmail().equals(account.getAuthCodeEmail()))
      throw new LogicException(LogicErrorList.NotMatched);
    account.setAuthCodeEmail("-confirmed");
    accountRepo.saveAndFlush(account);

    String message = "";
    if (serverName.equals("prod")) {
      message =
          "\n 총 회원수 (live): " + accountRepo.count() + "\n 가입한 이메일: " + verifyEmailDto.getEmail();
    } else {
      message =
          "\n 총 회원수 (dev): " + accountRepo.count() + "\n 가입한 이메일: " + verifyEmailDto.getEmail();
    }

    // Slack Notification
    if (!SlackNotificationUtil.send("registration", "Wannabit Bot", message))
      throw new LogicException(LogicErrorList.FailedSlackNotification);

    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isVerify", true);
    return map;
  }

  public Map<String, Boolean> sendResetPwdEmail(String email, String countryCode) {

    // 존재하지 않는 회원
    if (accountRepo.findByEmail(email) == null)
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    Account account = accountRepo.findByEmail(email);
    // 이미 인증된 경우
    if (!account.getAuthCodeEmail().equals("-confirmed"))
      throw new LogicException(LogicErrorList.NotVerifyEmail);

    String authCode = getAuthCode();
    String resetPwdUrl =
        "https://" + serviceDomain + "/resetPwd/" + authCode + "/" + email + "/" + countryCode;
    String title = "";
    String content = "";
    if (countryCode.equals("KR")) {
      title = "wannabit 비밀번호 변경 요청";
      content =
          "<!doctype HTML><html lang=\"ko\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, width=device-width\"><title>wannabit</title></head><body><div style=\"width: 98%; max-width: 950px; margin: 0 auto; padding: 20px; text-align: center; font-family: '맑은 고딕', Helvetica Neue, Helvetica, Arial, sans-serif;\"><div style=\"overflow: hidden; margin: 0 auto; border: 6px solid #65a7d2; background: #fff;\"><div style=\"width: 100%; background: #fbfbfb; margin: 0 auto; padding-top: 32px; padding-bottom: 26px; border-bottom: 1px solid #e8e8e8;\"><img src=\"https://wallet-dev.wannabit.io/assets/imgs/home/wannabit_logo.png\" style=\"display: block;margin: 0 auto;\" height=\"28\" alt=\"\"></div><div style=\"padding:50px 25px 50px;\"><h2 style=\"font-size: 24px; font-weight: bold; color: #396485; margin: 0; margin-bottom: 30px;\">비밀번호 변경</h2><p style=\"line-height: 1.6;\">회원님께서 비밀번호 변경 요청을 하셨습니다.<br>아래 버튼을 클릭하여 비밀번호를 변경해주세요.</p>"
              + "<a href=\"" + resetPwdUrl
              + "\" style=\"display: inline-block; margin: 22px 0 30px; padding: 20px; color: #fff; background-color: #65a7d2; text-decoration: none; border-radius: 5px;\">비밀번호 변경하기</a><p style=\"line-height: 1.6; margin-bottom: 14px;\">저희 서비스를 이용해주셔서 감사합니다.</p><p style=\"font-size: 12px; line-height: 1.6; margin: 0;\">* 해당 메일은 발신전용 메일입니다. 문의사항은 <a href=\"mailto:help@wannabit.io\" style=\"color:#396485;\">help@wannabit.io</a>로 문의해주세요.</p></div><div style=\"padding: 20px 25px 40px; color: #636363;\"><span style=\"display: block; margin-bottom: 10px; font-size: 12px;\">© Wannabit Inc. All rights reserved.</span><p style=\"line-height: 1.6; font-size: 12px;\">고객센터 <a href=\"mailto:help@wannabit.io\" style=\"color: #396485;\">help@wannabit.io</a></p></div></div></div></body></html>";
    } else if (countryCode.equals("CN")) {
      title = "wannabit 密码重置";
      content =
          "<!doctype HTML><html lang=\"cn\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, width=device-width\"><title>wannabit</title></head><body><div style=\"width: 98%; max-width: 950px; margin: 0 auto; padding: 20px; text-align: center; font-family: '맑은 고딕', Helvetica Neue, Helvetica, Arial, sans-serif;\"><div style=\"overflow: hidden; margin: 0 auto; border: 6px solid #65a7d2; background: #fff;\"><div style=\"width: 100%; background: #fbfbfb; margin: 0 auto; padding-top: 32px; padding-bottom: 26px; border-bottom: 1px solid #e8e8e8;\"><img src=\"https://wallet-dev.wannabit.io/assets/imgs/home/wannabit_logo.png\" style=\"display: block;margin: 0 auto;\" height=\"28\" alt=\"\"></div><div style=\"padding:50px 25px 50px;\"><h2 style=\"font-size: 24px; font-weight: bold; color: #396485; margin: 0; margin-bottom: 30px;\">密码重置</h2><p style=\"line-height: 1.6;\">你最近启动了一个密码重置您的Wannabit 账户。<br>要完成此过程，请单击下面的链接。</p>"
              + "<a href=\"" + resetPwdUrl
              + "\" style=\"display: inline-block; margin: 22px 0 30px; padding: 20px; color: #fff; background-color: #65a7d2; text-decoration: none; border-radius: 5px;\">密码重置</a><p style=\"line-height: 1.6; margin-bottom: 14px;\">谢谢使用我们的服务。</p><p style=\"font-size: 12px; line-height: 1.6; margin: 0;\">* 此为自动回复， 请勿直接回复此邮件。有任何问题和疑问请随时直接联系 <a href=\"mailto:help@wannabit.io\" style=\"color:#396485;\">help@wannabit.io</a></p></div><div style=\"padding: 20px 25px 40px; color: #636363;\"><span style=\"display: block; margin-bottom: 10px; font-size: 12px;\">© Wannabit Inc. All rights reserved.</span><p style=\"line-height: 1.6; font-size: 12px;\">고객센터 <a href=\"mailto:help@wannabit.io\" style=\"color: #396485;\">help@wannabit.io</a></p></div></div></div></body></html>";
    } else {
      title = "Password Reset Request";
      content =
          "<!doctype HTML><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, width=device-width\"><title>wannabit</title></head><body><div style=\"width: 98%; max-width: 950px; margin: 0 auto; padding: 20px; text-align: center; font-family: '맑은 고딕', Helvetica Neue, Helvetica, Arial, sans-serif;\"><div style=\"overflow: hidden; margin: 0 auto; border: 6px solid #65a7d2; background: #fff;\"><div style=\"width: 100%; background: #fbfbfb; margin: 0 auto; padding-top: 32px; padding-bottom: 26px; border-bottom: 1px solid #e8e8e8;\"><img src=\"https://wallet-dev.wannabit.io/assets/imgs/home/wannabit_logo.png\" style=\"display: block;margin: 0 auto;\" height=\"28\" alt=\"\"></div><div style=\"padding:50px 25px 50px;\"><h2 style=\"font-size: 24px; font-weight: bold; color: #396485; margin: 0; margin-bottom: 30px;\">RESET MY PASSWORD</h2><p style=\"line-height: 1.6;\">You have requested a new password for your wannabit account.<br>Click the link below to reset your password</p>"
              + "<a href=\"" + resetPwdUrl
              + "\" style=\"display: inline-block; margin: 22px 0 30px; padding: 20px; color: #fff; background-color: #65a7d2; text-decoration: none; border-radius: 5px;\">RESET MY PASSWORD</a><p style=\"line-height: 1.6; margin-bottom: 14px;\">Thank you for using our service.</p><p style=\"font-size: 12px; line-height: 1.6; margin: 0;\">* Do not respond to this e-mail. For any inquiries, please contact <a href=\"mailto:help@wannabit.io\" style=\"color:#396485;\">help@wannabit.io</a></p></div><div style=\"padding: 20px 25px 40px; color: #636363;\"><span style=\"display: block; margin-bottom: 10px; font-size: 12px;\">© Wannabit Inc. All rights reserved.</span><p style=\"line-height: 1.6; font-size: 12px;\">Support Center <a href=\"mailto:help@wannabit.io\" style=\"color: #396485;\">help@wannabit.io</a></p></div></div></div></body></html>";
    }

    boolean isSent = MailSenderUtil.send(email, title, content);

    if (!isSent)
      throw new LogicException(LogicErrorList.MailModuleException);

    account.setAuthCodePassword(authCode);

    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isSent", true);
    return map;
  }

  public Map<String, Boolean> resetPwd(ResetPwdDto resetPwdDto) {

    Account account = accountRepo.findByEmail(resetPwdDto.getEmail());

    // 존재하지 않는 회원
    if (account == null)
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    // AuthCode 불일치
    if (!resetPwdDto.getAuthCodePassword().equals(account.getAuthCodePassword()))
      throw new LogicException(LogicErrorList.NotMatched);

    account.setAuthCodePassword("-confirmed");
    account.setPassword(resetPwdDto.getPassword());
    accountRepo.save(account);

    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isReset", true);
    return map;
  }

  public Map<String, Boolean> verifyOtp(VerifyOTPDto verifyOTPDto) {

    Account account = accountRepo.findOne(verifyOTPDto.getIdfAccount());

    // 회원이 존재하지 않을경우
    if (account == null)
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    // OTPKey가 존재하지 않거나 임시OTPKey일경우
    if (account.getOtpKey() == null || account.getOtpKey().contains("-temp"))
      throw new LogicException(LogicErrorList.DoesNotExist_OTPKey);

    boolean verifyOtp =
        OTPUtil.verify(account.getOtpKey(), Integer.valueOf(verifyOTPDto.getOtpCode()));
    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isVerify", verifyOtp);
    return map;
  }

  public Map<String, Boolean> verifyAuthCodeEmail(
      VerifyAuthCodePasswordDto verifyAuthCodePasswordDto) {

    Account account = accountRepo.findByEmail(verifyAuthCodePasswordDto.getEmail());

    // 회원이 존재하지 않을경우
    if (account == null)
      throw new LogicException(LogicErrorList.DoesNotExist_Account);

    // AuthCodePassword가 일치하지 않을경우
    if (!account.getAuthCodePassword().equals(verifyAuthCodePasswordDto.getAuthCodePassword()))
      throw new LogicException(LogicErrorList.NotMatchedAuthCodePassword);

    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isVerify", true);
    return map;
  }

  public Map<String, Boolean> sendQnaEmail(QnaDto qnaDto) {

    // automatic email notification to help@wannabit.io
    boolean isSent = MailSenderUtil.send("help@wannabit.io", qnaDto.getEmail() + " 님의 문의사항입니다.",
        "답변 받을 회원 이메일: " + qnaDto.getEmail() + "<br><hr><br>" + qnaDto.getContent());

    if (!isSent)
      throw new LogicException(LogicErrorList.MailModuleException);

    String title = "";
    String content = "";

    if (qnaDto.getCountryCode().equals("KR")) {
      title = "wannabit 문의사항 접수완료";
      content =
          "<!doctype HTML><html lang=\"ko\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, width=device-width\"><title>wannabit</title></head><body><div style=\"width: 98%; max-width: 950px; margin: 0 auto; padding: 20px; text-align: center; font-family: '맑은 고딕', Helvetica Neue, Helvetica, Arial, sans-serif;\"><div style=\"overflow: hidden; margin: 0 auto; border: 6px solid #65a7d2; background: #fff;\"><div style=\"width: 100%; background: #fbfbfb; margin: 0 auto; padding-top: 32px; padding-bottom: 26px; border-bottom: 1px solid #e8e8e8;\"><img src=\"https://wallet-dev.wannabit.io/assets/imgs/home/wannabit_logo.png\" style=\"display: block;margin: 0 auto;\" height=\"28\" alt=\"\"></div><div style=\"padding:50px 25px 50px;\"><h2 style=\"font-size: 24px; font-weight: bold; color: #396485; margin: 0; margin-bottom: 30px;\">문의사항 접수 완료</h2><p style=\"line-height: 1.6; margin-bottom: 30px;\">회원님께서 문의하신 내용이 정상적으로 접수되었습니다.<br>확인 후 해당 이메일로 친절히 답변드리겠습니다.</p><p style=\"line-height: 1.6; margin-bottom: 14px;\">저희 서비스를 이용해주셔서 감사합니다.</p><p style=\"font-size: 12px; line-height: 1.6; margin: 0;\">* 해당 메일은 발신전용 메일입니다. 문의사항은 <a href=\"mailto:help@wannabit.io\" style=\"color:#396485;\">help@wannabit.io</a>로 문의해주세요.</p></div><div style=\"padding: 20px 25px 40px; color: #636363;\"><span style=\"display: block; margin-bottom: 10px; font-size: 12px;\">© Wannabit Inc. All rights reserved.</span><p style=\"line-height: 1.6; font-size: 12px;\">고객센터 <a href=\"mailto:help@wannabit.io\" style=\"color: #396485;\">help@wannabit.io</a></p></div></div></div></body></html>";
    } else if (qnaDto.getCountryCode().equals("CN")) {
      title = "wannabit Inquiry Submitted";
      content =
          "<!doctype HTML><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, width=device-width\"><title>wannabit</title></head><body><div style=\"width: 98%; max-width: 950px; margin: 0 auto; padding: 20px; text-align: center; font-family: '맑은 고딕', Helvetica Neue, Helvetica, Arial, sans-serif;\"><div style=\"overflow: hidden; margin: 0 auto; border: 6px solid #65a7d2; background: #fff;\"><div style=\"width: 100%; background: #fbfbfb; margin: 0 auto; padding-top: 32px; padding-bottom: 26px; border-bottom: 1px solid #e8e8e8;\"><img src=\"https://wallet-dev.wannabit.io/assets/imgs/home/wannabit_logo.png\" style=\"display: block;margin: 0 auto;\" height=\"28\" alt=\"\"></div><div style=\"padding:50px 25px 50px;\"><h2 style=\"font-size: 24px; font-weight: bold; color: #396485; margin: 0; margin-bottom: 30px;\">Inquiry submitted</h2><p style=\"line-height: 1.6; margin-bottom: 30px;\">Your inquiry has been successfully submitted.<br>We will respond by e-mail after reviewing your issue.</p><p style=\"line-height: 1.6; margin-bottom: 14px;\">Thank you for using our service.</p><p style=\"font-size: 12px; line-height: 1.6; margin: 0;\">* This is an e-mail only. For any inquiries, please contact <a href=\"mailto:help@wannabit.io\" style=\"color:#396485;\">help@wannabit.io</a></p></div><div style=\"padding: 20px 25px 40px; color: #636363;\"><span style=\"display: block; margin-bottom: 10px; font-size: 12px;\">© Wannabit Inc. All rights reserved.</span><p style=\"line-height: 1.6; font-size: 12px;\">Support Center <a href=\"mailto:help@wannabit.io\" style=\"color: #396485;\">help@wannabit.io</a></p></div></div></div></body></html>";
    } else {
      title = "wannabit Inquiry Submitted";
      content =
          "<!doctype HTML><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, width=device-width\"><title>wannabit</title></head><body><div style=\"width: 98%; max-width: 950px; margin: 0 auto; padding: 20px; text-align: center; font-family: '맑은 고딕', Helvetica Neue, Helvetica, Arial, sans-serif;\"><div style=\"overflow: hidden; margin: 0 auto; border: 6px solid #65a7d2; background: #fff;\"><div style=\"width: 100%; background: #fbfbfb; margin: 0 auto; padding-top: 32px; padding-bottom: 26px; border-bottom: 1px solid #e8e8e8;\"><img src=\"https://wallet-dev.wannabit.io/assets/imgs/home/wannabit_logo.png\" style=\"display: block;margin: 0 auto;\" height=\"28\" alt=\"\"></div><div style=\"padding:50px 25px 50px;\"><h2 style=\"font-size: 24px; font-weight: bold; color: #396485; margin: 0; margin-bottom: 30px;\">Inquiry submitted</h2><p style=\"line-height: 1.6; margin-bottom: 30px;\">Your inquiry has been successfully submitted.<br>We will respond by e-mail after reviewing your issue.</p><p style=\"line-height: 1.6; margin-bottom: 14px;\">Thank you for using our service.</p><p style=\"font-size: 12px; line-height: 1.6; margin: 0;\">* This is an e-mail only. For any inquiries, please contact <a href=\"mailto:help@wannabit.io\" style=\"color:#396485;\">help@wannabit.io</a></p></div><div style=\"padding: 20px 25px 40px; color: #636363;\"><span style=\"display: block; margin-bottom: 10px; font-size: 12px;\">© Wannabit Inc. All rights reserved.</span><p style=\"line-height: 1.6; font-size: 12px;\">Support Center <a href=\"mailto:help@wannabit.io\" style=\"color: #396485;\">help@wannabit.io</a></p></div></div></div></body></html>";
    }

    // QnA automatic email notification to the user
    boolean isNotified = MailSenderUtil.send(qnaDto.getEmail(), title, content);

    if (!isNotified)
      throw new LogicException(LogicErrorList.MailModuleException);

    // slack notification
    if (!SlackNotificationUtil.send("help", "Wannabit Bot",
        qnaDto.getEmail() + " 님의 문의사항입니다.\n" + qnaDto.getContent()))
      throw new LogicException(LogicErrorList.FailedSlackNotification);

    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("isSent", true);
    return map;
  }

  private static String getAuthCode() {
    Random rnd = new Random();
    StringBuffer buf = new StringBuffer();
    // 10자리 영문숫자 조합 임시 비빌번호 생성
    for (int i = 0; i < 10; i++) {
      if (rnd.nextBoolean()) {
        buf.append((char) ((rnd.nextInt(26)) + 65));
      } else {
        buf.append((rnd.nextInt(10)));
      }
    }
    return buf.toString();
  }


}
