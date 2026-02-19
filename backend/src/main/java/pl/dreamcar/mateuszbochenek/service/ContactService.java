package pl.dreamcar.mateuszbochenek.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dreamcar.mateuszbochenek.dto.ContactRequest;
import pl.dreamcar.mateuszbochenek.model.*;
import pl.dreamcar.mateuszbochenek.repository.CarRepository;
import pl.dreamcar.mateuszbochenek.repository.MessageRepository;
import pl.dreamcar.mateuszbochenek.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Builder
public class ContactService {

    private final MessageRepository contactRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Transactional
    public Long create(ContactRequest req, String emailJwt) {

        User user = userRepository.findByEmail(emailJwt)
                .orElseThrow(() -> new RuntimeException("User not found: " + emailJwt));

        ContactCategory category = parseCategory(req.category());

        Car car = null;
        if (req.carId() != null) {
            car = carRepository.findById(req.carId())
                    .orElseThrow(() -> new RuntimeException("Car not found: " + req.carId()));
        }

        Message savedMessage = contactRepository.save(Message.builder()
                .user(user)
                .name(req.name())
                .email(req.email())
                .subject(req.subject())
                .message(req.message())
                .category(category)
                .car(car)
                .status(MessageStatus.NEW)
                .build());

        return savedMessage.getId();
    }

    private ContactCategory parseCategory(String rawText) {
        if (rawText == null || rawText.isBlank()) return ContactCategory.GENERAL;
        try {
            return ContactCategory.valueOf(rawText.trim().toUpperCase());
        } catch (Exception e) {
            return ContactCategory.GENERAL;
        }
    }
}
