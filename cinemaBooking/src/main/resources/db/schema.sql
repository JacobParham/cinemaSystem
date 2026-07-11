CREATE TABLE IF NOT EXISTS movies (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    rating VARCHAR(10),
    description TEXT,
    poster_url TEXT,
    trailer_url TEXT,
    status ENUM('Currently Running', 'Coming Soon') NOT NULL
);

CREATE TABLE IF NOT EXISTS showtimes (
    showtime_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id)
);

CREATE TABLE IF NOT EXISTS accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    promotions BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER'
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    showtime_id INT NOT NULL,
    adult_tickets INT DEFAULT 0,
    child_tickets INT DEFAULT 0,
    senior_tickets INT DEFAULT 0,
    total_price DECIMAL(10,2),
    FOREIGN KEY (showtime_id) REFERENCES showtimes(showtime_id)
);

CREATE TABLE IF NOT EXISTS payment_cards (
    card_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    card_holder VARCHAR(255) NOT NULL,
    card_number_enc TEXT NOT NULL,
    card_last4 VARCHAR(4) NOT NULL,
    expiration VARCHAR(10) NOT NULL,
    cvv_enc TEXT,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
