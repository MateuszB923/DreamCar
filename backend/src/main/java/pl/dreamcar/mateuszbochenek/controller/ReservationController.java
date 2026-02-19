package pl.dreamcar.mateuszbochenek.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.dreamcar.mateuszbochenek.dto.ReservationCreateRequest;
import pl.dreamcar.mateuszbochenek.dto.ReservationResponse;
import pl.dreamcar.mateuszbochenek.service.ReservationService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public ResponseEntity<?> create(@Valid @RequestBody ReservationCreateRequest request, Authentication authentication) {
        Long id = reservationService.create(request, authentication.getName());
        return ResponseEntity.ok(Map.of("id", id));
    }

    @GetMapping("/me/reservations")
    public List<ReservationResponse> listRes(Authentication authentication) {
        return reservationService.listReservations(authentication.getName());
    }
}
