package com.cinema.booking.model;

import java.io.Serializable;
import java.util.Objects;

public class FavoriteMovieId implements Serializable {

    private Integer accountId;
    private Integer movieId;

    public FavoriteMovieId() {}

    public FavoriteMovieId(Integer accountId, Integer movieId) {
        this.accountId = accountId;
        this.movieId = movieId;
    }

    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }
    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteMovieId)) return false;
        FavoriteMovieId that = (FavoriteMovieId) o;
        return Objects.equals(accountId, that.accountId) && Objects.equals(movieId, that.movieId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, movieId);
    }
}
