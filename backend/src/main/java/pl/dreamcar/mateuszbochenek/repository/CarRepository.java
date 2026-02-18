package pl.dreamcar.mateuszbochenek.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dreamcar.mateuszbochenek.model.Car;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @EntityGraph(attributePaths = {"carSpec", "carDescription"})
    List<Car> findAll();

    @EntityGraph(attributePaths = {"carSpec", "carDescription"})
    Optional<Car> findById(Long id);

}
