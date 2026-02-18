package pl.dreamcar.mateuszbochenek.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.dreamcar.mateuszbochenek.dto.CarReviewResponse;
import pl.dreamcar.mateuszbochenek.service.CarService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    private final CarService carService;

    @GetMapping
    public List<CarReviewResponse> listAll() {
        return carService.findAllReviews();
    }

    @GetMapping("/car/{carId}")
    public List<CarReviewResponse> listByCar(@PathVariable Long carId) {
        return carService.findReviewsByCarId(carId);
    }

    @DeleteMapping("/{reviewId}")
    public void delete(@PathVariable Long reviewId) {
        carService.deleteReview(reviewId);
    }
}
