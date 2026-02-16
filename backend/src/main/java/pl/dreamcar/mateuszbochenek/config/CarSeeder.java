package pl.dreamcar.mateuszbochenek.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.dreamcar.mateuszbochenek.model.Car;
import pl.dreamcar.mateuszbochenek.model.CarDescription;
import pl.dreamcar.mateuszbochenek.model.CarSpec;
import pl.dreamcar.mateuszbochenek.model.Drivetrain;
import pl.dreamcar.mateuszbochenek.repository.CarRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.seed", name = "cars", havingValue = "true", matchIfMissing = true)
public class CarSeeder implements CommandLineRunner {

    private final CarRepository carRepository;

    @Override
    public void run(String... args) {
        if (carRepository.count() > 0) return;

        List<Car> cars = List.of(
                buildCar(
                        "Audi", "RS7 Sportback", 2024,
                        new BigDecimal("3200.00"), "/images/audi.jpg", true,
                        new BigDecimal("3.7"), 305, 610, Drivetrain.AWD,
                        "4.0L V8 Twin Turbo", 11_400, new BigDecimal("18.7"),
                        "Audi RS7 Sportback",
                        "Audi RS7 Sportback to perfekcyjne połączenie elegancji, mocy i technologii. "
                                + "Napędzane 4.0-litrowym silnikiem V8 Twin Turbo o mocy 610 KM i napędem quattro, "
                                + "oferuje błyskawiczne przyspieszenie i doskonałą trakcję w każdych warunkach. "
                                + "Komfortowe wnętrze klasy premium i sportowy charakter sprawiają, że RS7 to auto, "
                                + "które potrafi być zarówno limuzyną na co dzień, jak i bezkompromisową bestią na autostradzie."
                ),
                buildCar(
                        "Porsche", "911 GT3 RS", 2025,
                        new BigDecimal("3700.00"), "/images/porsche.jpg", true,
                        new BigDecimal("3.0"), 312, 525, Drivetrain.RWD,
                        "4.0L Boxer", 9_800, new BigDecimal("18.0"),
                        "Porsche 911 GT3 RS",
                        "Porsche 911 GT3 RS to esencja niemieckiej precyzji i motorsportowego DNA. "
                                + "Dzięki wolnossącemu silnikowi 4.0L, wysokim obrotom i perfekcyjnemu wyważeniu, "
                                + "auto dostarcza czystych emocji za kierownicą. Każdy zakręt to przyjemność, "
                                + "a charakterystyczny dźwięk boksera przypomina, dlaczego 911 to legenda toru i ulicy."
                ),
                buildCar(
                        "Mercedes-Benz", "AMG GT R", 2024,
                        new BigDecimal("3900.00"), "/images/benz.jpg", true,
                        new BigDecimal("3.4"), 318, 585, Drivetrain.RWD,
                        "4.0L V8 Biturbo", 5_300, new BigDecimal("20.0"),
                        "Mercedes-AMG GT R",
                        "Mercedes-AMG GT R to szczyt niemieckiej inżynierii – połączenie luksusu i bezkompromisowych osiągów. "
                                + "Pod maską pracuje potężne V8 Biturbo o mocy 585 KM, przenoszone na tylną oś przez "
                                + "7-biegową skrzynię AMG Speedshift. Agresywny wygląd, aktywna aerodynamika i dźwięk silnika "
                                + "sprawiają, że GT R to nie tylko samochód — to emocje zamknięte w metalu."
                ),
                buildCar(
                        "Aston Martin", "DBS Superleggera", 2023,
                        new BigDecimal("4200.00"), "/images/aston.jpg", true,
                        new BigDecimal("2.9"), 340, 700, Drivetrain.AWD,
                        "5.2L V12 Twin Turbo", 7_000, new BigDecimal("22.5"),
                        "Aston Martin DBS Superleggera",
                        "Aston Martin DBS Superleggera to kwintesencja brytyjskiego stylu i mocy. "
                                + "Napędzany potężnym silnikiem V12, oferuje niezrównane połączenie elegancji i osiągów. "
                                + "Idealny zarówno na tor, jak i luksusową podróż wzdłuż wybrzeża."
                )
        );

        carRepository.saveAll(cars);
    }

    private static Car buildCar(
            String brand,
            String model,
            int year,
            BigDecimal pricePerDay,
            String imageUrl,
            boolean available,
            BigDecimal zeroToHundred,
            int topSpeedKmh,
            int powerHp,
            Drivetrain drivetrain,
            String engine,
            int mileageKm,
            BigDecimal fuelConsumption,
            String title,
            String description
    ) {
        Car car = Car.builder()
                .brand(brand)
                .model(model)
                .year(year)
                .pricePerDay(pricePerDay)
                .imageUrl(imageUrl)
                .available(available)
                .build();

        CarSpec spec = CarSpec.builder()
                .zeroToHundredSeconds(zeroToHundred)
                .topSpeedKmh(topSpeedKmh)
                .powerHp(powerHp)
                .drivetrain(drivetrain)
                .engine(engine)
                .mileageKm(mileageKm)
                .fuelConsumptionL100(fuelConsumption)
                .build();

        CarDescription carDescription = CarDescription.builder()
                .title(title)
                .description(description)
                .build();

        car.setSpec(spec);
        car.setDescription(carDescription);

        return car;
    }
}
