package pl.dreamcar.mateuszbochenek.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.dreamcar.mateuszbochenek.dto.AccountResponse;
import pl.dreamcar.mateuszbochenek.dto.ChangePasswordRequest;
import pl.dreamcar.mateuszbochenek.dto.DeleteAccountRequest;
import pl.dreamcar.mateuszbochenek.model.User;
import pl.dreamcar.mateuszbochenek.repository.UserRepository;
import pl.dreamcar.mateuszbochenek.service.AccountService;

import java.util.Map;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class AccountController {

    private final UserRepository userRepository;
    private final AccountService accountService;

    @GetMapping
    public AccountResponse greetAccount(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return new AccountResponse(user.getId(), user.getEmail(), user.getRole());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        accountService.changePassword(request, authentication.getName());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMe(@Valid @RequestBody DeleteAccountRequest request, Authentication authentication) {
        accountService.deleteAccount(authentication.getName(), request.password());
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
