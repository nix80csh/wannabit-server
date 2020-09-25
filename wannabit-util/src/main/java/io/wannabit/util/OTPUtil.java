package io.wannabit.util;

import java.util.concurrent.TimeUnit;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class OTPUtil {

  // create secretKey
  public static String create() {
    GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
    GoogleAuthenticatorKey secretKey = googleAuthenticator.createCredentials();
    return secretKey.getKey();
  }

  // verify
  public static boolean verify(String secretKey, int code) {
    GoogleAuthenticatorConfigBuilder gacb = new GoogleAuthenticatorConfigBuilder()
        .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30)).setWindowSize(5).setCodeDigits(6);
    GoogleAuthenticator googleAuth = new GoogleAuthenticator(gacb.build());
    return googleAuth.authorize(secretKey, code);
  }

}
