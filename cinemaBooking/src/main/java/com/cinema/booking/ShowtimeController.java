package com.cinema.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.model.Movie;
import com.cinema.booking.model.Showtime;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.ShowtimeRepository;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public ShowtimeController(ShowtimeRepository showtimeRepository, MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeRepository.findAll()
                .stream()
                .map(ShowtimeResponse::from)
                .toList();
    }

    @GetMapping("/movie/{movieId}")
    public List<ShowtimeResponse> getShowtimesByMovie(@PathVariable int movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found: " + movieId));

        return showtimeRepository.findByMovie(movie)
                .stream()
                .map(ShowtimeResponse::from)
                .toList();
    }

    public record ShowtimeResponse(
            int showtimeId,
            int movieId,
            String movieTitle,
            LocalDate showDate,
            LocalTime showTime
    ) {
        public static ShowtimeResponse from(Showtime showtime) {
            Movie movie = showtime.getMovie();
            return new ShowtimeResponse(
                    showtime.getShowtimeId(),
                    movie.getMovieId(),
                    movie.getTitle(),
                    showtime.getShowDate(),
                    showtime.getShowTime()
            );
        }
    }
}
