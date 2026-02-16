package pl.dreamcar.mateuszbochenek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dreamcar.mateuszbochenek.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
}
