package com.knowtify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendTopicEmail(String userEmail, String userName, String topicName, String explanation, String domain) {
        if (mailSender == null) {
            logger.warn("Mail sender not configured");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setFrom("knowtify@example.com");
            message.setSubject("📚 " + domain + " - Daily Topic: " + topicName);
            message.setText(buildEmailBody(userName, topicName, explanation, domain));

            mailSender.send(message);
            logger.info("Email sent to {} for topic {}", userEmail, topicName);
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage());
        }
    }

    private String buildEmailBody(String userName, String topicName, String explanation, String domain) {
        return "Hi " + userName + ",\n\n" +
                "Here's your daily learning topic from Knowtify:\n\n" +
                "📖 Domain: " + domain + "\n" +
                "📚 Topic: " + topicName + "\n\n" +
                "Explanation:\n" +
                explanation + "\n\n" +
                "Go to Knowtify to answer this card and continue your learning:\n" +
                "https://knowtify-bb7d.onrender.com\n\n" +
                "Keep learning! 🚀\n\n" +
                "Best regards,\nKnowtify Team";
    }
}
