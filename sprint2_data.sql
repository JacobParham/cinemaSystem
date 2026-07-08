-- Cinema E-Booking System Sprint 1 + Sprint 2 Compatible Database
-- MySQL 8+
-- Purpose:
-- 1. Preserve original Sprint 1 movies, showtimes, and bookings structure/data.
-- 2. Add Sprint 2 support for registration, login, profile editing,
--    password reset/change password, logout/session support, and movie preferences.
-- 3. Match the domain class diagram without breaking existing Sprint 1 code.

CREATE DATABASE IF NOT EXISTS cinema_booking;
USE cinema_booking;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS booking_promotions;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS showtimes;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS showrooms;
DROP TABLE IF EXISTS theatre;
DROP TABLE IF EXISTS favorite_movies;
DROP TABLE IF EXISTS recommendations;
DROP TABLE IF EXISTS preferences;
DROP TABLE IF EXISTS payment_cards;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS email_verification_tokens;
DROP TABLE IF EXISTS user_sessions;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS promotions;
DROP TABLE IF EXISTS movies;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- ORIGINAL SPRINT 1 TABLES
-- These are kept compatible with the original schema.
-- =========================================================

CREATE TABLE movies (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    rating VARCHAR(10),
    description TEXT,
    poster_url TEXT,
    trailer_url TEXT,
    status ENUM('Currently Running', 'Coming Soon') NOT NULL
);

CREATE TABLE showtimes (
    showtime_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id)
);

CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    showtime_id INT NOT NULL,
    adult_tickets INT DEFAULT 0,
    child_tickets INT DEFAULT 0,
    senior_tickets INT DEFAULT 0,
    total_price DECIMAL(10,2),
    FOREIGN KEY (showtime_id) REFERENCES showtimes(showtime_id)
);

-- =========================================================
-- ORIGINAL MOVIE DATA
-- Copied without changing the original values.
-- =========================================================

INSERT INTO movies 

(title, genre, rating, description, poster_url, trailer_url, status) 

VALUES 

('Backrooms', 'Horror', 'R', 'A strange doorway appears in the basement of a furniture storeroom.', 'https://www.themoviedb.org/t/p/w1280/rhGx6E3qRNMgj3i5su2oukNHwIQ.jpg', 'https://www.youtube.com/embed/0HjdiohVOik?si=OBkqyQGIvwezePb4', 'Currently Running'), 

  

('Toy Story 5', 'Animation', 'PG', 'When Bonnie receives a Lilypad tablet as a gift and becomes obsessed, Buzz, Woody, Jessie and the rest of the gang''s jobs become exponentially harder when they have to go head to head with the all-new threat to playtime.', 'https://www.themoviedb.org/t/p/w1280/a6H2U7pjibMia41TwyFVd1PVQw3.jpg', 'https://www.youtube.com/embed/c51ND9Hdbw0?si=XqRigDklBpu9hySn', 'Currently Running'), 

  

('Obsession', 'Horror', 'R', 'After breaking the mysterious "One Wish Willow" to win his crush''s heart, a hopeless romantic finds himself getting exactly what he asked for but soon discovers that some desires come at a dark, sinister price.', 'https://www.themoviedb.org/t/p/w1280/bRwnj8WEKBCvmfeUNOukJPwB43K.jpg', 'https://www.youtube.com/embed/xJYoN-fX2j0?si=FO1HEDfsLWzEcaHl', 'Currently Running'), 

  

('Spider-Man: Brand New Day', 'Action', 'PG-13', 'Fighting crime full-time as Spider-Man in a world that doesn''t remember him—and the pressure of seeing his old friends move on without him—sparks a change in Peter Parker he may not have the power to control. But that transformation might also be the only thing that can stop a shocking new threat to the city and those he loves - a powerful villain no one can even see.', 'https://www.themoviedb.org/t/p/w1280/yyB2VJEW3an2xCdcYCPQhn9QERR.jpg', 'https://www.youtube.com/embed/62bIsvRcPv0?si=_2sG99NQ38WxV4mz', 'Coming Soon'), 

  

('The Mandalorian and Grogu', 'Adventure', 'PG-13', 'The evil Empire has fallen, and Imperial warlords remain scattered throughout the galaxy. As the fledgling New Republic works to protect everything the Rebellion fought for, they have enlisted the help of legendary Mandalorian bounty hunter Din Djarin and his young apprentice Grogu.', 'https://www.themoviedb.org/t/p/w1280/5Vi8dSauVwH1HOsiZceDMbRr1Ca.jpg', 'https://www.youtube.com/embed/IHWlvwu8t1w?si=W2mi8ccvphrl3QQi', 'Currently Running'), 

  

