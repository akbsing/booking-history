package com.akbsing.bookinghistory;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking-history")
public class BookingHistoryController {

    private final BookingEventRepository bookingEventRepository;

    public BookingHistoryController(BookingEventRepository bookingEventRepository) {
        this.bookingEventRepository = bookingEventRepository;
    }

    @GetMapping
    public List<BookingEvent> getBookingHistory() {
        return bookingEventRepository.findAllByOrderByOccurredAtAscEventIdAsc();
    }
}
