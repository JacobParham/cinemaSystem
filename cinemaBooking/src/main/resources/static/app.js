// Sprint 1 integration note:
// Replace the hardcoded `movies` array below with fetch() calls to the backend.
// Use #moviesLoading and #moviesError while loading. See README.md for API details.

const movies = [
    {
        title: "Backrooms",
        genre: "Horror",
        rating: "R",
        status: "Currently Running",
        posterUrl: "https://www.themoviedb.org/t/p/w1280/rhGx6E3qRNMgj3i5su2oukNHwIQ.jpg",
        trailerUrl: "https://www.youtube.com/embed/0HjdiohVOik?si=OBkqyQGIvwezePb4",
        showtimes: ["2:00 PM", "5:00 PM", "8:00 PM"],
        description: "A strange doorway appears in the basement of a furniture storeroom."
    },
    {
        title: "Toy Story 5",
        genre: "Animation",
        rating: "PG",
        status: "Currently Running",
        posterUrl: "https://www.themoviedb.org/t/p/w1280/a6H2U7pjibMia41TwyFVd1PVQw3.jpg",
        trailerUrl: "https://www.youtube.com/embed/c51ND9Hdbw0?si=XqRigDklBpu9hySn",
        showtimes: ["1:00 PM", "4:00 PM", "7:00 PM"],
        description: "Buzz, Woody, Jessie, and the gang face a new threat to playtime."
    },
    {
        title: "The Mandalorian and Grogu",
        genre: "Adventure",
        rating: "PG-13",
        status: "Currently Running",
        posterUrl: "https://www.themoviedb.org/t/p/w1280/5Vi8dSauVwH1HOsiZceDMbRr1Ca.jpg",
        trailerUrl: "https://www.youtube.com/embed/IHWlvwu8t1w?si=W2mi8ccvphrl3QQi",
        showtimes: ["3:30 PM", "6:30 PM", "9:30 PM"],
        description: "A legendary Mandalorian bounty hunter and Grogu help protect the New Republic."
    },
    {
        title: "Spider-Man: Brand New Day",
        genre: "Action",
        rating: "PG-13",
        status: "Coming Soon",
        posterUrl: "https://www.themoviedb.org/t/p/w1280/yyB2VJEW3an2xCdcYCPQhn9QERR.jpg",
        trailerUrl: "https://www.youtube.com/embed/62bIsvRcPv0?si=_2sG99NQ38WxV4mz",
        showtimes: ["Coming Soon"],
        description: "Peter Parker faces a shocking new threat while rebuilding his life as Spider-Man."
    },
    {
        title: "Moana",
        genre: "Adventure",
        rating: "PG",
        status: "Coming Soon",
        posterUrl: "https://www.themoviedb.org/t/p/w1280/zKVgiv5qHCvCLT4A2ymJi5QeXDH.jpg",
        trailerUrl: "https://www.youtube.com/embed/EEz5xbzYPKI?si=O1Lw03OqET8RvjrX",
        showtimes: ["Coming Soon"],
        description: "Moana answers the ocean's call and sails beyond the reef of Motunui."
    },
    {
        title: "The Odyssey",
        genre: "Fantasy",
        rating: "R",
        status: "Coming Soon",
        posterUrl: "https://www.themoviedb.org/t/p/w1280/krVa7rKCQb4OBfsr2LTJv4rTz5q.jpg",
        trailerUrl: "https://www.youtube.com/embed/f_bKjZeJBBI?si=5lUQS2RhBFI31cyQ",
        showtimes: ["Coming Soon"],
        description: "Odysseus begins a dangerous journey home after the Trojan War."
    }
];

const homePage = document.querySelector("#homePage");
const movieDetailsPage = document.querySelector("#movieDetailsPage");
const bookingPage = document.querySelector("#bookingPage");
const searchInput = document.querySelector("#searchInput");
const genreFilter = document.querySelector("#genreFilter");
const currentMovies = document.querySelector("#currentMovies");
const comingSoonMovies = document.querySelector("#comingSoonMovies");
const noResults = document.querySelector("#noResults");
const backToMoviesButton = document.querySelector("#backToMoviesButton");
const detailsTitle = document.querySelector("#detailsTitle");
const detailsMeta = document.querySelector("#detailsMeta");
const detailsTrailer = document.querySelector("#detailsTrailer");
const detailsDescription = document.querySelector("#detailsDescription");
const detailsGenre = document.querySelector("#detailsGenre");
const detailsRating = document.querySelector("#detailsRating");
const detailsPoster = document.querySelector("#detailsPoster");
const showtimeButtons = document.querySelector("#showtimeButtons");
const bookingMovieTitle = document.querySelector("#bookingMovieTitle");
const bookingShowtime = document.querySelector("#bookingShowtime");
const bookingPoster = document.querySelector("#bookingPoster");
const adultTickets = document.querySelector("#adultTickets");
const childTickets = document.querySelector("#childTickets");
const seniorTickets = document.querySelector("#seniorTickets");
const seatLayout = document.querySelector("#seatLayout");
const selectedSeatsText = document.querySelector("#selectedSeatsText");
const ticketTotal = document.querySelector("#ticketTotal");
const bookingFeedback = document.querySelector("#bookingFeedback");
const checkoutButton = document.querySelector("#checkoutButton");
const backToDetailsButton = document.querySelector("#backToDetailsButton");
const bookingBackToMoviesButton = document.querySelector("#bookingBackToMoviesButton");

