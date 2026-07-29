package com.cinema.booking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @Override
    public void sendVerificationEmail(Account account, String token) {
        // No-op for tests.
    }

    // Test helper
    public String getLastTokenFor(String email) {
        return lastResetTokens.get(email.toLowerCase());
    }

    @Override
    public void sendPromotionEmail(
            String recipientEmail,
            String promotionName,
            String promotionCode,
            String description,
            String discountPercent,
            String startDate,
            String endDate
    ) {
        System.out.println(
                "Promotion email disabled. "
                        + "Would send promotion "
                        + promotionCode
                        + " to "
                        + recipientEmail
        );
    }

    @Override
    public void sendOrderConfirmation(
            String recipientEmail,
            String recipientName,
            String movieTitle,
            String showroomName,
            LocalDate showDate,
            LocalTime showTime,
            int adultTickets,
            int childTickets,
            int seniorTickets,
            BigDecimal adultPrice,
            BigDecimal childPrice,
            BigDecimal seniorPrice,
            String seatNumbers,
            BigDecimal totalPrice
    ) {
        // No-op for tests.
    }
}
