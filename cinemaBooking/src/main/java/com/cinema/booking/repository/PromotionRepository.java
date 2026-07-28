package com.cinema.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.model.Promotion;

public interface PromotionRepository
        extends JpaRepository<Promotion, Integer> {

    boolean existsByCodeIgnoreCase(String code);

    List<Promotion> findAllByOrderByCreatedAtDesc();
}