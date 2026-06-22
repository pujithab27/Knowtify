package com.knowtify.controller;

import com.knowtify.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final EmailService emailService;

    @GetMapping("/send-email")
    public Map<String, String> sendTestEmail() {
        try {
            emailService.sendTopicEmail(
                "test@example.com",
                "Test User",
                "Arrays",
                "Arrays are contiguous memory blocks storing elements with O(1) random access.",
                "DSA"
            );
            return Map.of("status", "✅ Email sent! Check Mailtrap inbox");
        } catch (Exception e) {
            return Map.of("status", "❌ Error: " + e.getMessage());
        }
    }
}
