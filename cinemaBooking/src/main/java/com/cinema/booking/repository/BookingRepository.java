package com.cinema.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.model.Booking;
import com.cinema.booking.model.Showtime;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByShowtime(Showtime showtime);
}
