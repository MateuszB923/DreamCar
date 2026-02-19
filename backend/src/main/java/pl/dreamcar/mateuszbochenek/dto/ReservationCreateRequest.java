package pl.dreamcar.mateuszbochenek.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ReservationCreateRequest(

        @NotNull
        Long carId,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @Size(max = 1000)
        String note
) {}
