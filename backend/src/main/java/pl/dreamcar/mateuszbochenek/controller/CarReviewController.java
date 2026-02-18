package pl.dreamcar.mateuszbochenek.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.dreamcar.mateuszbochenek.dto.CarReviewCreateRequest;
import pl.dreamcar.mateuszbochenek.dto.CarReviewResponse;
import pl.dreamcar.mateuszbochenek.service.CarService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cars")
public class CarReviewController {

    private final CarService carService;

    @GetMapping("/{carId}/reviews")
    public List<CarReviewResponse> listReviews(@PathVariable Long carId) {
        return carService.findReviewsByCarId(carId);
    }

    @PostMapping("/{carId}/reviews")
    public CarReviewResponse addReview(@PathVariable Long carId, @Valid @RequestBody CarReviewCreateRequest request, Authentication authentication) {
        return carService.addReview(carId, request, authentication);
    }
}