('Michael', 'Drama', 'PG-13', 'The story of Michael Jackson, one of the most influential artists the world has ever known, and his life beyond the music. His journey from the discovery of his extraordinary talent as the lead of the Jackson Five, to the visionary artist whose creative ambition fueled a relentless pursuit to become the biggest entertainer in the world, highlighting both his life off-stage and some of the most iconic performances from his early solo career.', 'https://www.themoviedb.org/t/p/w1280/zm0KAbOjlt9eR5y7vDiL2dEOwMl.jpg', 'https://www.youtube.com/embed/3zOLzsbOleM?si=a3I8eM8alQzu-XLw', 'Currently Running'), 

  

('Minions & Monsters', 'Animation', 'PG', 'This is the rambunctious, ridiculous and totally true story of how the Minions conquered Hollywood, became movie stars, lost everything, unleashed monsters onto the world and then banded together to try and save the planet from the mayhem they had just created.', 'https://www.themoviedb.org/t/p/w1280/nz7i42yhLIJ4ve9JKgM6NthoLHO.jpg', 'https://www.youtube.com/embed/V-O-uBaHk3c?si=TGVqIvWG984VaAov', 'Coming Soon'), 

  

('The Odyssey', 'Fantasy', 'R', 'Odysseus, the legendary King of Ithaca, embarks on a long and perilous journey home following the Trojan War. Throughout his voyage, he is forced to confront the whims of gods, mythological monsters, and trials that stretch both his cunning and his humanity to the breaking point.', 'https://www.themoviedb.org/t/p/w1280/krVa7rKCQb4OBfsr2LTJv4rTz5q.jpg', 'https://www.youtube.com/embed/f_bKjZeJBBI?si=5lUQS2RhBFI31cyQ', 'Coming Soon'), 

  

('Moana', 'Adventure', 'PG', 'Moana answers the Ocean''s call and, for the first time, voyages beyond the reef of her island of Motunui with the infamous demigod Maui on an unforgettable journey to restore prosperity to her people.', 'https://www.themoviedb.org/t/p/w1280/zKVgiv5qHCvCLT4A2ymJi5QeXDH.jpg', 'https://www.youtube.com/embed/EEz5xbzYPKI?si=O1Lw03OqET8RvjrX', 'Coming Soon'), 

  

('Disclosure Day', 'Thriller', 'PG-13', 'A cybersecurity expert becomes a whistleblower after uncovering secrets about aliens, putting him on the run from a corporation. Meanwhile, a meteorologist experiencing strange phenomena joins forces with him to prove there''s life beyond our understanding.', 'https://www.themoviedb.org/t/p/w1280/3o5YPjDGDTcTDL5ftDA9NwN9dLd.jpg', 'https://www.youtube.com/embed/SCYT8vb2siQ?si=VUgNo5IUNctyvxfq', 'Currently Running');

-- =========================================================
-- ORIGINAL SHOWTIME DATA
-- Copied without changing the original values.
-- =========================================================

INSERT INTO showtimes (movie_id, show_date, show_time)
VALUES
(1, '2026-07-24', '14:00:00'),
(1, '2026-07-24', '20:00:00'),

(2, '2026-07-24', '13:00:00'),
(2, '2026-07-24', '19:00:00'),

(5, '2026-07-24', '15:30:00'),
(5, '2026-07-24', '18:30:00'),

(3, '2026-07-25', '15:00:00'),
(3, '2026-07-25', '21:00:00'),

(6, '2026-07-25', '13:30:00'),
(6, '2026-07-25', '19:30:00'),

(10, '2026-07-25', '16:30:00'),
(10, '2026-07-25', '21:30:00'),

(4, '2026-07-26', '14:30:00'),
(4, '2026-07-26', '20:30:00'),

(7, '2026-07-26', '11:00:00'),
(7, '2026-07-26', '17:00:00'),

(9, '2026-07-26', '12:00:00'),
(9, '2026-07-26', '18:00:00'),

(8, '2026-07-27', '14:00:00'),
(8, '2026-07-27', '21:00:00'),

(2, '2026-07-27', '16:00:00'),
(5, '2026-07-27', '12:30:00'),
(10, '2026-07-27', '18:30:00'),

(1, '2026-07-28', '17:00:00'),
(3, '2026-07-28', '18:00:00'),
(4, '2026-07-28', '20:00:00'),
(6, '2026-07-28', '16:30:00'),
(9, '2026-07-28', '15:00:00');

