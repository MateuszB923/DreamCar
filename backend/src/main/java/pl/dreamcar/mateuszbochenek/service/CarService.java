package pl.dreamcar.mateuszbochenek.service;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dreamcar.mateuszbochenek.dto.*;
import pl.dreamcar.mateuszbochenek.mappers.ReviewMapper;
import pl.dreamcar.mateuszbochenek.model.Car;
import pl.dreamcar.mateuszbochenek.model.CarDescription;
import pl.dreamcar.mateuszbochenek.model.CarReview;
import pl.dreamcar.mateuszbochenek.model.CarSpec;
import pl.dreamcar.mateuszbochenek.repository.CarRepository;
import pl.dreamcar.mateuszbochenek.mappers.CarMapper;
import pl.dreamcar.mateuszbochenek.exception.NotFoundException;
import pl.dreamcar.mateuszbochenek.repository.CarReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final CarReviewRepository carReviewRepository;
    private final ReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    public List<CarResponse> findAll() {
        return carRepository.findAll().stream().map(carMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CarResponse findById(Long id) {
        return carMapper.toResponse(getCarOrThrow(id));
    }

    @Transactional
    public CarResponse create(CarCreateRequest request) {
        Car car = Car.builder()
                .brand(request.brand())
                .model(request.model())
                .year(request.year())
                .pricePerDay(request.pricePerDay())
                .imageUrl(request.imageUrl())
                .available(request.available() == null || request.available())
                .build();

        car.setSpec(carMapper.toSpecEntity(request.spec()));

        car.setDescription(carMapper.toDescriptionEntity(request.title(), request.description()));

        Car saved = carRepository.save(car);
        return carMapper.toResponse(saved);
    }

    @Transactional
    public CarResponse update(Long id, CarUpdateRequest request) {
        Car car = getCarOrThrow(id);

        if (request.brand() != null) car.setBrand(request.brand());
        if (request.model() != null) car.setModel(request.model());
        if (request.year() != null) car.setYear(request.year());
        if (request.pricePerDay() != null) car.setPricePerDay(request.pricePerDay());
        if (request.imageUrl() != null) car.setImageUrl(request.imageUrl());
        if (request.available() != null) car.setAvailable(request.available());

        if (request.spec() != null) {
            CarSpec spec = car.getCarSpec();
            if (spec == null) {
                car.setSpec(carMapper.toSpecEntity(request.spec()));
            } else {
                carMapper.applySpecPatch(spec, request.spec());
            }
        }

        if (request.title() != null || request.description() != null) {
            CarDescription desc = car.getCarDescription();

            if (desc == null) {
                car.setDescription(carMapper.toDescriptionEntity(request.title(), request.description()));
            } else {
                if (request.title() != null) desc.setTitle(request.title());
                if (request.description() != null) desc.setDescription(request.description());
            }
        }

        return carMapper.toResponse(car);
    }

    @Transactional
    public void delete(Long id) {
        Car car = getCarOrThrow(id);
        carRepository.delete(car);
    }

    @Transactional(readOnly = true)
    public List<CarReviewResponse> findReviewsByCarId(Long carId) {
        return carReviewRepository.findByCarIdOrderByIdAsc(carId).stream()
                .map(r -> CarReviewResponse.builder()
                        .id(r.getId())
                        .author(r.getAuthor())
                        .review(r.getReview())
                        .build())
                .toList();
    }

    @Transactional
    public CarReviewResponse addReview(Long carId, CarReviewCreateRequest request, Authentication auth) {
        Car car = getCarOrThrow(carId);

        String author = (auth != null ? auth.getName() : "anonymous");

        CarReview saved = carReviewRepository.save(
                CarReview.builder()
                        .car(car)
                        .author(author)
                        .review(request.review())
                        .build()
        );

        return CarReviewResponse.builder()
                .id(saved.getId())
                .author(saved.getAuthor())
                .review(saved.getReview())
                .build();
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!carReviewRepository.existsById(reviewId)) {
            throw new NotFoundException("Review not found: " + reviewId);
        }
        carReviewRepository.deleteById(reviewId);
    }

    public List<CarReviewResponse> findAllReviews() {
        return carReviewRepository.findAllByOrderByIdAsc()
                .stream()
                .map(reviewMapper::toReviewResponse)
                .toList();
    }

    private Car getCarOrThrow(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Car not found: " + id));
    }
}
