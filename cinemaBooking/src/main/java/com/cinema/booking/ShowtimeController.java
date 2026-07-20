package com.cinema.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.model.Movie;
import com.cinema.booking.model.Showroom;
import com.cinema.booking.model.Showtime;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.ShowroomRepository;
import com.cinema.booking.repository.ShowtimeRepository;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final ShowroomRepository showroomRepository;

    public ShowtimeController(
            ShowtimeRepository showtimeRepository,
            MovieRepository movieRepository,
            ShowroomRepository showroomRepository
    ) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.showroomRepository = showroomRepository;
    }


    @GetMapping
    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeRepository.findAll()
                .stream()
                .map(ShowtimeResponse::from)
                .toList();
    }


    @GetMapping("/movie/{movieId}")
    public ResponseEntity<?> getShowtimesByMovie(
            @PathVariable int movieId
    ) {
        Movie movie = movieRepository.findById(movieId)
                .orElse(null);

        if (movie == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            "Movie with ID " + movieId + " was not found."
                    ));
        }

        List<ShowtimeResponse> showtimes =
                showtimeRepository.findByMovie(movie)
                        .stream()
                        .map(ShowtimeResponse::from)
                        .toList();

        return ResponseEntity.ok(showtimes);
    }

    @PostMapping
    public ResponseEntity<?> createShowtime(
            @RequestBody CreateShowtimeRequest request
    ) {
        ResponseEntity<ErrorResponse> validationError =
                validateCreateRequest(request);

        if (validationError != null) {
            return validationError;
        }

        Movie movie = movieRepository.findById(request.movieId())
                .orElse(null);

        if (movie == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            "The selected movie was not found."
                    ));
        }

        Showroom showroom =
                showroomRepository.findById(request.showroomId())
                        .orElse(null);

        if (showroom == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            "The selected showroom was not found."
                    ));
        }

        boolean showroomConflict =
                showtimeRepository
                        .existsByShowroomAndShowDateAndShowTime(
                                showroom,
                                request.showDate(),
                                request.showTime()
                        );

        if (showroomConflict) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(
                            showroom.getShowroomName()
                                    + " already has a movie scheduled "
                                    + "for that date and time."
                    ));
        }

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setShowroom(showroom);
        showtime.setShowDate(request.showDate());
        showtime.setShowTime(request.showTime());

        try {
            Showtime savedShowtime =
                    showtimeRepository.save(showtime);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ShowtimeResponse.from(savedShowtime));

        } catch (DataIntegrityViolationException exception) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(
                            "That showroom already has a movie scheduled "
                                    + "for that date and time."
                    ));
        }
    }


    private ResponseEntity<ErrorResponse> validateCreateRequest(
            CreateShowtimeRequest request
    ) {
        if (request == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            "Showtime information is required."
                    ));
        }

        if (request.movieId() <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            "A valid movie is required."
                    ));
        }

        if (request.showroomId() <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            "A valid showroom is required."
                    ));
        }

        if (request.showDate() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            "A show date is required."
                    ));
        }

        if (request.showTime() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            "A show time is required."
                    ));
        }

        if (request.showDate().isBefore(LocalDate.now())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            "A showtime cannot be scheduled in the past."
                    ));
        }

        if (
                request.showDate().isEqual(LocalDate.now())
                        && request.showTime().isBefore(LocalTime.now())
        ) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            "A showtime cannot be scheduled in the past."
                    ));
        }

        return null;
    }

    public record CreateShowtimeRequest(
            int movieId,
            int showroomId,
            LocalDate showDate,
            LocalTime showTime
    ) {
    }

    public record ShowtimeResponse(
            int showtimeId,
            int movieId,
            String movieTitle,
            int showroomId,
            String showroomName,
            int showroomSeatCount,
            LocalDate showDate,
            LocalTime showTime
    ) {
        public static ShowtimeResponse from(Showtime showtime) {
            return new ShowtimeResponse(
                    showtime.getShowtimeId(),
                    showtime.getMovie().getMovieId(),
                    showtime.getMovie().getTitle(),
                    showtime.getShowroom().getShowroomId(),
                    showtime.getShowroom().getShowroomName(),
                    showtime.getShowroom().getSeatCount(),
                    showtime.getShowDate(),
                    showtime.getShowTime()
            );
        }
    }

    public record ErrorResponse(String message) {
    }
}
