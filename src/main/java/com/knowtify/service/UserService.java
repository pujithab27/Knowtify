package com.knowtify.service;

import com.knowtify.entity.User;
import com.knowtify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(String username, String email, String password, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setNotificationTime("12:00");
        user.setNotificationFrequency("daily");
        user.setTotalCardsLearned(0);
        user.setCurrentStreak(0);
        user.setLongestStreak(0);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUserPreferences(Long userId, Set<String> domains, String notificationTime, String notificationFrequency) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setPreferredDomains(domains != null ? domains : new HashSet<>());
        user.setNotificationTime(notificationTime != null ? notificationTime : "12:00");
        user.setNotificationFrequency(notificationFrequency != null ? notificationFrequency : "daily");
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateUserPreferencesFromList(Long userId, List<String> domains, String notificationTime, String notificationFrequency) {
        Set<String> domainSet = domains != null ? new HashSet<>(domains) : new HashSet<>();
        return updateUserPreferences(userId, domainSet, notificationTime, notificationFrequency);
    }

    public User updateUserStats(Long userId, Integer cardsLearned, Integer currentStreak, Integer longestStreak) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setTotalCardsLearned(cardsLearned);
        user.setCurrentStreak(currentStreak);
        user.setLongestStreak(longestStreak);
        user.setLastActivityTime(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User incrementCardLearned(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setTotalCardsLearned((user.getTotalCardsLearned() != null ? user.getTotalCardsLearned() : 0) + 1);
        user.setLastActivityTime(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateLastActivityTime(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setLastActivityTime(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User findOrCreate(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(username + "@knowtify.local");
                    newUser.setPassword("default");
                    newUser.setFullName(username);
                    newUser.setNotificationTime("12:00");
                    newUser.setNotificationFrequency("daily");
                    return userRepository.save(newUser);
                });
    }

    public User getDefaultUser() {
        return findOrCreate("default_user");
    }
}
