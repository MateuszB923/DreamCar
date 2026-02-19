package pl.dreamcar.mateuszbochenek.dto;

import pl.dreamcar.mateuszbochenek.model.ReservationStatus;

import java.time.Instant;
import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        Long carId,
        String carName,
        LocalDate startDate,
        LocalDate endDate,
        ReservationStatus status,
        Instant createdAt
) {}
