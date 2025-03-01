package com.events.application.controller;

import com.events.application.model.EventEntity;
import com.events.application.service.BookingService;
import com.events.application.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequestMapping("/api/organizer")
@RestController

public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private BookingService bookingService;

    @GetMapping("/get-all-events")
    public ResponseEntity<?> getEvents(@RequestHeader("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer ")){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return eventService.getEvents(authToken);
    }

    @PostMapping("/createEvent")
    public ResponseEntity<?> createEvent(@RequestHeader("Authorization") String token,
                                         @RequestBody EventEntity events) {
        if(token==null || !token.startsWith("Bearer ")){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return eventService.createEvent(authToken,events);

    }

    @PutMapping("/updateEvents/{event_id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long event_id,
                                         @RequestHeader("Authorization") String token,
                                         @RequestBody EventEntity events) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken = token.substring(7);
        return eventService.updateEvent(event_id,authToken, events);
    }
    @DeleteMapping("/deleteEvent/{event_id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long event_id,
                                         @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken = token.substring(7);
        return eventService.deleteEvent(authToken,event_id);
    }

    @GetMapping("/get-bookings/{event_id}")
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

    @GetMapping("/totalEvents")
    public ResponseEntity<?> getTotalEvents(@RequestHeader ("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }

        return eventService.getTotalEvents();
    }

    @GetMapping("/recent-events")
    public ResponseEntity<?> getRecentEvents(@RequestHeader ("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        List<EventEntity> recentEvents = eventService.getRecentEvents();
        return new ResponseEntity<>(recentEvents, HttpStatus.OK);
    }

    @GetMapping("/total-ticket-bookings")
    public ResponseEntity<?> getTotalTicketBookings(@RequestHeader ("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        return bookingService.getTotalTicketsBookedOverall();
    }
    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable Long eventId, @RequestHeader("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        return bookingService.getEventById(eventId);
    }

    @GetMapping("/upcoming-events")
    public ResponseEntity<?> getUpcomingEvents(@RequestHeader ("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer")){
            throw new RuntimeException("Invalid token");
        }
        return eventService.getUpcomingEvents();
    }

    @PostMapping("/push-mail-notification")
    public ResponseEntity<?> pushMailNotification(@RequestHeader ("Authorization") String token, @RequestParam Long eventId) {
        if (token == null || !token.startsWith("Bearer")) {
            throw new RuntimeException("Invalid token");
        }
        return eventService.pushMailNotification(eventId);
    }

}
