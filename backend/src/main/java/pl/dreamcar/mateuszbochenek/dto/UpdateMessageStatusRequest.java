package pl.dreamcar.mateuszbochenek.dto;

import jakarta.validation.constraints.NotNull;
import pl.dreamcar.mateuszbochenek.model.MessageStatus;

public record UpdateMessageStatusRequest(
        @NotNull
        MessageStatus status
) {}
