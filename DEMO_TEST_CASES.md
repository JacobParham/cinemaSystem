# Sprint 3 Demo Test Cases

Checkout order summary, checkout email confirmation, payment processing, and their associated non-functional requirements are excluded from this demo.

## Demo prerequisites

1. Start MySQL with an empty `cinemaSystem` database.
2. Start the Spring Boot application.
3. Confirm the seed contains movies, showtimes, and these showrooms:
   - Showroom 1: 32 seats
   - Showroom 2: 24 seats
   - Showroom 3: 40 seats
4. Open the application in a private browser window so old session data does not affect the run.

## Required execution paths

| ID | Area | Input / Steps | Expected result |
| --- | --- | --- | --- |
| A1 | Add Movie - valid | Log in as admin. Add a movie with title, genre, and status. | Success message appears and the movie is immediately visible in the user movie list. |
| A2 | Add Movie - invalid | Submit Add Movie with title, genre, or status empty. | Submission is blocked and the required-field message is shown. No movie is stored. |
| S1 | Add Showtime - valid | Select a movie, a future date/time, and Showroom 1. | Showtime is stored, appears in the admin list, and appears on that movie's user page. |
| S2 | Showtime conflict | Repeat S1 with the same date, time, and showroom. | Request is rejected with a scheduling-conflict message. Only one showtime remains stored. |
| S3 | Showtime - invalid past date | Submit a date/time in the past. | Request is rejected with a past-showtime validation message. |
| S4 | Three-showroom data | Schedule or display one showtime in each showroom. | All three showroom names display correctly and can hold showtimes at the same clock time when the rooms differ. |
| U1 | Showtime visibility | Open the movie used in S1. | Its database-backed date, time, and showroom appear in chronological order. |
| U2 | Ticket types | Choose one adult, one child, and one senior ticket. | Ticket count is 3 and total is `$33.00`. |
| U3 | Seat-map capacity | Open showtimes in Showrooms 1, 2, and 3. | Seat maps display 32, 24, and 40 seats respectively. |
| U4 | Booked-seat prevention | Open the first seeded showtime and inspect seats A1 and A2. | A1 and A2 are gray/unavailable and cannot be selected. |
| U5 | Valid seat selection | Choose two tickets and two available seats. | Both seats are selected and the selected-seat summary lists both. |
| U6 | Too few seats | Choose two tickets, select one seat, and click Proceed to Checkout. | Navigation is blocked and the UI requests exactly two seats. |
| U7 | Too many seats | Choose one ticket and attempt to select a second seat. | The second selection is blocked. |
| U8 | Zero tickets | Attempt to select a seat or continue with zero tickets. | Seat selection/continuation is blocked with a helpful error. |
| U9 | Session reservation | Choose matching tickets and seats, then click Proceed to Checkout while logged out. Log in successfully. | The same selected seats remain associated with the booking session and the flow resumes. |

## Continuous integration demo

Run this path without restarting the application:

1. Admin adds a movie.
2. Confirm the movie appears in the user portal.
3. Admin adds a future showtime for that movie.
4. Attempt the same showroom/date/time again and show the conflict error.
5. Return to the user portal and open the new movie/showtime.
6. Select ticket quantities and matching available seats.
7. Demonstrate that a booked seat is unavailable and that a seat/ticket mismatch is rejected.

