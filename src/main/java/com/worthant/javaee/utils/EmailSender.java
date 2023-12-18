package com.worthant.javaee.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class EmailSender {
    public static void sendLoginEmail(String email) {
        // Set up your email properties
        Properties props = new Properties();
        // SMTP configuration

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("boris0indeed@gmail.com", "b3514132");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("boris0indeed@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Login Notification");
            message.setText("You've successfully logged in!");

            Transport.send(message);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

}
