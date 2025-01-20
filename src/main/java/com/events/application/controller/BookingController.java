package com.events.application.controller;

import com.events.application.model.BookingEntity;
import com.events.application.service.BookingService;
import com.events.application.service.EventService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/bookEvents")
@RestController
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private EventService eventService;

    @PostMapping("/bookTickets/{event_id}")
    public ResponseEntity<?> bookTickets(@PathVariable Long event_id, @RequestBody BookingEntity bookingEntity, @RequestHeader("Authorization") String token) throws MessagingException {
        if (token == null || !token.startsWith("Bearer")) {
            throw new RuntimeException("Invalid token");
        }
        String authToken = token.substring(7);
        return bookingService.bookTicket(event_id,bookingEntity, authToken);
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getAllBookings(@RequestHeader("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        return bookingService.getAllBookings();
    }


    @GetMapping("/bookings/{event_id}")
    public ResponseEntity<?> getBookingsByEventId(@PathVariable Long event_id, @RequestHeader("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        return bookingService.getBookingsByEventId(event_id);
    }


    @GetMapping("/bookings/{booking_id}")
    public ResponseEntity<?> getBookingsByBookingId(@PathVariable Long booking_id, @RequestHeader("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        return bookingService.getBookingsByBookingId(booking_id);
    }

    @GetMapping("/{eventId}/totalTickets")
    public ResponseEntity<?> getTotalTicketsBooked(@RequestHeader("Authorization") String token,@PathVariable Long eventId) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        return bookingService.getTotalTicketsBooked(eventId);
    }

    @DeleteMapping("/bookings/{booking_id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long booking_id, @RequestHeader("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        return bookingService.cancelBooking(booking_id);
    }


    @GetMapping("/users/{userId}/bookings")
    public ResponseEntity<?> getUserBookingHistory(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        return bookingService.getUserBookingHistory(userId);
    }

    @GetMapping("/events/{eventId}/check-availability")
    public ResponseEntity<?> checkTicketAvailability(
            @PathVariable Long eventId,
            @RequestParam Integer requestedTickets,
            @RequestHeader("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        return bookingService.checkTicketAvailability(eventId, requestedTickets);
    }

    @PostMapping("/events/{eventId}/send-cancellation-mail")
    public ResponseEntity<?> sendEventCancellationMailToAllRegisteredUsers(@PathVariable Long eventId, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer")) {
            throw new RuntimeException("Invalid token");
        }

        return bookingService.sendEventCancellationMailToAllRegisteredUsers(eventId);
    }



}
