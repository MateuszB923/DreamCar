package pl.dreamcar.mateuszbochenek.dto;

import java.time.Instant;

public record MessageResponse(
        Long id,
        String subject,
        String message,
        String category,
        String status,
        Instant createdAt,
        Long carId
) {}