let currentMovieTitle = "";
let selectedSeats = [];
let ticketCounts = {
    adult: 0,
    child: 0,
    senior: 0
};

const ticketPrices = {
    adult: 14,
    child: 9,
    senior: 10
};

function isBookableShowtime(showtime) {
    return showtime.toLowerCase() !== "coming soon";
}

function getTotalTickets() {
    return ticketCounts.adult + ticketCounts.child + ticketCounts.senior;
}

function clearBookingFeedback() {
    bookingFeedback.textContent = "";
    bookingFeedback.className = "booking-feedback";
}

function showBookingFeedback(message, type) {
    bookingFeedback.textContent = message;
    bookingFeedback.className = "booking-feedback booking-feedback--" + type;
}

function trimSelectedSeats() {
    const totalTickets = getTotalTickets();

    if (selectedSeats.length <= totalTickets) {
        return;
    }

    const seatsToRemove = selectedSeats.slice(totalTickets);
    selectedSeats = selectedSeats.slice(0, totalTickets);

    seatsToRemove.forEach(function (seatName) {
        const seatButton = document.querySelector('.seat-button[data-seat="' + seatName + '"]');
        if (seatButton) {
            seatButton.classList.remove("selected-seat");
        }
    });
}

function refreshSeatAvailability() {
    const totalTickets = getTotalTickets();
    const seatButtons = document.querySelectorAll(".seat-button");

    seatButtons.forEach(function (button) {
        const isSelected = button.classList.contains("selected-seat");
        const atSeatLimit = !isSelected && selectedSeats.length >= totalTickets && totalTickets > 0;

        button.disabled = totalTickets === 0 || atSeatLimit;
        button.classList.toggle("seat-button--disabled", button.disabled);
    });
}

function createMovieCard(movie) {
    return `
        <article class="movie-card">
            <img src="${movie.posterUrl}" alt="${movie.title} poster">
            <div class="movie-info">
                <p class="movie-meta">${movie.genre} | ${movie.rating}</p>
                <h3>${movie.title}</h3>
                <p>${movie.description}</p>
                <button class="details-button" type="button" data-title="${movie.title}">View Details</button>
            </div>
        </article>
    `;
}

function renderMovies() {
    const searchText = searchInput.value.toLowerCase();
    const selectedGenre = genreFilter.value;

    const filteredMovies = movies.filter(function (movie) {
        const matchesTitle = movie.title.toLowerCase().includes(searchText);
        const matchesGenre = selectedGenre === "All" || movie.genre === selectedGenre;

        return matchesTitle && matchesGenre;
    });

    const runningMovies = filteredMovies.filter(function (movie) {
        return movie.status === "Currently Running";
    });

    const soonMovies = filteredMovies.filter(function (movie) {
        return movie.status === "Coming Soon";
    });

    currentMovies.innerHTML = runningMovies.map(createMovieCard).join("");
    comingSoonMovies.innerHTML = soonMovies.map(createMovieCard).join("");

    noResults.style.display = filteredMovies.length === 0 ? "block" : "none";
    connectDetailsButtons();
}

function connectDetailsButtons() {
    const detailsButtons = document.querySelectorAll(".details-button");

    detailsButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            showMovieDetails(button.dataset.title);
        });
    });
}

function showMovieDetails(movieTitle) {
    const selectedMovie = movies.find(function (movie) {
        return movie.title === movieTitle;
    });

    if (!selectedMovie) {
        return;
    }

    detailsTitle.textContent = selectedMovie.title;
    detailsMeta.textContent = selectedMovie.genre + " | " + selectedMovie.rating + " | " + selectedMovie.status;
    detailsTrailer.src = selectedMovie.trailerUrl;
    detailsDescription.textContent = selectedMovie.description;
    detailsGenre.textContent = selectedMovie.genre;
    detailsRating.textContent = selectedMovie.rating;
    detailsPoster.src = selectedMovie.posterUrl;
    detailsPoster.alt = selectedMovie.title + " poster";
    currentMovieTitle = selectedMovie.title;

    showtimeButtons.innerHTML = selectedMovie.showtimes.map(function (showtime) {
        const isBookable = isBookableShowtime(showtime);
        const buttonClass = isBookable ? "showtime-button" : "showtime-button showtime-button--disabled";
        const disabledAttr = isBookable ? "" : " disabled";

        return `<button class="${buttonClass}" type="button" data-showtime="${showtime}"${disabledAttr}>${showtime}</button>`;
    }).join("");

    connectShowtimeButtons(selectedMovie);

    homePage.style.display = "none";
    movieDetailsPage.style.display = "block";
    bookingPage.style.display = "none";
    window.scrollTo(0, 0);
}

