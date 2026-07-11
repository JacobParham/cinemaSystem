package com.cinema.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.cinema.booking.model.Account;

@Service
@Profile("!test")
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public SmtpEmailService(JavaMailSender mailSender, @Value("${spring.mail.from:no-reply@cinemabooking.local}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendRegistrationConfirmation(Account account) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(account.getEmail());
        message.setSubject("Welcome to Cinema Booking");
        message.setText("Hello " + account.getFirstName() + " " + account.getLastName() + ",\n\n"
                + "Your account has been created successfully.\n"
                + "You can now sign in and book tickets.\n\n"
                + "Thanks,\n"
                + "Cinema Booking Team");

        try {
            mailSender.send(message);
        } catch (Exception ex) {
            // Log and swallow exceptions during development so registration still succeeds
            System.err.println("Warning: failed to send registration email: " + ex.getMessage());
        }
    }

    @Override
    public void sendProfileChangeNotification(Account account) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(account.getEmail());
        message.setSubject("Your profile was updated");
        message.setText("Hello " + account.getFirstName() + ",\n\nYour profile information was updated. If you did not make this change, please contact support immediately.\n\nThanks,\nCinema Booking Team");

        try {
            mailSender.send(message);
        } catch (Exception ex) {
            System.err.println("Warning: failed to send profile-change email: " + ex.getMessage());
        }
    }

    @Override
    public void sendPasswordReset(Account account, String token) {
        String link = "https://localhost:8080/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(account.getEmail());
        message.setSubject("Password reset request");
        message.setText("Hello " + account.getFirstName() + ",\n\nWe received a request to reset your password. Use the link below to set a new password (expires in 1 hour):\n\n" + link + "\n\nIf you didn't request this, please ignore this email.\n\nThanks,\nCinema Booking Team");

        try {
            mailSender.send(message);
        } catch (Exception ex) {
            System.err.println("Warning: failed to send password-reset email: " + ex.getMessage());
        }
    }
}
