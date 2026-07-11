package com.cinema.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.model.PaymentCard;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Integer> {
    List<PaymentCard> findByAccountAccountId(int accountId);
}
