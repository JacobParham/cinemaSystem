package com.cinema.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cinema.booking.model.Movie;
import com.cinema.booking.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void searchMoviesReturnsAllMoviesWhenTitleQueryHasNoMatches() {
        Movie movie = new Movie();
        movie.setTitle("Spider-Man");

        when(movieRepository.findByTitleContainingIgnoreCase("hero")).thenReturn(List.of());
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        List<Movie> results = movieService.searchMovies("hero", null, null);

        assertThat(results).containsExactly(movie);
    }
}
