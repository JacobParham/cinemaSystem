package com.cinema.booking.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.model.Account;
import com.cinema.booking.service.AccountService;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AccountService accountService;

    public AdminUserController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AdminUserResponse> getCustomers() {
        return accountService.getCustomersForAdmin().stream()
                .map(AdminUserResponse::from)
                .toList();
    }

    @PatchMapping("/{accountId}/suspension")
    public ResponseEntity<?> updateSuspension(
            @PathVariable Integer accountId,
            @RequestBody SuspensionRequest request) {
        if (request.suspended() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "The suspended field is required."));
        }

        try {
            Account account = accountService.setCustomerSuspended(accountId, request.suspended());
            return ResponseEntity.ok(AdminUserResponse.from(account));
        } catch (IllegalArgumentException exception) {
            HttpStatus status = exception.getMessage().equals("Account not found.")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status)
                    .body(Map.of("message", exception.getMessage()));
        }
    }

    public record SuspensionRequest(Boolean suspended) {
    }

    public record AdminUserResponse(
            Integer accountId,
            String firstName,
            String lastName,
            String email,
            String status) {

        private static AdminUserResponse from(Account account) {
            return new AdminUserResponse(
                    account.getAccountId(),
                    account.getFirstName(),
                    account.getLastName(),
                    account.getEmail(),
                    account.getStatus());
        }
    }
}
