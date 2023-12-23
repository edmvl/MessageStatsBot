package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepo extends JpaRepository<Trip, Long> {
    List<Trip> findAllByDateTimeBetweenAndPublishedOrderByDateTimeAsc(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd, boolean accepted);

}
