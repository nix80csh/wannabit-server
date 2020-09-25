
package io.wannabit.util;

import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailSenderUtil {

  private static final String host = "smtp.gmail.com";
  private static final int port = 587;
  private static final String password = "";
  private static final String protocol = "smtp";
  private static final String from = "no-reply@wannabit.io";

  public static boolean send(String toEmail, String subject, String content) {

    Properties mailProperties = new Properties();
    mailProperties.put("mail.smtp.auth", true);
    mailProperties.put("mail.smtp.starttls.enable", true);
    mailProperties.put("mail.smtp.starttls.required", true);

    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost(host);
    javaMailSender.setPort(port);
    javaMailSender.setUsername(from);
    javaMailSender.setPassword(password);
    javaMailSender.setProtocol(protocol);
    javaMailSender.setJavaMailProperties(mailProperties);

    MimeMessage msg = javaMailSender.createMimeMessage();
    try {
      msg.setSubject(subject);
      msg.setText(content, "UTF-8", "html");
      msg.addRecipient(RecipientType.TO, new InternetAddress(toEmail));
      msg.setFrom(new InternetAddress(from));
      javaMailSender.send(msg);
      return true;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }
}
