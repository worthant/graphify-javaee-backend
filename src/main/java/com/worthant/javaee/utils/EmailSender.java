package com.worthant.javaee.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

import static com.worthant.javaee.config.SecurityConfig.getEmailAuthKey;

@Slf4j
public class EmailSender {

    private static final Session emailSession;

    static {
        // Setup smtp server
        Properties props = new Properties();
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        emailSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("boris0indeed@gmail.com", getEmailAuthKey());
            }
        });
    }

    public static void sendEmail(String email, String subject, String text) {
        try {
            Message message = new MimeMessage(emailSession);
            message.setFrom(new InternetAddress("yourEmail@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException e) {
            log.error("Error sending email: {}", e.getMessage(), e);
        }
    }

    public static void sendSignUpEmail(String email, String username, String password) {
        String subject = "Welcome to Our Service!";
        String text = "Hello " + username + ",\n\n" +
                "You have successfully signed up with the following credentials:\n" +
                "Username: " + username + "\n" +
                "Password: " + password + "\n\n" +
                "Do not forget your password and keep it safe.";

        sendEmail(email, subject, text);
    }

    public static void sendPasswordResetEmail(String email, String password) {
        String subject = "Your Password Has Been Reset";
        String text = "Hello,\n\n" +
                "Your password has been reset to: " + password + "\n" +
                "Please change it after your next login for security reasons.";

        sendEmail(email, subject, text);
    }

    public static void main(String[] args) {
        sendSignUpEmail("b_dvorkin@niuitmo.ru", "username", "password123");
        sendPasswordResetEmail("b_dvorkin@niuitmo.ru", "newPassword123");
    }
}


