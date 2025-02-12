package com.events.application.service;

import com.events.application.model.EventEntity;
import com.events.application.repository.EventRepository;
import com.events.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService;

    public ResponseEntity<?> createEvent(String token, EventEntity events) {
        if(token==null){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        try {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEvent_name(events.getEvent_name());
            eventEntity.setEvent_description(events.getEvent_description());
            eventEntity.setEvent_location(events.getEvent_location());
            eventEntity.setEvent_price(events.getEvent_price());
            eventEntity.setCategory(events.getCategory());
            eventEntity.setCapacity(events.getCapacity());
            eventEntity.setEvent_date(events.getEvent_date());
            eventEntity.setEvent_time(events.getEvent_time());
            eventEntity.setArtists(events.getArtists());
            eventEntity.setAge_restrictions(events.getAge_restrictions());
            eventEntity.setOrganizer_name(events.getOrganizer_name());
            eventEntity.setOrganizer_contact_details(events.getOrganizer_contact_details());
            eventEntity.setLanguage(events.getLanguage());
            eventEntity.setEvent_checkIn_time( events.getEvent_time().minusMinutes(40));
            eventRepository.save(eventEntity);
            return new ResponseEntity<>("Event created successfully", HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>("Event creation failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getEvents(String token) {
        if(token==null){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        List<EventEntity> events= eventRepository.findAll();
        if(!events.isEmpty()) {
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
        return new ResponseEntity<>("There are no events", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getEventsByLocation(String token, String location) {
        if(token==null){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        List<EventEntity> events= eventRepository.findByEvent_locationContaining(location);
        if(!events.isEmpty()) {
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
        return new ResponseEntity<>("There are no events", HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<?> getEventsByDate(String token, LocalDate date) {
        if(token==null){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        List<EventEntity> events= eventRepository.findByEvent_date(date);
        if(!events.isEmpty()) {
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
        return new ResponseEntity<>("There are no events", HttpStatus.BAD_REQUEST);
    }



    public ResponseEntity<?> getEventsByCategory(String token, String category) {
        if(token==null){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        List<EventEntity> events= eventRepository.findByEventCategory(category);
        if(!events.isEmpty()) {
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
        return new ResponseEntity<>("There are no events", HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<?> getEventsByTime(String token, LocalTime time) {
        if(token==null){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        List<EventEntity> events= eventRepository.findByEvent_time(time);
        if(!events.isEmpty()) {
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
        return new ResponseEntity<>("There are no events", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> updateEvent(Long id, String token,EventEntity event) {
        if(token==null){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        Optional<EventEntity> eventEntity= eventRepository.findById(id);
        if(eventEntity.isEmpty()){
            return new ResponseEntity<>("Event not found", HttpStatus.BAD_REQUEST);
        }
        try {
            EventEntity updateEvent = eventEntity.get();
            updateEvent.setEvent_name(event.getEvent_name());
            updateEvent.setEvent_description(event.getEvent_description());
            updateEvent.setEvent_location(event.getEvent_location());
            updateEvent.setEvent_price(event.getEvent_price());
            updateEvent.setCategory(event.getCategory());
            updateEvent.setCapacity(event.getCapacity());
            updateEvent.setEvent_date(event.getEvent_date());
            updateEvent.setEvent_time(event.getEvent_time());
            updateEvent.setArtists(event.getArtists());
            updateEvent.setAge_restrictions(event.getAge_restrictions());
            updateEvent.setOrganizer_name(event.getOrganizer_name());
            updateEvent.setOrganizer_contact_details(event.getOrganizer_contact_details());
            updateEvent.setLanguage(event.getLanguage());
            eventRepository.save(updateEvent);
            return new ResponseEntity<>("Event updated successfully", HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("Event updation failed "+e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteEvent(String token, Long id) {
        if(token==null){
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        Optional<EventEntity> eventEntity= eventRepository.findById(id);
        if(eventEntity.isEmpty()){
            return new ResponseEntity<>("Event not found", HttpStatus.BAD_REQUEST);
        }
        try {

            eventRepository.deleteById(id);
            bookingService.sendEventCancellationMailToAllRegisteredUsers(id);
            return new ResponseEntity<>("Event deleted successfully", HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("Event deletion failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<List<EventEntity>> searchEvents(String keyword) {
        if(keyword!=null && !keyword.isEmpty()){
            return new ResponseEntity<>( eventRepository.searchEvents(keyword), HttpStatus.OK);
        }
        return new ResponseEntity<>( eventRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<?> getTotalEvents() {
        return new ResponseEntity<>(eventRepository.findAll().size(), HttpStatus.OK);
    }

    public List<EventEntity> getRecentEvents() {
        List<EventEntity> recentEvents = eventRepository.findRecentEvents();
        return recentEvents;
    }

    public ResponseEntity<?> getUpcomingEvents() {
        List<EventEntity> upcomingEvents = eventRepository.findUpcomingEvents();
        if(upcomingEvents.isEmpty()) {
            return new ResponseEntity<>("No upcoming events found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(upcomingEvents, HttpStatus.OK);
    }

    public ResponseEntity<?> pushMailNotification(Long eventId) {
        List<String> mails= userRepository.findAllMails();
        EventEntity eventEntity=eventRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("Event does not exist"));;
        mailService.pushNotification(mails, eventEntity);
        return new ResponseEntity<>("Done", HttpStatus.OK);

    }
}
