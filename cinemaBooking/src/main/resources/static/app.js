// Sprint 1 integration note:
// Replace the hardcoded `movies` array below with fetch() calls to the backend.
// Use #moviesLoading and #moviesError while loading. See README.md for API details.

let movies = [];
let favoriteMovieIds = [];
let savedCards = [];

const SESSION_USER_KEY = "cinemaWorldCurrentUser";
let currentUser = readCurrentUser();

// Sprint 2 teammate TODOs:
// TODO: Replace demo login/roles with backend authentication validation. The login response needs
//       a stable role field (for example, `role: "CUSTOMER" | "ADMIN"`) plus a server session/token.
// TODO: Connect registration and registration confirmation email endpoints.
// TODO: Connect forgot/reset password email and token endpoints; hash passwords on the backend.
// TODO: Connect profile edits to database saving and send email notifications after profile changes.
// TODO: Connect favorite movies to the backend instead of keeping them in browser memory.
// TODO: Move address/payment card limits to backend validation and implement card encryption.
// TODO: Complete all endpoint connections and full admin pages (movies, promotions, users, showtimes).
// TODO: Add final server-side security/access-control hardening; these client checks are demo UX only.

const sprintTwoEndpoints = {
    register: "/register",
    login: "/login",
    forgotPassword: "/forgot-password",
    resetPassword: "/reset-password",
    profile: "/profile",
    changePassword: "/change-password",
    favorites: "/favorites",
    addressLookup: "/address-lookup",
    logout: "/logout",
    cards: "/profile/cards",
    removeCard: "/profile/cards/remove"
};

const demoAddressMatches = [
    "233 Broad St, Athens, GA 30601",
    "233 E Broad St, Athens, GA 30601",
    "233 W Broad St, Athens, GA 30601",
    "233 Broad Ave, Atlanta, GA 30312",
    "233 Broad River Rd, Columbia, SC 29210",
    "233 Broadway, New York, NY 10279"
];

async function loadMovies() {
    const response = await fetch("/movies");
    if (!response.ok) {
        throw new Error("Failed to load movies: " + response.status);
    }
    const data = await response.json();

    // Load showtimes for each movie
    const moviesWithShowtimes = await Promise.all(data.map(async function (movie) {
        if (movie.status === "Coming Soon") {
            return { ...movie, showtimes: [{ time: "Coming Soon", showtimeId: null }] };
        }
        
        try {
            const showtimeResponse = await fetch("/showtimes/movie/" + movie.movieId);
            if (showtimeResponse.ok) {
                const showtimes = await showtimeResponse.json();
                const formattedShowtimes = showtimes.map(function (st) {
                    const timeStr = formatShowTime(st.showTime);
                    return { 
                        time: timeStr || "TBD", 
                        showtimeId: st.showtimeId 
                    };
                });
                return { ...movie, showtimes: formattedShowtimes };
            }
        } catch (error) {
            console.error("Failed to load showtimes for movie:", movie.movieId, error);
        }
        
        // Fallback to hardcoded times if showtimes fail to load
        return { 
            ...movie, 
            showtimes: [
                { time: "2:00 PM", showtimeId: null },
                { time: "5:00 PM", showtimeId: null },
                { time: "8:00 PM", showtimeId: null }
            ]
        };
    }));

    movies = moviesWithShowtimes;
}

