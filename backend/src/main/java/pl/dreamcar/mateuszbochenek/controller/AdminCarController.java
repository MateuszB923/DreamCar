package pl.dreamcar.mateuszbochenek.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.dreamcar.mateuszbochenek.dto.CarCreateRequest;
import pl.dreamcar.mateuszbochenek.dto.CarResponse;
import pl.dreamcar.mateuszbochenek.dto.CarUpdateRequest;
import pl.dreamcar.mateuszbochenek.service.CarService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cars")
@RequiredArgsConstructor
public class AdminCarController {

    private final CarService carService;

    @GetMapping
    public List<CarResponse> getAll() {
        return carService.findAll();
    }

    @GetMapping("/{id}")
    public CarResponse getOne(@PathVariable Long id) {
        return carService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponse createCar(@Valid @RequestBody CarCreateRequest request) {
        return carService.create(request);
    }

    @PatchMapping("/{id}")
    public CarResponse update(@PathVariable Long id, @Valid @RequestBody CarUpdateRequest request) {
        return carService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        carService.delete(id);
    }
}
