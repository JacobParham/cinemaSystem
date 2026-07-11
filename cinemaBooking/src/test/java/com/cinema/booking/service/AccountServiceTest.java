package com.cinema.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cinema.booking.model.Account;
import com.cinema.booking.repository.AccountRepository;
import com.cinema.booking.service.EmailService;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void registerAccountHashesPasswordAndSavesAccount() {
        when(accountRepository.findByEmailIgnoreCase("new.user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("strong-password")).thenReturn("hashed-password");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account savedAccount = accountService.registerAccount(
                "New",
                "User",
                "new.user@example.com",
                "strong-password",
                false);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(passwordEncoder).encode("strong-password");
        verify(accountRepository).save(accountCaptor.capture());
        verify(emailService).sendRegistrationConfirmation(accountCaptor.getValue());

        Account saved = accountCaptor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("New");
        assertThat(saved.getLastName()).isEqualTo("User");
        assertThat(saved.getEmail()).isEqualTo("new.user@example.com");
        assertThat(saved.getPassword()).isEqualTo("hashed-password");
        assertThat(saved.isPromotions()).isFalse();
        assertThat(saved.getRole()).isEqualTo("CUSTOMER");
        assertThat(savedAccount.getPassword()).isEqualTo("hashed-password");
    }
}
