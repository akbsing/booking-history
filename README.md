# Booking History

A read-only Spring Boot service that exposes booking lifecycle events stored by the booking service in MongoDB.

## Prerequisites

- Java 21 for running with Gradle
- Docker and Docker Compose for the containerized development environment and integration tests

## Run locally

Start a local MongoDB instance, then run the application:

```shell
docker compose up -d mongodb
./gradlew bootRun
```

Alternatively, build and run the complete environment:

```shell
docker compose up --build
```

The API is available at `http://localhost:8080/booking-history`, the health endpoint at
`http://localhost:8080/actuator/health`, and the OpenAPI document at
`http://localhost:8080/openapi.yaml`.

## MongoDB document

Events are read from the `bookingEvent` collection. The `eventId` field is stored as MongoDB's `_id`:

```json
{
  "_id": "evt-01J9Y9C2Z7R8A6G5K4M3N2P1Q0",
  "bookingId": "booking-123",
  "restaurantId": "restaurant-456",
  "date": "2026-09-12",
  "numberOfDiners": 4,
  "eventType": "BOOKING_CREATED",
  "occurredAt": "2026-08-31T11:58:21Z"
}
```

## API

```shell
curl http://localhost:8080/booking-history
```

The response is a JSON array ordered by `occurredAt` from oldest to newest, with `eventId` as the
tie-breaker.

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `MONGODB_URI` | `mongodb://localhost:27017/booking_history` | MongoDB connection URI and database |
| `SERVER_PORT` | `8080` | HTTP server port |

No database credentials are embedded in the application. Supply credentials through `MONGODB_URI`
when authentication is enabled.

## Test and build

```shell
./gradlew build
```

Integration tests start MongoDB in a Testcontainer and do not require a fixed host port.