-- =========================================================
-- SPRINT 2 USER / AUTHENTICATION TABLES
-- Passwords should be stored as BCrypt hashes by the Java app.
-- Sample hashes below are placeholders for demo accounts.
-- =========================================================

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'CUSTOMER') NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE customers (
    customer_id INT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    status ENUM('Active', 'Inactive', 'Suspended') NOT NULL DEFAULT 'Inactive',
    phone VARCHAR(25),
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE admins (
    admin_id INT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    FOREIGN KEY (admin_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE email_verification_tokens (
    verification_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE password_reset_tokens (
    token_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE user_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    logged_out_at DATETIME NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =========================================================
-- PROFILE EDITING TABLES
-- One address max per customer.
-- Three payment cards max per customer, enforced by slot + trigger.
-- Card numbers should be encrypted by the app before insertion.
-- =========================================================

CREATE TABLE addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL UNIQUE,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE payment_cards (
    payment_card_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    card_slot TINYINT NOT NULL,
    encrypted_card_number VARBINARY(512) NOT NULL,
    card_last_four CHAR(4) NOT NULL,
    expiration_date DATE NOT NULL,
    billing_address_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (card_slot BETWEEN 1 AND 3),
    UNIQUE (customer_id, card_slot),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (billing_address_id) REFERENCES addresses(address_id)
);

DELIMITER $$
CREATE TRIGGER prevent_more_than_three_cards
BEFORE INSERT ON payment_cards
FOR EACH ROW
BEGIN
    IF (SELECT COUNT(*) FROM payment_cards WHERE customer_id = NEW.customer_id) >= 3 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A customer cannot store more than 3 payment cards.';
    END IF;
END$$
DELIMITER ;

CREATE TABLE preferences (
    preference_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL UNIQUE,
    preferred_genre VARCHAR(100),
    preferred_rating VARCHAR(10),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE favorite_movies (
    customer_id INT NOT NULL,
    movie_id INT NOT NULL,
    added_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (customer_id, movie_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE
);

CREATE TABLE recommendations (
    recommendation_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    movie_id INT NOT NULL,
    reason VARCHAR(255),
    created_date DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE
);

-- =========================================================
-- THEATRE / SHOWROOM / SEAT TABLES FROM CLASS DIAGRAM
-- Added without changing original showtime data.
-- showtimes gets a nullable showroom_id for compatibility.
-- =========================================================

CREATE TABLE theatre (
    theatre_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL
);

CREATE TABLE showrooms (
    showroom_id INT AUTO_INCREMENT PRIMARY KEY,
    theatre_id INT NOT NULL,
    showroom_name VARCHAR(100) NOT NULL,
    seat_count INT NOT NULL,
    FOREIGN KEY (theatre_id) REFERENCES theatre(theatre_id) ON DELETE CASCADE
);

CREATE TABLE seats (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    showroom_id INT NOT NULL,
    row_number VARCHAR(10) NOT NULL,
    seat_number INT NOT NULL,
    UNIQUE (showroom_id, row_number, seat_number),
    FOREIGN KEY (showroom_id) REFERENCES showrooms(showroom_id) ON DELETE CASCADE
);

ALTER TABLE showtimes
ADD COLUMN showroom_id INT NULL,
ADD CONSTRAINT fk_showtimes_showroom
    FOREIGN KEY (showroom_id) REFERENCES showrooms(showroom_id);

-- Link bookings to customers without removing old booking columns.
ALTER TABLE bookings
ADD COLUMN customer_id INT NULL,
ADD CONSTRAINT fk_bookings_customer
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id);

-- =========================================================
-- PROMOTIONS, TICKETS, AND BOOKING EXTENSIONS
-- tickets adds detailed ticket/seat support while bookings keeps
-- adult_tickets, child_tickets, senior_tickets for old code.
-- =========================================================

CREATE TABLE promotions (
    promotion_id INT AUTO_INCREMENT PRIMARY KEY,
    promo_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    expiration_date DATE NOT NULL
);

CREATE TABLE booking_promotions (
    booking_id INT NOT NULL,
    promotion_id INT NOT NULL,
    PRIMARY KEY (booking_id, promotion_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id) ON DELETE CASCADE
);

CREATE TABLE tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    seat_id INT NOT NULL,
    ticket_type ENUM('Adult', 'Senior', 'Child') NOT NULL,
    price DECIMAL(6,2) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(seat_id),
    UNIQUE (booking_id, seat_id)
);

-- =========================================================
-- SAMPLE SPRINT 2 DATA
-- Demo password hash note:
-- Replace these with hashes generated by your Java BCrypt code.
-- Do NOT store plain text passwords.
-- =========================================================

INSERT INTO users (email, password_hash, role) VALUES
('admin@cinema.com', '$2a$10$replaceWithRealBCryptHashForAdmin', 'ADMIN'),
('customer1@example.com', '$2a$10$replaceWithRealBCryptHashForCustomer1', 'CUSTOMER'),
('customer2@example.com', '$2a$10$replaceWithRealBCryptHashForCustomer2', 'CUSTOMER');

INSERT INTO admins (admin_id, name) VALUES
(1, 'System Admin');

INSERT INTO customers (customer_id, first_name, last_name, status, phone) VALUES
(2, 'Test', 'Customer', 'Active', '555-111-2222'),
(3, 'Inactive', 'Customer', 'Inactive', '555-333-4444');

INSERT INTO email_verification_tokens (customer_id, token, expires_at, verified) VALUES
(2, 'demo-active-user-token', DATE_ADD(NOW(), INTERVAL 1 DAY), TRUE),
(3, 'demo-inactive-user-token', DATE_ADD(NOW(), INTERVAL 1 DAY), FALSE);

INSERT INTO password_reset_tokens (user_id, token, expires_at, used) VALUES
(2, 'demo-password-reset-token', DATE_ADD(NOW(), INTERVAL 1 HOUR), FALSE);

INSERT INTO addresses (customer_id, street, city, state, zip_code) VALUES
(2, '123 Main Street', 'Athens', 'GA', '30601');

INSERT INTO payment_cards
(customer_id, card_slot, encrypted_card_number, card_last_four, expiration_date, billing_address_id)
VALUES
(2, 1, AES_ENCRYPT('4111111111111111', 'demo-key-change-this'), '1111', '2028-12-01', 1),
(2, 2, AES_ENCRYPT('5555555555554444', 'demo-key-change-this'), '4444', '2029-06-01', 1);

INSERT INTO preferences (customer_id, preferred_genre, preferred_rating) VALUES
(2, 'Horror', 'PG-13'),
(3, 'Animation', 'PG');

INSERT INTO favorite_movies (customer_id, movie_id) VALUES
(2, 1),
(2, 3),
(2, 10);

INSERT INTO recommendations (customer_id, movie_id, reason, created_date) VALUES
(2, 3, 'Recommended because you like Horror movies.', CURDATE()),
(2, 10, 'Recommended because of your booking activity and preferences.', CURDATE());

INSERT INTO theatre (name) VALUES
('Cinema E-Booking Main Theatre');

INSERT INTO showrooms (theatre_id, showroom_name, seat_count) VALUES
(1, 'Showroom 1', 40),
(1, 'Showroom 2', 30),
(1, 'Showroom 3', 20);

-- Create sample seats.
INSERT INTO seats (showroom_id, row_number, seat_number) VALUES
(1, 'A', 1), (1, 'A', 2), (1, 'A', 3), (1, 'A', 4), (1, 'A', 5),
(1, 'B', 1), (1, 'B', 2), (1, 'B', 3), (1, 'B', 4), (1, 'B', 5),
(2, 'A', 1), (2, 'A', 2), (2, 'A', 3), (2, 'A', 4), (2, 'A', 5),
(2, 'B', 1), (2, 'B', 2), (2, 'B', 3), (2, 'B', 4), (2, 'B', 5),
(3, 'A', 1), (3, 'A', 2), (3, 'A', 3), (3, 'A', 4), (3, 'A', 5),
(3, 'B', 1), (3, 'B', 2), (3, 'B', 3), (3, 'B', 4), (3, 'B', 5);

-- Assign showrooms to existing showtimes without changing their original movie/date/time values.
UPDATE showtimes
SET showroom_id = CASE
    WHEN MOD(showtime_id, 3) = 1 THEN 1
    WHEN MOD(showtime_id, 3) = 2 THEN 2
    ELSE 3
END;

INSERT INTO promotions (promo_code, description, discount_amount, expiration_date) VALUES
('WELCOME10', 'Welcome discount for new customers', 10.00, '2026-12-31'),
('SUMMER5', 'Summer movie discount', 5.00, '2026-08-31');

-- Sample booking keeps original old columns and also links to a customer.
INSERT INTO bookings (showtime_id, adult_tickets, child_tickets, senior_tickets, total_price, customer_id) VALUES
(1, 2, 0, 0, 25.98, 2);

INSERT INTO tickets (booking_id, seat_id, ticket_type, price) VALUES
(1, 1, 'Adult', 12.99),
(1, 2, 'Adult', 12.99);

INSERT INTO booking_promotions (booking_id, promotion_id) VALUES
(1, 1);

-- =========================================================
-- USEFUL DEMO QUERIES
-- =========================================================

-- Login lookup:
-- SELECT u.user_id, u.email, u.password_hash, u.role, c.status
-- FROM users u
-- LEFT JOIN customers c ON u.user_id = c.customer_id
-- WHERE u.email = ?;

-- Profile page lookup:
-- SELECT u.email, c.first_name, c.last_name, c.status, c.phone,
--        a.street, a.city, a.state, a.zip_code
-- FROM users u
-- JOIN customers c ON u.user_id = c.customer_id
-- LEFT JOIN addresses a ON c.customer_id = a.customer_id
-- WHERE c.customer_id = ?;

-- Favorites lookup:
-- SELECT m.*
-- FROM favorite_movies fm
-- JOIN movies m ON fm.movie_id = m.movie_id
-- WHERE fm.customer_id = ?;
