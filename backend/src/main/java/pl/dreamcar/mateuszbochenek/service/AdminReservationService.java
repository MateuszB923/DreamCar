package pl.dreamcar.mateuszbochenek.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.dreamcar.mateuszbochenek.dto.AdminReservationResponse;
import pl.dreamcar.mateuszbochenek.mappers.AdminReservationMapper;
import pl.dreamcar.mateuszbochenek.model.Reservation;
import pl.dreamcar.mateuszbochenek.model.ReservationStatus;
import pl.dreamcar.mateuszbochenek.repository.ReservationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationRepository reservationRepository;
    private final AdminReservationMapper adminReservationMapper;

    @Transactional(readOnly = true)
    public List<AdminReservationResponse> listReservations(ReservationStatus status, Long carId) {

        List<Reservation> reservations;

        if (status == null && carId == null) {
            reservations = reservationRepository.findAllByOrderByCreatedAtDescIdDesc();
        } else if (status != null && carId == null) {
            reservations = reservationRepository.findAllByReservationStatusOrderByCreatedAtDescIdDesc(status);
        } else if (status == null) {
            reservations = reservationRepository.findAllByCarIdOrderByCreatedAtDescIdDesc(carId);
        } else {
            reservations = reservationRepository.findAllByReservationStatusAndCarIdOrderByCreatedAtDescIdDesc(status, carId);
        }

        return reservations.stream().map(adminReservationMapper::toDto).toList();
    }

    @Transactional
    public void updateStatus(Long id, ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found: " + id));

        reservation.setReservationStatus(newStatus);
        reservationRepository.save(reservation);
    }

}