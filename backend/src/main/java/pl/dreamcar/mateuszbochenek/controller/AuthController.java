package pl.dreamcar.mateuszbochenek.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dreamcar.mateuszbochenek.dto.AuthResponse;
import pl.dreamcar.mateuszbochenek.dto.LoginRequest;
import pl.dreamcar.mateuszbochenek.dto.RegisterRequest;
import pl.dreamcar.mateuszbochenek.service.AuthService;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        Long id = authService.register(request);
        return ResponseEntity.created(URI.create("/api/users/" + id)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
