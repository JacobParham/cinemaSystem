package com.cinema.booking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.cinema.booking.model.Account;

public interface EmailService {
    void sendRegistrationConfirmation(Account account);
    void sendProfileChangeNotification(Account account);
    void sendPasswordReset(Account account, String token);
    void sendVerificationEmail(Account account, String token);
    void sendPromotionEmail(String recipientEmail, String promotionName, String promotionCode, String description, String discountPercent, String startDate, String endDate);
    void sendOrderConfirmation(String recipientEmail, String recipientName, String movieTitle,
            String showroomName, LocalDate showDate, LocalTime showTime,
            int adultTickets, int childTickets, int seniorTickets,
            BigDecimal adultPrice, BigDecimal childPrice, BigDecimal seniorPrice,
            String seatNumbers, BigDecimal totalPrice);
}
