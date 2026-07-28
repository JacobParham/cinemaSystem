package com.cinema.booking.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.booking.controller.PromotionController.CreatePromotionRequest;
import com.cinema.booking.controller.PromotionController.PromotionResponse;
import com.cinema.booking.controller.PromotionController.PromotionResult;
import com.cinema.booking.model.Account;
import com.cinema.booking.model.Promotion;
import com.cinema.booking.repository.AccountRepository;
import com.cinema.booking.repository.PromotionRepository;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;

    public PromotionService(
            PromotionRepository promotionRepository,
            AccountRepository accountRepository,
            EmailService emailService
    ) {
        this.promotionRepository = promotionRepository;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
    }

    @Transactional
    public PromotionResult createPromotion(
            CreatePromotionRequest request
    ) {
        validateRequest(request);

        String normalizedCode = request.code()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (promotionRepository
                .existsByCodeIgnoreCase(normalizedCode)) {

            throw new IllegalArgumentException(
                    "A promotion with this code already exists."
            );
        }

        Promotion promotion = new Promotion();
        promotion.setCode(normalizedCode);
        promotion.setName(request.name().trim());
        promotion.setDescription(
                cleanOptional(request.description())
        );
        promotion.setDiscountPercent(
                request.discountPercent()
        );
        promotion.setStartDate(request.startDate());
        promotion.setEndDate(request.endDate());
        promotion.setActive(true);

        Promotion savedPromotion =
                promotionRepository.save(promotion);

        int emailsSent = 0;
        int emailsFailed = 0;

        if (request.sendEmail()) {
            EmailResult emailResult =
                    sendPromotionEmails(savedPromotion);

            emailsSent = emailResult.sent();
            emailsFailed = emailResult.failed();
        }

        return new PromotionResult(
                PromotionResponse.from(savedPromotion),
                emailsSent,
                emailsFailed
        );
    }

    @Transactional(readOnly = true)
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PromotionResponse::from)
                .toList();
    }

    private EmailResult sendPromotionEmails(
            Promotion promotion
    ) {
        List<Account> subscribedCustomers =
                accountRepository.findByPromotionsTrue();

        int sent = 0;
        int failed = 0;

        for (Account account : subscribedCustomers) {
            try {
                emailService.sendPromotionEmail(
                        account.getEmail(),
                        promotion.getName(),
                        promotion.getCode(),
                        promotion.getDescription(),
                        promotion.getDiscountPercent()
                                .stripTrailingZeros()
                                .toPlainString(),
                        promotion.getStartDate().toString(),
                        promotion.getEndDate().toString()
                );

                sent++;

            } catch (Exception exception) {
                failed++;

                System.err.println(
                        "Could not send promotion email to "
                                + account.getEmail()
                                + ": "
                                + exception.getMessage()
                );
            }
        }

        return new EmailResult(sent, failed);
    }

    private void validateRequest(
            CreatePromotionRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Promotion information is required."
            );
        }

        if (request.code() == null
                || request.code().isBlank()) {

            throw new IllegalArgumentException(
                    "Promotion code is required."
            );
        }

        if (request.name() == null
                || request.name().isBlank()) {

            throw new IllegalArgumentException(
                    "Promotion name is required."
            );
        }

        BigDecimal discount =
                request.discountPercent();

        if (discount == null
                || discount.compareTo(
                BigDecimal.ZERO
        ) <= 0
                || discount.compareTo(
                new BigDecimal("100")
        ) > 0) {

            throw new IllegalArgumentException(
                    "Discount percent must be between 1 and 100."
            );
        }

        if (request.startDate() == null
                || request.endDate() == null) {

            throw new IllegalArgumentException(
                    "Start and end dates are required."
            );
        }

        if (request.endDate()
                .isBefore(request.startDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date."
            );
        }
    }

    private String cleanOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private record EmailResult(
            int sent,
            int failed
    ) {
    }
}