package pl.dreamcar.mateuszbochenek.dto;

import lombok.Builder;

@Builder
public record CarReviewResponse(
        Long id,
        String author,
        String review
) {}
