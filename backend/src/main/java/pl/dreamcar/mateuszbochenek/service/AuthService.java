package pl.dreamcar.mateuszbochenek.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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


    //TODO sprawdzic czy tu nie trzeba transactional
    public Long register(RegisterRequest request){

        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email jest już zajęty");
        }

        if (!isValidPassword(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Hasło musi mieć min. 8 znaków, 1 dużą literę, 1 cyfrę i 1 znak specjalny (np. !@#)");
        }

        String hash = passwordEncoder.encode(request.password());
        User user = new User(request.email(), hash, Role.USER);

        return userRepository.save(user).getId();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nieprawidłowy email lub hasło");
        } catch (LockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Konto jest zablokowane");
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Konto jest wyłączone");
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nie udało się zalogować");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nieprawidłowy email lub hasło"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    private boolean isValidPassword(String p) {
        if (p == null) return false;
        return p.length() >= 8
                && p.matches(".*[A-Z].*")
                && p.matches(".*\\d.*")
                && p.matches(".*[^a-zA-Z0-9].*");
    }
}
