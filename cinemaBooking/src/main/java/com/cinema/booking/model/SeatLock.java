package com.cinema.booking.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "seat_locks", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"showtime_id", "seat_number"}, name = "unique_showtime_seat")
    },
    indexes = {
        @Index(columnList = "showtime_id"),
        @Index(columnList = "expires_at")
    })
public class SeatLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lock_id")
    private int lockId;

    @Column(name = "showtime_id", nullable = false)
    private int showtimeId;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public SeatLock() {
        this.createdAt = LocalDateTime.now();
    }

    public SeatLock(int showtimeId, String seatNumber, Integer accountId, String sessionId, LocalDateTime expiresAt) {
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.accountId = accountId;
        this.sessionId = sessionId;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }

    public int getLockId() { return lockId; }
    public void setLockId(int lockId) { this.lockId = lockId; }

    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}