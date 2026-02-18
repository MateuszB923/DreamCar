package pl.dreamcar.mateuszbochenek.controller;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dreamcar.mateuszbochenek.dto.CarResponse;
import pl.dreamcar.mateuszbochenek.dto.CarReviewResponse;
import pl.dreamcar.mateuszbochenek.service.CarService;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public List<CarResponse> listCars() {
        return carService.findAll();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly=true)
    public CarResponse getCarById(@PathVariable Long id) {
        return carService.findById(id);
    }
}
