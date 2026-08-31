package com.akbsing.bookinghistory;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingEventRepository extends MongoRepository<BookingEvent, String> {

    List<BookingEvent> findAllByOrderByOccurredAtAscEventIdAsc();
}
