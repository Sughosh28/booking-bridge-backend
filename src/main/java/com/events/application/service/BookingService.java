package com.events.application.service;

import com.events.application.jwt.JwtService;
import com.events.application.model.BookingEntity;
import com.events.application.model.BookingHistoryEntity;
import com.events.application.model.EventEntity;
import com.events.application.model.UserEntity;
import com.events.application.repository.BookingHistoryRepository;
import com.events.application.repository.BookingRepository;
import com.events.application.repository.EventRepository;
import com.events.application.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private BookingHistoryRepository bookingHistoryRepository;


    @Cacheable(value = "allBookings") // Caches the result
    public ResponseEntity<?> getAllBookings() {
        List<BookingEntity> bookings = bookingRepository.findAll();
        if (bookings.isEmpty()) {
            return new ResponseEntity<>("No bookings found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }


    @Cacheable(value = "eventBookings", key = "#eventId") // Cache by Event ID
    public ResponseEntity<?> getBookingsByEventId(Long eventId) {
        Optional<BookingEntity> bookings = bookingRepository.findByEventId(eventId);
        if (bookings.isEmpty()) {
            return new ResponseEntity<>("No bookings found for this event.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bookings.get(), HttpStatus.OK);
    }

    @CachePut(value = "allBookings") // Update cache when booking is added
    public ResponseEntity<?> bookTicket(Long event_id, BookingEntity bookingEntity, String authToken) throws MessagingException {
        String username;
        Long userId;
        userId = jwtService.extractUserId(authToken);
        username = jwtService.extractUsername(authToken);
        Optional<UserEntity> eventUser = userRepository.findById(userId);
        if (eventUser.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        UserEntity userEntity = eventUser.get();
        if (!Objects.equals(username, userEntity.getUsername())) {
            return new ResponseEntity<>("User not authorized", HttpStatus.UNAUTHORIZED);
        }
        Optional<EventEntity> events = eventRepository.findById(event_id);
        if (events.isEmpty()) {
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }
        EventEntity eventEntity = events.get();
        try {
            if (bookingEntity.getNo_of_tickets() <= 0) {
                return ResponseEntity.badRequest().body("Number of tickets must be greater than 0");
            }
            if (eventEntity.getCapacity() < bookingEntity.getNo_of_tickets()) {
                return ResponseEntity.badRequest().body("Not enough tickets available");
            }
            BookingEntity bookEvent = new BookingEntity();
            bookEvent.setEvent(eventEntity);
            bookEvent.setUser(userEntity);
            bookEvent.setBooking_date(LocalDate.now());
            bookEvent.setBooking_time(LocalTime.now());
            bookEvent.setNo_of_tickets(bookingEntity.getNo_of_tickets());
            bookEvent.setTotal_price(getTicketPrice(bookingEntity.getNo_of_tickets(), eventEntity.getEvent_price()));
            eventEntity.setCapacity(eventEntity.getCapacity() - bookingEntity.getNo_of_tickets());
            eventRepository.save(eventEntity);
            bookingRepository.save(bookEvent);
            mailService.sendBookingConfirmationMail(userEntity.getEmail(), eventEntity.getEvent_name(), eventEntity.getEvent_location(), eventEntity.getEvent_checkIn_time(), bookEvent.getNo_of_tickets(), bookEvent.getTotal_price(), bookEvent.getBooking_id(), eventEntity.getEvent_date(), eventEntity.getEvent_time());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking successful");
            response.put("event_name", eventEntity.getEvent_name());
            response.put("event_date", eventEntity.getEvent_date().toString());
            response.put("event_time", eventEntity.getEvent_time().toString());
            response.put("booking", bookEvent);

            BookingHistoryEntity bookingHistory = new BookingHistoryEntity();
            bookingHistory.setBookingId(bookEvent.getBooking_id());
            bookingHistory.setUserId(userEntity.getId());
            bookingHistory.setEventId(eventEntity.getId());
            bookingHistory.setEventName(eventEntity.getEvent_name());
            bookingHistory.setNumberOfTickets(bookEvent.getNo_of_tickets());
            bookingHistory.setTotalAmount(bookEvent.getTotal_price());
            bookingHistory.setBookingDate(LocalDateTime.now());
            bookingHistory.setBookingStatus("Booked");
            bookingHistory.setCancellationDate(null);
            bookingHistoryRepository.save(bookingHistory);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            mailService.sendBookingFailureMail(userEntity.getEmail());
            return new ResponseEntity<>("Booking failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getBookingsByBookingId(Long bookingId) {
        try {
            BookingEntity booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("No bookings found with ID: " + bookingId));

            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    public Double getTicketPrice(int no_of_tickets, double event_price) {
        return no_of_tickets * event_price;
    }

    @Cacheable(value = "totalTickets", key = "#eventId") // Cache total tickets
    public ResponseEntity<?> getTotalTicketsBooked(Long eventId) {
        try {
            EventEntity event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

            Long totalTickets = bookingRepository.getTotalTicketsBookedForEvent(eventId);

            if (totalTickets == null) {
                totalTickets = 0L;
            }
            Map<String, Object> response = new HashMap<>();
            response.put("eventId", eventId);
            response.put("event_name", event.getEvent_name());
            response.put("totalTickets", totalTickets);
            response.put("remainingTickets", event.getCapacity());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Some error occurred", HttpStatus.OK);
        }
    }

    @CacheEvict(value = "eventBookings", key = "#eventId") // Remove event bookings cache on cancellation
    public ResponseEntity<?> cancelBooking(Long bookingId, String token) {
        Long userId = jwtService.extractUserId(token);
        Optional<UserEntity> eventUser = userRepository.findById(userId);
        String email = eventUser.get().getEmail();

        try {
            Optional<BookingEntity> booking = bookingRepository.findById(bookingId);
            if (booking.isPresent()) {
                BookingEntity bookingEntity = booking.get();
                EventEntity event = bookingEntity.getEvent();
                Integer ticketsBooked = bookingEntity.getNo_of_tickets();

                Optional<BookingHistoryEntity> existingHistory = bookingHistoryRepository.findByBookingId(bookingId);
                if (existingHistory.isPresent()) {
                    BookingHistoryEntity history = existingHistory.get();
                    history.setBookingStatus("Cancelled");
                    history.setCancellationDate(LocalDateTime.now());
                    bookingHistoryRepository.save(history);
                }

                event.setCapacity(event.getCapacity() + ticketsBooked);
                eventRepository.save(event);

                mailService.sendBookingCancellationMail(email, bookingId);
                bookingRepository.deleteById(bookingId);

                return new ResponseEntity<>("Your tickets have been cancelled successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Booking not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while cancelling the booking.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getUserBookingHistory(Long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
        }
        List<BookingHistoryEntity> bookings = bookingHistoryRepository.findBookingHistoryByUserId(userId);
        if (bookings.isEmpty()) {
            return new ResponseEntity<>("No bookings found for this user", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }


    public ResponseEntity<?> getEventById(Long eventId) {
        if (eventId == null || eventId <= 0) {
            return ResponseEntity.badRequest().body("Invalid event ID");
        }

        try {
            EventEntity event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));


            return ResponseEntity.ok(event);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving event details: " + e.getMessage());
        }
    }


    public ResponseEntity<?> sendEventCancellationMailToAllRegisteredUsers(Long eventId) {
        List<BookingEntity> bookings = bookingRepository.findAllByEventId(eventId);
        if (bookings.isEmpty()) {
            return new ResponseEntity<>("No bookings found for the event", HttpStatus.NOT_FOUND);
        }

        List<String> failedEmails = new ArrayList<>();

        for (BookingEntity booking : bookings) {
            try {
                UserEntity user = booking.getUser();
                EventEntity event = booking.getEvent();
                mailService.sendEventCancellationMail(user.getEmail(), event.getEvent_name(), event.getEvent_date());
            } catch (MessagingException e) {
                failedEmails.add(booking.getUser().getEmail());
            }
        }

        if (!failedEmails.isEmpty()) {
            return new ResponseEntity<>("Failed to send emails to: " + String.join(", ", failedEmails), HttpStatus.PARTIAL_CONTENT);
        }
        return new ResponseEntity<>("Cancellation emails sent successfully to all registered users", HttpStatus.OK);
    }

    public ResponseEntity<?> getTotalBookingsCount() {
        Long totalBookings = bookingRepository.getTotalBookings();
        Map<String, Object> response = new HashMap<>();
        response.put("totalBookings", totalBookings);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getTotalTicketsBookedOverall() {
        Long totalTickets = bookingRepository.getTotalTicketsBooked();
        if (totalTickets == null) {
            totalTickets = 0L;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("totalTickets", totalTickets);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


}
