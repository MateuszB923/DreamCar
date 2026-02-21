package pl.dreamcar.mateuszbochenek.mappers;

import org.springframework.stereotype.Component;
import pl.dreamcar.mateuszbochenek.dto.AdminReservationResponse;
import pl.dreamcar.mateuszbochenek.model.Reservation;

@Component
public class AdminReservationMapper {

    public AdminReservationResponse toDto(Reservation reservation) {
        String carName = (reservation.getCar() == null) ? null : (reservation.getCar().getBrand() + " " + reservation.getCar().getModel());

        return new AdminReservationResponse(
                reservation.getId(),
                reservation.getCreatedAt(),
                reservation.getReservationStatus(),
                reservation.getUser() != null ? reservation.getUser().getEmail() : null,
                reservation.getCar() != null ? reservation.getCar().getId() : null,
                carName,
                reservation.getReservationStartDate(),
                reservation.getReservationEndDate(),
                reservation.getNote()
        );
    }
}
