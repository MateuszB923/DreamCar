package pl.dreamcar.mateuszbochenek.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.dreamcar.mateuszbochenek.model.Role;
import pl.dreamcar.mateuszbochenek.model.User;
import pl.dreamcar.mateuszbochenek.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String adminEmail = System.getenv("ADMIN_EMAIL");
        String adminPassword = System.getenv("ADMIN_PASSWORD");

        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }

        if (userRepository.existsByEmail(adminEmail)) {
            User existingUser = userRepository.findByEmail(adminEmail).orElseThrow();
            if (existingUser.getRole() != Role.ADMIN) {
                existingUser.setRole(Role.ADMIN);
                userRepository.save(existingUser);
            }
            return;
        }

        User admin = new User(adminEmail, passwordEncoder.encode(adminPassword), Role.ADMIN);
        userRepository.save(admin);
    }
}
