package com.example.cabn;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    private static final String SENDER_EMAIL = "brnike01@gmail.com";
    private static final String APP_PASSWORD = "eobahdggaxzyjheg";  // Use App Password

    public static boolean sendEMAIL(String receiverEmail, int otp) {
        final boolean[] emailSent = {false};

        // SMTP Server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject("OTP Verification");
            message.setText("Your OTP is: " + otp + "\n\nDo not share it with anyone.");

            // Send email
            Transport.send(message);
            emailSent[0] = true;
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Email sending failed: " + e.getMessage());
        }

        return emailSent[0];
    }
}
