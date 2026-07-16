package com.cinema.booking.controller;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.model.Account;
import com.cinema.booking.model.FavoriteMovie;
import com.cinema.booking.model.PaymentCard;
import com.cinema.booking.repository.AccountRepository;
import com.cinema.booking.repository.FavoriteMovieRepository;
import com.cinema.booking.repository.PaymentCardRepository;
import com.cinema.booking.service.AccountService;

@RestController
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final FavoriteMovieRepository favoriteMovieRepository;

    public AccountController(AccountService accountService,
                             AccountRepository accountRepository,
                             PaymentCardRepository paymentCardRepository,
                             FavoriteMovieRepository favoriteMovieRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.paymentCardRepository = paymentCardRepository;
        this.favoriteMovieRepository = favoriteMovieRepository;
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
                    "role", account.getRole(),
                    "status", account.getStatus()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
        }
    }

    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) return null;
        return auth.getName();
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        String email = currentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));

        return accountRepository.findByEmailIgnoreCase(email)
                .<ResponseEntity<?>>map(account -> {
                    List<PaymentCard> cards = paymentCardRepository.findByAccountAccountId(account.getAccountId());
                    List<Map<String, Object>> cardList = cards.stream().map(c -> Map.<String, Object>of(
                            "cardId",     c.getCardId(),
                            "cardHolder", c.getCardHolder(),
                            "last4",      c.getLast4(),
                            "expiration", c.getExpiration()
                    )).collect(Collectors.toList());

                    return ResponseEntity.ok(Map.of(
                            "firstName",  account.getFirstName(),
                            "lastName",   account.getLastName(),
                            "email",      account.getEmail(),
                            "status",     account.getStatus(),
                            "promotions", account.getPromotions(),
                            "address",    account.getAddress() != null ? account.getAddress() : "",
                            "cards",      cardList
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account not found")));
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
            String address = payload.containsKey("address") ? String.valueOf(payload.get("address")) : null;
            Account updated = accountService.updateProfile(email, firstName, lastName, promotions, address);
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

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
        try {
            accountService.verifyAccount(token);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/")
                    .build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites() {
        String email = currentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        Account account = accountRepository.findByEmailIgnoreCase(email).orElse(null);
        if (account == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account not found"));
        List<Integer> movieIds = favoriteMovieRepository.findByAccountId(account.getAccountId())
                .stream().map(FavoriteMovie::getMovieId).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("movieIds", movieIds));
    }

    @PostMapping("/favorites/{movieId}")
    public ResponseEntity<?> addFavorite(@PathVariable("movieId") int movieId) {
        String email = currentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        Account account = accountRepository.findByEmailIgnoreCase(email).orElse(null);
        if (account == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account not found"));
        if (!favoriteMovieRepository.existsByAccountIdAndMovieId(account.getAccountId(), movieId)) {
            FavoriteMovie fm = new FavoriteMovie();
            fm.setAccountId(account.getAccountId());
            fm.setMovieId(movieId);
            favoriteMovieRepository.save(fm);
        }
        return ResponseEntity.ok(Map.of("message", "Added to favorites"));
    }

    @DeleteMapping("/favorites/{movieId}")
    public ResponseEntity<?> removeFavorite(@PathVariable("movieId") int movieId) {
        String email = currentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        Account account = accountRepository.findByEmailIgnoreCase(email).orElse(null);
        if (account == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account not found"));
        favoriteMovieRepository.deleteByAccountIdAndMovieId(account.getAccountId(), movieId);
        return ResponseEntity.ok(Map.of("message", "Removed from favorites"));
    }

    // Development helper: retrieve last token from NoOpEmailService when running with 'test' profile.
    @PostMapping("/dev/last-reset-token")
    public ResponseEntity<?> getLastResetToken(@RequestBody Map<String, String> payload) {
        String email = String.valueOf(payload.getOrDefault("email", "")).trim();
        if (email.isBlank()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email required"));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("message", "Not available in this profile"));
    }
}
