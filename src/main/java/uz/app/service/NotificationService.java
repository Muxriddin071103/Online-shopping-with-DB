package uz.app.service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class NotificationService {
    private final String username = "e6eee41e86ccdd";
    private final String password = "70cfee1331058c";

    public String generateCode(){
        Random rand = new Random();
        int code = 100000 + rand.nextInt(999999);
        return String.valueOf(code);
    }

    public void sendCodeToEmail(String toEmail, String confirmCode){
        Properties props = new Properties();
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username,password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Confirmation code!!!");
            message.setText("Your confirmation code is: " + confirmCode);
            Transport.send(message);
            System.out.println("Confirmation code sent to email " + toEmail);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static NotificationService notificationService;
    private NotificationService() {}
    public static NotificationService getInstance() {
        if (notificationService == null) {
            notificationService = new NotificationService();
        }
        return notificationService;
    }
}
