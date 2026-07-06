// Sprint 1 integration note:
// Replace the hardcoded `movies` array below with fetch() calls to the backend.
// Use #moviesLoading and #moviesError while loading. See README.md for API details.

let movies = [];
let favoriteMovieIds = [];
let savedCards = ["Visa ending in 1111", "Mastercard ending in 2222", "Amex ending in 3333"];

// Please insert endpoints
const sprintTwoEndpoints = {
    register: "/register",
    login: "/login",
    resetPassword: "/password-reset",
    profile: "/profile",
    changePassword: "/change-password",
    favorites: "/favorites",
    addressLookup: "/address-lookup"
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

    movies = data.map(function (movie) {
        const showtimes = movie.status === "Coming Soon"
            ? ["Coming Soon"]
            : ["2:00 PM", "5:00 PM", "8:00 PM"];
        return { ...movie, showtimes: showtimes };
    });
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
const resetPasswordForm = document.querySelector("#resetPasswordForm");
const profileForm = document.querySelector("#profileForm");
const loginMessage = document.querySelector("#loginMessage");
const registerMessage = document.querySelector("#registerMessage");
const resetPasswordMessage = document.querySelector("#resetPasswordMessage");
const profileMessage = document.querySelector("#profileMessage");
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

let currentMovieTitle = "";
let deletingExpirationSlash = false;
let deletingCardNumberSpace = false;
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

function setFormMessage(element, message, type) {
    element.textContent = message;
    element.className = "form-message form-message--" + type;
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

function toggleFavorite(movieId) {
    if (favoriteMovieIds.includes(movieId)) {
        favoriteMovieIds = favoriteMovieIds.filter(function (favoriteId) {
            return favoriteId !== movieId;
        });
    } else {
        favoriteMovieIds.push(movieId);
    }

    // Please insert endpoints
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
    accountPages.forEach(function (page) {
        page.style.display = "none";
    });
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
    detailsTrailer.src = "";
    homePage.style.display = pageId === "homePage" ? "block" : "none";
    movieDetailsPage.style.display = "none";
    bookingPage.style.display = "none";

    accountPages.forEach(function (page) {
        page.style.display = page.id === pageId ? "block" : "none";
    });

    if (pageId === "profilePage") {
        renderPaymentCards();
        renderFavoriteMovies();
    }

    window.scrollTo(0, 0);
}

function renderPaymentCards() {
    addCardButton.disabled = savedCards.length >= 3;
    paymentCardList.innerHTML = savedCards.map(function (card, index) {
        return `
            <div class="simple-list-row">
                <span>${card}</span>
                <button type="button" data-card-index="${index}">Remove</button>
            </div>
        `;
    }).join("");

    document.querySelectorAll("[data-card-index]").forEach(function (button) {
        button.addEventListener("click", function () {
            savedCards.splice(Number(button.dataset.cardIndex), 1);
            renderPaymentCards();
            hideCardFields();
            setFormMessage(profileMessage, "Card removed. Save profile when ready.", "success");
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

function saveCard() {
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

    // Please insert endpoints
    savedCards.push("Card ending in " + cardDigits.slice(-4));
    hideCardFields();
    renderPaymentCards();
    setFormMessage(profileMessage, "Card added. Save profile when ready.", "success");
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

function handleLogin(event) {
    event.preventDefault();
    // Please insert endpoints
    setFormMessage(loginMessage, "endpoints not connected yet", "success");
}

function handleRegister(event) {
    event.preventDefault();

    if (registerForm.password.value !== registerForm.confirmPassword.value) {
        setFormMessage(registerMessage, "Passwords must match.", "error");
        return;
    }

    // Please insert endpoints
    setFormMessage(registerMessage, "endpoints not connected yet", "success");
}

function handleResetPassword(event) {
    event.preventDefault();
    // Please insert endpoints
    setFormMessage(resetPasswordMessage, "endpoints not connected yet", "success");
}

function handleProfileSave(event) {
    event.preventDefault();
    // Please insert endpoints
    setFormMessage(profileMessage, "endpoints not connected yet", "success");
}

function handleChangePassword() {
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

    // Please insert endpoints
    setFormMessage(changePasswordMessage, "endpoints not connected yet", "success");
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
resetPasswordForm.addEventListener("submit", handleResetPassword);
profileForm.addEventListener("submit", handleProfileSave);

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
    await loadMovies();
    loadGenres();
    renderMovies();
}

init();
