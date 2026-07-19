package com.cinema.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.booking.model.SeatLock;
import com.cinema.booking.repository.SeatLockRepository;

@Service
public class SeatLockService {

    private final SeatLockRepository seatLockRepository;
    private static final int LOCK_DURATION_MINUTES = 15;

    public SeatLockService(SeatLockRepository seatLockRepository) {
        this.seatLockRepository = seatLockRepository;
    }

    /**
     * Generate a unique session ID for the current booking session
     */
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Clean up expired locks (should be called periodically)
     */
    @Transactional
    public void cleanupExpiredLocks() {
        seatLockRepository.deleteExpiredLocks(LocalDateTime.now());
    }

    /**
     * Attempt to lock seats for a showtime
     * @return true if all seats were successfully locked, false otherwise
     */
    @Transactional
    public boolean lockSeats(int showtimeId, List<String> seatNumbers, Integer accountId, String sessionId) {
        // First, clean up any expired locks
        cleanupExpiredLocks();

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);

        for (String seatNumber : seatNumbers) {
            // Check if seat is already locked or booked
            SeatLock existingLock = seatLockRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
            
            if (existingLock != null) {
                // If the lock belongs to the same session, it's okay (re-locking)
                if (!existingLock.getSessionId().equals(sessionId)) {
                    // Seat is locked by someone else
                    return false;
                }
                // Update the expiration time for existing lock from same session
                existingLock.setExpiresAt(expiresAt);
                seatLockRepository.save(existingLock);
            } else {
                // Create new lock
                SeatLock newLock = new SeatLock(showtimeId, seatNumber, accountId, sessionId, expiresAt);
                seatLockRepository.save(newLock);
            }
        }
        return true;
    }

    /**
     * Release locks for a specific session
     */
    @Transactional
    public void releaseSessionLocks(String sessionId) {
        seatLockRepository.deleteBySessionId(sessionId);
    }

    /**
     * Release locks for specific seats
     */
    @Transactional
    public void releaseSeatLocks(int showtimeId, List<String> seatNumbers) {
        for (String seatNumber : seatNumbers) {
            seatLockRepository.deleteByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
        }
    }

    /**
     * Get all locked seats for a showtime (excluding expired locks)
     */
    public List<SeatLock> getActiveLocksForShowtime(int showtimeId) {
        cleanupExpiredLocks();
        return seatLockRepository.findByShowtimeId(showtimeId);
    }

    /**
     * Check if a specific seat is locked
     */
    public boolean isSeatLocked(int showtimeId, String seatNumber) {
        cleanupExpiredLocks();
        SeatLock lock = seatLockRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
        return lock != null;
    }
}