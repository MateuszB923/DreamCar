package pl.dreamcar.mateuszbochenek.dto;

import pl.dreamcar.mateuszbochenek.model.Role;

public record AccountResponse(
        Long id,
        String email,
        Role role
) {}
