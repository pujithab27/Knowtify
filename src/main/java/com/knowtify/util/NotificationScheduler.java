package com.knowtify.util;

import com.knowtify.entity.Knowledge;
import com.knowtify.entity.User;
import com.knowtify.repository.UserRepository;
import com.knowtify.service.EmailService;
import com.knowtify.service.KnowledgeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);
    private final UserRepository userRepository;
    private final KnowledgeService knowledgeService;
    private final EmailService emailService;

    public NotificationScheduler(UserRepository userRepository, KnowledgeService knowledgeService, EmailService emailService) {
        this.userRepository = userRepository;
        this.knowledgeService = knowledgeService;
        this.emailService = emailService;
    }

    @Scheduled(fixedDelay = 60000)
    public void sendScheduledNotifications() {
        try {
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            logger.debug("Checking notifications at {}", currentTime);

            List<User> users = userRepository.findAll();

            for (User user : users) {
                if (shouldSendNotification(user, currentTime)) {
                    sendEmailToUser(user);
                }
            }
        } catch (Exception e) {
            logger.error("Error in notification scheduler: {}", e.getMessage());
        }
    }

    private boolean shouldSendNotification(User user, String currentTime) {
        return user.getNotificationTime() != null &&
               user.getNotificationTime().equals(currentTime) &&
               user.getPreferredDomains() != null &&
               !user.getPreferredDomains().isEmpty() &&
               user.getEmail() != null;
    }

    private void sendEmailToUser(User user) {
        try {
            String domain = user.getPreferredDomains().iterator().next();
            Knowledge card = knowledgeService.getNextKnowledgeCard(user.getId(), domain);

            if (card != null && card.getTopic() != null) {
                emailService.sendTopicEmail(
                    user.getEmail(),
                    user.getFullName(),
                    card.getTopic().getName(),
                    card.getTopic().getContent(),
                    domain
                );
            }
        } catch (Exception e) {
            logger.warn("Could not send email to user {}: {}", user.getId(), e.getMessage());
        }
    }
}
