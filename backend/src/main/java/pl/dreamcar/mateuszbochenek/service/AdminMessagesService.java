package pl.dreamcar.mateuszbochenek.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.dreamcar.mateuszbochenek.dto.AdminMessageResponse;
import pl.dreamcar.mateuszbochenek.mappers.AdminMessageMapper;
import pl.dreamcar.mateuszbochenek.model.Message;
import pl.dreamcar.mateuszbochenek.model.MessageStatus;
import pl.dreamcar.mateuszbochenek.repository.MessageRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMessagesService {

    private final MessageRepository messageRepository;
    private final AdminMessageMapper mapper;

    @Transactional(readOnly = true)
    public List<AdminMessageResponse> listMessages(MessageStatus status) {
        List<Message> messages = (status == null)
                ? messageRepository.findAllByOrderByCreatedAtDescIdDesc()
                : messageRepository.findAllByStatusOrderByCreatedAtDescIdDesc(status);

        return messages.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public void updateMessageStatus(Long id, MessageStatus newStatus) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found: " + id));

        message.setStatus(newStatus);

        if (newStatus == MessageStatus.READ && message.getReadAt() == null) {
            message.setReadAt(Instant.now());
        }
        if (newStatus == MessageStatus.NEW) {
            message.setReadAt(null);
        }

        messageRepository.save(message);
    }
}
