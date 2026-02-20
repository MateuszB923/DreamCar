package pl.dreamcar.mateuszbochenek.dto;

import pl.dreamcar.mateuszbochenek.model.Role;

public record AdminUsersResponse(

        Long id,
        String email,
        Role role,
        boolean locked
) {
}
