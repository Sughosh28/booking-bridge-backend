package com.events.application.controller;

import com.events.application.model.EventEntity;
import com.events.application.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequestMapping("/api/events")
@RestController
public class EventController {
    @Autowired
    private EventService eventService;

    @PostMapping("/createEvent")
    public ResponseEntity<?> createEvent(@RequestHeader("Authorization") String token,
                                         @RequestBody EventEntity events) {
        if(token==null || !token.startsWith("Bearer ")){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return eventService.createEvent(authToken,events);

    }

    @GetMapping("/getEvents")
    public ResponseEntity<?> getEvents(@RequestHeader("Authorization") String token) {
        if(token==null || !token.startsWith("Bearer ")){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return eventService.getEvents(authToken);
    }


    @GetMapping("/getEventsByLocation")
    public ResponseEntity<?> getEventsByLocation(@RequestHeader("Authorization") String token,
                                                 @RequestParam String location) {
        if(token==null || !token.startsWith("Bearer ")){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return eventService.getEventsByLocation(authToken, location);
    }

    @GetMapping("/getEventsByCategory")
    public ResponseEntity<?> getEventsByCategory(@RequestHeader("Authorization") String token,
                                                 @RequestParam String category) {
        if(token==null || !token.startsWith("Bearer ")){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return eventService.getEventsByCategory(authToken, category);
    }

    @GetMapping("/getEventsByDate")
    public ResponseEntity<?> getEventsByDate(@RequestHeader("Authorization") String token,
                                             @RequestParam LocalDate date) {
        if(token==null || !token.startsWith("Bearer ")){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return eventService.getEventsByDate(authToken, date);
    }

    @GetMapping("/getEventsByTime")
    public ResponseEntity<?> getEventsByTime(@RequestHeader("Authorization") String token,
                                             @RequestParam LocalTime time) {
        if(token==null || !token.startsWith("Bearer ")){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String authToken= token.substring(7);
        return eventService.getEventsByTime(authToken, time);
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

    @GetMapping("/search")
    public ResponseEntity<List<EventEntity>> searchEvents(@RequestHeader("Authorization") String token,
                                                          @RequestParam(required = false) String keyword) {
        return eventService.searchEvents(keyword);
    }




}
