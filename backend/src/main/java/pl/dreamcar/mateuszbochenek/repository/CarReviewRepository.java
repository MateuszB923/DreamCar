package pl.dreamcar.mateuszbochenek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dreamcar.mateuszbochenek.model.CarReview;

import java.util.List;

@Repository
public interface CarReviewRepository extends JpaRepository<CarReview, Long> {
    List<CarReview> findByCarIdOrderByIdAsc(Long carId);
}
