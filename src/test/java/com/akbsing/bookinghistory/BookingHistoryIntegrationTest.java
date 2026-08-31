package com.akbsing.bookinghistory;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class BookingHistoryIntegrationTest {

    @Container
    static final MongoDBContainer MONGODB = new MongoDBContainer("mongo:8.0.14");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingEventRepository bookingEventRepository;

    @DynamicPropertySource
    static void configureMongoDb(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGODB::getReplicaSetUrl);
    }

    @BeforeEach
    void clearBookingEvents() {
        bookingEventRepository.deleteAll();
    }

    @Test
    void returnsAnEmptyHistoryWhenNoEventsExist() throws Exception {
        mockMvc.perform(get("/booking-history"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void returnsEventsInChronologicalOrderWithEventIdAsTieBreaker() throws Exception {
        BookingEvent latest = event("event-c", Instant.parse("2026-08-31T12:00:00Z"));
        BookingEvent tiedSecond = event("event-b", Instant.parse("2026-08-31T11:00:00Z"));
        BookingEvent earliest = event("event-a", Instant.parse("2026-08-31T10:00:00Z"));
        BookingEvent tiedFirst = event("event-a2", Instant.parse("2026-08-31T11:00:00Z"));
        bookingEventRepository.saveAll(List.of(latest, tiedSecond, earliest, tiedFirst));

        mockMvc.perform(get("/booking-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].eventId").value("event-a"))
                .andExpect(jsonPath("$[1].eventId").value("event-a2"))
                .andExpect(jsonPath("$[2].eventId").value("event-b"))
                .andExpect(jsonPath("$[3].eventId").value("event-c"))
                .andExpect(jsonPath("$[0].date").value("2026-09-12"))
                .andExpect(jsonPath("$[0].occurredAt").value("2026-08-31T10:00:00Z"));
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
