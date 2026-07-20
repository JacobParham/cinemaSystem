package com.cinema.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import com.cinema.booking.model.EmailVerificationToken;
import com.cinema.booking.model.PaymentCard;
import com.cinema.booking.repository.AccountRepository;
import com.cinema.booking.repository.EmailVerificationTokenRepository;
import com.cinema.booking.repository.PasswordResetTokenRepository;
import com.cinema.booking.repository.PaymentCardRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void registerAccountHashesPasswordAndSavesAccount() {
        when(accountRepository.findByEmailIgnoreCase("new.user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("strong-password")).thenReturn("hashed-password");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account savedAccount = accountService.registerAccount(
                "New",
                "User",
                "new.user@example.com",
                "strong-password",
                false);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(passwordEncoder).encode("strong-password");
        verify(accountRepository).save(accountCaptor.capture());
        verify(emailVerificationTokenRepository).save(any(EmailVerificationToken.class));
        verify(emailService).sendVerificationEmail(eq(accountCaptor.getValue()), anyString());

        Account saved = accountCaptor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("New");
        assertThat(saved.getLastName()).isEqualTo("User");
        assertThat(saved.getEmail()).isEqualTo("new.user@example.com");
        assertThat(saved.getPassword()).isEqualTo("hashed-password");
        assertThat(saved.getPromotions()).isFalse();
        assertThat(saved.getRole()).isEqualTo("CUSTOMER");
        assertThat(savedAccount.getPassword()).isEqualTo("hashed-password");
    }

    @Test
    void updatePaymentCardReplacesEditableDetailsForOwner() {
        Account owner = new Account();
        owner.setEmail("owner@example.com");

        PaymentCard card = new PaymentCard();
        card.setCardId(7);
        card.setAccount(owner);
        card.setCardHolder("Old Name");
        card.setCardNumberEnc("old-encrypted-number");
        card.setLast4("1111");
        card.setExpiration("10/30");
        card.setCvvEnc("old-encrypted-cvv");

        when(paymentCardRepository.findById(7)).thenReturn(Optional.of(card));
        when(cryptoService.encrypt("5555555555554444")).thenReturn("new-encrypted-number");
        when(cryptoService.encrypt("321")).thenReturn("new-encrypted-cvv");
        when(paymentCardRepository.save(card)).thenReturn(card);

        PaymentCard updated = accountService.updatePaymentCard(
                "owner@example.com",
                7,
                "New Name",
                "5555 5555 5555 4444",
                "12/99",
                "321");

        assertThat(updated.getCardHolder()).isEqualTo("New Name");
        assertThat(updated.getExpiration()).isEqualTo("12/99");
        assertThat(updated.getLast4()).isEqualTo("4444");
        assertThat(updated.getCardNumberEnc()).isEqualTo("new-encrypted-number");
        assertThat(updated.getCvvEnc()).isEqualTo("new-encrypted-cvv");
        verify(paymentCardRepository).save(card);
    }

    @Test
    void updatePaymentCardKeepsEncryptedValuesWhenNumberAndCvvAreOmitted() {
        Account owner = new Account();
        owner.setEmail("owner@example.com");

        PaymentCard card = new PaymentCard();
        card.setCardId(7);
        card.setAccount(owner);
        card.setCardNumberEnc("existing-encrypted-number");
        card.setLast4("1111");
        card.setCvvEnc("existing-encrypted-cvv");
        when(paymentCardRepository.findById(7)).thenReturn(Optional.of(card));
        when(paymentCardRepository.save(card)).thenReturn(card);

        PaymentCard updated = accountService.updatePaymentCard(
                "owner@example.com",
                7,
                "Updated Name",
                "",
                "12/99",
                "");

        assertThat(updated.getCardHolder()).isEqualTo("Updated Name");
        assertThat(updated.getExpiration()).isEqualTo("12/99");
        assertThat(updated.getCardNumberEnc()).isEqualTo("existing-encrypted-number");
        assertThat(updated.getLast4()).isEqualTo("1111");
        assertThat(updated.getCvvEnc()).isEqualTo("existing-encrypted-cvv");
        verify(cryptoService, never()).encrypt(anyString());
        verify(paymentCardRepository).save(card);
    }

    @Test
    void updatePaymentCardRejectsDifferentOwner() {
        Account owner = new Account();
        owner.setEmail("owner@example.com");

        PaymentCard card = new PaymentCard();
        card.setCardId(7);
        card.setAccount(owner);
        when(paymentCardRepository.findById(7)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> accountService.updatePaymentCard(
                "other@example.com",
                7,
                "Other User",
                "",
                "12/99",
                ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not authorized to edit this card");

        verify(paymentCardRepository, never()).save(any(PaymentCard.class));
    }
}
