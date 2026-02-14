package pl.dreamcar.mateuszbochenek.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.dreamcar.mateuszbochenek.config.JwtService;
import pl.dreamcar.mateuszbochenek.dto.AuthResponse;
import pl.dreamcar.mateuszbochenek.dto.LoginRequest;
import pl.dreamcar.mateuszbochenek.dto.RegisterRequest;
import pl.dreamcar.mateuszbochenek.model.Role;
import pl.dreamcar.mateuszbochenek.model.User;
import pl.dreamcar.mateuszbochenek.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Long register(RegisterRequest request){
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("This email is already taken");
        }

        String hash = passwordEncoder.encode(request.password());
        User user = new User(request.email(), hash, Role.USER);

        return userRepository.save(user).getId();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
