package com.events.application.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String event_name;
    private String event_location;
    private Double event_price;
    private int capacity;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate event_date;
    @JsonFormat(pattern = "HH:mm", timezone = "UTC")
    private LocalTime event_time;
    @JsonFormat(pattern = "HH:mm", timezone = "UTC")
    private LocalTime event_checkIn_time;
    private String event_description;
    @ElementCollection
    private List<String> artists;
    private String age_restrictions;
    private String language;
    private String organizer_name;
    private String organizer_contact_details;
    private String category;

    @OneToMany(mappedBy = "event", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<BookingEntity> bookings;


    public String getOrganizer_name() {
        return organizer_name;
    }

    public void setOrganizer_name(String organizer_name) {
        this.organizer_name = organizer_name;
    }

    public String getOrganizer_contact_details() {
        return organizer_contact_details;
    }

    public void setOrganizer_contact_details(String organizer_contact_details) {
        this.organizer_contact_details = organizer_contact_details;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAge_restrictions() {
        return age_restrictions;
    }

    public void setAge_restrictions(String age_restrictions) {
        this.age_restrictions = age_restrictions;
    }

    public LocalTime getEvent_checkIn_time() {
        return event_checkIn_time;
    }

    public void setEvent_checkIn_time(LocalTime event_checkIn_time) {
        this.event_checkIn_time = event_checkIn_time;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public Double getEvent_price() {
        return event_price;
    }

    public void setEvent_price(Double event_price) {
        this.event_price = event_price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LocalDate getEvent_date() {
        return event_date;
    }

    public void setEvent_date(LocalDate event_date) {
        this.event_date = event_date;
    }

    public LocalTime getEvent_time() {
        return event_time;
    }

    public void setEvent_time(LocalTime event_time) {
        this.event_time = event_time;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }


}
