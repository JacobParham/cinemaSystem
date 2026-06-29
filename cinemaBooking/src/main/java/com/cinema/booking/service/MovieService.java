package com.cinema.booking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cinema.booking.exception.MovieNotFoundException;
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
                .orElseThrow(() -> new MovieNotFoundException(movieId));
    }

    public List<Movie> getMoviesByStatus(String status) {
        return movieRepository.findByStatus(status);
    }

    public List<Movie> searchMovies(String title, String genre, String status) {
        String normalizedTitle = title == null ? null : title.trim();
        String normalizedGenre = genre == null ? null : genre.trim();
        String normalizedStatus = status == null ? null : status.trim();

        boolean hasTitle = normalizedTitle != null && !normalizedTitle.isBlank();
        boolean hasGenre = normalizedGenre != null && !normalizedGenre.isBlank();
        boolean hasStatus = normalizedStatus != null && !normalizedStatus.isBlank();

        if (hasTitle && hasGenre && hasStatus) {
            return movieRepository.findByTitleContainingIgnoreCaseAndGenreIgnoreCaseAndStatus(normalizedTitle, normalizedGenre, normalizedStatus);
        }
        if (hasTitle && hasGenre) {
            return movieRepository.findByTitleContainingIgnoreCaseAndGenreIgnoreCase(normalizedTitle, normalizedGenre);
        }
        if (hasTitle && hasStatus) {
            return movieRepository.findByTitleContainingIgnoreCaseAndStatus(normalizedTitle, normalizedStatus);
        }
        if (hasGenre && hasStatus) {
            return movieRepository.findByGenreIgnoreCaseAndStatus(normalizedGenre, normalizedStatus);
        }
        if (hasTitle) {
            List<Movie> titleMatches = movieRepository.findByTitleContainingIgnoreCase(normalizedTitle);
            return titleMatches.isEmpty() ? movieRepository.findAll() : titleMatches;
        }
        if (hasGenre) {
            return movieRepository.findByGenreIgnoreCase(normalizedGenre);
        }
        if (hasStatus) {
            return movieRepository.findByStatus(normalizedStatus);
        }
        return movieRepository.findAll();
    }
}
