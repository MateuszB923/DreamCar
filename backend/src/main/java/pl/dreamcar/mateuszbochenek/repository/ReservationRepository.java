package pl.dreamcar.mateuszbochenek.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dreamcar.mateuszbochenek.model.Reservation;
import pl.dreamcar.mateuszbochenek.model.ReservationStatus;

import java.time.LocalDate;
import java.util.Collection;
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

    @Query("""
    select (count(r) > 0) from Reservation r
    where r.car.id = :carId
      and r.reservationStatus in :activeStatuses
      and r.reservationStartDate <= :endDate
      and r.reservationEndDate >= :startDate
    """)
    boolean existsOverlap(@Param("carId") Long carId,
                          @Param("activeStatuses") Collection<ReservationStatus> activeStatuses,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);
}
