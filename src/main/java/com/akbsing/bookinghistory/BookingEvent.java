package com.akbsing.bookinghistory;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bookingEvent")
public record BookingEvent(
        @Id String eventId,
        String bookingId,
        String restaurantId,
        LocalDate date,
        int numberOfDiners,
        String eventType,
        Instant occurredAt) {
}
