package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "booking")
public class Booking {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id")
    String userId;

    @Column(name = "trip_id")
    Long tripId;

    @Column(name = "date_time")
    LocalDateTime dateTime;

    @Column(name = "start_location")
    String startFrom;

    @Column(name = "destination")
    String destination;

    @Column(name = "seat")
    int seat;

    @Column(name = "accepted")
    boolean accepted;

}