function connectShowtimeButtons(movie) {
    const buttons = document.querySelectorAll(".showtime-button");

    buttons.forEach(function (button) {
        button.addEventListener("click", function () {
            if (!isBookableShowtime(button.dataset.showtime)) {
                return;
            }

            showBookingPage(movie.title, button.dataset.showtime);
        });
    });
}

function showBookingPage(movieTitle, showtime) {
    const selectedMovie = movies.find(function (movie) {
        return movie.title === movieTitle;
    });

    if (!selectedMovie) {
        return;
    }

    bookingMovieTitle.textContent = movieTitle;
    bookingShowtime.textContent = showtime;
    bookingPoster.src = selectedMovie.posterUrl;
    bookingPoster.alt = movieTitle + " poster";
    currentMovieTitle = movieTitle;
    resetBookingForm();
    createSeatLayout();

    homePage.style.display = "none";
    movieDetailsPage.style.display = "none";
    bookingPage.style.display = "block";
    detailsTrailer.src = "";
    window.scrollTo(0, 0);
}

function resetBookingForm() {
    ticketCounts = {
        adult: 0,
        child: 0,
        senior: 0
    };
    selectedSeats = [];
    clearBookingFeedback();
    updateBookingSummary();
}

function changeTicketCount(ticketType, amount) {
    const newCount = ticketCounts[ticketType] + amount;
    ticketCounts[ticketType] = Math.max(0, newCount);
    trimSelectedSeats();
    clearBookingFeedback();
    updateBookingSummary();
}

function createSeatLayout() {
    const rows = ["A", "B", "C", "D"];
    const seatsPerRow = 8;
    let seatButtons = "";

    rows.forEach(function (row) {
        for (let seatNumber = 1; seatNumber <= seatsPerRow; seatNumber++) {
            const seatName = row + seatNumber;
            seatButtons += `<button class="seat-button" type="button" data-seat="${seatName}">${seatName}</button>`;
        }
    });

    seatLayout.innerHTML = seatButtons;
    connectSeatButtons();
}

function connectSeatButtons() {
    const seatButtons = document.querySelectorAll(".seat-button");

    seatButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            toggleSeat(button);
        });
    });
}

function toggleSeat(button) {
    const seatName = button.dataset.seat;
    const totalTickets = getTotalTickets();

    if (totalTickets === 0) {
        showBookingFeedback("Choose at least one ticket before selecting seats.", "error");
        return;
    }

    if (selectedSeats.includes(seatName)) {
        selectedSeats = selectedSeats.filter(function (seat) {
            return seat !== seatName;
        });
        button.classList.remove("selected-seat");
    } else if (selectedSeats.length >= totalTickets) {
        showBookingFeedback("You can only select as many seats as tickets.", "error");
        return;
    } else {
        selectedSeats.push(seatName);
        button.classList.add("selected-seat");
    }

    clearBookingFeedback();
    updateBookingSummary();
}

function updateBookingSummary() {
    adultTickets.textContent = ticketCounts.adult;
    childTickets.textContent = ticketCounts.child;
    seniorTickets.textContent = ticketCounts.senior;

    const total =
        ticketCounts.adult * ticketPrices.adult +
        ticketCounts.child * ticketPrices.child +
        ticketCounts.senior * ticketPrices.senior;

    selectedSeatsText.textContent = selectedSeats.length > 0 ? selectedSeats.join(", ") : "None";
    ticketTotal.textContent = "$" + total.toFixed(2);
    refreshSeatAvailability();
}

function handleCheckout() {
    const totalTickets = getTotalTickets();

    if (totalTickets === 0) {
        showBookingFeedback("Add at least one ticket before checkout.", "error");
        return;
    }

    if (selectedSeats.length !== totalTickets) {
        showBookingFeedback("Select exactly " + totalTickets + " seat(s) to match your tickets.", "error");
        return;
    }

    showBookingFeedback(
        "Prototype only: your selection is ready, but booking is not saved yet. Payment comes in a later sprint.",
        "success"
    );
}

function returnToCurrentMovie() {
    showMovieDetails(currentMovieTitle);
}

function showHomePage() {
    detailsTrailer.src = "";
    movieDetailsPage.style.display = "none";
    bookingPage.style.display = "none";
    homePage.style.display = "block";
    window.scrollTo(0, 0);
}

function loadGenres() {
    const genres = [];

    movies.forEach(function (movie) {
        if (!genres.includes(movie.genre)) {
            genres.push(movie.genre);
        }
    });

    genres.sort();

    genres.forEach(function (genre) {
        const option = document.createElement("option");
        option.value = genre;
        option.textContent = genre;
        genreFilter.appendChild(option);
    });
}

searchInput.addEventListener("input", renderMovies);
genreFilter.addEventListener("change", renderMovies);
backToMoviesButton.addEventListener("click", showHomePage);
backToDetailsButton.addEventListener("click", returnToCurrentMovie);
bookingBackToMoviesButton.addEventListener("click", showHomePage);
checkoutButton.addEventListener("click", handleCheckout);

document.querySelectorAll(".counter-button").forEach(function (button) {
    button.addEventListener("click", function () {
        changeTicketCount(button.dataset.ticket, Number(button.dataset.change));
    });
});

loadGenres();
renderMovies();
