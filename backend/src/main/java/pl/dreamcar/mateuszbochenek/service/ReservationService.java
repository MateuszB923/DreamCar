package pl.dreamcar.mateuszbochenek.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.dreamcar.mateuszbochenek.dto.ReservationCreateRequest;
import pl.dreamcar.mateuszbochenek.dto.ReservationResponse;
import pl.dreamcar.mateuszbochenek.model.Car;
import pl.dreamcar.mateuszbochenek.model.Reservation;
import pl.dreamcar.mateuszbochenek.model.ReservationStatus;
import pl.dreamcar.mateuszbochenek.model.User;
import pl.dreamcar.mateuszbochenek.repository.CarRepository;
import pl.dreamcar.mateuszbochenek.repository.ReservationRepository;
import pl.dreamcar.mateuszbochenek.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Transactional
    public Long create(ReservationCreateRequest request, String emailJwt) {

        if (request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data końcowa nie może być wcześniejsza niż początkowa");
        }
        if (request.startDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data startu nie może być z przeszłości");
        }

        User user = userRepository.findByEmail(emailJwt)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Car car = carRepository.findById(request.carId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));

        Reservation savedReservation = reservationRepository.save(Reservation.builder()
                .user(user)
                .car(car)
                .reservationStartDate(request.startDate())
                .reservationEndDate(request.endDate())
                .note(request.note())
                .reservationStatus(ReservationStatus.PENDING)
                .build());

        return savedReservation.getId();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> listReservations(String emailJwt) {
        User user = userRepository.findByEmail(emailJwt)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return reservationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(r -> new ReservationResponse(
                        r.getId(),
                        r.getCar().getId(),
                        r.getCar().getBrand() + " " + r.getCar().getModel(),
                        r.getReservationStartDate(),
                        r.getReservationEndDate(),
                        r.getReservationStatus(),
                        r.getCreatedAt()
                ))
                .toList();
    }
}