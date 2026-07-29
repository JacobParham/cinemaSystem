package com.cinema.booking.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.model.Account;
import com.cinema.booking.repository.AccountRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;

    public AuthController(AuthenticationManager authenticationManager, AccountRepository accountRepository) {
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> payload, HttpServletRequest request, HttpServletResponse response) {
        String email = String.valueOf(payload.getOrDefault("email", "")).trim().toLowerCase();
        String password = String.valueOf(payload.getOrDefault("password", ""));

        try {
            HttpSession existingSession = request.getSession(false);
            if (existingSession != null) {
                existingSession.invalidate();
            }
            SecurityContextHolder.clearContext();

            if ("admin@cinemaworld.com".equals(email)) {
                // Demo override: admin account uses the fixed password "anything"
                if (!"anything".equals(password)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
                }
                Account account = accountRepository.findByEmailIgnoreCase(email)
                        .orElseGet(() -> {
                            Account admin = new Account("Cinema", "Admin", email, "", false, "ADMIN");
                            return accountRepository.save(admin);
                        });

                Authentication adminAuthentication =
                        UsernamePasswordAuthenticationToken.authenticated(
                                account.getEmail(),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(adminAuthentication);
                SecurityContextHolder.setContext(context);
                new HttpSessionSecurityContextRepository().saveContext(context, request, response);

                HttpSession session = request.getSession();
                return ResponseEntity.ok(Map.of(
                        "email", email,
                        "role", "ADMIN",
                        "accountId", account.getAccountId(),
                        "sessionId", session.getId()
                ));
            }

            Account account = accountRepository.findByEmailIgnoreCase(email).orElse(null);
            if (account != null && "Inactive".equalsIgnoreCase(account.getStatus())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Account is not verified. Please check your email to verify your account."));
            }
            if (account != null && "Suspended".equalsIgnoreCase(account.getStatus())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "This account has been suspended. Please contact Cinema World support."));
            }

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
            Authentication auth = authenticationManager.authenticate(token);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            new HttpSessionSecurityContextRepository().saveContext(context, request, response);

            HttpSession session = request.getSession(true);

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
