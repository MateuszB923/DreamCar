package pl.dreamcar.mateuszbochenek.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import pl.dreamcar.mateuszbochenek.model.Drivetrain;

import java.math.BigDecimal;

public record CarSpecDto(
        @NotNull
        @DecimalMin("0.1")
        @DecimalMax("99.9")
        @Digits(integer = 3, fraction = 1)
        BigDecimal zeroToHundredSeconds,

        @NotNull
        @Min(1)
        @Max(450)
        Integer topSpeedKmh,

        @NotNull
        @Min(1)
        @Max(2000)
        Integer powerHp,

        @NotNull
        @Enumerated(EnumType.STRING)
        Drivetrain drivetrain,

        @NotBlank
        @Size(max = 60)
        String engine,

        @NotNull
        @Min(0)
        @Max(2_000_000)
        Integer mileageKm,

        @NotNull
        @DecimalMin("0.1")
        @DecimalMax("99.9")
        @Digits(integer = 3, fraction = 1)
        BigDecimal fuelConsumptionL100

) {}
