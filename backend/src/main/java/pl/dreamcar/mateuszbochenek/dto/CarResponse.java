package pl.dreamcar.mateuszbochenek.dto;

import pl.dreamcar.mateuszbochenek.model.Drivetrain;

import java.math.BigDecimal;

public record CarResponse(
        Long id,
        String brand,
        String model,
        Integer year,
        BigDecimal pricePerDay,
        String imageUrl,
        boolean available,

        BigDecimal zeroToHundredSeconds,
        Integer topSpeedKmh,
        Integer powerHp,
        Drivetrain drivetrain,
        String engine,
        Integer mileageKm,
        BigDecimal fuelConsumptionL100,

        String title,
        String description
) {}
