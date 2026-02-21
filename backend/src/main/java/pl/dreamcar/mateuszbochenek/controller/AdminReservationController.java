package pl.dreamcar.mateuszbochenek.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dreamcar.mateuszbochenek.dto.AdminReservationResponse;
import pl.dreamcar.mateuszbochenek.dto.UpdateReservationStatusRequest;
import pl.dreamcar.mateuszbochenek.model.ReservationStatus;
import pl.dreamcar.mateuszbochenek.service.AdminReservationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reservations")
public class AdminReservationController {

    private final AdminReservationService adminReservationsService;

    @GetMapping
    public List<AdminReservationResponse> list(@RequestParam(required = false) ReservationStatus status, @RequestParam(required = false) Long carId) {
        return adminReservationsService.listReservations(status, carId);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateReservationStatusRequest request) {
        adminReservationsService.updateStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }
}
