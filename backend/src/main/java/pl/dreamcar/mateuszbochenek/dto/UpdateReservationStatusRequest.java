package pl.dreamcar.mateuszbochenek.dto;

import jakarta.validation.constraints.NotNull;
import pl.dreamcar.mateuszbochenek.model.ReservationStatus;

public record UpdateReservationStatusRequest(
        @NotNull
        ReservationStatus status
) {}
