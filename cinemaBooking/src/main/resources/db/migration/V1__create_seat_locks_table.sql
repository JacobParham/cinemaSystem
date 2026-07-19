CREATE TABLE IF NOT EXISTS seat_locks (
    lock_id INT AUTO_INCREMENT PRIMARY KEY,
    showtime_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    account_id INT,
    session_id VARCHAR(100) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    UNIQUE KEY unique_showtime_seat (showtime_id, seat_number),
    INDEX idx_showtime (showtime_id),
    INDEX idx_expires_at (expires_at)
);