const homePage = document.querySelector("#homePage");
const movieDetailsPage = document.querySelector("#movieDetailsPage");
const bookingPage = document.querySelector("#bookingPage");
const accountPages = document.querySelectorAll(".account-page");
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
const loginForm = document.querySelector("#loginForm");
const registerForm = document.querySelector("#registerForm");
const forgotPasswordForm = document.querySelector("#forgotPasswordForm");
const resetPasswordForm = document.querySelector("#resetPasswordForm");
const forgotStep1 = document.querySelector("#forgotStep1");
const forgotStep2 = document.querySelector("#forgotStep2");
const profileForm = document.querySelector("#profileForm");
const loginMessage = document.querySelector("#loginMessage");
const registerMessage = document.querySelector("#registerMessage");
const forgotPasswordMessage = document.querySelector("#forgotPasswordMessage");
const resetPasswordMessage = document.querySelector("#resetPasswordMessage");
const profileMessage = document.querySelector("#profileMessage");
const resetToken = document.querySelector("#resetToken");
const resetNewPassword = document.querySelector("#resetNewPassword");
const resetConfirmNewPassword = document.querySelector("#resetConfirmNewPassword");
const addCardButton = document.querySelector("#addCardButton");
const saveCardButton = document.querySelector("#saveCardButton");
const cancelCardButton = document.querySelector("#cancelCardButton");
const cardFields = document.querySelector("#cardFields");
const cardName = document.querySelector("#cardName");
const cardNumber = document.querySelector("#cardNumber");
const cardExpiration = document.querySelector("#cardExpiration");
const cardCvv = document.querySelector("#cardCvv");
const cardZip = document.querySelector("#cardZip");
const cardMessage = document.querySelector("#cardMessage");
const currentPassword = document.querySelector("#currentPassword");
const newPassword = document.querySelector("#newPassword");
const confirmNewPassword = document.querySelector("#confirmNewPassword");
const changePasswordButton = document.querySelector("#changePasswordButton");
const changePasswordMessage = document.querySelector("#changePasswordMessage");
const paymentCardList = document.querySelector("#paymentCardList");
const favoriteMovieList = document.querySelector("#favoriteMovieList");
const profileAddress = document.querySelector("#profileAddress");
const addressSuggestions = document.querySelector("#addressSuggestions");
const logoutButton = document.querySelector("#logoutButton");
const roleNavigationItems = document.querySelectorAll("[data-nav-audience]");

let currentMovieTitle = "";
let currentMovieId = 0;
let currentShowtimeId = 0;
let currentSessionId = "";
let deletingExpirationSlash = false;
let deletingCardNumberSpace = false;
let selectedSeats = [];
let bookedSeats = [];
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

function setFormMessage(element, message, type) {
    element.textContent = message;
    element.className = "form-message form-message--" + type;
}

function readCurrentUser() {
    try {
        const storedUser = sessionStorage.getItem(SESSION_USER_KEY);
        return storedUser ? JSON.parse(storedUser) : null;
    } catch (error) {
        sessionStorage.removeItem(SESSION_USER_KEY);
        return null;
    }
}

function getCurrentAudience() {
    if (!currentUser) {
        return "public";
    }
    return currentUser.role === "ADMIN" ? "admin" : "customer";
}

function renderNavigation() {
    const audience = getCurrentAudience();
    roleNavigationItems.forEach(function (item) {
        const allowedAudiences = item.dataset.navAudience.split(",");
        item.hidden = !allowedAudiences.includes(audience);
    });
}

function isPageAllowed(pageId) {
    if (pageId === "adminPage") {
        return Boolean(currentUser && currentUser.role === "ADMIN");
    }
    if (pageId === "profilePage" || pageId === "bookingPage") {
        return Boolean(currentUser && currentUser.role === "CUSTOMER");
    }
    return true;
}

function handleUnauthorizedPage(pageId) {
    if (currentUser) {
        showHomePage();
        return;
    }
    showPage("loginPage");
    setFormMessage(loginMessage, "Please log in to view that page.", "error");
}

function validateRequiredFields(form, messageElement) {
    const emptyRequiredField = Array.from(form.querySelectorAll("[required]")).find(function (field) {
        return !field.value.trim();
    });

    if (emptyRequiredField) {
        setFormMessage(messageElement, "Please complete all required fields.", "error");
        emptyRequiredField.focus();
        return false;
    }

    if (!form.checkValidity()) {
        setFormMessage(messageElement, "Please correct the highlighted field.", "error");
        form.reportValidity();
        return false;
    }

    return true;
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
    const isFavorite = favoriteMovieIds.includes(movie.movieId);
    const favoriteClass = isFavorite ? "favorite-button favorite-button--active" : "favorite-button";
    const favoriteLabel = isFavorite ? "Remove from favorites" : "Add to favorites";
    const heartIcon = `
        <svg class="favorite-icon" viewBox="0 0 100 90" aria-hidden="true">
            <path d="M50 82 C20 56 8 39 8 23 C8 10 18 3 30 3 C39 3 46 8 50 18 C54 8 61 3 70 3 C82 3 92 10 92 23 C92 39 80 56 50 82 Z"></path>
        </svg>
    `;

    return `
        <article class="movie-card">
            <img src="${movie.posterUrl}" alt="${movie.title} poster">
            <div class="movie-info">
                <p class="movie-meta">${movie.genre} | ${movie.rating}</p>
                <h3>${movie.title}</h3>
                <div class="movie-actions">
                    <button class="details-button" type="button" data-title="${movie.title}">View Details</button>
                    <button class="${favoriteClass}" type="button" data-movie-id="${movie.movieId}" title="${favoriteLabel}" aria-label="${favoriteLabel}">${heartIcon}</button>
                </div>
            </div>
        </article>
    `;
}

