package pl.dreamcar.mateuszbochenek.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CarCreateRequest(

        @NotBlank
        @Size(max = 50)
        String brand,

        @NotBlank
        @Size(max = 80)
        String model,

        @NotNull
        @Min(1886)
        @Max(2030)
        Integer year,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 8, fraction = 2)
        BigDecimal pricePerDay,

        @NotBlank
        String imageUrl,

        @NotNull
        Boolean available,

        @Valid
        @NotNull
        CarSpecDto spec,

        @NotBlank @Size(max = 120)
        String title,

        @NotBlank
        String description
) {}
