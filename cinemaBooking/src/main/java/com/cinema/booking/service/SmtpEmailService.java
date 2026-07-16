package com.cinema.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cinema.booking.model.Account;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
@Profile("!test")
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String fromName;

    public SmtpEmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.from:no-reply@cinemabooking.local}") String fromAddress,
            @Value("${spring.mail.from-name:Cinema Booking}") String fromName) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
    }

    private MimeMessageHelper createMessage(String to) throws Exception {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
        helper.setFrom(new InternetAddress(fromAddress, fromName));
        helper.setTo(to);
        return helper;
    }

    @Override
    public void sendRegistrationConfirmation(Account account) {
        try {
            MimeMessageHelper helper = createMessage(account.getEmail());
            helper.setSubject("Welcome to Cinema Booking");
            helper.setText("Hello " + account.getFirstName() + " " + account.getLastName() + ",\n\n"
                    + "Your account has been created successfully.\n"
                    + "You can now sign in and book tickets.\n\n"
                    + "Thanks,\n"
                    + "Cinema Booking Team");
            mailSender.send(helper.getMimeMessage());
        } catch (Exception ex) {
            System.err.println("Warning: failed to send registration email: " + ex.getMessage());
        }
    }

    @Override
    public void sendProfileChangeNotification(Account account) {
        try {
            MimeMessageHelper helper = createMessage(account.getEmail());
            helper.setSubject("Your profile was updated");
            helper.setText("Hello " + account.getFirstName() + ",\n\n"
                    + "Your profile information was updated. If you did not make this change, please contact support immediately.\n\n"
                    + "Thanks,\n"
                    + "Cinema Booking Team");
            mailSender.send(helper.getMimeMessage());
        } catch (Exception ex) {
            System.err.println("Warning: failed to send profile-change email: " + ex.getMessage());
        }
    }

    @Override
    public void sendPasswordReset(Account account, String token) {
        String link = "https://localhost:8080/reset-password?token=" + token;
        try {
            MimeMessageHelper helper = createMessage(account.getEmail());
            helper.setSubject("Password reset request");
            helper.setText("Hello " + account.getFirstName() + ",\n\n"
                    + "We received a request to reset your password. Use the link below to set a new password (expires in 1 hour):\n\n"
                    + link + "\n\n"
                    + "If you didn't request this, please ignore this email.\n\n"
                    + "Thanks,\n"
                    + "Cinema Booking Team");
            mailSender.send(helper.getMimeMessage());
        } catch (Exception ex) {
            System.err.println("Warning: failed to send password-reset email: " + ex.getMessage());
        }
    }

    @Override
    public void sendVerificationEmail(Account account, String token) {
        String link = "http://localhost:8080/verify?token=" + token;
        try {
            MimeMessageHelper helper = createMessage(account.getEmail());
            helper.setSubject("Verify your Cinema Booking account");
            helper.setText("Hello " + account.getFirstName() + ",\n\n"
                    + "Thank you for registering. Please click the link below to verify your account:\n\n"
                    + link + "\n\n"
                    + "This link expires in 24 hours.\n\n"
                    + "Thanks,\n"
                    + "Cinema Booking Team");
            mailSender.send(helper.getMimeMessage());
        } catch (Exception ex) {
            System.err.println("Warning: failed to send verification email: " + ex.getMessage());
        }
    }
}