function createFavoriteMovieCard(movie) {
    const favoriteLabel = "Remove from favorites";
    const heartIcon = `
        <svg class="favorite-icon" viewBox="0 0 100 90" aria-hidden="true">
            <path d="M50 82 C20 56 8 39 8 23 C8 10 18 3 30 3 C39 3 46 8 50 18 C54 8 61 3 70 3 C82 3 92 10 92 23 C92 39 80 56 50 82 Z"></path>
        </svg>
    `;

    return `
        <article class="favorite-card">
            <button class="favorite-poster-button" type="button" data-title="${movie.title}" title="${movie.title}" aria-label="View details for ${movie.title}">
                <img src="${movie.posterUrl}" alt="${movie.title} poster">
            </button>
            <button class="favorite-button favorite-button--active" type="button" data-movie-id="${movie.movieId}" title="${favoriteLabel}" aria-label="${favoriteLabel}">${heartIcon}</button>
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
    connectFavoriteButtons();
}

function connectDetailsButtons() {
    const detailsButtons = document.querySelectorAll(".details-button");

    detailsButtons.forEach(function (button) {
        button.onclick = function () {
            showMovieDetails(button.dataset.title);
        };
    });
}

function connectFavoriteButtons() {
    const favoriteButtons = document.querySelectorAll(".favorite-button");

    favoriteButtons.forEach(function (button) {
        button.onclick = function () {
            toggleFavorite(Number(button.dataset.movieId));
        };
    });
}

async function toggleFavorite(movieId) {
    const removing = favoriteMovieIds.includes(movieId);
    try {
        const resp = await fetch("/favorites/" + movieId, {
            method: removing ? "DELETE" : "POST"
        });
        if (!resp.ok) return;
    } catch (e) {
        return;
    }
    if (removing) {
        favoriteMovieIds = favoriteMovieIds.filter(function (id) { return id !== movieId; });
    } else {
        favoriteMovieIds.push(movieId);
    }
    renderMovies();
    renderFavoriteMovies();
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

    showtimeButtons.innerHTML = selectedMovie.showtimes.map(function (showtimeObj) {
        const showtime = showtimeObj.time;
        const showtimeId = showtimeObj.showtimeId;
        const isBookable = isBookableShowtime(showtime);
        const buttonClass = isBookable ? "showtime-button" : "showtime-button showtime-button--disabled";
        const disabledAttr = isBookable ? "" : " disabled";

        return `<button class="${buttonClass}" type="button" data-showtime="${showtime}" data-showtime-id="${showtimeId}"${disabledAttr}>${showtime}</button>`;
    }).join("");

    connectShowtimeButtons(selectedMovie);

    homePage.style.display = "none";
    movieDetailsPage.style.display = "block";
    bookingPage.style.display = "none";
    accountPages.forEach(function (page) {
        page.style.display = "none";
    });
    window.scrollTo(0, 0);
}

function connectShowtimeButtons(movie) {
    const buttons = document.querySelectorAll(".showtime-button");

    buttons.forEach(function (button) {
        button.addEventListener("click", function () {
            if (!isBookableShowtime(button.dataset.showtime)) {
                return;
            }

            showBookingPage(movie.title, movie.movieId, button.dataset.showtime, button.dataset.showtimeId);
        });
    });
}

function showBookingPage(movieTitle, movieId, showtime, showtimeId) {
    if (!isPageAllowed("bookingPage")) {
        handleUnauthorizedPage("bookingPage");
        return;
    }

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
    currentMovieId = movieId;
    currentShowtimeId = showtimeId;
    
    // Generate a new session ID for this booking session
    currentSessionId = generateSessionId();
    
    resetBookingForm();
    createSeatLayout();
    loadBookedSeats(showtimeId);

    homePage.style.display = "none";
    movieDetailsPage.style.display = "none";
    bookingPage.style.display = "block";
    accountPages.forEach(function (page) {
        page.style.display = "none";
    });
    detailsTrailer.src = "";
    window.scrollTo(0, 0);
}

function generateSessionId() {
    return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
}

function formatShowTime(showTime) {
    if (!showTime) return "";
    
    // If it's already a string in the format like "20:00:00", parse it
    let hours, minutes;
    
    if (typeof showTime === 'string') {
        const parts = showTime.split(':');
        hours = parseInt(parts[0], 10);
        minutes = parseInt(parts[1], 10);
    } else if (showTime.hour !== undefined && showTime.minute !== undefined) {
        // If it's an object with hour/minute properties
        hours = showTime.hour;
        minutes = showTime.minute;
    } else {
        return showTime.toString();
    }
    
    // Convert to 12-hour format
    const period = hours >= 12 ? 'PM' : 'AM';
    let displayHours = hours % 12;
    if (displayHours === 0) displayHours = 12;
    
    const displayMinutes = minutes.toString().padStart(2, '0');
    return `${displayHours}:${displayMinutes} ${period}`;
}

function resetBookingForm() {
    ticketCounts = {
        adult: 0,
        child: 0,
        senior: 0
    };
    selectedSeats = [];
    bookedSeats = [];
    clearBookingFeedback();
    updateBookingSummary();
}

async function loadBookedSeats(showtimeId) {
    if (!showtimeId) {
        bookedSeats = [];
        return;
    }

    try {
        const response = await fetch("/bookings/seats/" + showtimeId);
        if (!response.ok) {
            console.error("Failed to load booked seats:", response.status);
            bookedSeats = [];
            return;
        }
        const data = await response.json();
        bookedSeats = data.bookedSeats || [];
        markBookedSeats();
    } catch (error) {
        console.error("Error loading booked seats:", error);
        bookedSeats = [];
    }
}

function markBookedSeats() {
    const seatButtons = document.querySelectorAll(".seat-button");
    
    seatButtons.forEach(function (button) {
        const seatName = button.dataset.seat;
        if (bookedSeats.includes(seatName)) {
            button.classList.add("booked-seat");
            button.disabled = true;
            button.classList.add("seat-button--disabled");
        } else {
            button.classList.remove("booked-seat");
        }
    });
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

    // Prevent selecting already booked seats
    if (bookedSeats.includes(seatName)) {
        showBookingFeedback("This seat is already booked.", "error");
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
    
    // Lock seats when the user has selected the right number
    const totalTickets = getTotalTickets();
    if (selectedSeats.length === totalTickets && totalTickets > 0) {
        lockSelectedSeats();
    }
}

async function lockSelectedSeats() {
    if (!currentShowtimeId || selectedSeats.length === 0) {
        return;
    }

    try {
        const response = await fetch("/bookings/lock-seats", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                showtimeId: currentShowtimeId,
                seatNumbers: selectedSeats.join(","),
                sessionId: currentSessionId
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            showBookingFeedback(errorData.message || "Failed to lock seats", "error");
            // Refresh booked seats if locking failed
            loadBookedSeats(currentShowtimeId);
        }
    } catch (error) {
        console.error("Error locking seats:", error);
        showBookingFeedback("Failed to lock seats. Please try again.", "error");
    }
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
    accountPages.forEach(function (page) {
        page.style.display = "none";
    });
    homePage.style.display = "block";
    window.scrollTo(0, 0);
}

function showHomeSection(sectionId) {
    showHomePage();

    if (!sectionId) {
        return;
    }

    const section = document.querySelector(sectionId);
    if (section) {
        section.scrollIntoView();
    }
}

function showPage(pageId) {
    if (!isPageAllowed(pageId)) {
        handleUnauthorizedPage(pageId);
        return;
    }

    detailsTrailer.src = "";
    homePage.style.display = pageId === "homePage" ? "block" : "none";
    movieDetailsPage.style.display = "none";
    bookingPage.style.display = "none";

    accountPages.forEach(function (page) {
        page.style.display = page.id === pageId ? "block" : "none";
    });

    if (pageId === "profilePage") {
        loadProfile();
    }

    if (pageId === "resetPasswordPage") {
        forgotStep1.hidden = false;
        forgotStep2.hidden = true;
    }

    window.scrollTo(0, 0);
}

async function loadProfile() {
    try {
        const response = await fetch(sprintTwoEndpoints.profile);
        if (!response.ok) {
            if (response.status === 401) {
                showPage("loginPage");
                setFormMessage(loginMessage, "Please log in to view your profile.", "error");
            }
            return;
        }
        const data = await response.json();
        document.querySelector("#profileFirstName").value = data.firstName || "";
        document.querySelector("#profileLastName").value = data.lastName || "";
        document.querySelector("#profileEmail").value = data.email || "";
        profileAddress.value = data.address || "";
        document.querySelector("#registerPromotions") && (document.querySelector("#profilePromotions") || null);
        if (document.querySelector("#profilePromotions")) {
            document.querySelector("#profilePromotions").checked = data.promotions || false;
        }

        // Store cards from backend and render
        savedCards = (data.cards || []).map(function (c) {
            return { cardId: c.cardId, label: "Card ending in " + c.last4, last4: c.last4 };
        });

        // Load favorites from backend
        try {
            const favResp = await fetch("/favorites");
            if (favResp.ok) {
                const favData = await favResp.json();
                favoriteMovieIds = favData.movieIds || [];
                renderMovies();
            }
        } catch (e) { /* leave favoriteMovieIds as-is */ }

        renderPaymentCards();
        renderFavoriteMovies();
    } catch (error) {
        setFormMessage(profileMessage, "Could not load profile data.", "error");
    }
}

function renderPaymentCards() {
    addCardButton.disabled = savedCards.length >= 3;
    paymentCardList.innerHTML = savedCards.map(function (card, index) {
        const label = card.label || card;
        const cardId = card.cardId;
        return `
            <div class="simple-list-row">
                <span>${label}</span>
                <button type="button" data-card-index="${index}" data-card-id="${cardId || ""}">Remove</button>
            </div>
        `;
    }).join("");

    document.querySelectorAll("[data-card-index]").forEach(function (button) {
        button.addEventListener("click", async function () {
            const cardId = button.dataset.cardId;
            if (cardId) {
                try {
                    const resp = await fetch(sprintTwoEndpoints.cards + "/" + cardId, {
                        method: "DELETE"
                    });
                    const data = await resp.json();
                    if (!resp.ok) {
                        setFormMessage(profileMessage, data.message || "Could not remove card.", "error");
                        return;
                    }
                } catch (e) {
                    setFormMessage(profileMessage, "Could not remove card.", "error");
                    return;
                }
            }
            savedCards.splice(Number(button.dataset.cardIndex), 1);
            renderPaymentCards();
            hideCardFields();
            setFormMessage(profileMessage, "Card removed.", "success");
        });
    });
}

function renderFavoriteMovies() {
    const favorites = movies.filter(function (movie) {
        return favoriteMovieIds.includes(movie.movieId);
    });

    if (favorites.length === 0) {
        favoriteMovieList.innerHTML = "<p>No favorite movies yet.</p>";
        return;
    }

    favoriteMovieList.innerHTML = favorites.map(createFavoriteMovieCard).join("");
    document.querySelectorAll(".favorite-poster-button").forEach(function (button) {
        button.onclick = function () {
            showMovieDetails(button.dataset.title);
        };
    });
    connectFavoriteButtons();
}

function renderAddressSuggestions() {
    const searchText = profileAddress.value.toLowerCase();
    if (searchText.length < 5) {
        addressSuggestions.innerHTML = "";
        return;
    }

    // Please insert endpoints
    const matches = demoAddressMatches.filter(function (address) {
        return address.toLowerCase().includes(searchText);
    });

    addressSuggestions.innerHTML = matches.map(function (address) {
        return `<option value="${address}"></option>`;
    }).join("");
}

function addDemoCard() {
    if (savedCards.length >= 3) {
        setFormMessage(profileMessage, "You can only store up to 3 payment cards.", "error");
        return;
    }

    cardFields.hidden = false;
    cardName.focus();
    setFormMessage(profileMessage, "", "success");
}

function hideCardFields() {
    cardFields.hidden = true;
    cardName.value = "";
    cardNumber.value = "";
    cardExpiration.value = "";
    cardCvv.value = "";
    cardZip.value = "";
    setFormMessage(cardMessage, "", "success");
}

async function saveCard() {
    const cardDigits = cardNumber.value.replace(/\D/g, "");
    const cvvDigits = cardCvv.value.replace(/\D/g, "");
    const expirationError = getCardExpirationError();

    if (!cardName.value || !cardNumber.value || !cardExpiration.value || !cardCvv.value || !cardZip.value) {
        setFormMessage(cardMessage, "Please complete all card fields.", "error");
        return;
    }

    if (cardDigits.length < 13 || cardDigits.length > 19) {
        setFormMessage(cardMessage, "Please enter a valid card number.", "error");
        return;
    }

    if (cvvDigits.length < 3 || cvvDigits.length > 4) {
        setFormMessage(cardMessage, "Please enter a valid CVV.", "error");
        return;
    }

    if (expirationError) {
        setFormMessage(cardMessage, expirationError, "error");
        return;
    }

    if (savedCards.length >= 3) {
        setFormMessage(cardMessage, "You can only store up to 3 payment cards.", "error");
        return;
    }

    try {
        const response = await fetch(sprintTwoEndpoints.cards, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                cardHolder: cardName.value.trim(),
                cardNumber: cardDigits,
                expiration: cardExpiration.value.trim(),
                cvv: cvvDigits
            })
        });
        const data = await response.json();
        if (!response.ok) {
            setFormMessage(cardMessage, data.message || "Could not save card.", "error");
            return;
        }
        savedCards.push({ cardId: data.cardId, label: "Card ending in " + data.last4, last4: data.last4 });
        hideCardFields();
        renderPaymentCards();
        setFormMessage(profileMessage, "Card added successfully.", "success");
    } catch (error) {
        setFormMessage(cardMessage, "Could not save card. Please try again.", "error");
    }
}

function formatCardNumber() {
    let digits = cardNumber.value.replace(/\D/g, "").slice(0, 19);

    if (deletingCardNumberSpace && digits.length > 0 && digits.length % 4 === 0) {
        digits = digits.slice(0, -1);
    }

    cardNumber.value = digits.replace(/(.{4})/g, "$1 ").trim();
    deletingCardNumberSpace = false;
}

function checkCardNumberBackspace(event) {
    deletingCardNumberSpace = event.key === "Backspace" &&
        cardNumber.selectionStart === cardNumber.selectionEnd &&
        cardNumber.selectionStart > 0 &&
        cardNumber.value.charAt(cardNumber.selectionStart - 1) === " ";
}

function formatCardCvv() {
    cardCvv.value = cardCvv.value.replace(/\D/g, "").slice(0, 4);
}

function formatCardExpiration() {
    const digits = cardExpiration.value.replace(/\D/g, "").slice(0, 4);

    if (deletingExpirationSlash && digits.length === 2) {
        cardExpiration.value = digits.slice(0, 1);
    } else if (digits.length === 2) {
        cardExpiration.value = digits + "/";
    } else if (digits.length > 2) {
        cardExpiration.value = digits.slice(0, 2) + "/" + digits.slice(2);
    } else {
        cardExpiration.value = digits;
    }

    deletingExpirationSlash = false;
}

function getCardExpirationError() {
    const digits = cardExpiration.value.replace(/\D/g, "");

    if (digits.length !== 4) {
        return "Please enter expiration as MM/YY.";
    }

    const month = Number(digits.slice(0, 2));
    const year = Number("20" + digits.slice(2));
    const today = new Date();
    const currentMonth = today.getMonth() + 1;
    const currentYear = today.getFullYear();

    if (month < 1 || month > 12) {
        return "Expiration month must be between 01 and 12.";
    }

    if (year < currentYear || (year === currentYear && month < currentMonth)) {
        return "This card is expired.";
    }

    return "";
}

function checkExpirationBackspace(event) {
    deletingExpirationSlash = event.key === "Backspace" &&
        cardExpiration.selectionStart === 3 &&
        cardExpiration.selectionEnd === 3 &&
        cardExpiration.value.endsWith("/");
}

async function handleLogin(event) {
    event.preventDefault();
    if (!validateRequiredFields(loginForm, loginMessage)) {
        return;
    }

    const email = loginForm.email.value.trim().toLowerCase();
    const password = loginForm.password.value;

    try {
        const response = await fetch(sprintTwoEndpoints.login, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email, password: password })
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.message || "Invalid credentials");
        }

        currentUser = { email: data.email, role: data.role };
        sessionStorage.setItem(SESSION_USER_KEY, JSON.stringify(currentUser));
        renderNavigation();
        loginForm.reset();

        if (data.role === "ADMIN") {
            showPage("adminPage");
        } else {
            showHomePage();
        }
    } catch (error) {
        setFormMessage(loginMessage, error.message, "error");
    }
}

async function handleRegister(event) {
    event.preventDefault();

    if (!validateRequiredFields(registerForm, registerMessage)) {
        return;
    }

    if (registerForm.password.value !== registerForm.confirmPassword.value) {
        setFormMessage(registerMessage, "Passwords must match.", "error");
        return;
    }

    try {
        const response = await fetch(sprintTwoEndpoints.register, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                firstName: registerForm.firstName.value.trim(),
                lastName: registerForm.lastName.value.trim(),
                email: registerForm.email.value.trim().toLowerCase(),
                password: registerForm.password.value,
                promotions: registerForm.promotions.checked
            })
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.message || "Unable to create account.");
        }

        setFormMessage(registerMessage, data.message || "Account created successfully.", "success");
        registerForm.reset();
    } catch (error) {
        setFormMessage(registerMessage, error.message, "error");
    }
}

async function handleForgotPassword(event) {
    event.preventDefault();
    if (!validateRequiredFields(forgotPasswordForm, forgotPasswordMessage)) {
        return;
    }

    const email = forgotPasswordForm.querySelector("input[name='email']").value.trim().toLowerCase();

    try {
        const response = await fetch(sprintTwoEndpoints.forgotPassword, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email })
        });
        const data = await response.json();
        setFormMessage(forgotPasswordMessage, data.message || "If that email is registered, a reset link has been sent.", "success");
        // Advance to step 2 so user can enter their token
        forgotStep1.hidden = true;
        forgotStep2.hidden = false;
    } catch (error) {
        setFormMessage(forgotPasswordMessage, "Could not send reset email. Please try again.", "error");
    }
}

async function handleResetPassword(event) {
    event.preventDefault();
    if (!validateRequiredFields(resetPasswordForm, resetPasswordMessage)) {
        return;
    }

    const tokenValue = resetToken.value.trim();
    const passwordValue = resetNewPassword.value;
    const confirmValue = resetConfirmNewPassword.value;

    if (passwordValue !== confirmValue) {
        setFormMessage(resetPasswordMessage, "New passwords must match.", "error");
        resetConfirmNewPassword.focus();
        return;
    }

    if (passwordValue.length < 8) {
        setFormMessage(resetPasswordMessage, "New password must be at least 8 characters.", "error");
        resetNewPassword.focus();
        return;
    }

    try {
        const response = await fetch(sprintTwoEndpoints.resetPassword, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ token: tokenValue, newPassword: passwordValue })
        });
        const data = await response.json();
        if (!response.ok) {
            setFormMessage(resetPasswordMessage, data.message || "Unable to reset password.", "error");
            return;
        }
        setFormMessage(resetPasswordMessage, data.message || "Password reset successfully. You can now log in.", "success");
        resetPasswordForm.reset();
        forgotPasswordForm.reset();
        // Return to step 1 for next time
        forgotStep1.hidden = false;
        forgotStep2.hidden = true;
    } catch (error) {
        setFormMessage(resetPasswordMessage, "Could not reset password. Please try again.", "error");
    }
}

async function handleProfileSave(event) {
    event.preventDefault();
    if (!validateRequiredFields(profileForm, profileMessage)) {
        return;
    }

    try {
        const response = await fetch(sprintTwoEndpoints.profile, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                firstName: document.querySelector("#profileFirstName").value.trim(),
                lastName: document.querySelector("#profileLastName").value.trim(),
                address: profileAddress.value.trim(),
                promotions: document.querySelector("#profilePromotions") ?
                    document.querySelector("#profilePromotions").checked : undefined
            })
        });
        const data = await response.json();
        if (!response.ok) {
            setFormMessage(profileMessage, data.message || "Could not save profile.", "error");
            return;
        }
        setFormMessage(profileMessage, data.message || "Profile saved successfully.", "success");
    } catch (error) {
        setFormMessage(profileMessage, "Could not save profile. Please try again.", "error");
    }
}

async function handleChangePassword() {
    if (!currentPassword.value || !newPassword.value || !confirmNewPassword.value) {
        setFormMessage(changePasswordMessage, "Please complete all password fields.", "error");
        return;
    }

    if (newPassword.value !== confirmNewPassword.value) {
        setFormMessage(changePasswordMessage, "New passwords must match.", "error");
        return;
    }

    if (currentPassword.value === newPassword.value) {
        setFormMessage(changePasswordMessage, "New password must be different from current password.", "error");
        return;
    }

    try {
        const response = await fetch(sprintTwoEndpoints.changePassword, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                currentPassword: currentPassword.value,
                newPassword: newPassword.value
            })
        });
        const data = await response.json();
        if (!response.ok) {
            setFormMessage(changePasswordMessage, data.message || "Could not change password.", "error");
            return;
        }
        setFormMessage(changePasswordMessage, data.message || "Password changed successfully.", "success");
        currentPassword.value = "";
        newPassword.value = "";
        confirmNewPassword.value = "";
    } catch (error) {
        setFormMessage(changePasswordMessage, "Could not change password. Please try again.", "error");
    }
}

async function handleLogout() {
    try {
        await fetch(sprintTwoEndpoints.logout, { method: "POST" });
    } catch (e) {
        // proceed with local cleanup even if server call fails
    }
    currentUser = null;
    sessionStorage.removeItem(SESSION_USER_KEY);
    localStorage.removeItem(SESSION_USER_KEY);
    sessionStorage.removeItem("authToken");
    localStorage.removeItem("authToken");
    renderNavigation();
    showPage("loginPage");
    setFormMessage(loginMessage, "You have been logged out.", "success");
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
addCardButton.addEventListener("click", addDemoCard);
saveCardButton.addEventListener("click", saveCard);
cancelCardButton.addEventListener("click", hideCardFields);
cardNumber.addEventListener("keydown", checkCardNumberBackspace);
cardNumber.addEventListener("input", formatCardNumber);
cardCvv.addEventListener("input", formatCardCvv);
cardExpiration.addEventListener("keydown", checkExpirationBackspace);
cardExpiration.addEventListener("input", formatCardExpiration);
profileAddress.addEventListener("input", renderAddressSuggestions);
changePasswordButton.addEventListener("click", handleChangePassword);
loginForm.addEventListener("submit", handleLogin);
registerForm.addEventListener("submit", handleRegister);
forgotPasswordForm.addEventListener("submit", handleForgotPassword);
resetPasswordForm.addEventListener("submit", handleResetPassword);
profileForm.addEventListener("submit", handleProfileSave);
logoutButton.addEventListener("click", handleLogout);

document.querySelectorAll("[data-page]").forEach(function (button) {
    button.addEventListener("click", function (event) {
        event.preventDefault();
        showPage(button.dataset.page);
    });
});

document.querySelectorAll('.nav-links a[href^="#"]').forEach(function (link) {
    link.addEventListener("click", function (event) {
        event.preventDefault();
        showHomeSection(link.getAttribute("href"));
    });
});

document.querySelectorAll(".counter-button").forEach(function (button) {
    button.addEventListener("click", function () {
        changeTicketCount(button.dataset.ticket, Number(button.dataset.change));
    });
});

async function init() {
    renderNavigation();
    try {
        await loadMovies();
        loadGenres();
        renderMovies();
    } catch (error) {
        document.querySelector("#moviesError").hidden = false;
    }
}

init();
