package com.cinema.booking.service;

import com.cinema.booking.model.Account;

public interface EmailService {
    void sendRegistrationConfirmation(Account account);
    void sendProfileChangeNotification(Account account);
    void sendPasswordReset(Account account, String token);
    void sendVerificationEmail(Account account, String token);
    void sendPromotionEmail(String recipientEmail, String promotionName, String promotionCode, String description, String discountPercent, String startDate, String endDate);
}
