package com.cinema.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cinema.booking.model.Movie;
import com.cinema.booking.model.Showroom;
import com.cinema.booking.model.Showtime;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.ShowroomRepository;
import com.cinema.booking.repository.ShowtimeRepository;

@ExtendWith(MockitoExtension.class)
class ShowtimeControllerTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ShowroomRepository showroomRepository;

    private ShowtimeController controller;
    private Movie movie;
    private Showroom showroom;

    @BeforeEach
    void setUp() {
        controller = new ShowtimeController(
                showtimeRepository,
                movieRepository,
                showroomRepository);

        movie = new Movie();
        movie.setMovieId(1);
        movie.setTitle("Demo Movie");

        showroom = new Showroom();
        showroom.setShowroomId(2);
        showroom.setShowroomName("Showroom 2");
        showroom.setSeatCount(24);
    }

    @Test
    void createShowtimeStoresValidRequest() {
        LocalDate showDate = LocalDate.now().plusDays(1);
        LocalTime showTime = LocalTime.of(18, 30);
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(showroomRepository.findById(2)).thenReturn(Optional.of(showroom));
        when(showtimeRepository.existsByShowroomAndShowDateAndShowTime(
                showroom, showDate, showTime)).thenReturn(false);
        when(showtimeRepository.save(any(Showtime.class))).thenAnswer(invocation -> {
            Showtime saved = invocation.getArgument(0);
            saved.setShowtimeId(10);
            return saved;
        });

        ResponseEntity<?> response = controller.createShowtime(
                new ShowtimeController.CreateShowtimeRequest(1, 2, showDate, showTime));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(ShowtimeController.ShowtimeResponse.class);
        ShowtimeController.ShowtimeResponse body =
                (ShowtimeController.ShowtimeResponse) response.getBody();
        assertThat(body.showtimeId()).isEqualTo(10);
        assertThat(body.showroomSeatCount()).isEqualTo(24);
        verify(showtimeRepository).save(any(Showtime.class));
    }

    @Test
    void createShowtimeRejectsShowroomConflict() {
        LocalDate showDate = LocalDate.now().plusDays(1);
        LocalTime showTime = LocalTime.of(20, 0);
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(showroomRepository.findById(2)).thenReturn(Optional.of(showroom));
        when(showtimeRepository.existsByShowroomAndShowDateAndShowTime(
                showroom, showDate, showTime)).thenReturn(true);

        ResponseEntity<?> response = controller.createShowtime(
                new ShowtimeController.CreateShowtimeRequest(1, 2, showDate, showTime));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    @Test
    void createShowtimeRejectsPastDate() {
        ResponseEntity<?> response = controller.createShowtime(
                new ShowtimeController.CreateShowtimeRequest(
                        1,
                        2,
                        LocalDate.now().minusDays(1),
                        LocalTime.NOON));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }
}
