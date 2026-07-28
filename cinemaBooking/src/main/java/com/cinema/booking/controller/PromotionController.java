package com.cinema.booking.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.model.Promotion;
import com.cinema.booking.service.PromotionService;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(
            PromotionService promotionService
    ) {
        this.promotionService = promotionService;
    }

    /**
     * POST /api/promotions
     *
     * Saves a promotion and optionally sends an email
     * announcement to subscribed customers.
     */
    @PostMapping
    public ResponseEntity<?> createPromotion(
            @RequestBody CreatePromotionRequest request
    ) {
        try {
            PromotionResult result =
                    promotionService.createPromotion(request);

            String message;

            if (request.sendEmail()) {
                message =
                        "Promotion created. "
                                + result.emailsSent()
                                + " promotion email(s) sent.";

                if (result.emailsFailed() > 0) {
                    message += " "
                            + result.emailsFailed()
                            + " email(s) could not be sent.";
                }
            } else {
                message =
                        "Promotion created successfully.";
            }

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", message,
                            "promotion", result.promotion(),
                            "emailsSent", result.emailsSent(),
                            "emailsFailed", result.emailsFailed()
                    ));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }

    /**
     * GET /api/promotions
     *
     * Returns all saved promotions for the admin panel.
     */
    @GetMapping
    public ResponseEntity<List<PromotionResponse>>
    getPromotions() {

        return ResponseEntity.ok(
                promotionService.getAllPromotions()
        );
    }

    /**
     * Request body received from the admin promotion form.
     */
    public record CreatePromotionRequest(
            String code,
            String name,
            BigDecimal discountPercent,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            boolean sendEmail
    ) {
    }

    /**
     * Promotion information returned to the frontend.
     */
    public record PromotionResponse(
            Integer promotionId,
            String code,
            String name,
            String description,
            BigDecimal discountPercent,
            LocalDate startDate,
            LocalDate endDate,
            boolean active
    ) {
        public static PromotionResponse from(
                Promotion promotion
        ) {
            return new PromotionResponse(
                    promotion.getPromotionId(),
                    promotion.getCode(),
                    promotion.getName(),
                    promotion.getDescription(),
                    promotion.getDiscountPercent(),
                    promotion.getStartDate(),
                    promotion.getEndDate(),
                    promotion.isActive()
            );
        }
    }

    /**
     * Internal result returned by the service.
     *
     * It includes the saved promotion and email counts.
     */
    public record PromotionResult(
            PromotionResponse promotion,
            int emailsSent,
            int emailsFailed
    ) {
    }
}