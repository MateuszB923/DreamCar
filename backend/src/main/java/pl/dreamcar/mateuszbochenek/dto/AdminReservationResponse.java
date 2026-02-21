package pl.dreamcar.mateuszbochenek.dto;

import pl.dreamcar.mateuszbochenek.model.ReservationStatus;

import java.time.Instant;
import java.time.LocalDate;

public record AdminReservationResponse(

        Long id,
        Instant createdAt,
        ReservationStatus status,
        String userEmail,
        Long carId,
        String carName,
        LocalDate startDate,
        LocalDate endDate,
        String note
) {}
