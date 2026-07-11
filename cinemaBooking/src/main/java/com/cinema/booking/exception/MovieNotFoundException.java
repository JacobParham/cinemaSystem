package com.cinema.booking.exception;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(int movieId) {
        super("Movie not found with id: " + movieId);
    }
}
