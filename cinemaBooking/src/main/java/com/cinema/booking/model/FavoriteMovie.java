package com.cinema.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "favorite_movies")
@IdClass(FavoriteMovieId.class)
public class FavoriteMovie {

    @Id
    @Column(name = "account_id")
    private Integer accountId;

    @Id
    @Column(name = "movie_id")
    private Integer movieId;

    public FavoriteMovie() {}

    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }
    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }
}
