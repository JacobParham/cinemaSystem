package com.cinema.booking.repository;

import com.cinema.booking.model.FavoriteMovie;
import com.cinema.booking.model.FavoriteMovieId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FavoriteMovieRepository extends JpaRepository<FavoriteMovie, FavoriteMovieId> {
    List<FavoriteMovie> findByAccountId(Integer accountId);
    boolean existsByAccountIdAndMovieId(Integer accountId, Integer movieId);
    @Transactional
    void deleteByAccountIdAndMovieId(Integer accountId, Integer movieId);
}
