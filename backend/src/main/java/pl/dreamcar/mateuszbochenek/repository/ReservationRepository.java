package pl.dreamcar.mateuszbochenek.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dreamcar.mateuszbochenek.model.Reservation;
import pl.dreamcar.mateuszbochenek.model.ReservationStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Reservation> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"user", "car"})
    List<Reservation> findAllByOrderByCreatedAtDescIdDesc();

    @EntityGraph(attributePaths = {"user", "car"})
    List<Reservation> findAllByReservationStatusOrderByCreatedAtDescIdDesc(ReservationStatus reservationStatus);

    @EntityGraph(attributePaths = {"user", "car"})
    List<Reservation> findAllByCarIdOrderByCreatedAtDescIdDesc(Long carId);

    @EntityGraph(attributePaths = {"user", "car"})
    List<Reservation> findAllByReservationStatusAndCarIdOrderByCreatedAtDescIdDesc(ReservationStatus reservationStatus, Long carId);
}
