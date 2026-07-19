package com.cinema.booking.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.model.Movie;
import com.cinema.booking.service.MovieService;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{movieId}")
    public Movie getMovieById(@PathVariable int movieId) {
        return movieService.getMovieById(movieId);
    }

    @GetMapping("/current")
    public List<Movie> getCurrentlyRunningMovies() {
        return movieService.getMoviesByStatus("Currently Running");
    }

    @GetMapping("/coming-soon")
    public List<Movie> getComingSoonMovies() {
        return movieService.getMoviesByStatus("Coming Soon");
    }

    @GetMapping("/search")
    public List<Movie> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String status) {
        return movieService.searchMovies(title, genre, status);
    }

    @PostMapping
    public ResponseEntity<?> createMovie(@RequestBody Map<String, Object> payload) {
        try {
            String title = String.valueOf(payload.getOrDefault("title", "")).trim();
            String genre = String.valueOf(payload.getOrDefault("genre", "")).trim();
            String rating = payload.containsKey("rating") ? String.valueOf(payload.get("rating")).trim() : null;
            String description = payload.containsKey("description") ? String.valueOf(payload.get("description")).trim() : null;
            String posterUrl = payload.containsKey("posterUrl") ? String.valueOf(payload.get("posterUrl")).trim() : null;
            String trailerUrl = payload.containsKey("trailerUrl") ? String.valueOf(payload.get("trailerUrl")).trim() : null;
            String status = String.valueOf(payload.getOrDefault("status", "")).trim();

            Movie movie = movieService.createMovie(title, genre, rating, description, posterUrl, trailerUrl, status);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Movie created successfully",
                    "movieId", movie.getMovieId(),
                    "title", movie.getTitle(),
                    "genre", movie.getGenre(),
                    "status", movie.getStatus()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }
}