package com.cinema.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cinema.booking.model.Booking;
import com.cinema.booking.model.Showtime;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByShowtime(Showtime showtime);

    // Returns all non-null seat_numbers strings for a given showtime
    @Query("SELECT b.seatNumbers FROM Booking b WHERE b.showtime.showtimeId = :showtimeId AND b.seatNumbers IS NOT NULL")
    List<String> findSeatNumbersByShowtimeId(@Param("showtimeId") int showtimeId);
}
