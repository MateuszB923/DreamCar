package pl.dreamcar.mateuszbochenek.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dreamcar.mateuszbochenek.dto.ContactRequest;
import pl.dreamcar.mateuszbochenek.service.ContactService;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<?> send(@Valid @RequestBody ContactRequest req, Authentication auth) {
        Long id = contactService.create(req, auth.getName());
        return ResponseEntity.ok(Map.of("id", id));
    }
}
