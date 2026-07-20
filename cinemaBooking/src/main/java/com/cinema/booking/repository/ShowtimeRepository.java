package com.cinema.booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.model.Movie;
import com.cinema.booking.model.Showroom;
import com.cinema.booking.model.Showtime;

public interface ShowtimeRepository
        extends JpaRepository<Showtime, Integer> {

    List<Showtime> findByMovie(Movie movie);

    boolean existsByShowroomAndShowDateAndShowTime(
            Showroom showroom,
            LocalDate showDate,
            LocalTime showTime
    );
}