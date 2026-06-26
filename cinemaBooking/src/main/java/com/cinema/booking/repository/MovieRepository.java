package com.cinema.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByStatus(String status);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByGenreIgnoreCase(String genre);

    List<Movie> findByTitleContainingIgnoreCaseAndStatus(String title, String status);

    List<Movie> findByGenreIgnoreCaseAndStatus(String genre, String status);

    List<Movie> findByTitleContainingIgnoreCaseAndGenreIgnoreCase(String title, String genre);

    List<Movie> findByTitleContainingIgnoreCaseAndGenreIgnoreCaseAndStatus(String title, String genre, String status);
}
