package com.cinema.booking.service;

import com.cinema.booking.model.Account;

public interface EmailService {
    void sendRegistrationConfirmation(Account account);
    void sendProfileChangeNotification(Account account);
    void sendPasswordReset(Account account, String token);
    void sendVerificationEmail(Account account, String token);
}
