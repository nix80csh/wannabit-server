package io.wannabit.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AmazonSimpleEmailUtil {

  private static final String from = "no-reply@wannabit.io";
  private static final String host = "email-smtp.us-west-2.amazonaws.com";
  private static final String port = "587";
  private static final String user = "";
  private static final String password = "";

  public static boolean send(String toName, String Subject, String body) {
    try {
      Properties props = System.getProperties();
      props.put("mail.transport.protocol", "smtp");
      props.put("mail.smtp.port", port);
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.auth", "true");

      Session session = Session.getDefaultInstance(props);
      MimeMessage msg = new MimeMessage(session);
      msg.setFrom(from);
      msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toName));
      msg.setSubject(Subject);
      msg.setContent(body, "text/html; charset=utf-8");
      Transport transport = session.getTransport();

      System.out.println("Sending...");
      transport.connect(host, user, password);
      transport.sendMessage(msg, msg.getAllRecipients());
      System.out.println("Email sent!");
      transport.close();
      return true;
    } catch (Exception ex) {
      return false;
    }
  }
}
