package pl.dreamcar.mateuszbochenek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.dreamcar.mateuszbochenek.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
