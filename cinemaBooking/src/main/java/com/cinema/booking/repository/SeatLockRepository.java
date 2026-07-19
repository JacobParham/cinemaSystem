package com.cinema.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.booking.model.SeatLock;

public interface SeatLockRepository extends JpaRepository<SeatLock, Integer> {

    // Find all locks for a specific showtime
    List<SeatLock> findByShowtimeId(int showtimeId);

    // Find a specific lock for a showtime and seat
    SeatLock findByShowtimeIdAndSeatNumber(int showtimeId, String seatNumber);

    // Find locks by session ID
    List<SeatLock> findBySessionId(String sessionId);

    // Delete expired locks
    @Modifying
    @Transactional
    @Query("DELETE FROM SeatLock sl WHERE sl.expiresAt < :now")
    void deleteExpiredLocks(@Param("now") LocalDateTime now);

    // Delete locks by session ID
    @Modifying
    @Transactional
    void deleteBySessionId(String sessionId);

    // Delete locks for a specific showtime and seat
    @Modifying
    @Transactional
    void deleteByShowtimeIdAndSeatNumber(int showtimeId, String seatNumber);
}