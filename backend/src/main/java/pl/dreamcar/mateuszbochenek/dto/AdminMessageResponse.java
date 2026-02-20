package pl.dreamcar.mateuszbochenek.dto;

import pl.dreamcar.mateuszbochenek.model.ContactCategory;
import pl.dreamcar.mateuszbochenek.model.MessageStatus;

import java.time.Instant;

public record AdminMessageResponse(

        Long id,
        Instant createdAt,
        Instant readAt,
        MessageStatus status,
        String userEmail,
        String name,
        String email,
        String subject,
        ContactCategory category,
        Long carId,
        String message
) {}
