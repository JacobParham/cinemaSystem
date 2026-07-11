package com.cinema.booking.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.model.Account;
import com.cinema.booking.repository.AccountRepository;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;

    public AuthController(AuthenticationManager authenticationManager, AccountRepository accountRepository) {
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        String email = String.valueOf(payload.getOrDefault("email", "")).trim().toLowerCase();
        String password = String.valueOf(payload.getOrDefault("password", ""));

        try {
            if ("admin@cinemaworld.com".equals(email)) {
                // Demo override: allow admin login for the special demo account with any password.
                Account account = accountRepository.findByEmailIgnoreCase(email)
                        .orElseGet(() -> {
                            Account admin = new Account("Cinema", "Admin", email, "", false, "ADMIN");
                            return accountRepository.save(admin);
                        });
                HttpSession session = request.getSession(true);
                return ResponseEntity.ok(Map.of(
                        "email", email,
                        "role", account.getRole(),
                        "accountId", account.getAccountId(),
                        "sessionId", session.getId()
                ));
            }

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Ensure session is created
            HttpSession session = request.getSession(true);

            Account account = accountRepository.findByEmailIgnoreCase(email).orElse(null);

            return ResponseEntity.ok(Map.of(
                    "email", email,
                    "role", account != null ? account.getRole() : "CUSTOMER",
                    "accountId", account != null ? account.getAccountId() : null,
                    "sessionId", session.getId()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        }
    }
}
