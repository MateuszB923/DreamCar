package pl.dreamcar.mateuszbochenek.mappers;

import org.springframework.stereotype.Component;
import pl.dreamcar.mateuszbochenek.dto.AdminMessageResponse;
import pl.dreamcar.mateuszbochenek.model.Message;

@Component
public class AdminMessageMapper {

    public AdminMessageResponse toDto(Message message) {
        return new AdminMessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getReadAt(),
                message.getStatus(),
                message.getUser() != null ? message.getUser().getEmail() : null,
                message.getName(),
                message.getEmail(),
                message.getSubject(),
                message.getCategory(),
                message.getCar() != null ? message.getCar().getId() : null,
                message.getMessage()
        );
    }
}
