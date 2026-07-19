package com.cinema.booking.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.model.Booking;
import com.cinema.booking.model.SeatLock;
import com.cinema.booking.model.Showtime;
import com.cinema.booking.repository.AccountRepository;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.ShowtimeRepository;
import com.cinema.booking.service.SeatLockService;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final AccountRepository accountRepository;
    private final SeatLockService seatLockService;

    public BookingController(BookingRepository bookingRepository,
                             ShowtimeRepository showtimeRepository,
                             AccountRepository accountRepository,
                             SeatLockService seatLockService) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.accountRepository = accountRepository;
        this.seatLockService = seatLockService;
    }

    /**
     * GET /bookings/seats/{showtimeId}
     * Returns a flat list of already-booked AND locked seat labels for the given showtime.
     * Used by the frontend to mark seats as taken before the user makes a selection.
     */
    @GetMapping("/seats/{showtimeId}")
    public ResponseEntity<Map<String, List<String>>> getBookedSeats(@PathVariable int showtimeId) {
        // Get already booked seats
        List<String> rawEntries = bookingRepository.findSeatNumbersByShowtimeId(showtimeId);

        // Each entry is a comma-separated string like "A1,A2,B3" — flatten them all
        List<String> bookedSeats = rawEntries.stream()
                .flatMap(entry -> Arrays.stream(entry.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // Get currently locked seats
        List<SeatLock> activeLocks = seatLockService.getActiveLocksForShowtime(showtimeId);
        List<String> lockedSeats = activeLocks.stream()
                .map(SeatLock::getSeatNumber)
                .collect(Collectors.toList());

        // Combine both lists
        List<String> unavailableSeats = Stream.concat(bookedSeats.stream(), lockedSeats.stream())
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("bookedSeats", unavailableSeats));
    }

    /**
     * POST /bookings/lock-seats
     * Locks seats for the current session to prevent other users from selecting them.
     * Request body: { showtimeId, seatNumbers, sessionId }
     */
    @PostMapping("/lock-seats")
    public ResponseEntity<Map<String, Object>> lockSeats(
            @RequestBody SeatLockRequest request,
            Principal principal) {

        // Validate showtime exists
        Showtime showtime = showtimeRepository.findById(request.showtimeId()).orElse(null);
        if (showtime == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid showtime."));
        }

        // Parse seat numbers
        List<String> seatNumbers = request.seatNumbers() == null
                ? Collections.emptyList()
                : Arrays.stream(request.seatNumbers().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

        if (seatNumbers.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "At least one seat must be provided."));
        }

        // Get account ID if user is logged in
        Integer accountId = null;
        if (principal != null) {
            accountId = accountRepository.findByEmailIgnoreCase(principal.getName())
                    .map(a -> a.getAccountId())
                    .orElse(null);
        }

        // Attempt to lock the seats
        boolean locked = seatLockService.lockSeats(
                request.showtimeId(),
                seatNumbers,
                accountId,
                request.sessionId()
        );

        if (!locked) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "One or more seats are already locked or booked."));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Seats successfully locked.",
                "expiresAt", java.time.LocalDateTime.now().plusMinutes(15).toString()
        ));
    }

    /**
     * POST /bookings/release-seats
     * Releases seat locks for the current session.
     * Request body: { sessionId }
     */
    @PostMapping("/release-seats")
    public ResponseEntity<Map<String, String>> releaseSeats(
            @RequestBody SeatReleaseRequest request) {

        seatLockService.releaseSessionLocks(request.sessionId());
        return ResponseEntity.ok(Map.of("message", "Seat locks released."));
    }

    /**
     * POST /bookings
     * Saves a new booking. Requires the user to be logged in.
     * Request body: { showtimeId, adultTickets, childTickets, seniorTickets, seatNumbers, totalPrice, sessionId }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(
            @RequestBody BookingRequest request,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "You must be logged in to book tickets."));
        }

        // Validate showtime exists
        Showtime showtime = showtimeRepository.findById(request.showtimeId()).orElse(null);
        if (showtime == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid showtime."));
        }

        // Validate total tickets > 0
        int totalTickets = request.adultTickets() + request.childTickets() + request.seniorTickets();
        if (totalTickets <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "At least one ticket is required."));
        }

        // Validate seats match ticket count
        List<String> requestedSeats = request.seatNumbers() == null
                ? Collections.emptyList()
                : Arrays.stream(request.seatNumbers().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

        if (requestedSeats.size() != totalTickets) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Number of seats must match total tickets."));
        }

        // Check none of the requested seats are already taken
        List<String> alreadyBooked = bookingRepository.findSeatNumbersByShowtimeId(request.showtimeId())
                .stream()
                .flatMap(entry -> Arrays.stream(entry.split(",")))
                .map(String::trim)
                .collect(Collectors.toList());

        List<String> conflicts = requestedSeats.stream()
                .filter(alreadyBooked::contains)
                .collect(Collectors.toList());

        if (!conflicts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Seats already taken: " + String.join(", ", conflicts)));
        }

        // Verify that the current session has these seats locked
        if (request.sessionId() != null && !request.sessionId().isEmpty()) {
            List<SeatLock> sessionLocks = seatLockService.getActiveLocksForShowtime(request.showtimeId())
                    .stream()
                    .filter(lock -> lock.getSessionId().equals(request.sessionId()))
                    .collect(Collectors.toList());

            List<String> lockedSeats = sessionLocks.stream()
                    .map(SeatLock::getSeatNumber)
                    .collect(Collectors.toList());

            // Check if all requested seats are locked by this session
            boolean allSeatsLocked = requestedSeats.stream().allMatch(lockedSeats::contains);
            if (!allSeatsLocked) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Seat lock has expired. Please select your seats again."));
            }
        }

        // Resolve account id from session principal
        Integer accountId = accountRepository.findByEmailIgnoreCase(principal.getName())
                .map(a -> a.getAccountId())
                .orElse(null);

        // Persist the booking
        Booking booking = new Booking();
        booking.setShowtime(showtime);
        booking.setAccountId(accountId);
        booking.setAdultTickets(request.adultTickets());
        booking.setChildTickets(request.childTickets());
        booking.setSeniorTickets(request.seniorTickets());
        booking.setSeatNumbers(String.join(",", requestedSeats));
        booking.setTotalPrice(request.totalPrice());

        Booking saved = bookingRepository.save(booking);

        // Release the seat locks for this session
        if (request.sessionId() != null && !request.sessionId().isEmpty()) {
            seatLockService.releaseSessionLocks(request.sessionId());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "bookingId", saved.getBookingId(),
                "message", "Booking confirmed."
        ));
    }

    public record BookingRequest(
            int showtimeId,
            int adultTickets,
            int childTickets,
            int seniorTickets,
            String seatNumbers,
            BigDecimal totalPrice,
            String sessionId
    ) {}

    public record SeatLockRequest(
            int showtimeId,
            String seatNumbers,
            String sessionId
    ) {}

    public record SeatReleaseRequest(
            String sessionId
    ) {}
}
