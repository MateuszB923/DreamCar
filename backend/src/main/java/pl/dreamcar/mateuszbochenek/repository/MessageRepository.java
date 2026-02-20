package pl.dreamcar.mateuszbochenek.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dreamcar.mateuszbochenek.model.Message;
import pl.dreamcar.mateuszbochenek.model.MessageStatus;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"user", "car"})
    List<Message> findAllByOrderByCreatedAtDescIdDesc();

    @EntityGraph(attributePaths = {"user", "car"})
    List<Message> findAllByStatusOrderByCreatedAtDescIdDesc(MessageStatus status);
}
