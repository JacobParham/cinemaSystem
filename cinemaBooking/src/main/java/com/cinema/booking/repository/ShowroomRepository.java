package com.cinema.booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.model.Showroom;

public interface ShowroomRepository
        extends JpaRepository<Showroom, Integer> {

    Optional<Showroom> findByShowroomName(String showroomName);
}
