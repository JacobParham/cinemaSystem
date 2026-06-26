package com.cinema.booking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cinema.booking.model.Movie;
import com.cinema.booking.repository.MovieRepository;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(int movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found: " + movieId));
    }

    public List<Movie> getMoviesByStatus(String status) {
        return movieRepository.findByStatus(status);
    }

    public List<Movie> searchMovies(String title, String genre, String status) {
        boolean hasTitle = title != null && !title.isBlank();
        boolean hasGenre = genre != null && !genre.isBlank();
        boolean hasStatus = status != null && !status.isBlank();

        if (hasTitle && hasGenre && hasStatus) {
            return movieRepository.findByTitleContainingIgnoreCaseAndGenreIgnoreCaseAndStatus(title, genre, status);
        }
        if (hasTitle && hasGenre) {
            return movieRepository.findByTitleContainingIgnoreCaseAndGenreIgnoreCase(title, genre);
        }
        if (hasTitle && hasStatus) {
            return movieRepository.findByTitleContainingIgnoreCaseAndStatus(title, status);
        }
        if (hasGenre && hasStatus) {
            return movieRepository.findByGenreIgnoreCaseAndStatus(genre, status);
        }
        if (hasTitle) {
            return movieRepository.findByTitleContainingIgnoreCase(title);
        }
        if (hasGenre) {
            return movieRepository.findByGenreIgnoreCase(genre);
        }
        if (hasStatus) {
            return movieRepository.findByStatus(status);
        }
        return movieRepository.findAll();
    }
}
