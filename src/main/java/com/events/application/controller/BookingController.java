package com.events.application.controller;

import com.events.application.jwt.JwtService;
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
    @Autowired
    private JwtService jwtService;

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
        String cleanToken = token.substring(7).trim();
        Long userId = jwtService.extractUserId(cleanToken);
        return bookingService.getAllBookingsByUser(userId);
    }

    @DeleteMapping("/bookings/{booking_id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long booking_id, @RequestHeader("Authorization") String token) {

        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        String cleanToken = token.substring(7).trim();

        return bookingService.cancelBooking(booking_id, cleanToken);
    }


    @GetMapping("/users/bookings")
    public ResponseEntity<?> getUserBookingHistory(@RequestHeader("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        String cleanToken = token.substring(7).trim();
        Long userId = jwtService.extractUserId(cleanToken);
        return bookingService.getUserBookingHistory(userId);
    }




}
