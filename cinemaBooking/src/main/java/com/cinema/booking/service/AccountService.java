package com.cinema.booking.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cinema.booking.model.Account;
import com.cinema.booking.repository.AccountRepository;
import com.cinema.booking.model.PaymentCard;
import com.cinema.booking.repository.PaymentCardRepository;
import com.cinema.booking.model.PasswordResetToken;
import com.cinema.booking.repository.PasswordResetTokenRepository;
import com.cinema.booking.model.EmailVerificationToken;
import com.cinema.booking.repository.EmailVerificationTokenRepository;

import java.time.YearMonth;
import java.util.Optional;

import com.cinema.booking.service.CryptoService;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PaymentCardRepository paymentCardRepository;
    private final CryptoService cryptoService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, EmailService emailService,
                          PaymentCardRepository paymentCardRepository, CryptoService cryptoService,
                          PasswordResetTokenRepository passwordResetTokenRepository,
                          EmailVerificationTokenRepository emailVerificationTokenRepository) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.paymentCardRepository = paymentCardRepository;
        this.cryptoService = cryptoService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
    }

    public Account registerAccount(String firstName, String lastName, String email, String password, boolean promotions) {
        if (accountRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException("Account already exists for that email.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        Account account = new Account(firstName, lastName, email.toLowerCase(), encodedPassword, promotions, "CUSTOMER");
        Account savedAccount = accountRepository.save(account);

        String verificationToken = java.util.UUID.randomUUID().toString().replaceAll("-", "");
        EmailVerificationToken evt = new EmailVerificationToken();
        evt.setAccount(savedAccount);
        evt.setToken(verificationToken);
        evt.setExpiresAt(java.time.Instant.now().plusSeconds(86400));
        emailVerificationTokenRepository.save(evt);
        emailService.sendVerificationEmail(savedAccount, verificationToken);

        return savedAccount;
    }

    public void verifyAccount(String token) {
        Optional<EmailVerificationToken> opt = emailVerificationTokenRepository.findByToken(token);
        if (opt.isEmpty()) throw new IllegalArgumentException("Invalid or expired verification link.");
        EmailVerificationToken evt = opt.get();
        if (evt.getExpiresAt().isBefore(java.time.Instant.now())) {
            emailVerificationTokenRepository.delete(evt);
            throw new IllegalArgumentException("Verification link has expired.");
        }
        Account account = evt.getAccount();
        account.setStatus("Active");
        accountRepository.save(account);
        emailVerificationTokenRepository.delete(evt);
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        Optional<Account> opt = accountRepository.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) throw new IllegalArgumentException("Account not found");
        Account account = opt.get();
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public Account updateProfile(String email, String firstName, String lastName, Boolean promotions, String address) {
        Optional<Account> opt = accountRepository.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) throw new IllegalArgumentException("Account not found");
        Account account = opt.get();
        if (firstName != null && !firstName.isBlank()) account.setFirstName(firstName);
        if (lastName != null && !lastName.isBlank()) account.setLastName(lastName);
        if (promotions != null) account.setPromotions(promotions);
        if (address != null) account.setAddress(address.isBlank() ? null : address.trim());
        Account saved = accountRepository.save(account);
        emailService.sendProfileChangeNotification(saved);
        return saved;
    }

    public PaymentCard addPaymentCard(String email, String cardHolder, String cardNumber, String expiration, String cvv) {
        Optional<Account> opt = accountRepository.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) throw new IllegalArgumentException("Account not found");
        Account account = opt.get();
        if (paymentCardRepository.findByAccountAccountId(account.getAccountId()).size() >= 3) {
            throw new IllegalArgumentException("Maximum of 3 payment cards allowed.");
        }

        validateCardMetadata(cardHolder, expiration);
        String cardDigits = validateCardNumber(cardNumber);
        String cvvDigits = validateCvv(cvv);

        PaymentCard card = new PaymentCard();
        card.setAccount(account);
        card.setCardHolder(cardHolder.trim());
        card.setExpiration(expiration.trim());
        String encNumber = cryptoService.encrypt(cardDigits);
        card.setCardNumberEnc(encNumber);
        card.setLast4(cardDigits.substring(cardDigits.length() - 4));
        card.setCvvEnc(cryptoService.encrypt(cvvDigits));
        return paymentCardRepository.save(card);
    }

    public PaymentCard updatePaymentCard(
            String email,
            int cardId,
            String cardHolder,
            String cardNumber,
            String expiration,
            String cvv) {
        PaymentCard card = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        if (!card.getAccount().getEmail().equalsIgnoreCase(email)) {
            throw new IllegalArgumentException("Not authorized to edit this card");
        }

        validateCardMetadata(cardHolder, expiration);
        card.setCardHolder(cardHolder.trim());
        card.setExpiration(expiration.trim());

        boolean replacingNumber = cardNumber != null && !cardNumber.isBlank();
        boolean replacingCvv = cvv != null && !cvv.isBlank();

        if (replacingNumber) {
            String cardDigits = validateCardNumber(cardNumber);
            if (!replacingCvv) {
                throw new IllegalArgumentException("CVV is required when replacing the card number.");
            }
            card.setCardNumberEnc(cryptoService.encrypt(cardDigits));
            card.setLast4(cardDigits.substring(cardDigits.length() - 4));
        }

        if (replacingCvv) {
            card.setCvvEnc(cryptoService.encrypt(validateCvv(cvv)));
        }

        return paymentCardRepository.save(card);
    }

    public void removePaymentCard(String email, int cardId) {
        Optional<PaymentCard> opt = paymentCardRepository.findById(cardId);
        if (opt.isEmpty()) throw new IllegalArgumentException("Card not found");
        PaymentCard card = opt.get();
        if (!card.getAccount().getEmail().equalsIgnoreCase(email)) {
            throw new IllegalArgumentException("Not authorized to remove this card");
        }
        paymentCardRepository.deleteById(cardId);
    }

    private static void validateCardMetadata(String cardHolder, String expiration) {
        if (cardHolder == null || cardHolder.trim().isBlank()) {
            throw new IllegalArgumentException("Name on card is required.");
        }

        if (expiration == null || !expiration.trim().matches("\\d{2}/\\d{2}")) {
            throw new IllegalArgumentException("Expiration must use MM/YY format.");
        }

        String normalizedExpiration = expiration.trim();
        int month = Integer.parseInt(normalizedExpiration.substring(0, 2));
        int year = 2000 + Integer.parseInt(normalizedExpiration.substring(3, 5));
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Expiration month must be between 01 and 12.");
        }
        if (YearMonth.of(year, month).isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("This card is expired.");
        }
    }

    private static String validateCardNumber(String cardNumber) {
        if (cardNumber == null || !cardNumber.matches("[\\d\\s-]+")) {
            throw new IllegalArgumentException("Card number may contain digits, spaces, or hyphens only.");
        }
        String digits = cardNumber.replaceAll("\\D", "");
        if (digits.length() < 13 || digits.length() > 19) {
            throw new IllegalArgumentException("Card number must contain 13 to 19 digits.");
        }
        return digits;
    }

    private static String validateCvv(String cvv) {
        String digits = cvv == null ? "" : cvv.trim();
        if (!digits.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("CVV must contain 3 or 4 digits.");
        }
        return digits;
    }

    public String createPasswordResetToken(String email) {
        Optional<Account> opt = accountRepository.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) throw new IllegalArgumentException("Account not found");
        Account account = opt.get();
        String token = java.util.UUID.randomUUID().toString().replaceAll("-", "");
        PasswordResetToken prt = new PasswordResetToken();
        prt.setAccount(account);
        prt.setToken(token);
        prt.setExpiresAt(java.time.Instant.now().plusSeconds(3600));
        passwordResetTokenRepository.save(prt);
        emailService.sendPasswordReset(account, token);
        // Dev: log token so integration tests / manual tests can pick it up from logs
        System.out.println("DEBUG: password reset token for " + account.getEmail() + " = " + token);
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> opt = passwordResetTokenRepository.findByToken(token);
        if (opt.isEmpty()) throw new IllegalArgumentException("Invalid or expired token");
        PasswordResetToken prt = opt.get();
        if (prt.getExpiresAt().isBefore(java.time.Instant.now())) {
            passwordResetTokenRepository.delete(prt);
            throw new IllegalArgumentException("Token expired");
        }
        Account account = prt.getAccount();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        passwordResetTokenRepository.delete(prt);
    }

    public void resetPasswordByEmail(String email, String newPassword) {
        Optional<Account> opt = accountRepository.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) throw new IllegalArgumentException("Account not found for that email.");
        Account account = opt.get();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }
}
