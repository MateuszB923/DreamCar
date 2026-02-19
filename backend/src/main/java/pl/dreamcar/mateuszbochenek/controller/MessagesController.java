package pl.dreamcar.mateuszbochenek.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dreamcar.mateuszbochenek.dto.MessageResponse;
import pl.dreamcar.mateuszbochenek.service.ContactService;

import java.util.List;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MessagesController {

    private final ContactService contactService;

    @GetMapping("/messages")
    public List<MessageResponse> myMessages(Authentication auth) {
        return contactService.listMessages(auth.getName());
    }
}
