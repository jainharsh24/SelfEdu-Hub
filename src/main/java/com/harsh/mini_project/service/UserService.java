package com.harsh.mini_project.service;

import com.harsh.mini_project.model.AppUser;
import com.harsh.mini_project.model.Level;
import com.harsh.mini_project.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser register(String username, String rawPassword) {
        String normalized = normalizeUsername(username);
        if (userRepository.existsByUsername(normalized)) {
            throw new IllegalArgumentException("Username already exists");
        }
        AppUser user = new AppUser();
        user.setUsername(normalized);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setPreferredLevel(Level.BEGINNER);
        user.setPreferredDomain("");
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public AppUser getByUsername(String username) {
        String normalized = normalizeUsername(username);
        return userRepository.findByUsername(normalized)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        AppUser user = getByUsername(username);
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public AppUser updateSettings(String currentUsername, String requestedUsername, Level preferredLevel, String preferredDomain) {
        AppUser user = getByUsername(currentUsername);
        String normalizedUsername = normalizeUsername(requestedUsername);

        if (!normalizedUsername.isBlank() && !normalizedUsername.equals(user.getUsername()) && userRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (preferredLevel == null) {
            throw new IllegalArgumentException("Preferred level is required");
        }

        if (!normalizedUsername.isBlank()) {
            user.setUsername(normalizedUsername);
        }
        user.setPreferredLevel(preferredLevel);
        user.setPreferredDomain(normalizePreferenceDomain(preferredDomain));
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = getByUsername(username);
        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private String normalizePreferenceDomain(String preferredDomain) {
        return preferredDomain == null ? "" : preferredDomain.trim();
    }
}
