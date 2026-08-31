package com.akbsing.bookinghistory;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookingHistoryController.class)
class BookingHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingEventRepository bookingEventRepository;

    @Test
    void returnsAnEmptyHistory() throws Exception {
        when(bookingEventRepository.findAllByOrderByOccurredAtAscEventIdAsc()).thenReturn(List.of());

        mockMvc.perform(get("/booking-history"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void returnsTheRepositoryOrderedHistory() throws Exception {
        BookingEvent earliest = event("event-a", Instant.parse("2026-08-31T10:00:00Z"));
        BookingEvent latest = event("event-b", Instant.parse("2026-08-31T12:00:00Z"));
        when(bookingEventRepository.findAllByOrderByOccurredAtAscEventIdAsc())
                .thenReturn(List.of(earliest, latest));

        mockMvc.perform(get("/booking-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventId").value("event-a"))
                .andExpect(jsonPath("$[1].eventId").value("event-b"));
    }

    private static BookingEvent event(String eventId, Instant occurredAt) {
        return new BookingEvent(
                eventId,
                "booking-123",
                "restaurant-456",
                LocalDate.of(2026, 9, 12),
                4,
                "BOOKING_CREATED",
                occurredAt);
    }
}
