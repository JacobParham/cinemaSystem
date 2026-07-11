package com.cinema.booking.controller;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.model.Account;
import com.cinema.booking.model.PaymentCard;
import com.cinema.booking.service.AccountService;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping({"/register", "/accounts/register"})
    public ResponseEntity<?> register(@RequestBody Map<String, Object> payload) {
        try {
            String firstName = String.valueOf(payload.getOrDefault("firstName", "")).trim();
            String lastName = String.valueOf(payload.getOrDefault("lastName", "")).trim();
            String email = String.valueOf(payload.getOrDefault("email", "")).trim();
            String password = String.valueOf(payload.getOrDefault("password", ""));
            boolean promotions = Boolean.parseBoolean(String.valueOf(payload.getOrDefault("promotions", false)));

            Account account = accountService.registerAccount(firstName, lastName, email, password, promotions);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Account created successfully",
                    "accountId", account.getAccountId(),
                    "email", account.getEmail(),
                    "role", account.getRole()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
        }
    }

    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
        String current = payload.getOrDefault("currentPassword", "");
        String next = payload.getOrDefault("newPassword", "");
        String email = currentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        try {
            accountService.changePassword(email, current, next);
            return ResponseEntity.ok(Map.of("message", "Password changed"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> payload) {
        String email = currentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        try {
            String firstName = String.valueOf(payload.getOrDefault("firstName", "")).trim();
            String lastName = String.valueOf(payload.getOrDefault("lastName", "")).trim();
            Boolean promotions = payload.containsKey("promotions") ? Boolean.parseBoolean(String.valueOf(payload.get("promotions"))) : null;
            Account updated = accountService.updateProfile(email, firstName, lastName, promotions);
            return ResponseEntity.ok(Map.of("message", "Profile updated", "email", updated.getEmail(), "firstName", updated.getFirstName(), "lastName", updated.getLastName()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/profile/cards")
    public ResponseEntity<?> addCard(@RequestBody Map<String, String> payload) {
        String email = currentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        try {
            String holder = payload.getOrDefault("cardHolder", "");
            String number = payload.getOrDefault("cardNumber", "");
            String expiration = payload.getOrDefault("expiration", "");
            String cvv = payload.getOrDefault("cvv", "");
            PaymentCard card = accountService.addPaymentCard(email, holder, number, expiration, cvv);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("cardId", card.getCardId(), "last4", card.getLast4()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/profile/cards/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable("id") int id) {
        String email = currentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        try {
            accountService.removePaymentCard(email, id);
            return ResponseEntity.ok(Map.of("message", "Card removed"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = String.valueOf(payload.getOrDefault("email", "")).trim();
        if (email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email required"));
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Please provide a valid email address."));
        }
        try {
            accountService.createPasswordResetToken(email);
            return ResponseEntity.ok(Map.of("message", "If that email exists, a reset link has been sent."));
        } catch (IllegalArgumentException ex) {
            // Do not reveal account existence; still return 200
            return ResponseEntity.ok(Map.of("message", "If that email exists, a reset link has been sent."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = String.valueOf(payload.getOrDefault("token", "")).trim();
        String email = String.valueOf(payload.getOrDefault("email", "")).trim();
        String newPassword = String.valueOf(payload.getOrDefault("newPassword", ""));

        if (newPassword.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "New password required"));
        }

        try {
            if (!token.isBlank()) {
                accountService.resetPassword(token, newPassword);
            } else {
                if (email.isBlank()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email required"));
                }
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Please provide a valid email address."));
                }
                accountService.resetPasswordByEmail(email.toLowerCase(), newPassword);
            }
            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    // Development helper: retrieve last token from NoOpEmailService when running with 'test' profile.
    @PostMapping("/dev/last-reset-token")
    public ResponseEntity<?> getLastResetToken(@RequestBody Map<String, String> payload) {
        String email = String.valueOf(payload.getOrDefault("email", "")).trim();
        if (email.isBlank()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email required"));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("message", "Not available in this profile"));
    }
}
