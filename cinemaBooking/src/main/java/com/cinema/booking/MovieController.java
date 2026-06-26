package com.cinema.booking;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
