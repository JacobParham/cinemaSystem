package com.cinema.booking.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.booking.model.Booking;
import com.cinema.booking.model.Movie;
import com.cinema.booking.model.Showroom;
import com.cinema.booking.model.Showtime;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.ShowroomRepository;
import com.cinema.booking.repository.ShowtimeRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final ShowroomRepository showroomRepository;
    private final BookingRepository bookingRepository;

    public DatabaseSeeder(
            MovieRepository movieRepository,
            ShowtimeRepository showtimeRepository,
            ShowroomRepository showroomRepository,
            BookingRepository bookingRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
        this.showroomRepository = showroomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, Movie> moviesByTitle = movieRepository.count() == 0
                ? seedMovies()
                : movieRepository.findAll().stream().collect(Collectors.toMap(
                        Movie::getTitle,
                        Function.identity(),
                        (first, ignored) -> first,
                        LinkedHashMap::new));

        if (showtimeRepository.count() == 0) {
            seedShowtimes(moviesByTitle);
        }

        if (bookingRepository.count() == 0) {
            seedDemoBooking();
        }
    }

    private Map<String, Movie> seedMovies() {
        List<Movie> movies = List.of(
                movie(
                        "Backrooms",
                        "Horror",
                        "R",
                        "A strange doorway appears in the basement of a furniture storeroom.",
                        "https://www.themoviedb.org/t/p/w1280/rhGx6E3qRNMgj3i5su2oukNHwIQ.jpg",
                        "https://www.youtube.com/embed/0HjdiohVOik?si=OBkqyQGIvwezePb4",
                        "Currently Running"),
                movie(
                        "Toy Story 5",
                        "Animation",
                        "PG",
                        "When Bonnie receives a Lilypad tablet as a gift and becomes obsessed, Buzz, Woody, Jessie and the rest of the gang's jobs become exponentially harder when they have to go head to head with the all-new threat to playtime.",
                        "https://www.themoviedb.org/t/p/w1280/a6H2U7pjibMia41TwyFVd1PVQw3.jpg",
                        "https://www.youtube.com/embed/c51ND9Hdbw0?si=XqRigDklBpu9hySn",
                        "Currently Running"),
                movie(
                        "Obsession",
                        "Horror",
                        "R",
                        "After breaking the mysterious \"One Wish Willow\" to win his crush's heart, a hopeless romantic finds himself getting exactly what he asked for but soon discovers that some desires come at a dark, sinister price.",
                        "https://www.themoviedb.org/t/p/w1280/bRwnj8WEKBCvmfeUNOukJPwB43K.jpg",
                        "https://www.youtube.com/embed/xJYoN-fX2j0?si=FO1HEDfsLWzEcaHl",
                        "Currently Running"),
                movie(
                        "Spider-Man: Brand New Day",
                        "Action",
                        "PG-13",
                        "Fighting crime full-time as Spider-Man in a world that doesn't remember him—and the pressure of seeing his old friends move on without him—sparks a change in Peter Parker he may not have the power to control.",
                        "https://www.themoviedb.org/t/p/w1280/yyB2VJEW3an2xCdcYCPQhn9QERR.jpg",
                        "https://www.youtube.com/embed/62bIsvRcPv0?si=_2sG99NQ38WxV4mz",
                        "Coming Soon"),
                movie(
                        "The Mandalorian and Grogu",
                        "Adventure",
                        "PG-13",
                        "The evil Empire has fallen, and Imperial warlords remain scattered throughout the galaxy. As the fledgling New Republic works to protect everything the Rebellion fought for, they have enlisted the help of legendary Mandalorian bounty hunter Din Djarin and his young apprentice Grogu.",
                        "https://www.themoviedb.org/t/p/w1280/5Vi8dSauVwH1HOsiZceDMbRr1Ca.jpg",
                        "https://www.youtube.com/embed/IHWlvwu8t1w?si=W2mi8ccvphrl3QQi",
                        "Currently Running"),
                movie(
                        "Michael",
                        "Drama",
                        "PG-13",
                        "The story of Michael Jackson, one of the most influential artists the world has ever known, and his life beyond the music.",
                        "https://www.themoviedb.org/t/p/w1280/zm0KAbOjlt9eR5y7vDiL2dEOwMl.jpg",
                        "https://www.youtube.com/embed/3zOLzsbOleM?si=a3I8eM8alQzu-XLw",
                        "Currently Running"),
                movie(
                        "Minions & Monsters",
                        "Animation",
                        "PG",
                        "This is the rambunctious, ridiculous and totally true story of how the Minions conquered Hollywood, became movie stars, lost everything, unleashed monsters onto the world and then banded together to try and save the planet from the mayhem they had just created.",
                        "https://www.themoviedb.org/t/p/w1280/nz7i42yhLIJ4ve9JKgM6NthoLHO.jpg",
                        "https://www.youtube.com/embed/V-O-uBaHk3c?si=TGVqIvWG984VaAov",
                        "Coming Soon"),
                movie(
                        "The Odyssey",
                        "Fantasy",
                        "R",
                        "Odysseus, the legendary King of Ithaca, embarks on a long and perilous journey home following the Trojan War.",
                        "https://www.themoviedb.org/t/p/w1280/krVa7rKCQb4OBfsr2LTJv4rTz5q.jpg",
                        "https://www.youtube.com/embed/f_bKjZeJBBI?si=5lUQS2RhBFI31cyQ",
                        "Coming Soon"),
                movie(
                        "Moana",
                        "Adventure",
                        "PG",
                        "Moana answers the Ocean's call and, for the first time, voyages beyond the reef of her island of Motunui with the infamous demigod Maui on an unforgettable journey to restore prosperity to her people.",
                        "https://www.themoviedb.org/t/p/w1280/zKVgiv5qHCvCLT4A2ymJi5QeXDH.jpg",
                        "https://www.youtube.com/embed/EEz5xbzYPKI?si=O1Lw03OqET8RvjrX",
                        "Coming Soon"),
                movie(
                        "Disclosure Day",
                        "Thriller",
                        "PG-13",
                        "A cybersecurity expert becomes a whistleblower after uncovering secrets about aliens, putting him on the run from a corporation.",
                        "https://www.themoviedb.org/t/p/w1280/3o5YPjDGDTcTDL5ftDA9NwN9dLd.jpg",
                        "https://www.youtube.com/embed/SCYT8vb2siQ?si=VUgNo5IUNctyvxfq",
                        "Currently Running"));

        Map<String, Movie> moviesByTitle = new LinkedHashMap<>();
        for (Movie movie : movies) {
            moviesByTitle.put(movie.getTitle(), movieRepository.save(movie));
        }
        return moviesByTitle;
    }

    private void seedShowtimes(Map<String, Movie> moviesByTitle) {
        List<ShowtimeSeed> showtimeSeeds = List.of(
                showtime("Backrooms", "2026-07-24", "14:00:00"),
                showtime("Backrooms", "2026-07-24", "20:00:00"),
                showtime("Toy Story 5", "2026-07-24", "13:00:00"),
                showtime("Toy Story 5", "2026-07-24", "19:00:00"),
                showtime("The Mandalorian and Grogu", "2026-07-24", "15:30:00"),
                showtime("The Mandalorian and Grogu", "2026-07-24", "18:30:00"),
                showtime("Obsession", "2026-07-25", "15:00:00"),
                showtime("Obsession", "2026-07-25", "21:00:00"),
                showtime("Michael", "2026-07-25", "13:30:00"),
                showtime("Michael", "2026-07-25", "19:30:00"),
                showtime("Disclosure Day", "2026-07-25", "16:30:00"),
                showtime("Disclosure Day", "2026-07-25", "21:30:00"),
                showtime("Spider-Man: Brand New Day", "2026-07-26", "14:30:00"),
                showtime("Spider-Man: Brand New Day", "2026-07-26", "20:30:00"),
                showtime("Minions & Monsters", "2026-07-26", "11:00:00"),
                showtime("Minions & Monsters", "2026-07-26", "17:00:00"),
                showtime("Moana", "2026-07-26", "12:00:00"),
                showtime("Moana", "2026-07-26", "18:00:00"),
                showtime("The Odyssey", "2026-07-27", "14:00:00"),
                showtime("The Odyssey", "2026-07-27", "21:00:00"),
                showtime("Toy Story 5", "2026-07-27", "16:00:00"),
                showtime("The Mandalorian and Grogu", "2026-07-27", "12:30:00"),
                showtime("Disclosure Day", "2026-07-27", "18:30:00"),
                showtime("Backrooms", "2026-07-28", "17:00:00"),
                showtime("Obsession", "2026-07-28", "18:00:00"),
                showtime("Spider-Man: Brand New Day", "2026-07-28", "20:00:00"),
                showtime("Michael", "2026-07-28", "16:30:00"),
                showtime("Moana", "2026-07-28", "15:00:00"));

        List<Showroom> showrooms = List.of(
                requiredShowroom("Showroom 1"),
                requiredShowroom("Showroom 2"),
                requiredShowroom("Showroom 3"));

        for (int index = 0; index < showtimeSeeds.size(); index++) {
            ShowtimeSeed seed = showtimeSeeds.get(index);
            Movie movie = moviesByTitle.get(seed.movieTitle());
            if (movie == null) {
                continue;
            }

            Showtime showtime = new Showtime();
            showtime.setMovie(movie);
            showtime.setShowroom(showrooms.get(index % showrooms.size()));
            showtime.setShowDate(seed.showDate());
            showtime.setShowTime(seed.showTime());
            showtimeRepository.save(showtime);
        }
    }

    private Showroom requiredShowroom(String showroomName) {
        return showroomRepository.findByShowroomName(showroomName)
                .orElseThrow(() -> new IllegalStateException(
                        showroomName + " was not created by db/schema.sql."));
    }

    private void seedDemoBooking() {
        showtimeRepository.findAll().stream().findFirst().ifPresent(showtime -> {
            Booking booking = new Booking();
            booking.setShowtime(showtime);
            booking.setAdultTickets(2);
            booking.setChildTickets(0);
            booking.setSeniorTickets(0);
            booking.setSeatNumbers("A1,A2");
            booking.setTotalPrice(new BigDecimal("28.00"));
            bookingRepository.save(booking);
        });
    }

    private static Movie movie(
            String title,
            String genre,
            String rating,
            String description,
            String posterUrl,
            String trailerUrl,
            String status) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setRating(rating);
        movie.setDescription(description);
        movie.setPosterUrl(posterUrl);
        movie.setTrailerUrl(trailerUrl);
        movie.setStatus(status);
        return movie;
    }

    private static ShowtimeSeed showtime(String movieTitle, String showDate, String showTime) {
        return new ShowtimeSeed(movieTitle, LocalDate.parse(showDate), LocalTime.parse(showTime));
    }

    private record ShowtimeSeed(String movieTitle, LocalDate showDate, LocalTime showTime) {}
}
