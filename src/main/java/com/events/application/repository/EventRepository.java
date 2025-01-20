package com.events.application.repository;

import com.events.application.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT e FROM EventEntity e WHERE LOWER(e.event_location) LIKE LOWER(CONCAT('%', ?1, '%'))")

    List<EventEntity> findByEvent_locationContaining(String location);

    @Query("SELECT e FROM EventEntity e WHERE LOWER(e.category) LIKE LOWER(CONCAT('%', ?1, '%'))")

    List<EventEntity> findByEventCategory(String category);

    @Query("SELECT e FROM EventEntity e WHERE e.event_date = ?1")
    List<EventEntity> findByEvent_date(LocalDate date);


    @Query("SELECT e FROM EventEntity e WHERE e.event_time = ?1")
    List<EventEntity> findByEvent_time(LocalTime time);


    @Query("SELECT DISTINCT e FROM EventEntity e WHERE " +
            "LOWER(e.event_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.event_location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.event_description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<EventEntity> searchEvents(@Param("keyword") String keyword);



}
