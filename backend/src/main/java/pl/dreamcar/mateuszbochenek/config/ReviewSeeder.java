package pl.dreamcar.mateuszbochenek.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pl.dreamcar.mateuszbochenek.model.Car;
import pl.dreamcar.mateuszbochenek.model.CarReview;
import pl.dreamcar.mateuszbochenek.repository.CarRepository;
import pl.dreamcar.mateuszbochenek.repository.CarReviewRepository;

import java.util.List;

@Order(2)
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.seed", name = "reviews", havingValue = "true", matchIfMissing = true)
public class ReviewSeeder implements CommandLineRunner {

    private final CarRepository carRepository;
    private final CarReviewRepository reviewRepository;

    @Override
    public void run(String... args) {
        if (reviewRepository.count() > 0) return;

        List<Car> cars = carRepository.findAll();
        if (cars.isEmpty()) return;

        attachReviewsToCarModel(cars, "DBS Superleggera", List.of(
                review("Michał K.", "Aston Martin DBS Superleggera to potwór na kołach! Przyspieszenie wgniata w fotel, a dźwięk silnika to czysta poezja."),
                review("Aleksandra P.", "Auto w perfekcyjnym stanie - błyszczące, dopieszczone w każdym detalu. Czuć luksus od pierwszej chwili."),
                review("Krzysztof S.", "Przejażdżka DBS-em to coś, czego się nie zapomina. Stabilność i moc robią ogromne wrażenie, a obsługa była na najwyższym poziomie."),
                review("Bartosz W.","Wynajem przebiegł błyskawicznie, a auto wyglądało jak prosto z salonu. Idealne na weekendową wyprawę!"),
                review("Karolina S.", "Najlepszy samochód, jakim jeździłem. Aston DBS Superleggera łączy brutalną moc z elegancją - prawdziwe dzieło sztuki.")
        ));

        attachReviewsToCarModel(cars, "911 GT3 RS", List.of(
                review("Michał K.", "Porsche 911 GT3 RS to prawdziwa bestia! Auto trzyma się drogi jak przyklejone, a dźwięk silnika przyprawia o dreszcze."),
                review("Aleksandra P.", "Perfekcyjne prowadzenie i niesamowita precyzja w zakrętach. Czuć każdy detal niemieckiej inżynierii."),
                review("Krzysztof S.", "Miałem okazję przejechać się po torze i to było jak jazda w grze! Reakcja na gaz natychmiastowa, emocje nie do opisania."),
                review("Bartosz W.", "Wynajem przebiegł sprawnie, a GT3 RS był w idealnym stanie. Auto wyglądało jak nowe - pełen profesjonalizm."),
                review("Karolina S.", "Najlepszy samochód, jakim kiedykolwiek jeździłam. Brutalna moc i sportowy charakter w perfekcyjnej harmonii.")
        ));

        attachReviewsToCarModel(cars, "AMG GT R", List.of(
                review("Michał K.","Mercedes-AMG GT R to czysta adrenalina! Dźwięk silnika V8 przyprawia o ciarki, a auto prowadzi się jak marzenie."),
                review("Aleksandra P.","Wyjątkowy samochód - połączenie luksusu i agresji. Każde wciśnięcie gazu przypomina, że AMG to klasa sama w sobie."),
                review("Krzysztof S.","GT R prowadzi się perfekcyjnie, nawet przy dużych prędkościach. Stabilność i reakcja na kierownicę - absolutny top."),
                review("Bartosz W.","Wynajem przebiegł bezproblemowo, a auto wyglądało i brzmiało jak nowe. Jazda tym Mercedesem to czysta przyjemność.")
        ));

        attachReviewsToCarModel(cars, "", List.of(
                review("Aleksandra P.","Auto wygląda niesamowicie - elegancja i agresja w jednym. Prowadzenie RS7 to czysta przyjemność, nawet przy dużych prędkościach."),
                review("Michał K.","Audi RS7 to perfekcyjne połączenie komfortu i brutalnej mocy. Przyspieszenie wgniata w fotel, a wnętrze to czysty luksus."),
                review("Krzysztof S.","Silnik V8 Twin Turbo robi swoje. Niesamowity dźwięk, świetna trakcja i płynność jazdy - RS7 to idealne auto na każdą okazję."),
                review("Bartosz W.","Bardzo profesjonalna obsługa i perfekcyjny stan samochodu. RS7 prowadzi się jak marzenie, a komfort wnętrza jest topowy."),
                review("Karolina S.","To nie jest zwykłe Audi - to potwór w garniturze. Cicha elegancja i potężna moc pod maską - absolutne 10/10.")
        ));
    }

    private void attachReviewsToCarModel(List<Car> cars, String model, List<CarReview> reviews) {
        cars.stream()
                .filter(c -> model.equalsIgnoreCase(c.getModel()))
                .findFirst()
                .ifPresent(car -> {
                    for (CarReview r : reviews) {
                        r.setCar(car);
                    }
                    reviewRepository.saveAll(reviews);
                });
    }

    private CarReview review(String author, String text) {
        return CarReview.builder()
                .author(author)
                .review(text)
                .build();
    }
}
