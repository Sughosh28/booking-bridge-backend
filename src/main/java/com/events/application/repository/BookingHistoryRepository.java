package com.events.application.repository;

import com.events.application.model.BookingEntity;
import com.events.application.model.BookingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistoryEntity, Long> {

    @Query("SELECT b FROM BookingHistoryEntity b WHERE b.userId = :userId")
    List<BookingHistoryEntity> findBookingHistoryByUserId(@Param("userId") Long userId);

    Optional<BookingHistoryEntity> findByBookingId(Long bookingId);
}

