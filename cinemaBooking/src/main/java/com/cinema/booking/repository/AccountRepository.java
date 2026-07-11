package com.cinema.booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.model.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmailIgnoreCase(String email);
}
