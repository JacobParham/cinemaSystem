package com.cinema.booking.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BookingHistoryResponse(
        int bookingId,
        int showtimeId,
        int movieId,
        String movieTitle,
        String posterUrl,
        String showroomName,
        LocalDate showDate,
        LocalTime showTime,
        int adultTickets,
        int childTickets,
        int seniorTickets,
        String seatNumbers,
        BigDecimal totalPrice,
        LocalDateTime createdAt
) {
}