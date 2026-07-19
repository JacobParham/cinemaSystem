package com.cinema.booking.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private int bookingId;

    // Nullable — bookings placed by guests won't have an account
    @Column(name = "account_id")
    private Integer accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @Column(name = "adult_tickets")
    private int adultTickets;

    @Column(name = "child_tickets")
    private int childTickets;

    @Column(name = "senior_tickets")
    private int seniorTickets;

    // Comma-separated seat labels, e.g. "A1,A2,B3"
    @Column(name = "seat_numbers")
    private String seatNumbers;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    public Booking() {}

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }

    public Showtime getShowtime() { return showtime; }
    public void setShowtime(Showtime showtime) { this.showtime = showtime; }

    public int getShowtimeId() {
        return showtime == null ? 0 : showtime.getShowtimeId();
    }

    public void setShowtimeId(int showtimeId) {
        Showtime selectedShowtime = new Showtime();
        selectedShowtime.setShowtimeId(showtimeId);
        this.showtime = selectedShowtime;
    }

    public int getAdultTickets() { return adultTickets; }
    public void setAdultTickets(int adultTickets) { this.adultTickets = adultTickets; }

    public int getChildTickets() { return childTickets; }
    public void setChildTickets(int childTickets) { this.childTickets = childTickets; }

    public int getSeniorTickets() { return seniorTickets; }
    public void setSeniorTickets(int seniorTickets) { this.seniorTickets = seniorTickets; }

    public String getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(String seatNumbers) { this.seatNumbers = seatNumbers; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}
