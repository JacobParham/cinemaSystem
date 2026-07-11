package com.cinema.booking.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.cinema.booking.model.Account;

@Service
@Profile("test")
public class NoOpEmailService implements EmailService {

    private final ConcurrentMap<String, String> lastResetTokens = new ConcurrentHashMap<>();

    @Override
    public void sendRegistrationConfirmation(Account account) {
        // No-op for tests.
    }

    @Override
    public void sendProfileChangeNotification(Account account) {
        // No-op for tests.
    }

    @Override
    public void sendPasswordReset(Account account, String token) {
        lastResetTokens.put(account.getEmail().toLowerCase(), token);
    }

    // Test helper
    public String getLastTokenFor(String email) {
        return lastResetTokens.get(email.toLowerCase());
    }
}
