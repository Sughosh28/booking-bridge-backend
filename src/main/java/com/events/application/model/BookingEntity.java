package com.events.application.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long booking_id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private EventEntity event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    private Integer no_of_tickets;
    private Double total_price;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate booking_date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime booking_time;

    public Long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Integer getNo_of_tickets() {
        return no_of_tickets;
    }

    public void setNo_of_tickets(Integer no_of_tickets) {
        this.no_of_tickets = no_of_tickets;
    }

    public Double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(Double total_price) {
        this.total_price = total_price;
    }

    public LocalDate getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(LocalDate booking_date) {
        this.booking_date = booking_date;
    }

    public LocalTime getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(LocalTime booking_time) {
        this.booking_time = booking_time;
    }


}
